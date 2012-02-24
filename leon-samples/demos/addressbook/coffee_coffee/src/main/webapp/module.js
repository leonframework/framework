
setApplicationName("LeonDemoAddressBook");

install(new Packages.io.leon.persistence.mongo.LeonMongoModule);

loadFile("addressBookService.js");

var AjaxBinder = Packages.io.leon.web.ajax.AjaxBinder;
var ajaxBinder = new AjaxBinder(binder());
ajaxBinder.exposeJavaScript("/addressBookService", "addressBookService");
