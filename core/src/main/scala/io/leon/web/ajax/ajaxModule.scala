/*
 * Copyright (c) 2010 WeigleWilczek and others.

 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.ajax

import io.leon.javascript.JavaScriptObject
import com.google.inject.name.Names
import java.io.BufferedOutputStream
import com.google.inject.{AbstractModule, Key, Injector, Inject}
import com.google.inject.servlet.ServletModule
import javax.servlet.http.{HttpServlet, HttpServletResponse, HttpServletRequest}

class AjaxWebModule extends ServletModule {
  override def configureServlets() {
    install(new AjaxModule)
    serve("/leon/fc").`with`(classOf[AjaxProcessor])
  }
}

class AjaxModule extends AbstractModule {
  def configure() {
    bind(classOf[AjaxProcessor]).asEagerSingleton()
  }
}

class AjaxProcessor @Inject()(injector: Injector) extends HttpServlet {

  override def service(req: HttpServletRequest, res: HttpServletResponse) {
    val target = req.getParameter("target")
    val args = req.getParameter("args")

    val obj :: members = target.split('.').toList
    val jsObj = injector.getInstance(Key.get(classOf[JavaScriptObject], Names.named(obj)))
    val result = jsObj.jsonApply(members, args)

    val out = new BufferedOutputStream(res.getOutputStream)
    out.write(result.getBytes)
    out.close()
  }

}
