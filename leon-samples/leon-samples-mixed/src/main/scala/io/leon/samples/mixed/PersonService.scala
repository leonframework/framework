package io.leon.samples.mixed

/*
 * Copyright (c) 2011 WeigleWilczek and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
import reflect.BeanInfo
import com.google.inject.Inject
import com.mongodb.casbah.MongoDB
import com.mongodb.casbah.commons.MongoDBObject
import io.leon.web.comet.CometRegistry

@BeanInfo
case class Person(firstName: String, lastName: String, address: Address, hobbies: Seq[String]) {
  private def this() = this(null, null, null, null)
}

@BeanInfo
case class Address(zipcode: String, city: String, country: Country) {
  private def this() = this(null, null, null)
}

@BeanInfo
case class Country(isoCode: String, name: String) {
  private def this() = this(null, null)
}

@BeanInfo
case class State(isoCode: String, name: String) {
  private def this() = this(null, null)
}


class PersonService @Inject()(mongo: MongoDB, cometRegistry: CometRegistry) {

  def getCountries: Seq[Country] =
    Country("de", "Germany") ::
    Country("es", "Spain") ::
    Country("fr", "France") ::
    Country("nl", "Netherlands") :: Nil

  def getStates(countryCode: String) =
    states.getOrElse(countryCode, Nil)

  def doSomething(person: Person): Person = {

    //////////////////
    //cometRegistry.publish("personUpdates", Map("country" -> "abc"), Map("a" -> 123, "b" -> 456))
    /////////////////////////






    println("doSomething got: " + person)

    val dbObject = MongoDBObject("fullName" -> "%s %s".format(person.firstName, person.lastName))
    mongo("doSomething").save(dbObject)

    person.copy(lastName = person.lastName * 2)
  }

  def pojoPerson(person: PojoPerson): PojoPerson = {
    println("pojoPerson: " + person)
    person.setFirstName("Hello World")

    person
  }

  // --- sample data ---

  private val states = Map(
    "de" -> List(
      State("de-bw", "Baden Württemberg"),
      State("de-by", "Bayern"),
      State("de-be", "Berlin")),
    "es" -> List(
      State("es-aa", "Andalucía"),
      State("es-bb", "Castilla y León"),
      State("es-cc", "Cataluña")),
    "fr" -> List(
      State("fr-aa", "Alsace"),
      State("fr-bb", "Chanpagne-Ardenne"),
      State("fr-cc", "Burgundy"))
  )
}
