package io.leon.rt;

import com.google.inject.Inject;
import io.leon.rt.converters.Converter;

import java.util.List;
import java.util.Map;

public class RelaxedTypes {

    private final Converter converter;

    public RelaxedTypes() {
        converter = new Converter();
    }

    @Inject
    public RelaxedTypes(Converter converter) {
        this.converter = converter;
    }

    public <E> Node<E> node(E value) {
        return new Node<E>(this, value);
    }

    public <K, V> MapNode<K, V> mapNode(Map<K, V> map) {
        return new MapNode<K, V>(this, map);
    }

    //public MapNode<Object, Object> mapNode(Object mapLike) {
    //    return new MapNode<Object, Object>(this, mapLike);
    //}

    public <E> ListNode<E> listNode(List<E> list) {
        return new ListNode<E>(this, list);
    }

    public Converter getConverter() {
        return converter;
    }

}
