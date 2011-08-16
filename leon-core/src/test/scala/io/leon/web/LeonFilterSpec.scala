package io.leon.web

import org.specs2.mutable.Specification


class LeonFilterSpec extends Specification {

  val JsConfig =
    """
    // module definition
    install(new Packages.io.leon.persistence.mongo.LeonMongoModule());
    install(new Packages.io.leon.resources.coffeescript.CoffeeScriptModule());
    install(new Packages.io.leon.resources.closure.ClosureTemplatesModule());

    // location of application files
    addLocation("leon-samples/leon-samples-mixed/WebContent");

    // server-side js files
    loadFile("/io/leon/samples/mixed/person.js");

    // ajax support
    browser("person").linksToServer("person");
    browser("personService").linksToServer(new Packages.io.leon.samples.mixed.PersonService);

    // comet support
    server("leon.browser").linksToAllPages("leon");

    // some javascript expressions
    var func = function(a) {
      return a;
    }
    """


  "A LeonFilter " should {

    "load a module configuration from javascript" in {
      val filter = new LeonFilter

      filter.loadModuleFromJavaScript("<LeonFilterSpec>", JsConfig)
      success
    }

  }

}