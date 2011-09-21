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
import com.google.inject.servlet.ServletModule
import io.leon.web.WebUtils
import io.leon.resources.{Resource, ResourceLoader}
import org.slf4j.LoggerFactory
import com.google.inject._
import name.Names

class ResourcesWebModule extends ServletModule {
  override def configureServlets() {
    install(new ResourcesModule)

    bind(classOf[ExposedUrlCheckFilter]).in(Scopes.SINGLETON)
    filter("/*").through(classOf[ExposedUrlCheckFilter])

    serve("/*").`with`(classOf[ResourcesServlet])
  }
}

class ResourcesModule extends AbstractModule {
  def configure() {
    bind(classOf[ResourcesServlet]).asEagerSingleton()
  }
}

class ResourcesServlet @Inject()(resourceLoader: ResourceLoader) extends HttpServlet {

  //private val logger = Logger.getLogger(getClass.getName)

  private val welcomeFiles = List("index.html", "index.xhtml", "index.htm")

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
        setResponseContentType(req, res)
        val stream = resource.createInputStream()
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

object ExposedUrl {
  def bind(binder: Binder, url: String) {
    binder.bind(classOf[ExposedUrl]).annotatedWith(Names.named(url)).toInstance(new ExposedUrl(url))
  }
}

class ExposedUrl(val urlRegex: String)

class ExposedUrlCheckFilter @Inject()(injector: Injector) extends Filter {

  import scala.collection.JavaConverters._

  private val logger = LoggerFactory.getLogger(getClass.getName)

  private def exposedUrls = injector.findBindingsByType(new TypeLiteral[ExposedUrl]() {}).asScala

  private def exposedUrlsRegex = exposedUrls map { _.getProvider.get().urlRegex.r }

  def init(config: FilterConfig) {}

  def destroy() {}

  def doFilter(_req: ServletRequest, _res: ServletResponse, chain: FilterChain) {
    val req = _req.asInstanceOf[HttpServletRequest]
    val res = _res.asInstanceOf[HttpServletResponse]

    val requestUrl = WebUtils.getRequestedResource(req)
    val isPublic = exposedUrlsRegex exists { _.findFirstIn(requestUrl).isDefined }
    if (isPublic) {
      logger.debug("Requested exposed URL {}", requestUrl)
      chain.doFilter(_req, _res)
    } else {
      logger.debug("Requested *private* URL {}", requestUrl)
      res.setStatus(403)
    }
  }

}

