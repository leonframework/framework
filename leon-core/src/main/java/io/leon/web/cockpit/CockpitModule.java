package io.leon.web.cockpit;

import io.leon.LeonModule;
import io.leon.web.browser.VirtualLeonJsFileBinder;

public class CockpitModule extends LeonModule {

    @Override
    protected void config() {
        VirtualLeonJsFileBinder leonJsBinder = new VirtualLeonJsFileBinder(binder());
        leonJsBinder.bindAndAddContribution(CockpitLeonJsContribution.class);

        addTopic("/leon/developmentMode/resourceWatcher/resourceChanged");
    }

}
