package io.leon.samples.ajax.reverser.java_js;

import io.leon.AbstractLeonConfiguration;

public class Module extends AbstractLeonConfiguration {

    @Override
    public void config() {
        browser("reverserService").linksToServer(ReverserService.class);
    }

}
