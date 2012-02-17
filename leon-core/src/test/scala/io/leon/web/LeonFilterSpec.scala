package io.leon.web

import org.testng.annotations.Test
import org.scalatest.Assertions
import org.testng.Assert

class LeonFilterSpec extends Assertions {

  val JsConfig =
    """
    // module definition
    install(new Packages.io.leon.persistence.mongo.LeonMongoModule());
    install(new Packages.io.leon.resources.coffeescript.CoffeeScriptModule());
    install(new Packages.io.leon.resources.soy.SoyTemplatesModule());

    // location of application files
    addLocation("leon-samples/leon-samples-cometping/WebContent");

    // server-side js files
    loadFile("/io/leon/samples/cometping/person.js");

    // ajax support
    browser("person").linksToServer("person");
    browser("personService").linksToServer(new Packages.io.leon.samples.cometping.PersonService);

    // comet support
    server("leon.browser").linksToAllPages("leon");

    // some javascript expressions
    var func = function(a) {
      return a;
    }
    """

  @Test def aLeonFilterShouldLoadAModuleConfigurationFromJavaScript() {
    val filter = new LeonFilter
    val alc = filter.createAndLoadModuleClass(JsConfig)
    Assert.assertNotNull(alc, "LeonFilter created module.")
  }

}
