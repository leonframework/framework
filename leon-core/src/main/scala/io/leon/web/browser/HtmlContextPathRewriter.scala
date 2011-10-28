/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.browser

import com.google.inject.{AbstractModule, Inject, Provider}
import io.leon.resources.htmltagsprocessor.{LeonTagRewriters, LeonTagRewriter}
import javax.servlet.http.HttpServletRequest
import java.util.HashMap
import net.htmlparser.jericho.{OutputDocument, Source}

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
            val attributeMap = new java.util.TreeMap[String, String]()
            tag.getAttributes.populateMap(attributeMap, false)

            attributeMap.put(attributeName, contextPath + link)

            Some((out: OutputDocument) => out.replace(tag.getAttributes, attributeMap))

          } else None
        }
      }
    }

    result.flatten
  }

}
