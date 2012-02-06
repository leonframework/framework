/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.ajax

import com.google.inject.servlet.ServletModule
import javax.servlet.http.{HttpServlet, HttpServletResponse, HttpServletRequest}
import com.google.inject._
import name.{Named, Names}
import io.leon.web.resources.ExposedUrl
import io.leon.web.browser.VirtualLeonJsFileContribution
import java.lang.StringBuffer
import org.slf4j.LoggerFactory
import java.io.{PrintWriter, BufferedOutputStream}
import com.google.gson.Gson

class AjaxWebModule extends ServletModule {
  override def configureServlets() {
    bind(classOf[AjaxCallServlet]).asEagerSingleton()
    serve("/leon/ajax").`with`(classOf[AjaxCallServlet])
    ExposedUrl.bind(binder(), "/leon/ajax")

    VirtualLeonJsFileContribution.bind(binder(), classOf[AjaxVirtualLeonJsFileContribution])
  }
}

trait AjaxHandler {
  def jsonApply(member: String, args: Seq[String]): String
}

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
      val args = (0 until argsSize) map { x => req.getParameter("arg" + x) }
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

class AjaxVirtualLeonJsFileContribution @Inject()(injector: Injector) extends VirtualLeonJsFileContribution {

  def content() = {
    import scala.collection.JavaConverters._
    val buffer = new StringBuffer()
    val serverObjects = injector.findBindingsByType(new TypeLiteral[AjaxHandler]() {})
    serverObjects.asScala foreach { o =>
      val browserName = o.getKey.getAnnotation.asInstanceOf[Named].value()
      buffer.append(createJavaScriptFunctionDeclaration(browserName))
    }
    buffer.toString
  }

  private def createJavaScriptFunctionDeclaration(name: String): String = {
    """
    leon.utils.createVar("server");
    leon.utils.createVar("server.%s");
    server.%s = function (methodName) {
      return function() {
        // convert arguments to array
        var args = Array.prototype.slice.call(arguments);

        // check if last argument is the callback function
        var callback = args[args.length - 1];
        if (typeof callback === 'function') {
          var params = args.slice(0, args.length - 1);
          leon.call("%s." + methodName, params, callback);
        } else {
          leon.call("%s." + methodName, args, function() {});
        }
      };
    }
    """.format(name, name, name, name)
  }

}
