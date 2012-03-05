package io.leon.web.browser.ajax.basic;

import io.leon.LeonAppMainModule;

public class AjaxTestModule extends LeonAppMainModule {

    @Override
    public void config() {
        // Java
        exposeJavaService("/ajaxServiceJava", AjaxService.class);

        // JavaScript
        loadFile(getClass(), "AjaxService.js");
        exposeUrl(".*");
        exposeJavaScript("/ajaxServiceJavaScript", "AjaxService");
    }

}
