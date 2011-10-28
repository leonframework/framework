/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.html

import net.htmlparser.jericho.Source
import com.google.inject.{AbstractModule, Inject, Provider}
import io.leon.resources.htmltagsprocessor.{LeonTagRewriters, LeonTagRewriter}
import javax.servlet.http.HttpServletRequest

class HtmlContextPathRewriter @Inject()(requestProvider: Provider[HttpServletRequest]) extends LeonTagRewriter {

  private val affectedTags = List(
    "a" -> "href",
    "script" -> "src",
    "object" -> "src",
    "img" -> "src",
    "link" -> "href",
    "area" -> "href",
    "base" -> "href",
    "input" -> "src")

  def process(doc: Source) = {
    import scala.collection.JavaConverters._

    val contextPath = requestProvider.get.getContextPath

    val result =
      for {
        (tagName, attributeName) <- affectedTags
      } yield {
        doc.getAllStartTags(tagName).asScala flatMap { tag =>

        Option(tag.getAttributeValue(attributeName)) flatMap { link =>
          if(link.startsWith("/")) {

            val attrs = tag.getAttributes.asScala filterNot { _.getName == attributeName }
            val attrOut = attrs map { a => a.getName + "=\"" + a.getValue +  "\"" } mkString " "

            val newTag = "<%s %s=%s%s %s>".format(tag.getName, attributeName, contextPath, link, attrOut)
            println("newTag: " + newTag + ", link = " + link)
            Some(tag -> newTag)
          } else None
        }
      }
    }

    result.flatten
  }

}

class HtmlContextPathRewriterModule extends AbstractModule {
  override def configure() {
    LeonTagRewriters.bind(binder(), classOf[HtmlContextPathRewriter])
  }
}
