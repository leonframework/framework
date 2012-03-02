package io.leon.web.browser.ajax.errordialog;

import io.leon.LeonModule;

public class ErrorDialogModule extends LeonModule {

    @Override
    public void config() {
        exposeJavaService("/ajaxService", AjaxService.class);
    }

}
