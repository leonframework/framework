package io.leon.config;

import java.util.*;

public class ConfigMap extends Hashtable<String, String> {

    public static final String APPLICATION_NAME_KEY = "leon.applicationNMame";
    public static final String DEPLOYMENT_MODE_KEY  = "leon.deploymentMode";

    public static final String DEVELOPMENT_MODE = "development";
    public static final String PRODUCTION_MODE = "production";

    // -- convenience --

    public String getOrElse(String key, String defaultValue) {
        String value = get(key);
        return value != null ? value : defaultValue;
    }

    public String getApplicationName() {
        return get(APPLICATION_NAME_KEY);
    }

    public String getDeploymentMode() {
        return getOrElse(DEPLOYMENT_MODE_KEY, DEVELOPMENT_MODE).toLowerCase();
    }

    public boolean isDevelopmentMode() {
        return DEVELOPMENT_MODE.equals(getDeploymentMode());
    }

    public boolean isProductionMode() {
        return PRODUCTION_MODE.equals(getDeploymentMode());
    }
}
