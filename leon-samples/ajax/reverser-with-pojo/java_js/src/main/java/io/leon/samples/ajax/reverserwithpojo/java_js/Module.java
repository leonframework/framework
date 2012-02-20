package io.leon.samples.ajax.reverserwithpojo.java_js;

import io.leon.AbstractLeonConfiguration;
import io.leon.web.ajax.AjaxBinder;

public class Module extends AbstractLeonConfiguration {

    @Override
    public void config() {
        AjaxBinder ajaxBinder = new AjaxBinder(super.binder());
        ajaxBinder.exposeJavaService("/reverserService", ReverserService.class);
    }

}
