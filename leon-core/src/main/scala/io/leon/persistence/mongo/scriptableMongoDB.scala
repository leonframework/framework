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
import com.mongodb.{DB, BasicDBObject}


class ScriptableMongoDB @Inject()(db: DB, engine: LeonScriptEngine) extends ScriptableObject {
  import MongoUtils._

  private val jsFunctionNames = Array(
    "getCollectionNames",
    "collectionExists",
    "authenticate",
    "getStats",
    "runCommand")

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

  def getLastError = new JavaScriptCommandResult(db.getLastError())

  def runCommand(cmd: ScriptableObject): ScriptableObject = {
    val dbo = scriptableToDbObject(cmd)
    val result = db.command(dbo, 0)

    new JavaScriptCommandResult(result)
  }

}
