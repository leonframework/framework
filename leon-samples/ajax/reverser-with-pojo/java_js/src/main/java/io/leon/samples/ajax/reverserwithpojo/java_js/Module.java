package io.leon.samples.ajax.reverserwithpojo.java_js;

import io.leon.AbstractLeonConfiguration;

public class Module extends AbstractLeonConfiguration {

    @Override
    public void config() {
        browser("reverserService").linksToServer(ReverserService.class);
    }

}
