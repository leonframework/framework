package io.leon.unitofwork;

import com.google.inject.Binding;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

import java.util.List;

public class UOWManager {

    private Injector injector;

    private List<Binding<UOWListener>> listener;

    @Inject
    public UOWManager(Injector injector) {
        this.injector = injector;
        listener = injector.findBindingsByType(new TypeLiteral<UOWListener>() {});
    }

    public void begin() {
        for (Binding<UOWListener> listenerBinding : listener) {
            UOWListener instance = injector.getInstance(listenerBinding.getKey());
            instance.begin();
        }
    }

    public void commit() {
        for (Binding<UOWListener> listenerBinding : listener) {
            UOWListener instance = injector.getInstance(listenerBinding.getKey());
            instance.commit();
        }
    }

}
