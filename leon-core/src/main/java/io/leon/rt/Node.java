package io.leon.rt;

import com.google.common.collect.Lists;
import io.leon.rt.option.Option;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class Node<E> {

    private final Rt rt;
    private final E element;

    protected Node(Rt rt, E element) {
        this.rt = rt;
        this.element = element;
    }

    private <A> A asInstanceOf(Class<A> clazz) {
        if (!clazz.isInstance(element)) {
            throw new IllegalArgumentException("Current element is not an instance of " + clazz.getName());
        }
        return clazz.cast(element);
    }

    private <A> Option<A> asInstanceOfOption(Class<A> clazz) {
        if (clazz.isInstance(element)) {
            return Option.some(clazz.cast(element));
        } else {
            return Option.none();
        }
    }

    public E val() {
        return element;
    }

    public String valString() {
        return rt.getConverter().convert(element.getClass(), String.class, element).getOrThrowException(
                this + " could not be coverted to a String.");
    }

    public int valInt() {
        return rt.getConverter().convert(element.getClass(), Integer.class, element).getOrThrowException(
                this + " could not be coverted to an Integer.");
    }

    public Node<?> get(String key) {
        Map map = asInstanceOf(Map.class);
        return rt.of(map.get(key));
    }

    public Node<?> get(int index) {
        for (Iterator iter : asInstanceOfOption(Iterator.class)) {
            ArrayList<Object> list = Lists.newArrayList();
            while (iter.hasNext()) {
                list.add(iter.next());
            }
            return rt.of(list.get(index));
        }
        for (Iterable iterable : asInstanceOfOption(Iterable.class)) {
            ArrayList<Object> list = Lists.newArrayList();
            for (Object anIterable : iterable) {
                list.add(anIterable);
            }
            return rt.of(list.get(index));
        }
        throw new UnsupportedOperationException("This element can not be accessed as a Iterator or Iterable.");
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
        return "Element(" + element + ")";
    }
}
