/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.persistence.mongo

import io.leon.javascript.LeonScriptEngine
import com.google.inject.Inject
import org.mozilla.javascript._
import com.mongodb.{WriteConcern, DB, BasicDBObject}


class ScriptableMongoDB @Inject()(db: DB, engine: LeonScriptEngine) extends ScriptableObject {
  import MongoUtils._

  private val jsFunctionNames = Array(
    "getCollectionNames",
    "collectionExists",
    "authenticate",
    "getStats",
    "runCommand",
    "getLastError",
    "setWriteConcern")

  defineFunctionProperties(jsFunctionNames, getClass, ScriptableObject.READONLY)

  def getClassName = getClass.getName

  override def get(name: String, start: Scriptable): AnyRef = {
    if (jsFunctionNames.contains(name)) {
      super.get(name, start)
    } else {
      new JavaScriptDBCollection(db.getCollection(name))
    }
  }

  def getCollectionNames = arrayToNativeArray(db.getCollectionNames().toArray)

  def collectionExists(name: String) = db.collectionExists(name)

  def authenticate(username: String,  password: String) = db.authenticate(username, password.toArray)

  def getStats = runCommand(new BasicDBObject("dbstats", true))

  def getLastError = Option(db.getLastError()) map { err => new JavaScriptCommandResult(err) } getOrElse null

  def runCommand(cmd: ScriptableObject): ScriptableObject = {
    val dbo = scriptableToDbObject(cmd)
    val result = db.command(dbo, 0)

    new JavaScriptCommandResult(result)
  }
  
  def setWriteConcern(concern: ScriptableObject) {
    def toInt(any: Any): Int = any match {
      case i:Int => i
      case d:Double => d.toInt
      case s: String => s.toInt
    }

    if(concern == null) {
      db.setWriteConcern(WriteConcern.NORMAL)
    } else {
      val w = Option(concern.get("w")).map(toInt) getOrElse 0
      val wtimeout = Option(concern.get("wtimeout")).map(toInt) getOrElse 0
      val fsync = Option(concern.get("fsync").asInstanceOf[Boolean]) getOrElse false
      val j = Option(concern.get("j").asInstanceOf[Boolean]) getOrElse false

      db.setWriteConcern(new WriteConcern(w, wtimeout, fsync, j))
    }
  }
}
