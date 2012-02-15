/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.persistence.mongo

import com.mongodb.CommandResult
import org.mozilla.javascript.{Undefined, ScriptableObject}


private[mongo] class JavaScriptCommandResult(result: CommandResult) extends ScriptableObject {
  import scala.collection.JavaConverters._

  private val jsFunctionNames = Array("ok", "getErrorMessage", "getException", "throwOnError")

  result.toMap.asScala foreach { case (k: String, v) =>
    v match {
      case value: AnyRef => defineProperty(k, MongoUtils.javaToJs(value), ScriptableObject.PERMANENT)
      case null => defineProperty(k, Undefined.instance, ScriptableObject.PERMANENT)
    }
  }

  defineFunctionProperties(jsFunctionNames, getClass, ScriptableObject.PERMANENT)

  def getClassName = "CommandResult"

  def ok = result.ok()

  def getErrorMessage = result.getErrorMessage

  def getException = result.getException

  def throwOnError() { result.throwOnError() }

  override def getDefaultValue(typeHint: Class[_]) = "undefined"
}