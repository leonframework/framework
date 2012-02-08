package io.leon.unitofwork;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class UOWFeatureModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(UOWManager.class).in(Scopes.SINGLETON);
    }

}
