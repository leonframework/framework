package io.leon.config;

import java.util.*;

/**
 * The ConfigMap holds all configuration parameters of a LEON application.
 *
 * Parameters are read from the following places and priority:
 * <ol>
 *     <li>System environment</li>
 *     <li>JVM Properties</li>
 *     <li>/leon.properties file in classpath (optional)</li>
 *     <li>LeonFilter init parameters in web.xml</li>
 *     <li>Module configuration parameters</li>
 * </ol>
 *
 * To get a reference of the {@link ConfigMap} use one of the following ways:
 * <ul>
 *     <li>Let it inject (preferred way)</li>
 *     <li>call <code>ConfigMapHolder.getInstance().getConfigMap()</code></li>
 * </ul>
 *
 * To bind your own configuration parameters in a module use {@link ConfigBinder}:
 * <code><pre>
 * ConfigBinder configBinder = new ConfigBinder(binder());
 * configBinder.configValue("yourConfigKey", "yourConfigValue");
 * </pre></code>
 *
 */
public class ConfigMap extends Hashtable<String, String> {

    public static final String APPLICATION_NAME_KEY = "leon.applicationName";
    public static final String DEPLOYMENT_MODE_KEY  = "leon.deploymentMode";

    public static final String DEVELOPMENT_MODE = "development";
    public static final String PRODUCTION_MODE = "production";

    public ConfigMap() {

    }

    public ConfigMap(Map<String, String> map) {
        super(map);
    }

    /**
     * Imports entries from the given <code>configMap</code> without overriding existing entries.
     * @param configMap the {@link ConfigMap} to import.
     */
    public void importConfigMap(ConfigMap configMap) {
        for (Map.Entry<String, String> entry : configMap.entrySet()) {
            if(! this.containsKey(entry.getKey())) {
                this.put(entry.getKey(), entry.getValue());
            }
        }
    }

    // -- convenience --

    public String getOrElse(String key, String defaultValue) {
        String value = get(key);
        return value != null ? value : defaultValue;
    }

    public String getApplicationName() {
        return getOrElse(APPLICATION_NAME_KEY, "UnnamedLeonApp");
    }

    public String getDeploymentMode() {
        return getOrElse(DEPLOYMENT_MODE_KEY, PRODUCTION_MODE).toLowerCase();
    }

    public boolean isDevelopmentMode() {
        return DEVELOPMENT_MODE.equals(getDeploymentMode());
    }

    public boolean isProductionMode() {
        return PRODUCTION_MODE.equals(getDeploymentMode());
    }
}
