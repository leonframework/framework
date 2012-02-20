var AjaxBinder = Packages.io.leon.web.ajax.AjaxBinder;

loadFile("ReverserService.js");

var ajaxBinder = new AjaxBinder(binder());
ajaxBinder.exposeJavaScript("/reverserService", "ReverserService");
