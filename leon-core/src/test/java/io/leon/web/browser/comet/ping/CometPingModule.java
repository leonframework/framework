package io.leon.web.browser.comet.ping;

import io.leon.AbstractLeonConfiguration;
import io.leon.web.ajax.AjaxBinder;

public class CometPingModule extends AbstractLeonConfiguration {

    @Override
    public void config() {
        exposeUrl(".*");
        bind(PingService.class).asEagerSingleton();
        new AjaxBinder(super.binder()).exposeJavaService("/pingService", PingService.class);
    }

}
