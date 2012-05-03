package io.leon.web.browser.selenium.ajax.errordialog;

import io.leon.LeonAppMainModule;

public class ErrorDialogModule extends LeonAppMainModule {

    @Override
    public void config() {
        exposeJavaService("/ajaxService", AjaxService.class);
    }

}
