/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.persistence.mongo

import com.google.inject.Inject
import com.mongodb.casbah.MongoDB
import org.mozilla.javascript._

class ScriptableMongoDB @Inject()(mongo: MongoDB) extends ScriptableObject {

  def getClassName = getClass.getName

  override def get(name: String, start: Scriptable): AnyRef = {
    new JavaScriptDBCollection(mongo(name))
  }
}
