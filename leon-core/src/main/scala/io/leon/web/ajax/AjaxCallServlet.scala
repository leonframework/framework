/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.ajax

import com.google.gson.Gson
import org.slf4j.LoggerFactory
import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import java.io.BufferedOutputStream
import com.google.inject.{Key, Injector, Inject}
import com.google.inject.name.Names

class AjaxCallServlet @Inject()(injector: Injector, gson: Gson) extends HttpServlet {

  private val logger = LoggerFactory.getLogger(getClass)

  override def service(req: HttpServletRequest, res: HttpServletResponse) {
    res.setStatus(200)
    res.setContentType("application/json")
    res.setCharacterEncoding("utf-8")

    val out = new BufferedOutputStream(res.getOutputStream)
    val targetName = req.getParameter("target")
    try {
      val argsSize = req.getParameter("argsSize").toInt
      val args = (0 until argsSize) map {
        x => req.getParameter("arg" + x)
      }
      val (obj, member) = targetName.splitAt(targetName.lastIndexOf('.'))

      val handler = injector.getInstance(Key.get(classOf[AjaxHandler], Names.named(obj)))
      val result = handler.jsonApply(member.substring(1), args)


      out.write(result.getBytes("utf-8"))
      out.close()
    } catch {
      case e: Exception => {
        logger.warn("Error while handling AJAX request. Target: " + targetName)

        val errorResult = new java.util.HashMap[String, Any]()
        errorResult.put("leonAjaxError", true)
        errorResult.put("errorClass", e.getCause.getClass.getName)
        errorResult.put("errorMessage", e.getMessage)
        errorResult.put("errorStackTrace", e.getStackTrace)
        val errorString = gson.toJson(errorResult)
        out.write(errorString.getBytes("utf-8"))
        out.close()
      }
    }
  }

}
