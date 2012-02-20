package io.leon.config;

/**
 * A singleton which holds an instance of {ConfigMap}.
 *
 * The {ConfigMap} gets bound to Guice during application startup.
 */
public class ConfigMapHolder {

    private static ConfigMapHolder INSTANCE;

    private ConfigMap configMap = new ConfigMap();

    public ConfigMap getConfigMap() {
        return configMap;
    }

    private ConfigMapHolder() {
        // its a singleton
    }

    public static synchronized ConfigMapHolder getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new ConfigMapHolder();
        }

        return INSTANCE;
    }

}
