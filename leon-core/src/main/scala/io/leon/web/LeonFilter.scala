/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web

import com.google.inject.servlet.GuiceFilter
import javax.servlet.FilterConfig
import java.io.File
import scala.io.Source
import org.mozilla.javascript.{NativeJavaObject, Context}
import io.leon.{AbstractLeonConfiguration, LeonModule}
import java.lang.reflect.Method
import com.google.inject.{Injector, Guice}
import io.leon.resources.ResourceWatcher


class LeonFilter extends GuiceFilter {

  private var injector: Injector = _
  private val classLoader = Thread.currentThread.getContextClassLoader

  override def init(filterConfig: FilterConfig) {
    val moduleName = filterConfig.getInitParameter("module")

    val module =
      if(moduleName.endsWith(".js")) loadModuleFromJavaScript(new File(moduleName))
      else classLoader.loadClass(moduleName).asInstanceOf[Class[AbstractLeonConfiguration]].newInstance()

    injector = Guice.createInjector(new LeonModule, module)
    
    super.init(filterConfig)
  }

  override def destroy() {
    injector.getInstance(classOf[ResourceWatcher]).stop()
    super.destroy()
  }

  def loadModuleFromJavaScript(file: File): AbstractLeonConfiguration = {
    val js = Source.fromFile(file.getAbsoluteFile).getLines mkString "\n"

    loadModuleFromJavaScript(file.getName, js)
  }

  def loadModuleFromJavaScript(filename: String, js: String): AbstractLeonConfiguration = {

    // contains the names of all methods which are relevant for module configuration code.
    val methodNames: Set[String] = {
      import java.lang.reflect.Modifier._

      def getMethods(clazz: Class[_]): Set[Method] = {
        val superclass = clazz.getSuperclass
        if(superclass == null) clazz.getDeclaredMethods.toSet
        else if (superclass == classOf[Object]) clazz.getDeclaredMethods.toSet
        else clazz.getDeclaredMethods.toSet ++ getMethods(clazz.getSuperclass)
      }

      for {
        method <- getMethods(classOf[AbstractLeonConfiguration])
        modifiers = method.getModifiers if isPublic(modifiers) || isProtected(modifiers)
        name = method.getName if !(name contains "$") // ignore special 'scala' methods
      } yield name
    }

    val forwardMethods = methodNames map { name =>
      "var %s = function() { return self.%s.apply(self, arguments); }".format(name, name)
    } mkString "\n"

    val jsConfig =
      """
      var module = {
          config: function() {
             var self = this;
             %s

             %s
          }
      };
      new Packages.io.leon.AbstractLeonConfiguration(module);
      """.format(forwardMethods, js)

//    println(jsConfig)

    val ctx = Context.enter()
    val rhinoScope = ctx.initStandardObjects()
    val jsObject = ctx.evaluateString(rhinoScope, jsConfig, filename, 1, null).asInstanceOf[NativeJavaObject]
    val javaObject = jsObject.unwrap()
    Context.exit()

    javaObject.asInstanceOf[AbstractLeonConfiguration]
  }

}
