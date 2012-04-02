package io.leon.web.angular;

import com.google.inject.Inject;
import io.leon.resourceloading.Resource;
import io.leon.resourceloading.ResourceLoader;
import io.leon.resourceloading.ResourceUtils;
import io.leon.web.browser.VirtualLeonJsFileContribution;

import java.util.Map;

public class AngularLeonJsContribution implements VirtualLeonJsFileContribution {

    private final ResourceLoader resourceLoader;

    private final String angularVersion = "1.0.0rc3";
    
    private final String angularJsPath = "/"
            + getClass().getPackage().getName().replace('.', '/')
            + "/angular-" + angularVersion + ".js";

    private final String angularResourceJsPath = "/"
            + getClass().getPackage().getName().replace('.', '/')
            + "/angular-resource-" + angularVersion + ".js";
    
    private final String angularCookiesJsPath = "/"
            + getClass().getPackage().getName().replace('.', '/')
            + "/angular-cookies-" + angularVersion + ".js";
    
    private final String angularLeonIntegrationJsPath = "/"
            + getClass().getPackage().getName().replace('.', '/')
            + "/angular_leon_integration.js";

    private final String angularUtilsJsPath = "/"
            + getClass().getPackage().getName().replace('.', '/')
            + "/angular_utils.js";

    @Inject
    public AngularLeonJsContribution(final ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public String content(final Map<String, String> params) {
        final StringBuilder content = new StringBuilder();

        if (!("false".equals(params.get("loadAngular")))) {
            final Resource angularJs = resourceLoader.getResource(angularJsPath);
            content.append(ResourceUtils.inputStreamToString(angularJs.getInputStream()));
            
            final Resource angularResourceJs = resourceLoader.getResource(angularResourceJsPath);
            content.append(ResourceUtils.inputStreamToString(angularResourceJs.getInputStream()));
            
            final Resource angularCookiesJs = resourceLoader.getResource(angularCookiesJsPath);
            content.append(ResourceUtils.inputStreamToString(angularCookiesJs.getInputStream()));

            final Resource angularLeonIntegrationJs = resourceLoader.getResource(angularLeonIntegrationJsPath);
            content.append(ResourceUtils.inputStreamToString(angularLeonIntegrationJs.getInputStream()));

            final Resource angularLeonUtilsJs = resourceLoader.getResource(angularUtilsJsPath);
            content.append(ResourceUtils.inputStreamToString(angularLeonUtilsJs.getInputStream()));
        }

        if (!("false".equals(params.get("angularAutoCompile")))) {
            content.append("angular.element(document).ready(function(){" +
                    "angular.bootstrap(document);" +
                    "});");
        }

        return content.toString();
    }

}
