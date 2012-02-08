/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.resources

import com.google.inject.{TypeLiteral, Injector, Inject}
import io.leon.web.WebUtils
import javax.servlet._
import http.{HttpServletResponse, HttpServletRequest}
import org.slf4j.LoggerFactory

case class ExposedUrl(val urlRegex: String)

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

