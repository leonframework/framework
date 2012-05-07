/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.resourceloading

import java.util.{Collections, LinkedList}
import org.slf4j.LoggerFactory


class ResourceLoadingStack {

  private val logger = LoggerFactory.getLogger(getClass.getName)

  private val resourceLoadingStack = new ThreadLocal[LinkedList[String]] {
    override def initialValue() = {
      new LinkedList[String]()
    }
  }

  /**
   * @return a view (copy) of the current resource-loading stack
   */
  def getResourceLoadingStack(): java.util.List[String] = {
    Collections.unmodifiableList(resourceLoadingStack.get())
  }

  /**
   * @param fileName the file name to be pushed onto this stack
   */
  def pushResourceOnStack(fileName: String) {
    logger.trace("Pushing resource onto the resource-loading stack: [{}]", fileName)
    resourceLoadingStack.get().add(0, fileName)
  }

  /**
   * @return The object at the top of this stack.
   *
   * @throws IndexOutOfBoundsException if this stack is empty.
   */
  def popResourceFromStack() {
    val removed = resourceLoadingStack.get().remove(0)
    logger.trace("Removing the last resource from the resource-loading stack: [{}]", removed)
  }

}
