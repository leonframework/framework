/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.samples.mixed

import reflect.BeanInfo

@BeanInfo
case class Person(firstName: String, lastName: String, address: Address) {
  private def this() = this(null, null, null)
}

@BeanInfo
case class Address(zipcode: String, city: String) {
  private def this() = this(null, null)
}

class PersonService {

  def doSomething(person: Person): Person = {
    println("doSomething got: " + person)

    person.copy(lastName = person.lastName * 2)
  }

}