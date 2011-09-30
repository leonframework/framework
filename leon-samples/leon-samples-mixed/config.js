// module definition
install(new Packages.io.leon.persistence.mongo.LeonMongoModule());
install(new Packages.io.leon.resources.coffeescript.CoffeeScriptModule());
install(new Packages.io.leon.resources.closure.ClosureTemplatesModule());

// location of application files
addLocation("./www");

// server-side js files
loadFile("/io/leon/samples/mixed/person.js");
loadFile("/leonjax/leonjax_server.js");

// ajax support
browser("person").linksToServer("person");
browser("personService").linksToServer(Packages.io.leon.samples.mixed.PersonService);
browser("leonJaxService").linksToServer()

// dependency injection
bind(Packages.io.leon.samples.mixed.PersonService).asEagerSingleton();
