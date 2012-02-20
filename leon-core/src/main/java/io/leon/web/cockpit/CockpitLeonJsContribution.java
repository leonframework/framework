package io.leon.web.cockpit;

import com.google.inject.Inject;
import io.leon.resourceloading.Resource;
import io.leon.resourceloading.ResourceLoader;
import io.leon.resourceloading.ResourceUtils;
import io.leon.web.browser.VirtualLeonJsFileContribution;

import java.util.Map;

public class CockpitLeonJsContribution implements VirtualLeonJsFileContribution {

    private final ResourceLoader resourceLoader;

    @Inject
    public CockpitLeonJsContribution(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public String content(Map<String, String> params) {
        Resource resource = resourceLoader.getResource(
                getClass().getPackage().getName().replace('.', '/') + "/leon-cockpit.js");

        return ResourceUtils.inputStreamToString(resource.createInputStream());
    }
}
