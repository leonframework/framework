/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.persistence.mongo

import com.mongodb.MapReduceOutput
import com.mongodb.casbah.MongoCollection

class JavaScriptMapReduceOutput(output: MapReduceOutput) {
  import scala.collection.JavaConverters._
  import MongoUtils._

  def drop() { output.drop() }

  def getOutputCollection = new JavaScriptDBCollection(new MongoCollection(output.getOutputCollection))

  def getCommand = dbObjectToScriptable(output.getCommand)

  def getRaw = dbObjectToScriptable(output.getRaw)

  def results = arrayToNativeArray(output.results.asScala.toArray map { dbObjectToScriptable })

}