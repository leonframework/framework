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
    
    private final String angularFolderPath = "/" + getClass().getPackage().getName().replace('.', '/');

    private final String angularJsPath = angularFolderPath + "/angular-" + angularVersion + ".js";
    private final String angularResourceJsPath = angularFolderPath + "/angular-resource-" + angularVersion + ".js";
    private final String angularCookiesJsPath = angularFolderPath + "/angular-cookies-" + angularVersion + ".js";
    
    private final String angularLeonIntegration1JsPath = angularFolderPath + "/angular_leon_integration_1_basics.js";
    private final String angularLeonIntegration2JsPath = angularFolderPath + "/angular_leon_integration_2_utils.js";
    private final String angularLeonIntegration3JsPath = angularFolderPath + "/angular_leon_integration_3_crud.js";
    private final String angularLeonIntegration4JsPath = angularFolderPath + "/angular_leon_integration_4.js";

    private final String[] angularJsPathes = {
        angularJsPath,
        angularResourceJsPath,
        angularCookiesJsPath,
        angularLeonIntegration1JsPath,
        angularLeonIntegration2JsPath,
        angularLeonIntegration3JsPath,
        angularLeonIntegration4JsPath
    };

    @Inject
    public AngularLeonJsContribution(final ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public String content(final Map<String, String> params) {
        final StringBuilder content = new StringBuilder();

        if (!("false".equals(params.get("loadAngular")))) {
            for (final String path : angularJsPathes) {
                final Resource resoure = resourceLoader.getResource(path);
                content.append(ResourceUtils.inputStreamToString(resoure.getInputStream()));
            }
        }

        return content.toString();
    }

}
