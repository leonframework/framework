package io.leon.web.angular;

import com.google.inject.Inject;
import io.leon.resourceloading.Resource;
import io.leon.resourceloading.ResourceLoader;
import io.leon.resourceloading.ResourceUtils;
import io.leon.web.browser.VirtualLeonJsFileContribution;

import java.util.Map;

public class AngularLeonJsContribution implements VirtualLeonJsFileContribution {

    private final ResourceLoader resourceLoader;

    private final String angularJsPath = "/"
            + getClass().getPackage().getName().replace('.', '/')
            + "/angular-0.9.19.js";

    private final String angularLeonIntegrationJsPath = "/"
            + getClass().getPackage().getName().replace('.', '/')
            + "/angular_leon_integration.js";

    @Inject
    public AngularLeonJsContribution(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public String content(Map<String, String> params) {
        StringBuilder content = new StringBuilder();

        if (!("false".equals(params.get("loadAngular")))) {
            Resource aJs = resourceLoader.getResource(angularJsPath);
            content.append(ResourceUtils.inputStreamToString(aJs.getInputStream()));

            Resource aliJs = resourceLoader.getResource(angularLeonIntegrationJsPath);
            content.append(ResourceUtils.inputStreamToString(aliJs.getInputStream()));
        }

        if (!("false".equals(params.get("angularAutoCompile")))) {
            content.append("angular.element(document).ready(function(){" +
                    "angular.compile(document)();" +
                    "});");
        }

        return content.toString();
    }

}
