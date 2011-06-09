package io.leon.web

import javax.servlet.http.HttpServletRequest

object WebUtils {

  def getRequestUrl(req: HttpServletRequest): String = {
    val contextPath = req.getContextPath
    val requestUri = req.getRequestURI
    requestUri.substring(contextPath.size)
  }

}
