package io.leon.config;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class ConfigModule extends AbstractModule {

    public ConfigModule init() {
        ConfigMap configMap = ConfigMapHolder.getInstance().getConfigMap();

        // read properties without overriding existing values
        configMap.importConfigMap(new ConfigReader().readProperties());

        // read system settings with overriding existing values
        configMap.putAll(new ConfigReader().readEnvironment());

        return this;
    }

    @Override
    protected void configure() {
        ConfigMap configMap = ConfigMapHolder.getInstance().getConfigMap();

        bind(ConfigMap.class).toInstance(configMap);

        requestInjection(new Object() {
            @Inject
            public void init(Injector injector) {
                // Importing module config parameters without overriding existing values
                ConfigMap c = injector.getInstance(ConfigMap.class);
                c.importConfigMap(new ConfigReader().readModuleParameters(injector));
            }
        });
    }
}
