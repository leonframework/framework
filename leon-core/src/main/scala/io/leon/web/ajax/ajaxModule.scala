/*
 * Copyright (c) 2010 WeigleWilczek and others.

 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.ajax

import com.google.inject.servlet.ServletModule
import javax.servlet.http.{HttpServlet, HttpServletResponse, HttpServletRequest}
import com.google.inject._
import collection.JavaConversions
import name.{Named, Names}
import java.io.{BufferedWriter, BufferedOutputStream}

class AjaxWebModule extends ServletModule {
  override def configureServlets() {
    install(new AjaxModule)
    serve("/leon/ajax").`with`(classOf[AjaxProcessor])
    serve("/leon/browser.js").`with`(classOf[BrowserJsServlet])
  }
}

class AjaxModule extends AbstractModule {
  def configure() {
    bind(classOf[AjaxProcessor]).asEagerSingleton()
    bind(classOf[BrowserJsServlet]).asEagerSingleton()
  }
}

trait AjaxHandler {
  def jsonApply(members: List[String], args: String): String
}

class AjaxProcessor @Inject()(injector: Injector) extends HttpServlet {

  override def service(req: HttpServletRequest, res: HttpServletResponse) {
    val target = req.getParameter("target")
    val args = req.getParameter("args")

    val obj :: members = target.split('.').toList
    val handler = injector.getInstance(Key.get(classOf[AjaxHandler], Names.named(obj)))
    val result = handler.jsonApply(members, args)

    res.setContentType("application/json")
    val out = new BufferedOutputStream(res.getOutputStream)
    out.write(result.getBytes)
    out.close()
  }

}

class BrowserJsServlet @Inject()(injector: Injector) extends HttpServlet {

  override def service(req: HttpServletRequest, res: HttpServletResponse) {
    res.setContentType("text/javascript")
    val out = new BufferedWriter(res.getWriter)

    val serverObjects = injector.findBindingsByType(new TypeLiteral[AjaxHandler]() {})
    JavaConversions.asScalaBuffer(serverObjects) foreach { o =>
      val browserName = o.getKey.getAnnotation.asInstanceOf[Named].value()
      out.write(createJavaScriptFunctionDeclaration(browserName))
    }
    out.close()
  }

  private def createJavaScriptFunctionDeclaration(name: String): String = {
    // TODO support '.' in names
    """
    var %s = function (methodName) {
      return function() {
        var argLength = arguments.length - 1;
        var args = Array(argLength);
        for (var i = 0; i < argLength; i++) {
          args[i] = arguments[i];
        }
        var callback = arguments[arguments.length - 1];

        leon.call("%s." + methodName, args, callback);
      };
    }
    """.format(name, name)
  }

}
