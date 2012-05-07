/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resourceloading

import io.leon.config.ConfigMap
import com.google.inject.Inject
import java.io._
import java.util.concurrent.locks.ReentrantLock
import scala.collection.JavaConverters._
import org.slf4j.LoggerFactory
import io.leon.utils.{DateUtils, FileUtils}

class ResourceCache @Inject()(resourceLoadingStack: ResourceLoadingStack,
                              configMap: ConfigMap) {

  private val logger = LoggerFactory.getLogger(getClass.getName)

  private val appName = configMap.getApplicationName

  private val sep = File.separator

  private val javaIoTmpDir = System.getProperty("java.io.tmpdir")

  private val cacheDir = new File(javaIoTmpDir + sep + "leon" + sep + appName + sep + "cache")

  private val dependenciesDir = new File(javaIoTmpDir + sep + "leon" + sep + appName + sep + "dependencies")

  private val dependencyWriterLock = new ReentrantLock()

  private def getCacheFile(filename: String): File = {
    new File(cacheDir, filename)
  }

  private def getDependencyFile(fileName: String): File = {
    new File(dependenciesDir, fileName)
  }

  private def getResourceLoadingStack(): java.util.List[String] = {
    resourceLoadingStack.getResourceLoadingStack()
  }

  private def addDependency(rootResource: String, dependency: String) {
    logger.trace("Checking dependency [{}] of resource [{}]", dependency, rootResource)
    dependencyWriterLock.lock()
    try {
      val dependencyFile = getDependencyFile(rootResource)
      dependencyFile.getParentFile.mkdirs()

      // read dependency file
      val lines = FileUtils.readLines(dependencyFile)
      logger.trace("Current dependency list for resource [{}]: {}", rootResource, lines)

      // check if this is a new dependency
      if (!lines.contains(dependency)) {
        logger.trace("New dependency for resource [{}]: [{}]", rootResource, dependency)
        val writer = new BufferedWriter(new FileWriter(dependencyFile, true))
        // add new dependency
        writer.write(dependency + "\n")
        writer.close()
      }
    } finally {
      dependencyWriterLock.unlock()
    }
  }

  def doDependencyCheck(fileName: String) {
    if (getResourceLoadingStack().size() == 0) {
      // No nested resource loading
      return
    }
    if (getResourceLoadingStack().contains(fileName)) {
      // Current resource already checked (happens often e.g. when nested resource are checked for their timestamps)
      return
    }

    // Get "root" resource
    val rootResource = getResourceLoadingStack().get(getResourceLoadingStack().size() - 1)

    // Add the current resource as a dependency of the "root" resource
    addDependency(rootResource, fileName)
  }


  /**
   * @param resource The resource to check
   * @param resourceLoader A ResourceLoader instance. This is required so that the cache
   *                       can check the dependencies. Guice DI does not work here due to the
   *                       circular dependency.
   *
   * @return true if the cache is up to date, false otherwise
   */
  def isCacheUpToDate(resource: Resource, fileName: String, resourceLoader: ResourceLoader): Boolean = {
    logger.trace("Checking cache for resource [{}]", fileName)
    val cacheFile = getCacheFile(fileName)
    if (!cacheFile.exists()) {
      logger.debug("Resource [{}] does not exist in cache", fileName)
      return false
    }

    logger.trace(
      "Timestamp for resource [{}] is: [{}]",
      resource.name,
      DateUtils.timeInLongToReadableString(resource.getLastModified()))

    val cacheFileTimestamp = cacheFile.lastModified()
    logger.trace(
      "Timestamp in cache for resource [{}] is: [{}]",
      fileName,
      DateUtils.timeInLongToReadableString(cacheFileTimestamp))

    if (cacheFileTimestamp < resource.getLastModified()) {
      logger.debug("Cache for resource [{}] is not up to date.", fileName)
      return false
    }

    val dependencies = FileUtils.readLines(getDependencyFile(fileName))
    for (line <- dependencies.asScala) {
      logger.trace("Checking timestamp of dependency [{}]", line)

      val dependencyResource = resourceLoader.getResource(line)
      if (!dependencyResource.isCachable()) {
        logger.trace("Dependency [{}] is not cachable hence resource [{}] is not up to date", line, fileName)
        return false
      }

      if (!dependencyResource.wasLoadedFromCache()) {
        logger.trace("Dependency [{}] was not loaded from cache", line)
        return false
      }
    }

    // If we reach this line, resource is up to date
    true
  }

  /**
   * @param fileName the filename that should be used to put the resource into the cache
   * @param resource the resource that should be stored in the cache
   */
  def put(fileName: String, resource: Resource): Resource = {
    val cacheFile = new File(cacheDir, fileName)
    cacheFile.getParentFile.mkdirs()

    // TODO optimize! Do not use readLine, use bytes directly instead
    val reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(resource.getInputStream())))
    val writerFile = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(cacheFile))))
    val byteArrayOutput = new ByteArrayOutputStream()
    val writerMem = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(byteArrayOutput)))

    var line = reader.readLine()
    while (line != null) {
      writerFile.write(line + "\n")
      writerMem.write(line + "\n")
      line = reader.readLine()
    }
    writerFile.close()
    writerMem.close()
    reader.close()

    new Resource(fileName) {
      def getLastModified() = resource.getLastModified()

      def getInputStream() = new ByteArrayInputStream(byteArrayOutput.toByteArray)

      def isCachable() = true
    }
  }

  /**
   * @param fileName the file that should be looked up in the cache
   * @return the cached {@link Resource} or an {@link IllegalArgumentException} if the file could not be found
   */
  def get(fileName: String): Resource = {
    val f = getCacheFile(fileName)
    if (!f.exists()) {
      throw new IllegalArgumentException("The resource " + fileName + " does not exist in the cache.")
    }
    new Resource(fileName) {
      override def getLastModified() = f.lastModified()

      override def getInputStream() = new FileInputStream(f)

      override def isCachable() = true

      override def wasLoadedFromCache() = true
    }
  }

}
