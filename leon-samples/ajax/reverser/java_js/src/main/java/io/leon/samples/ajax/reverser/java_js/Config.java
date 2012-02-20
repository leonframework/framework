package io.leon.samples.ajax.reverser.java_js;

import io.leon.AbstractLeonConfiguration;
import io.leon.config.ConfigParameter;

public class Config extends AbstractLeonConfiguration {

    @Override
    public void config() {
        browser("reverserService").linksToServer(ReverserService.class);
    }

}
