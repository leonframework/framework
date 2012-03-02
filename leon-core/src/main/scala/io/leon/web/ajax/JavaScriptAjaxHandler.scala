/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.ajax

import com.google.inject.Inject
import com.google.gson.{JsonParser, Gson}
import io.leon.javascript.LeonScriptEngine
import org.slf4j.LoggerFactory

class JavaScriptAjaxHandler(targetObjectName: String) extends AjaxHandler {

  import scala.collection.JavaConverters._

  private val logger = LoggerFactory.getLogger(getClass.getName)

  @Inject
  var engine: LeonScriptEngine = _

  @Inject
  var gson: Gson = _

  def jsonApply(member: String, args: Seq[String]) = {
    logger.debug(
      "Invoking JavaScript ajax handler [{}.{}] with arguments {}.",
      Array(targetObjectName, member, args.asJava))

    val jp = new JsonParser
    val parsed = args map jp.parse
    val argsString = parsed.mkString(",")
    val call = targetObjectName + "." + member + "(" + argsString + ")"

    val result = engine.eval(call)
    gson.toJson(result)
  }

}

