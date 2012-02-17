package io.leon.config;

import com.google.inject.AbstractModule;


public class LeonConfigModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ConfigMap.class).toProvider(ConfigMapProvider.class);
    }
}
