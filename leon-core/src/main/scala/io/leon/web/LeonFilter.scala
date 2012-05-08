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
import java.lang.reflect.Method
import java.io.InputStream
import io.leon.config.{ConfigMapHolder, ConfigReader}
import javax.servlet._
import io.leon.resourceloading.watcher.ResourceWatcher
import io.leon.{DefaultWebAppGroupingModule, LeonAppMainModule}
import com.google.inject._
import org.apache.shiro.guice.web.ShiroWebModule
import scala.collection.JavaConverters._
import org.apache.shiro.guice.aop.ShiroAopModule

class LeonFilter(applicationModule: LeonAppMainModule) extends GuiceFilter {

  //private val logger = LoggerFactory.getLogger(this.getClass)

  private val classLoader = Thread.currentThread.getContextClassLoader

  private var injector: Injector = _

  def this() = this(null)

  private def setupConfigMap(filterConfig: FilterConfig) {
    val configMap = ConfigMapHolder.getInstance().getConfigMap
    val servletConfig = new ConfigReader().readFilterConfig(filterConfig)

    configMap.putAll(servletConfig)
  }

  private def getModule(filterConfig: FilterConfig): LeonAppMainModule = {
    if (applicationModule != null) {
      applicationModule
    } else {
      val moduleName = filterConfig.getInitParameter("module")
      if (moduleName.endsWith(".js")) {
        val _moduleName = if (moduleName.startsWith("/")) moduleName else "/" + moduleName
        val viaContext = filterConfig.getServletContext.getResourceAsStream(_moduleName)
        val inputStream = if (viaContext != null) {
          viaContext
        } else {
          classLoader.getResourceAsStream(moduleName)
        }
        loadModuleFromJavaScript(inputStream)
      } else {
        classLoader.loadClass(moduleName).asInstanceOf[Class[LeonAppMainModule]].newInstance()
      }
    }
  }

  def getInjector: Injector = {
    injector
  }

  override def init(filterConfig: FilterConfig) {
    StaticServletContextHolder.SERVLET_CONTEXT = filterConfig.getServletContext

    val defaultWebModule = new DefaultWebAppGroupingModule
    defaultWebModule.init()
    setupConfigMap(filterConfig)

    val module = getModule(filterConfig)

    // create a new module to ensure the binding ordering
    val app = new AbstractModule {
      def configure() {
        // Shiro
        if (module.isUseLeonShiroIntegration &&
          module.getShiroRealms != null &&
          module.getShiroRealms.size() > 0) {

          install(module.getShiroWebModule(filterConfig.getServletContext))
          install(ShiroWebModule.guiceFilterModule())
          install(new ShiroAopModule)
        }
        install(module)
        install(defaultWebModule)
      }
    }
    injector = Guice.createInjector(app)
    injector.injectMembers(this)
    super.init(filterConfig)
  }

  override def destroy() {
    injector.getInstance(classOf[ResourceWatcher]).stop()
    super.destroy()
  }

  def loadModuleFromJavaScript(file: InputStream): LeonAppMainModule = {
    require(file != null, "JavaScript module file not found!")
    val js = Source.fromInputStream(file).getLines().mkString("\n")
    createAndLoadModuleClass(js)
  }

  def createAndLoadModuleClass(js: String): LeonAppMainModule = {
    //logger.info("loading leon configuration from {}", filename)
    //logger.info("Base directory is {}", baseDir.getAbsolutePath)

    // contains the names of all methods which are relevant for module configuration code.
    val methodNames: Set[String] = {
      import java.lang.reflect.Modifier._

      def getMethods(clazz: Class[_]): Set[Method] = {
        val superclass = clazz.getSuperclass
        if (superclass == null) clazz.getDeclaredMethods.toSet
        else if (superclass == classOf[Object]) clazz.getDeclaredMethods.toSet
        else clazz.getDeclaredMethods.toSet ++ getMethods(clazz.getSuperclass)
      }

      for {
        method <- getMethods(classOf[LeonAppMainModule])
        modifiers = method.getModifiers if isPublic(modifiers) || isProtected(modifiers)
        name = method.getName if !(name contains "$") // ignore special 'scala' methods
      } yield name
    }

    val forwardMethods = methodNames map {
      name =>
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
      new Packages.io.leon.LeonAppMainModule(module);
      """.format(forwardMethods, js)

    // logger.debug("generated js config: {}", jsConfig)

    val ctx = Context.enter()
    val rhinoScope = ctx.initStandardObjects()
    val jsObject = ctx.evaluateString(rhinoScope, jsConfig, "<<Leon module JavaScript file>>", 1, null).asInstanceOf[NativeJavaObject]
    val javaObject = jsObject.unwrap()
    Context.exit()

    javaObject.asInstanceOf[LeonAppMainModule]
  }

}
