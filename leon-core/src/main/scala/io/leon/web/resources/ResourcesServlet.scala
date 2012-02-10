/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.resources

import javax.servlet._
import http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import io.leon.web.WebUtils
import io.leon.resourceloading.{ResourceLoader, Resource}
import com.google.inject._
import io.leon.web.htmltagsprocessor.LeonTagProcessor

class ResourcesServlet @Inject()(resourceLoader: ResourceLoader, leonTag: LeonTagProcessor) extends HttpServlet {

  //private val logger = Logger.getLogger(getClass.getName)

  private val welcomeFiles = List("index.html")

  override def service(req: HttpServletRequest, res: HttpServletResponse) {
    val url = WebUtils.getRequestedResource(req)
    doResource(req, res, url)
  }

  private def doResource(req: HttpServletRequest, res: HttpServletResponse, path: String) {
    val out = res.getOutputStream

    val possiblePaths =
      if(path.endsWith("/")) welcomeFiles map { path + _ }
      else if (path.split("/").last.contains(".")) List(path)
      else List(path) ::: (welcomeFiles map { path + "/" + _ })

    val resourceOption =
      possiblePaths.foldLeft[Option[Resource]](None) { _ orElse resourceLoader.getResourceOption(_) }

    resourceOption match {
      case Some(resource) => {

        val resourceTransformed =
          if(resource.name.endsWith(".html")) leonTag.transform(resource)
          else resource

        setResponseContentType(req, res)
        val stream = resourceTransformed.createInputStream()
        val buffer = new Array[Byte](1024)
        var bytesRead = 0
        while (bytesRead != -1) {
          out.write(buffer, 0, bytesRead)
          bytesRead = stream.read(buffer)
        }
      }
      case None => {
        res.setContentType("text/html")
        res.setStatus(404)
      }
    }
    out.close()
  }

  private def setResponseContentType(req: HttpServletRequest, res: HttpServletResponse) {
    Option(getServletContext.getMimeType(req.getRequestURI)) foreach res.setContentType
  }
  
}
