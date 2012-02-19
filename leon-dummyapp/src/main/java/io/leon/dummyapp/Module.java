package io.leon.dummyapp;

import io.leon.AbstractLeonConfiguration;
import io.leon.web.ajax.AjaxBinder;

public class Module extends AbstractLeonConfiguration {

    @Override
    public void config() {
        AjaxBinder ajaxBinder = new AjaxBinder(super.binder());
        ajaxBinder.exposeJavaService("/ajaxService", AjaxService.class);
    }

}
