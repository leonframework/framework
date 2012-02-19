package io.leon.web.angular;

import com.google.inject.AbstractModule;
import io.leon.web.browser.VirtualLeonJsFileBinder;

public class AngularModule extends AbstractModule {

    @Override
    protected void configure() {
        new VirtualLeonJsFileBinder(binder()).bindAndAddContribution(AngularAutoCompileLeonJsContribution.class);
    }

}
