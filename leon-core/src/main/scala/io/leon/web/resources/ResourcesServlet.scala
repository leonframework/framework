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
import io.leon.guice.GuiceUtils
import io.leon.web.servletwhiteboard.ServletBinding
import scala.collection.JavaConverters._

class ResourcesServlet @Inject()(injector: Injector,
                                 resourceLoader: ResourceLoader,
                                 leonTag: LeonTagProcessor) extends HttpServlet {

  private val welcomeFiles = List("index.html")

  private val exposedServlets = GuiceUtils.getAllBindingsForType(injector, classOf[ServletBinding]).asScala

  override def service(req: HttpServletRequest, res: HttpServletResponse) {
    val url = WebUtils.getRequestedResource(req)
    doResource(req, res, url)
  }

  private def doResource(req: HttpServletRequest, res: HttpServletResponse, path: String) {
    // Check for Servlets
    val url = WebUtils.getRequestedResource(req)
    val servletMatches = exposedServlets map { _.getProvider.get() } filter { sb => sb.url == url }
    if (servletMatches.size > 1) {
      throw new IllegalStateException("More than one Servlets were registered to handle the URL " + url)
    } else if (servletMatches.size == 1) {
      val servlet = servletMatches(0).servlet
      servlet.service(req, res)
      return
    }

    // Check for resources
    val possiblePaths =
      if(path.endsWith("/")) welcomeFiles map { path + _ }
      else if (path.split("/").last.contains(".")) List(path)
      else List(path) ::: (welcomeFiles map { path + "/" + _ })

    val resourceOption =
      possiblePaths.foldLeft[Option[Resource]](None) { _ orElse resourceLoader.getResourceOption(_) }
    val out = res.getOutputStream
    if (resourceOption.isDefined) {
      val resource = resourceOption.get
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

      out.close()
      return
    }

    // When we got here, no Servlet or Resource were suitable
    res.setContentType("text/html")
    res.setStatus(404)
    out.close()
  }

  private def setResponseContentType(req: HttpServletRequest, res: HttpServletResponse) {
    Option(getServletContext.getMimeType(req.getRequestURI)) foreach res.setContentType
  }
  
}