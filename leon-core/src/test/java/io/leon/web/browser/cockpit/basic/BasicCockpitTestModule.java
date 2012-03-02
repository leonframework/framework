package io.leon.web.browser.cockpit.basic;

import io.leon.LeonModule;

public class BasicCockpitTestModule extends LeonModule {

    @Override
    public void config() {
        exposeUrl(".*");
    }

}
