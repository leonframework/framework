package io.leon.web.browser.selenium.cockpit;

import io.leon.LeonModule;

public class BasicCockpitTestModule extends LeonModule {

    @Override
    public void config() {
        exposeUrl(".*");
    }

}
