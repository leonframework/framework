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

class ResourceCache @Inject()(configMap: ConfigMap) {

  private val appName = configMap.getApplicationName

  private val sep = File.separator

  private val javaIoTmpDir = System.getProperty("java.io.tmpdir")

  private val cacheDir = new File(javaIoTmpDir + sep + "leon" + sep + appName)

  private def getCacheFile(filename: String): File = {
    new File(cacheDir, filename)
  }

  /**
   * @param filename The filename to lookup the timestamp for
   * @return the timestamp of the file in the cache or 0L if the file does not exist in the cache
   */
  def getTimestampOfCacheFile(filename: String): Long = {
    val f = getCacheFile(filename)
    if (!f.exists())
      0
    else
      f.lastModified()
  }

  /**
   * @param fileName the filename that should be used to put the resource into the cache
   * @param resource the resource that should be stored in the cache
   */
  def put(fileName: String, resource: Resource) {
    val cacheFile = new File(cacheDir, fileName)
    cacheFile.getParentFile.mkdirs()

    val reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(resource.getInputStream())))
    val writer = new BufferedWriter(new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(cacheFile))))

    var line = reader.readLine()
    while (line != null) {
      writer.write(line + "\n")
      line = reader.readLine()
    }
    writer.close()
    reader.close()
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
      def getLastModified() = f.lastModified()

      def getInputStream() = new FileInputStream(f)
    }
  }

}
