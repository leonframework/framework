package io.leon.web.browser.cockpit.basic;

import io.leon.AbstractLeonConfiguration;

public class BasicCockpitTestModule extends AbstractLeonConfiguration {

    @Override
    public void config() {
        exposeUrl(".*");
    }

}
