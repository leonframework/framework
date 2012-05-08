package io.leon.web.browser.selenium.cockpit;

import io.leon.LeonAppMainModule;

public class BasicCockpitTestModule extends LeonAppMainModule {

    @Override
    public void config() {
        exposeUrl(".*");
    }

}
