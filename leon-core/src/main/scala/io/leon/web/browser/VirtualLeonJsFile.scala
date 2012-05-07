/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.browser

import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import com.google.inject.{Injector, Inject}
import io.leon.utils.{ResourceUtils, GuiceUtils}
import org.slf4j.LoggerFactory
import java.io.{EOFException, Writer, BufferedWriter}
import io.leon.resourceloading.ResourceLoader
import scala.collection.JavaConverters._

class VirtualLeonJsFile @Inject()(injector: Injector, loader: ResourceLoader) extends HttpServlet {

  private val logger = LoggerFactory.getLogger(getClass)

  private val contributions = GuiceUtils.getByType(injector, classOf[VirtualLeonJsFileContribution]).asScala map { c =>
    c.getProvider.get()
  }

  private def writeResource(out: Writer, name: String) {
    loader.getResourceOption(name) foreach { r =>
      out.write(ResourceUtils.inputStreamToString(r.getInputStream()))
    }
  }

  private def writeString(out: Writer,  string: String) {
    out.write(string + "\n")
  }

  override def service(req: HttpServletRequest, res: HttpServletResponse) {
    res.setContentType("text/javascript")
    val out = new BufferedWriter(res.getWriter)

    // static content
    writeString(out, "\"use strict\";")

    req.getParameter("env") match {
      case "desktop" | null => {
        writeResource(out, "/leon/browser/jquery-1.6.2.js")
        writeResource(out, "/leon/browser/leon-browser.js")
        writeResource(out, "/leon/browser/leon-shared.js")
        writeResource(out, "/leon/browser/leon-comet.js")
      }
      case "mobile" => {
        //writeResource(out, "/leon/browser/jquery-1.6.2.js")
        //writeResource(out, "/leon/browser/jquery.mobile-1.0b3.js")
        //writeResource(out, "/leon/browser/jquery-mobile-angular-adapter-1.0.2.js")
        //writeResource(out, "/leon/browser/leon-browser.js")
        //writeResource(out, "/leon/browser/leon-shared.js")
        //writeResource(out, "/leon/browser/leon-comet.js")
      }
      case _ => {
        sys.error("You can add either '?env=desktop' (default) or '?env=mobile' when loading leon.js.")
      }
    }

    // Convert request map from "String->Array[String]" to "String->String" by only
    // using the first value in the array
    val requestMap = (req.getParameterMap.asScala map { e =>
      e._1.asInstanceOf[String] -> e._2.asInstanceOf[Array[String]](0)
    }).asJava

    // dynamic content
    contributions foreach { c =>
      try {
        val content = c.content(requestMap)
        out.write(content + "\n")
      } catch {
        case e: RuntimeException if e.getCause.isInstanceOf[EOFException] => {
          logger.debug("VirtualLeonJsFileContribution threw error.", e)
        }
        case e: Throwable => logger.warn("VirtualLeonJsFileContribution threw error.", e)
      }
    }
    out.close()
  }

}
