package io.leon.samples.ajax.reverser.java_js;

import io.leon.AbstractLeonConfiguration;
import io.leon.web.ajax.AjaxBinder;

public class Config extends AbstractLeonConfiguration {

    @Override
    public void config() {
        AjaxBinder ajaxBinder = new AjaxBinder(super.binder());
        ajaxBinder.exposeJavaService("/reverserService", ReverserService.class);
    }

}
