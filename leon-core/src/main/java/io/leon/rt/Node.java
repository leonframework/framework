package io.leon.rt;

import io.leon.rt.option.Option;

import java.util.Collection;
import java.util.Map;

public class Node<E> extends CollectionNode<E> {

    private final E element;

    protected Node(RelaxedTypes rt, E element) {
        super(rt, null);
        this.element = element;
    }

    public E val() {
        return element;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<E> getCollection() {
        return (Collection<E>) getRt().getConverter().convert(element.getClass(), Collection.class, element).get();
    }

    public <A> A cast(Class<A> clazz) {
        for (A casted : castOption(clazz)) {
            return casted;
        }
        throw new IllegalArgumentException("Current element is not an instance of " + clazz.getName());
    }

    public <A> Option<A> castOption(Class<A> clazz) {
        if (clazz.isInstance(element)) {
            return Option.some(clazz.cast(element));
        } else {
            return Option.none();
        }
    }

    public String valString() {
        return getRt().getConverter().convert(element.getClass(), String.class, element).getOrThrowException(
                this + " could not be coverted to a String.");
    }

    public int valInt() {
        return getRt().getConverter().convert(element.getClass(), Integer.class, element).getOrThrowException(
                this + " could not be coverted to an Integer.");
    }

    public Node<?> get(String key) {
        Map map = cast(Map.class);
        return getRt().of(map.get(key));
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
