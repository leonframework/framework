package io.leon.web.cockpit;

import com.google.inject.AbstractModule;
import io.leon.web.browser.VirtualLeonJsFileBinder;

public class CockpitModule extends AbstractModule {

    @Override
    protected void configure() {
        VirtualLeonJsFileBinder leonJsBinder = new VirtualLeonJsFileBinder(binder());
        leonJsBinder.bindAndAddContribution(CockpitLeonJsContribution.class);
    }

}
