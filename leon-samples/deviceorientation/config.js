// module definition
install(new Packages.io.leon.resources.coffeescript.CoffeeScriptModule());
// install(new Packages.io.leon.resources.less.LessModule());

// location of application files
addLocation("./www");

// server-side js files
loadFile("/server.js");

// ajax support
browser("leoncomet").linksToServer();
