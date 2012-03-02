package io.leon.web.browser.ajax.basicjs;

import io.leon.LeonModule;

public class AjaxTestModule extends LeonModule {

    @Override
    public void config() {
        loadFile(getClass(), "AjaxService.js");
        exposeUrl(".*");
        exposeJavaScript("/ajaxService", "AjaxService");
    }

}
