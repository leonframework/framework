package io.leon.web.browser.ajax.basic;

import io.leon.LeonModule;

public class AjaxTestModule extends LeonModule {

    @Override
    public void config() {
        exposeUrl(".*");
        exposeJavaService("/ajaxService", AjaxService.class);
    }

}
