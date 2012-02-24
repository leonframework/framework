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
import io.leon.{AbstractLeonConfiguration, LeonDefaultWebAppGroupingModule}
import java.lang.reflect.Method
import java.io.InputStream
import com.google.inject.{Inject, Injector, Guice}
import io.leon.unitofwork.UOWManager
import io.leon.config.{ConfigMapHolder, ConfigReader}
import javax.servlet._
import io.leon.resourceloading.watcher.ResourceWatcher

class LeonFilter extends GuiceFilter {

  //private val logger = LoggerFactory.getLogger(this.getClass)

  private val classLoader = Thread.currentThread.getContextClassLoader

  private var injector: Injector = _

  @Inject
  private var uowManager: UOWManager = _

  override def init(filterConfig: FilterConfig) {
    StaticServletContextHolder.SERVLET_CONTEXT = filterConfig.getServletContext

    setupConfigMap(filterConfig)

    val moduleName = filterConfig.getInitParameter("module")
    val module =
      if(moduleName.endsWith(".js")) {
        val _moduleName = if (moduleName.startsWith("/")) moduleName else "/" + moduleName
        val viaContext = filterConfig.getServletContext.getResourceAsStream(_moduleName)
        val inputStream = if (viaContext != null) {
          viaContext
        } else {
          classLoader.getResourceAsStream(moduleName)
        }
        loadModuleFromJavaScript(inputStream)
      } else {
        classLoader.loadClass(moduleName).asInstanceOf[Class[AbstractLeonConfiguration]].newInstance()
      }

    injector = Guice.createInjector(new LeonDefaultWebAppGroupingModule, module)
    injector.injectMembers(this)
    super.init(filterConfig)
  }

  override def destroy() {
    injector.getInstance(classOf[ResourceWatcher]).stop()
    super.destroy()
  }

  override def doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, filterChain: FilterChain) {
    uowManager.begin(servletRequest)
    try {
      super.doFilter(servletRequest, servletResponse, filterChain)
    } finally {
      uowManager.commit()
    }
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

  def setupConfigMap(filterConfig: FilterConfig) {
    val configMap = ConfigMapHolder.getInstance().getConfigMap
    val servletConfig = new ConfigReader().readFilterConfig(filterConfig)

    configMap.putAll(servletConfig)
  }

}
