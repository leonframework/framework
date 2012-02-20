package io.leon.web.browser.ajax.errordialog;

import io.leon.AbstractLeonConfiguration;
import io.leon.web.ajax.AjaxBinder;

public class ErrorDialogModule extends AbstractLeonConfiguration {

    @Override
    public void config() {
        AjaxBinder ab = new AjaxBinder(super.binder());
        ab.exposeJavaService("/ajaxService", AjaxService.class);
    }

}
