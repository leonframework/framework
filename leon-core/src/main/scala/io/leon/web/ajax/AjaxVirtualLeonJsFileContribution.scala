/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.ajax

import com.google.inject._
import name.Named
import io.leon.web.browser.VirtualLeonJsFileContribution
import java.lang.StringBuffer
import io.leon.guice.GuiceUtils

class AjaxVirtualLeonJsFileContribution @Inject()(injector: Injector) extends VirtualLeonJsFileContribution {

  def content(params: java.util.Map[String, String]) = {
    import scala.collection.JavaConverters._
    val buffer = new StringBuffer()
    val serverObjects = GuiceUtils.getAllBindingsForType(injector, classOf[AjaxHandler])
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
