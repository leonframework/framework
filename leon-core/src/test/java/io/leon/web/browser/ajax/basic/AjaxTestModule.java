package io.leon.web.browser.ajax.basic;

import io.leon.LeonAppMainModule;

public class AjaxTestModule extends LeonAppMainModule {

    @Override
    public void config() {
        exposeJavaService("/ajaxService", AjaxService.class);
    }

}
