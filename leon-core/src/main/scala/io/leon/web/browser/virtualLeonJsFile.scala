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
import java.io.BufferedWriter
import com.google.inject.{Binder, TypeLiteral, Injector, Inject}
import com.google.inject.name.Names

object VirtualLeonJsFileContribution {
  def bind(binder: Binder, contribution: Class[_ <: VirtualLeonJsFileContribution]) {
    binder.bind(contribution).asEagerSingleton()
    binder.bind(classOf[VirtualLeonJsFileContribution])
      .annotatedWith(Names.named(contribution.getClass.getName))
      .to(contribution)
  }
}

trait VirtualLeonJsFileContribution {
  def content(): String
}

class VirtualLeonJsFile @Inject()(injector: Injector) extends HttpServlet {

  println("äääääääääääääääää")
  println("äääääääääääääääää")
  println("äääääääääääääääää")
  println("äääääääääääääääää")
  println("äääääääääääääääää")
  println("äääääääääääääääää")
  println("äääääääääääääääää")

  override def service(req: HttpServletRequest, res: HttpServletResponse) {
    import scala.collection.JavaConverters._

    println("ÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖ")
    println("ÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖ")
    println("ÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖÖ")

    res.setContentType("text/javascript")
    val out = new BufferedWriter(res.getWriter)

    val contributions = injector.findBindingsByType(new TypeLiteral[VirtualLeonJsFileContribution]() {})
    contributions.asScala foreach { binding =>
      val content = binding.getProvider.get().content()
      out.write(content)
    }
    out.close()
  }

}
