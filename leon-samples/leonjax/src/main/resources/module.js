// module definition
install(new Packages.io.leon.persistence.mongo.LeonMongoModule());
install(new Packages.io.leon.resources.closure.ClosureTemplatesModule());

// location of application files
addLocation("./www");

// server-side js files
loadFile("/leonjax_server.js");

// ajax support
browser("leonJaxService").linksToServer()
