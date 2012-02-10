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
import com.google.inject.{Binder, TypeLiteral, Injector, Inject}
import com.google.inject.name.Names
import io.leon.resources.{ResourceUtils, ResourceLoader}
import java.io.{Writer, BufferedWriter}


class VirtualLeonJsFile @Inject()(injector: Injector, loader: ResourceLoader) extends HttpServlet {

  private def writeResource(out: Writer, name: String) {
    loader.getResourceOption(name) foreach { r =>
      out.write(ResourceUtils.inputStreamToString(r.createInputStream()))
    }
  }
  
  private def writeString(out: Writer,  string: String) {
    out.write(string + "\n")
  }

  override def service(req: HttpServletRequest, res: HttpServletResponse) {
    import scala.collection.JavaConverters._
    res.setContentType("text/javascript")
    val out = new BufferedWriter(res.getWriter)

    // static content
    writeString(out, "\"use strict\";")

    req.getParameter("env") match {
      case "desktop" | null => {
        writeResource(out, "/leon/browser/jquery-1.5.2.min.js")
        writeResource(out, "/leon/browser/angular-0.9.19.js")
        writeResource(out, "/leon/browser/leon-browser.js")
        writeResource(out, "/leon/browser/leon-shared.js")
        writeResource(out, "/leon/browser/leon-comet.js")
      }
      case "mobile" => {
        writeResource(out, "/leon/browser/jquery-1.6.2.js")
        writeResource(out, "/leon/browser/angular-0.9.19.js")
        writeResource(out, "/leon/browser/jquery.mobile-1.0b3.js")
        writeResource(out, "/leon/browser/jquery-mobile-angular-adapter-1.0.2.js")
        writeResource(out, "/leon/browser/leon-browser.js")
        writeResource(out, "/leon/browser/leon-shared.js")
        writeResource(out, "/leon/browser/leon-comet.js")
      }
      case _ => {
        sys.error("You can add either '?env=desktop' (default) or '?env=mobile' when loading leon.js.")
      }
    }

    // dynamic content
    val contributions = injector.findBindingsByType(new TypeLiteral[VirtualLeonJsFileContribution]() {})
    contributions.asScala foreach { binding =>
      val content = binding.getProvider.get().content()
      out.write(content)
    }
    out.close()
  }

}
