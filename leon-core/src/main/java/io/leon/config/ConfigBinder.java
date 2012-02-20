package io.leon.config;

import com.google.inject.Binder;
import com.google.inject.name.Names;


public class ConfigBinder {

    private final Binder binder;

    public ConfigBinder(Binder binder) {
        this.binder = binder;
    }

    /**
     * Bind a configuration parameter to the {@link ConfigMap}.
     *
     * @param key the key
     * @param value the value
     */
    public void configValue(String key, String value) {
        binder.bind(ConfigValue.class)
              .annotatedWith(Names.named(String.format("config[%s]", key)))
              .toInstance(new ConfigValue(key, value));
    }
}
