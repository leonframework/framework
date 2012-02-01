package io.leon.samples.cometping;

import io.leon.AbstractLeonConfiguration;

public class Module extends AbstractLeonConfiguration {

    @Override
    public void config() {
        browser("pingService").linksToServer(PingService.class);
    }

}
