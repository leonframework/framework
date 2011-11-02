/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resources

import java.io.{ByteArrayInputStream, InputStreamReader, BufferedReader, InputStream}

object ResourceUtils {

  def inputStreamToString(stream: InputStream): String = {
      val br = new BufferedReader(new InputStreamReader(stream))
    val sb = new StringBuilder
    var line: String = br.readLine()

    while (line != null) {
      sb.append(line + "\n")
      line = br.readLine()
    }

    br.close()
    sb.toString()
  }

  def stringToInputStream(string: String): InputStream = {
    new ByteArrayInputStream(string.getBytes)
  }

}
