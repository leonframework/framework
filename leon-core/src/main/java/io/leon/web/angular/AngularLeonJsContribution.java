package io.leon.web.angular;

import com.google.inject.Inject;
import io.leon.resourceloading.Resource;
import io.leon.resourceloading.ResourceLoader;
import io.leon.resourceloading.ResourceUtils;
import io.leon.web.browser.VirtualLeonJsFileContribution;

import java.util.Map;

public class AngularLeonJsContribution implements VirtualLeonJsFileContribution {

    private final ResourceLoader resourceLoader;

    @Inject
    public AngularLeonJsContribution(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public String content(Map<String, String> params) {
        StringBuilder content = new StringBuilder();

        if (!("false".equals(params.get("loadAngular")))) {
            Resource resource = resourceLoader.getResource("/leon/browser/angular-0.9.19.js");
            content.append(ResourceUtils.inputStreamToString(resource.createInputStream()));
        }

        if (!("false".equals(params.get("angularAutoCompile")))) {
            content.append("angular.element(document).ready(function(){" +
                    "leon.angularDocument = angular.compile(document)();" +
                    "});");
        }

        return content.toString();
    }

}
