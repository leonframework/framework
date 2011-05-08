/*
 * Copyright (c) 2010 WeigleWilczek and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package com.ww.sjs

import javax.servlet._
import http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import java.io._
import person.Config
import com.google.inject.{Guice, Injector}

class SJSServlet extends HttpServlet {

  private val classLoader = Thread.currentThread.getContextClassLoader

  private val FILE_SEP = File.separator

  private var config: Config = _

  private var injector: Injector = _

  override def init(servletConfig: ServletConfig) {
    super.init(servletConfig)
    val moduleName = servletConfig.getInitParameter("module")
    val moduleClass = classLoader.loadClass(moduleName)
    config = moduleClass.newInstance.asInstanceOf[Config]
    injector = Guice.createInjector(config)
  }

  override def service(req: HttpServletRequest, res: HttpServletResponse) {
    val url = req.getRequestURI.split('/').toList.dropWhile(_ == "")

    url match {
      case "sjs" :: "_sjs" :: "jquery.js" :: Nil =>
        doResource(req, res, "internal" + FILE_SEP + "browser" + FILE_SEP + "jquery-1.5.1.min.js")

      case "sjs" :: "_sjs" :: "jquery.atmosphere.js" :: Nil =>
        doResource(req, res, "internal" + FILE_SEP + "browser" + FILE_SEP + "jquery.atmosphere.js")

      case "sjs" :: "_sjs" :: "knockout.js" :: Nil =>
        doResource(req, res, "internal" + FILE_SEP + "browser" + FILE_SEP + "knockout-1.2.0.debug.js")

      case "sjs" :: "_sjs" :: "sjs.js" :: Nil =>
        doResource(req, res, "internal" + FILE_SEP + "browser" + FILE_SEP + "sjs.js")

      case "sjs" :: "_sjs" :: "form2object.js" :: Nil =>
        doResource(req, res, "internal" + FILE_SEP + "browser" + FILE_SEP + "form2object.js")

      case "sjs" :: "_sjs" :: "module.js" :: Nil =>
        doString(req, res, config.getJavaScriptInclude)



      case "sjs" :: "_sjs" :: "fc" :: Nil =>
        doFunctionCall(req, res)

      case "sjs" :: "_sjs" :: "queue" :: Nil =>
        doQueue(req, res)

      case Nil =>
        doString(req, res, "root index")

      case "sjs" :: xs =>
        doResource(req, res, "public" + FILE_SEP + xs.mkString(FILE_SEP))
    }
  }

  private def doResource(req: HttpServletRequest, res: HttpServletResponse, path: String) {
    val in = getClass.getClassLoader.getResourceAsStream(path)
    val out = res.getOutputStream
    val buffer = new Array[Byte](1024)
    var bytesRead = 0
    while (bytesRead != -1) {
      out.write(buffer, 0, bytesRead)
      bytesRead = in.read(buffer)
    }
    out.close()
  }
  
  private def doFunctionCall(req: HttpServletRequest, res: HttpServletResponse) {
    val fnName = req.getParameter("fnName")
    val args = req.getParameter("args")

    val fn = config.getJavaScriptFunction(fnName)
    val result = fn(args)
    val out = new BufferedOutputStream(res.getOutputStream)
    out.write(result.getBytes)
    out.close()
  }

  private def doQueue(req: HttpServletRequest, res: HttpServletResponse) {
    
  }

  private def doString(req: HttpServletRequest, res: HttpServletResponse, string: String) {
    val out = new PrintWriter(new BufferedOutputStream(res.getOutputStream))
    out.write(string)
    out.close()
  }

}
