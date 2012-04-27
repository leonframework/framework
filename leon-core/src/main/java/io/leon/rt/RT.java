package io.leon.rt;

import com.google.inject.Inject;
import io.leon.rt.converters.Converter;

public class RT {

    private final Converter converter;

    public RT() {
        converter = new Converter();
    }

    @Inject
    public RT(Converter converter) {
        this.converter = converter;
    }

    public Node of(Object node) {
        return new Node(this, node);
    }

    public Converter getConverter() {
        return converter;
    }

}
