/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.htmltagsprocessor

import net.htmlparser.jericho.{OutputDocument, Source}


trait LeonTagRewriter {

  def process(doc: Source): Seq[OutputDocument => Unit]

}