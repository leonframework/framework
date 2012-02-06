package io.leon.samples.cometping.java_js;

import io.leon.AbstractLeonConfiguration;

public class Config extends AbstractLeonConfiguration {

    @Override
    public void config() {
        browser("pingService").linksToServer(PingService.class);
    }

}
