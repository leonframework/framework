package io.leon.web.browser.comet.ping;

import io.leon.LeonModule;
import io.leon.web.ajax.AjaxBinder;

public class CometPingModule extends LeonModule {

    @Override
    public void config() {
        exposeUrl(".*");
        bind(PingService.class).asEagerSingleton();
        new AjaxBinder(super.binder()).exposeJavaService("/pingService", PingService.class);
    }

}
