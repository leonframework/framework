package io.leon.samples.ajax.reverser.java_js;

import io.leon.AbstractLeonConfiguration;
import io.leon.config.ConfigParameter;

public class Config extends AbstractLeonConfiguration {

    @Override
    public void config() {
        bind(ConfigParameter.class).toInstance(new ConfigParameter("xyz", "b"));
        browser("reverserService").linksToServer(ReverserService.class);
    }

}
