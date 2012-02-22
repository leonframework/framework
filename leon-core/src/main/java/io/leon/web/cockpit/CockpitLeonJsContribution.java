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
        StringBuilder builder = new StringBuilder();

        if (!("false".equals(params.get("loadCockpit")))) {
            // Soy
            Resource cockpitSoy = resourceLoader.getResource(
                    getClass().getPackage().getName().replace('.', '/') + "/leon-cockpit-ui.js");
            builder.append(ResourceUtils.inputStreamToString(cockpitSoy.getInputStream()));

            // CoffeeScript
            Resource cockpitJs = resourceLoader.getResource(
                    getClass().getPackage().getName().replace('.', '/') + "/leon-cockpit.js");
            builder.append(ResourceUtils.inputStreamToString(cockpitJs.getInputStream()));
        }

        return builder.toString();
    }
}
