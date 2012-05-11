package io.leon.rt;

import java.util.List;
import java.util.Map;

public class Node<E> {

    private final RelaxedTypes rt;
    private final E element;

    protected Node(RelaxedTypes rt, E element) {
        this.rt = rt;
        this.element = element;
    }

    public E val() {
        return element;
    }

    public <A> A as(Class<A> targetType) {
       return rt.getConverter().convert(element.getClass(), targetType, element).get();
    }

    @SuppressWarnings("unchecked")
    public ListNode<Object> asList() {
        return rt.listNode(as(List.class));
    }

    public <A> ListNode<A> asList(Class<A> elementType) {
        return rt.listNode(as(List.class)).asListOf(elementType);
    }

    @SuppressWarnings("unchecked")
    public MapNode<Object, Object> asMap() {
        return rt.mapNode(as(Map.class));
    }

    public int asInt() {
        return as(Integer.class);
    }

    public String asString() {
        return as(String.class);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Node node = (Node) o;
        return !(element != null ? !element.equals(node.element) : node.element != null);
    }

    @Override
    public int hashCode() {
        return element != null ? element.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Node(" + element + ")";
    }
}
