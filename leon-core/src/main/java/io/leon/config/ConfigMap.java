package io.leon.config;

import java.util.*;

public class ConfigMap extends AbstractMap<Object, Object> {

    public static final String APPLICATION_NAME_KEY = "leon.applicationNMame";
    public static final String DEPLOYMENT_MODE_KEY  = "leon.deploymentMode";

    public static final String DEVELOPMENT_MODE = "development";
    public static final String PRODUCTION_MODE = "production";

    private final Map<Object, Object> map;

    ConfigMap(Map<Object, Object> map) {
        this.map  = map;
    }

    @Override
    public Set<Entry<Object, Object>> entrySet() {
        return map.entrySet();
    }

    @Override
    public String put(Object key, Object value) {
        throw new UnsupportedOperationException("changing the ConfigMap after startup is not supported! " +
                "Bind a ConfigParameter in your module configuration to bind new parameters");
    }

    public String getProperty(String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }

    // -- convenience --

    public String getApplicationName() {
        return getProperty(APPLICATION_NAME_KEY);
    }

    public String getDeploymentMode() {
        return getProperty(DEPLOYMENT_MODE_KEY, DEVELOPMENT_MODE).toLowerCase();
    }

    public boolean isDevelopmentMode() {
        return DEVELOPMENT_MODE.equals(getDeploymentMode());
    }

    public boolean isProductionMode() {
        return PRODUCTION_MODE.equals(getDeploymentMode());
    }
}
