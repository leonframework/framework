package io.leon.web.browser.selenium.comet;

import io.leon.LeonAppMainModule;

public class CometTestModule extends LeonAppMainModule {

    @Override
    public void config() {
        exposeUrl(".*");

        addTopic("/ping");
        addTopic("/numberPing");
        addTopic("/filterPing");

        addTopic("/pingTopic1");
        addTopic("/pingTopic2");
        addTopic("/pingTopic3");
        addTopic("/pingTopic4");
        addTopic("/pingTopic5");
        addTopic("/pingTopic6");
        addTopic("/pingTopic7");
        addTopic("/pingTopic8");
        addTopic("/pingTopic9");

        exposeJavaService("/pingService", PingService.class);
    }

}
