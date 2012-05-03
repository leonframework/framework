package io.leon.rt;

import com.google.inject.Inject;
import io.leon.rt.converters.Converter;

public class Rt {

    private final Converter converter;

    public Rt() {
        converter = new Converter();
    }

    @Inject
    public Rt(Converter converter) {
        this.converter = converter;
    }

    public <E> Node<E> of(E node) {
        return new Node<E>(this, node);
    }

    public Converter getConverter() {
        return converter;
    }

}
