
setApplicationName("LeonDemoAddressBook");

install(new Packages.io.leon.persistence.mongo.LeonMongoModule);

loadFile("addressBookService.js");
exposeJavaScript("/addressBookService", "addressBookService");
