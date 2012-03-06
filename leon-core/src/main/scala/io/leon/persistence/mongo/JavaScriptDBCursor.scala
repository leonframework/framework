/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.persistence.mongo

import org.mozilla.javascript.{Scriptable, ScriptableObject, Context, Function => RhinoFunction }
import com.mongodb.{DBObject, DBCursor}

private[mongo] class JavaScriptDBCursor(dbCursor: DBCursor) extends ScriptableObject {
  import MongoUtils._

  private val jsFunctionNames = Array(
    "toArray", "next", "hasNext", "skip", "limit", "sort",
    "size", "length", "count", "close", "forEach", "map")

  private lazy val list = {
    import scala.collection.JavaConverters._
    dbCursor.toArray.asScala map toScriptableMap
  }

  defineFunctionProperties(jsFunctionNames, getClass, ScriptableObject.READONLY)

  def getClassName = getClass.getName

  override def get(index: Int, start: Scriptable) = {
    // Attention: list is empty when next/ hasNext was called first
    list(index)
  }

  def hasNext() = {
    dbCursor.hasNext
  }

  def next() = {
    val dbo = dbCursor.next()
    toScriptableMap(dbo)
  }

  def skip(n: Int) = {
    val newCursor = dbCursor.skip(n)
    new JavaScriptDBCursor(newCursor)
  }

  def limit(n: Int) = {
    val newCursor = dbCursor.limit(n)
    new JavaScriptDBCursor(newCursor)
  }

  def sort(orderBy: ScriptableObject) = {
    new JavaScriptDBCursor(dbCursor.sort(orderBy))
  }

  override def size() = {
    dbCursor.size
  }

  def length() = size()

  def count() = {
    dbCursor.count
  }

  def close() {
    dbCursor.close();
  }

  def toArray() = {
    arrayToNativeArray(list.toArray)
  }

  def forEach(func: RhinoFunction) {
    val ctx = Context.enter()
    list foreach { obj =>
      func.call(ctx, this, this, Array(obj))
    }
    Context.exit()
  }

  def map(func: RhinoFunction) = {
    val ctx = Context.enter()
    val result =
      list map { obj =>
        func.call(ctx, this, this, Array(obj))
      }
    Context.exit();

    arrayToNativeArray(result.toArray)
  }
}
