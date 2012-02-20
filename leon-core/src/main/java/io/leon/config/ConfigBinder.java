package io.leon.config;

import com.google.inject.Binder;
import com.google.inject.name.Names;

public class ConfigBinder {

    private final Binder binder;

    public ConfigBinder(Binder binder) {
        this.binder = binder;
    }

    public void configParameter(String key, String value) {
        binder.bind(ConfigValue.class).annotatedWith(Names.named(String.format("%s=%s", key, value))).toInstance(new ConfigValue(key, value));
    }
}
