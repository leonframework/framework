package io.leon.web.resources

/*
 * Copyright (c) 2010 WeigleWilczek and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
import javax.servlet._
import http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import com.google.inject.servlet.ServletModule
import io.leon.AbstractLeonConfiguration
import com.google.inject.{AbstractModule, Inject}
import io.leon.guice.annotations.InternalPathsList

class ResourcesWebModule extends ServletModule {
  override def configureServlets() {
    install(new ResourcesModule)
    serve("/*").`with`(classOf[ResourcesServlet])
  }
}

class ResourcesModule extends AbstractModule {
  def configure() {
    bind(classOf[ResourcesServlet]).asEagerSingleton()
  }
}

class ResourcesServlet @Inject()(config: AbstractLeonConfiguration,
                                 @InternalPathsList internalPaths: List[String]) extends HttpServlet {

  //private val logger = Logger.getLogger(getClass.getName)

  override def service(req: HttpServletRequest, res: HttpServletResponse) {
    val contextPath = req.getContextPath
    val requestUri = req.getRequestURI
    val url = requestUri.substring(contextPath.size)

    if (isInternalPath(url)) {
      res.setStatus(403)
      // TODO send page
      return
    }

    val urlList = url.split('/').toList dropWhile { _ == "" }
    urlList match {
      case "leon" :: "jquery.js" :: Nil =>
        doResource(req, res, "/leon/jquery-1.5.1.min.js")

      case "leon" :: "knockout.js" :: Nil =>
        doResource(req, res, "/leon/knockout-1.2.0.debug.js")

      case xs =>
        doResource(req, res, "/" + xs.mkString("/"))
    }
  }

  private def isInternalPath(url: String): Boolean = {
    internalPaths exists { p => url.startsWith(p) }
  }

  private def doResource(req: HttpServletRequest, res: HttpServletResponse, path: String) {
    val out = res.getOutputStream
    val in = getClass.getClassLoader.getResourceAsStream(path)
    if (in != null) {
      setResponseContentType(req, res)
      val buffer = new Array[Byte](1024)
      var bytesRead = 0
      while (bytesRead != -1) {
        out.write(buffer, 0, bytesRead)
        bytesRead = in.read(buffer)
      }
    } else {
      res.setContentType("text/html")
      res.setStatus(404)
    }
    out.close()
  }

  private def setResponseContentType(req: HttpServletRequest, res: HttpServletResponse) {
    req.getRequestURI.split('.').lastOption map { _ match {
      case "html" => res.setContentType("text/html")
      case "js" => res.setContentType("text/javascript")
      case _ =>
    }}
  }
  
}
