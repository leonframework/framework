package io.leon.web.browser.comet.ping;

import io.leon.LeonModule;

public class CometTestModule extends LeonModule {

    @Override
    public void config() {
        exposeUrl(".*");

        addTopic("ping");
        addTopic("numberPing");
        addTopic("filterPing");

        exposeJavaService("/pingService", PingService.class);
    }

}
