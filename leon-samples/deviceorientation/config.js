// location of application files
addLocation("./www");

// server-side js files
loadFile("/server.js");

// ajax support
browser("leoncomet").linksToServer();
