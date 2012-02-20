package io.leon.config;


import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

/**
 * Provides methods to read configuration parameters from certain places.
 */
public class ConfigReader {

    public static final String LEON_PROPERTIES_FILE = "/leon.properties";

    private static final Logger logger = LoggerFactory.getLogger(ConfigReader.class);

    /**
     * Calls {readEnvironment(env)} with System.getEnv().
     *
     * @return the {ConfigMap}
     */
    public ConfigMap readEnvironment() {
        return readEnvironment(System.getenv());
    }

    /**
     * Creates a {ConfigMap} containing all system environment variables.
     *
     * Variable names prefixed with 'LEON_' will be transformed to camel case
     * (e.g. LEON_APPLICATION_NAME becomes leon.applicationName).
     *
     * @param env the entries to read
     * @return the {ConfigMap}
     */
    public ConfigMap readEnvironment(Map<String, String> env) {
        ConfigMap configMap = new ConfigMap();

        for (Map.Entry<String, String> entry : env.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("LEON_")) {
                key = prepareLeonSystemVariableName(key);
            }
            configMap.put(key, entry.getValue());
        }

        return configMap;
    }

    /**
     * Loads the JVM arguments and the properties from the optional '/leon.properties' file into
     * a {ConfigMap}.
     *
     * JVM arguments overrides properties from the leon.properties file.
     *
     * @return the {ConfigMap}
     */
    @SuppressWarnings("unchecked")
    public ConfigMap readProperties() {
        ConfigMap configMap = new ConfigMap();

        URL resource = ConfigMap.class.getResource(LEON_PROPERTIES_FILE);
        if (resource != null) {
            try {
                InputStream stream = resource.openStream();
                Properties properties = new Properties();
                properties.load(stream);

                logger.debug("Found {} in classpath", LEON_PROPERTIES_FILE);

                for (Map.Entry<Object, Object> entry: properties.entrySet()) {
                    String value = entry.getValue() != null ? entry.getValue().toString() : null;
                    configMap.put(entry.getKey().toString(), value);
                }

            } catch (IOException ex) {
                logger.error("Error while reading " + LEON_PROPERTIES_FILE, ex);
            }
        }

        // read JVM arguments
        for (Map.Entry<Object, Object> entry: System.getProperties().entrySet()) {
            String value = entry.getValue() != null ? entry.getValue().toString() : null;
            configMap.put(entry.getKey().toString(), value);
        }

        return configMap;
    }

    /**
     * Creates a {ConfigMap} with all init parameters in {filterConfig}
     * @param filterConfig the {FilterConfig} to read the parameters from.
     * @return {ConfigMap}
     */
    @SuppressWarnings("unchecked")
    public ConfigMap readFilterConfig(FilterConfig filterConfig) {
        ConfigMap configMap = new ConfigMap();
        Enumeration<String> parameterNames = filterConfig.getInitParameterNames();

        while(parameterNames.hasMoreElements()) {
            String key = parameterNames.nextElement();
            String value = filterConfig.getInitParameter(key);

            configMap.put(key, value);
        }

        return configMap;
    }

    /**
     * Creates a {ConfigMap} with all module configuration parameters.
     *
     * @see {ConfigBinder}
     * @param injector the {Injector} to look up for bindings.
     * @return the {ConfigMap}
     */
    public ConfigMap readModuleParameters(Injector injector) {
        ConfigMap configMap = new ConfigMap();
        List<Binding<ConfigValue>> parameterList = injector.findBindingsByType(TypeLiteral.get(ConfigValue.class));
        Map<String, String> map = new HashMap<String, String>();

        for (Binding<ConfigValue> configBinding : parameterList) {
            ConfigValue parameter = configBinding.getProvider().get();
            map.put(parameter.getKey(), parameter.getValue());
        }

        logger.debug("Found {} config parameters from modules", map.size());

        configMap.putAll(map);

        return configMap;
    }

    /**
     * Utility method to transform the variable name from e.g. 'LEON_APPLICATION_NAME' to 'leon.applicationName'.
     *
     * @param key the string to transform
     * @return the transformed string
     */
    private String prepareLeonSystemVariableName(String key) {
        if (! key.startsWith("LEON_"))
            throw new IllegalArgumentException("<key> must start with 'LEON_'");

        key = key.substring(5).toUpperCase();

        StringBuilder sb = new StringBuilder("leon.");
        boolean leaveUpperCase = false;

        for (char c : key.toCharArray()) {
            if(c == '_') {
                leaveUpperCase = true;
            } else if (leaveUpperCase) {
                sb.append(c);
                leaveUpperCase = false;
            } else {
                sb.append(Character.toLowerCase(c));
            }
        }

        return sb.toString();
    }
}
