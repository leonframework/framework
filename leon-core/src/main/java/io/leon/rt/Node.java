package io.leon.rt;

import com.google.common.collect.Lists;
import io.leon.rt.option.Option;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class Node {

    private final RT rt;
    private final Object node;

    protected Node(RT rt, Object node) {
        this.rt = rt;
        this.node = node;
    }

    private Node of(Object node) {
        return new Node(rt, node);
    }

    private <A> A asInstanceOf(Class<A> clazz) {
        if (!clazz.isInstance(node)) {
            throw new IllegalArgumentException("Current node is not an instance of " + clazz.getName());
        }
        return clazz.cast(node);
    }

    private <A> Option<A> asInstanceOfOption(Class<A> clazz) {
        if (clazz.isInstance(node)) {
            return Option.some(clazz.cast(node));
        } else {
            return Option.none();
        }
    }

    public Object val() {
        return node;
    }

    public String valString() {
        return String.valueOf(node);
    }

    public int valInt() {
        return Integer.parseInt(valString());
    }

    public Node get(String key) {
        Map map = asInstanceOf(Map.class);
        return of(map.get(key));
    }

    public Node get(int index) {
        for (Iterator iter : asInstanceOfOption(Iterator.class)) {
            ArrayList<Object> list = Lists.newArrayList();
            while (iter.hasNext()) {
                list.add(iter.next());
            }
            return of(list.get(index));
        }
        for (Iterable iterable : asInstanceOfOption(Iterable.class)) {
            ArrayList<Object> list = Lists.newArrayList();
            for (Object anIterable : iterable) {
                list.add(anIterable);
            }
            return of(list.get(index));
        }
        throw new UnsupportedOperationException("This node can not be accessed as a Iterator or Iterable.");
    }
}
