package io.leon.guice;

import com.google.inject.*;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.name.Names;

import java.util.List;

public class GuiceUtils {

    public static <T> ScopedBindingBuilder bindClassWithName(Binder binder, Class<T> iface, Class<? extends T> clazz) {
        return binder.bind(getKeyWithInterfaceAndClassName(iface, clazz)).to(clazz);
    }

    public static <T> Key<T> getKeyWithInterfaceAndClassName(Class<T> iface, Class<? extends T> clazz) {
        return Key.get(iface, Names.named(clazz.getName()));
    }

    public static <T> List<Binding<T>> getByType(Injector injector, Class<T> type) {
        TypeLiteral<T> typeLiteral = Key.get(type).getTypeLiteral();
        return injector.findBindingsByType(typeLiteral);
    }

}
