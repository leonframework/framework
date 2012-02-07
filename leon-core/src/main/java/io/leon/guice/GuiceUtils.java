package io.leon.guice;

import com.google.inject.Binder;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.name.Names;

public class GuiceUtils {

    public static <T> ScopedBindingBuilder bindClassWithName(Binder binder, Class<T> iface, Class<? extends T> clazz) {
        return binder.bind(iface).annotatedWith(Names.named(clazz.getName())).to(clazz);
    }

}
