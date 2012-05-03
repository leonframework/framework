package io.leon.rt;

import com.google.inject.Inject;
import io.leon.rt.converters.Converter;

import java.util.Collection;

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

    public <E> CollectionNode<E> of(Collection<E> collection) {
        return new CollectionNode<E>(this, collection);
    }

    public Converter getConverter() {
        return converter;
    }

}
