/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.browser

import com.google.inject.{Inject, Provider}
import io.leon.web.htmltagsprocessor.LeonTagRewriter
import javax.servlet.http.HttpServletRequest
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


  def process(referrer: String, in: Source, out: OutputDocument) {
    import scala.collection.JavaConverters._
    val contextPath = requestProvider.get.getContextPath

    for ((tagName, attributeName) <- affectedTags) {
      for (tag <- in.getAllStartTags(tagName).asScala) {
        val attributeValue = tag.getAttributeValue(attributeName)
        if (attributeValue != null && attributeValue.startsWith("/")) {
          val attributeMap = new java.util.LinkedHashMap[String, String]()
          tag.getAttributes.populateMap(attributeMap, false)
          attributeMap.put(attributeName, contextPath + attributeValue)
          out.replace(tag.getAttributes, attributeMap)
        }
      }
    }
  }

}
