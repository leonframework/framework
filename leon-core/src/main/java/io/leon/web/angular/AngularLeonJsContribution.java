package io.leon.web.angular;

import com.google.inject.Inject;
import io.leon.resourceloading.Resource;
import io.leon.resourceloading.ResourceLoader;
import io.leon.utils.ResourceUtils;
import io.leon.web.browser.VirtualLeonJsFileContribution;

import java.util.Map;

public class AngularLeonJsContribution implements VirtualLeonJsFileContribution {

    private final ResourceLoader resourceLoader;

    private final String angularVersion = "1.0.0";
    
    private final String angularFolderPath = "/" + getClass().getPackage().getName().replace('.', '/');

    private final String angularJsPath = angularFolderPath + "/angular-" + angularVersion + ".js";
    private final String angularBootstrapJsPath = angularFolderPath + "/angular-bootstrap-" + angularVersion + ".js";
    private final String angularResourceJsPath = angularFolderPath + "/angular-resource-" + angularVersion + ".js";
    private final String angularCookiesJsPath = angularFolderPath + "/angular-cookies-" + angularVersion + ".js";
    
    private final String angularLeonIntegrationJsPath = angularFolderPath + "/angular_leon_integration.js";

    private final String[] angularJsPathes = {
        angularJsPath,
        angularBootstrapJsPath,
        angularResourceJsPath,
        angularCookiesJsPath,
        angularLeonIntegrationJsPath
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
