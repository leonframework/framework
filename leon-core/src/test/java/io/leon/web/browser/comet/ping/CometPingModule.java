package io.leon.web.browser.comet.ping;

import io.leon.LeonModule;

public class CometPingModule extends LeonModule {

    @Override
    public void config() {
        exposeUrl(".*");
        addTopic("ping");
        exposeJavaService("/pingService", PingService.class);
    }

}
