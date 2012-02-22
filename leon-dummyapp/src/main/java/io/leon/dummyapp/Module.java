package io.leon.dummyapp;

import com.google.inject.Key;
import io.leon.AbstractLeonConfiguration;
import io.leon.web.ajax.AjaxBinder;

public class Module extends AbstractLeonConfiguration {

    @Override
    public void config() {
        loadFile("/io/leon/dummyapp/server.js");

        bind(ReverserService.class).asEagerSingleton();
        AjaxBinder ajaxBinder = new AjaxBinder(super.binder());
        ajaxBinder.exposeJavaService("/reverserService", Key.get(ReverserService.class));
    }

}
