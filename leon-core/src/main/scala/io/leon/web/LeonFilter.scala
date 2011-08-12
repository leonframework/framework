/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web

import com.google.inject.Guice
import com.google.inject.servlet.GuiceFilter
import javax.servlet.FilterConfig
import io.leon.{LeonModule, AbstractLeonConfiguration}
import java.io.File
import scala.io.Source
import org.mozilla.javascript.{NativeJavaObject, Context}


class LeonFilter extends GuiceFilter {

  private val classLoader = Thread.currentThread.getContextClassLoader

  override def init(filterConfig: FilterConfig) {
    val moduleName = filterConfig.getInitParameter("module")

    val module =
      if(moduleName.endsWith(".js")) loadModuleFromJavaScript(moduleName)
      else classLoader.loadClass(moduleName).asInstanceOf[Class[AbstractLeonConfiguration]].newInstance()

    Guice.createInjector(new LeonModule, module)
    
    super.init(filterConfig)
  }

  private def loadModuleFromJavaScript(filename: String): AbstractLeonConfiguration = {

    val configBody = {
      val _configFile = new File(filename).getAbsoluteFile
      val _src = Source.fromFile(_configFile)
      val _lines =
        for {
          line <- _src.getLines()
          trimmedLine = line.trim
        } yield trimmedLine match {
          case "" => trimmedLine
          case s if s.startsWith("//") => s
          // TODO: support multi line comments
          case s => "this." + s
        }

      _lines mkString "\n"
    }

    val jsConfig =
      """
      var module = {
          config: function() {
             %s
          }
      };
      new Packages.io.leon.AbstractLeonConfiguration(module);
      """.format(configBody)

    // println(jsConfig)

    val ctx = Context.enter()
    val rhinoScope = ctx.initStandardObjects()
    val jsObject = ctx.evaluateString(rhinoScope, jsConfig, filename, 1, null).asInstanceOf[NativeJavaObject]
    val javaObject = jsObject.unwrap()
    Context.exit()

    javaObject.asInstanceOf[AbstractLeonConfiguration]
  }

}
