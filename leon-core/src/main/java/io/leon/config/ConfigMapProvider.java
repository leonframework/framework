package io.leon.config;

import com.google.inject.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;


public class ConfigMapProvider implements Provider<ConfigMap> {

    public static final String LEON_PROPERTIES_FILE = "/leon.properties";

    private static final Logger logger = LoggerFactory.getLogger(ConfigMapProvider.class);

    private final Injector injector;

    @Inject
    public ConfigMapProvider(Injector injector) {
        this.injector = injector;
    }

    @Override
    public ConfigMap get() {

        // Exception: see http://code.google.com/p/google-guice/wiki/Servlets
//        ServletContext servletContext = injector.getInstance(ServletContext.class);

        Properties properties = readProperties();
        Map<Object, Object> moduleParameter = readModuleParameters();
//        Map<Object, Object> servletInitParameters = readServletContext(servletContext);
        Map<String, String> environment = readEnvironment();

        Map<Object, Object> configMap = new HashMap<Object, Object>();
        configMap.putAll(moduleParameter);
        configMap.putAll(properties);
//        configMap.putAll(servletInitParameters);
        configMap.putAll(environment);

        return new ConfigMap(configMap);
    }

    private Map<Object, Object> readModuleParameters() {
        // TODO use annotation-based approach
        List<Binding<ConfigParameter>> parameterList = injector.findBindingsByType(TypeLiteral.get(ConfigParameter.class));
        Map<Object, Object> map = new HashMap<Object, Object>();

        for (Binding<ConfigParameter> configBinding : parameterList) {
            ConfigParameter parameter = configBinding.getProvider().get();
            map.put(parameter.getKey(), parameter.getValue());
        }

        logger.debug("Found {} config parameters from modules", map.size());

        return map;
    }

    private Properties readProperties() {
        URL resource = ConfigMap.class.getResource(LEON_PROPERTIES_FILE);
        if (resource == null) {
           return new Properties();
        } else {
            try {
                InputStream stream = resource.openStream();
                Properties properties = new Properties();
                properties.load(stream);

                logger.debug("Found {} in classpath", LEON_PROPERTIES_FILE);

                return properties;
            } catch (IOException ex) {
                // TODO log
                return new Properties();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private Map<Object, Object> readServletContext(ServletContext servletContext) {

        // use LeonFilter to 'publish' init parameters

        Enumeration<String> parameterNames = servletContext.getInitParameterNames();
        Map<Object, Object> map = new HashMap<Object, Object>();

        while(parameterNames.hasMoreElements()) {
            String key = parameterNames.nextElement();
            String value = servletContext.getInitParameter(key);

            map.put(key, value);
        }

        return map;
    }

    private Map<String, String> readEnvironment() {
        Map<String, String> originalMap = System.getenv();
        Map<String, String> destMap = new HashMap<String, String>();

        for (Map.Entry<String, String> entry : originalMap.entrySet()) {
            destMap.put(entry.getKey(), entry.getValue().toLowerCase().replaceAll("_", "."));
        }

        return destMap;
    }
}
