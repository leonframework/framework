/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resources

import com.google.inject._
import java.lang.RuntimeException
import java.util.Date
import java.io.{FileOutputStream, FileInputStream, InputStream, File}
import org.apache.commons.io.FileUtils
import scala.io.Source
import scala.collection.JavaConversions._

class ResourceLoader @Inject()(injector: Injector,
                               resourceProcessorRegistry: ResourceProcessorRegistry) {

  import scala.collection.JavaConverters._

  val resourceLocations: List[Binding[ResourceLocation]] = {
    injector.findBindingsByType(new TypeLiteral[ResourceLocation]() {}).asScala.toList
  }

  def getResource(fileName: String): Resource = {
    getResourceOption(fileName) match {
      case Some(resource) => resource
      case None => throw new RuntimeException("Resource [%s] not found!".format(fileName))
    }
  }

  def getResourceOption(fileName: String): Option[Resource] = {
    val cacheLocation = System.getProperty("java.io.tmpdir")
    //System.out.println("Cache is at: " + cacheLocation)

    for (processor <- resourceProcessorRegistry.processorsForFile(fileName)) {
      val fileNameForProcessor = resourceProcessorRegistry.replaceFileNameEndingForProcessor(processor, fileName)

      for (rl <- resourceLocations) {
        rl.getProvider.get().getResource(fileNameForProcessor) match {
          case Some(res) => {

            val fileFromCache: File = getOrTransformResource(res, fileNameForProcessor, cacheLocation, processor)
            return Some(new Resource(fileName, () => fileFromCache.lastModified(), () => new FileInputStream(fileFromCache)))
          }
          case None => None
        }
      }
    }
    None
  }

  private def getOrTransformResource(res: Resource, fileNameForProcessor: String, cacheLocation: String, processor: ResourceProcessor):File = {
    val fileNameInCache: String = cacheLocation + fileNameForProcessor

    val cachedFile = new File(fileNameInCache)
    var doTransform = false

    if (cachedFile.exists()) {
      //System.out.println("File exists " + fileNameInCache)

      if (res.lastModified > cachedFile.lastModified())
        doTransform = true
    }
    else
      doTransform = true

    if (doTransform) {
      val processedRes = processor.process(res)

      //TODO get os seperator
      val dir = fileNameInCache.take(fileNameInCache.lastIndexOf("/"))

      System.out.println("Creating dir " + dir)
      new File(dir).mkdirs()
      System.out.println("Creating File " + fileNameInCache)

      val newFile = new File(fileNameInCache)

      val lines = Source.fromInputStream(processedRes.getInputStream).getLines().toList

      //System.out.println("Writing " + fileName + " to cache ....")
      FileUtils.writeLines(newFile, lines)

      //System.out.println("Written " + fileName + " to cache ....")
    }
    else {
      //System.out.println("Serving " + fileName + " from cache ....")
    }

    cachedFile
  }

}


