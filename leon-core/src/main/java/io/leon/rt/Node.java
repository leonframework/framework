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

    @SuppressWarnings("unchecked")
    public ListNode<Object> toList() {
        return rt.listNode(rt.getConverter().convert(element.getClass(), List.class, element).get());
    }

    @SuppressWarnings("unchecked")
    public MapNode<Object, Object> toMap() {
        return rt.mapNode(rt.getConverter().convert(element.getClass(), Map.class, element).get());
    }

    public String valString() {
        return rt.getConverter().convert(element.getClass(), String.class, element).getOrThrowException(
                this + " could not be coverted to a String.");
    }

    public int valInt() {
        return rt.getConverter().convert(element.getClass(), Integer.class, element).getOrThrowException(
                this + " could not be coverted to an Integer.");
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
