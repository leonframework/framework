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
import scala.io.Source
import org.mozilla.javascript.{NativeJavaObject, Context}
import io.leon.{AbstractLeonConfiguration, LeonModule}
import java.lang.reflect.Method
import io.leon.resources.ResourceWatcher
import org.slf4j.LoggerFactory
import java.io.InputStream
import javax.servlet.{FilterChain, ServletResponse, ServletRequest, FilterConfig}
import com.google.inject.{Inject, Injector, Guice}
import io.leon.unitofwork.UOWManager


class LeonFilter extends GuiceFilter {

  private val logger = LoggerFactory.getLogger(this.getClass)

  private val classLoader = Thread.currentThread.getContextClassLoader

  private var injector: Injector = _

  @Inject
  private var uowManager: UOWManager = _

  override def init(filterConfig: FilterConfig) {
    val moduleName = filterConfig.getInitParameter("module")

    val module =
      if(moduleName.endsWith(".js"))
        loadModuleFromJavaScript(classLoader.getResourceAsStream(moduleName))
      else
        classLoader.loadClass(moduleName).asInstanceOf[Class[AbstractLeonConfiguration]].newInstance()

    injector = Guice.createInjector(new LeonModule, module)
    injector.injectMembers(this)
    super.init(filterConfig)
  }


  override def doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, filterChain: FilterChain) {
    uowManager.begin()
    super.doFilter(servletRequest, servletResponse, filterChain)
    uowManager.commit()
  }

  override def destroy() {
    Option(injector) foreach { _.getInstance(classOf[ResourceWatcher]).stop() }
    super.destroy()
  }

  def loadModuleFromJavaScript(file: InputStream): AbstractLeonConfiguration = {
    require(file != null, "JavaScript module file not found!")
    val js = Source.fromInputStream(file).getLines().mkString("\n")
    createAndLoadModuleClass(js)
  }

  def createAndLoadModuleClass(js: String): AbstractLeonConfiguration = {
    //logger.info("loading leon configuration from {}", filename)
    //logger.info("Base directory is {}", baseDir.getAbsolutePath)

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

    // logger.debug("generated js config: {}", jsConfig)

    val ctx = Context.enter()
    val rhinoScope = ctx.initStandardObjects()
    val jsObject = ctx.evaluateString(rhinoScope, jsConfig, "<<Leon module JavaScript file>>", 1, null).asInstanceOf[NativeJavaObject]
    val javaObject = jsObject.unwrap()
    Context.exit()

    javaObject.asInstanceOf[AbstractLeonConfiguration]
  }

}
