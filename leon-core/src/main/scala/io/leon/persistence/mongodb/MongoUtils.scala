/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.persistence.mongodb

import java.util.logging.Logger
import com.mongodb._

import collection._
import scala.collection.JavaConverters
import javax.management.remote.rmi._RMIConnection_Stub

private[mongodb] object MongoUtils {

  def mapToDbObject(map: Map[_,_]): DBObject = {
    import JavaConverters._
    new BasicDBObject(map.asJava)
  }

  def dbObjectToMap(dbObject: DBObject): mutable.Map[_,_] = {
    import JavaConverters._
    dbObject.toMap.asScala
  }
}