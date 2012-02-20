package io.leon.config;


import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class ConfigMapBuilder {

    public static final String LEON_PROPERTIES_FILE = "/leon.properties";

    private static final Logger logger = LoggerFactory.getLogger(ConfigMapBuilder.class);

    private ConfigMap configMap = new ConfigMap();

    public ConfigMapBuilder readModuleParameters(Injector injector) {
        // TODO use annotation-based approach
        List<Binding<ConfigValue>> parameterList = injector.findBindingsByType(TypeLiteral.get(ConfigValue.class));
        Map<String, String> map = new HashMap<String, String>();

        for (Binding<ConfigValue> configBinding : parameterList) {
            ConfigValue parameter = configBinding.getProvider().get();
            map.put(parameter.getKey(), parameter.getValue());
        }

        logger.debug("Found {} config parameters from modules", map.size());

        configMap.putAll(map);

        return this;
    }

    @SuppressWarnings("unchecked")
    public ConfigMapBuilder readProperties() {
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
        return this;
    }

    @SuppressWarnings("unchecked")
    public ConfigMapBuilder readServletContext(ServletContext servletContext) {
        Enumeration<String> parameterNames = servletContext.getInitParameterNames();

        while(parameterNames.hasMoreElements()) {
            String key = parameterNames.nextElement();
            String value = servletContext.getInitParameter(key);

            configMap.put(key, value);
        }

        return this;
    }

    public ConfigMapBuilder readEnvironment() {
        Map<String, String> originalMap = System.getenv();

        for (Map.Entry<String, String> entry : originalMap.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith("LEON_")) {
                key = key.substring(5).toLowerCase().replaceAll("_", ".");
            }
            configMap.put(key, entry.getValue());
        }

        return this;
    }

    public ConfigMap create() {
        return configMap;
    }
}
