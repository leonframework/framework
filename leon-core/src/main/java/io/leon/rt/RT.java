package io.leon.rt;

import io.leon.rt.option.Option;

import java.util.List;
import java.util.Map;

public class RT {

    private final Object node;

    public static RT of(Object node) {
        return new RT(node);
    }

    private RT(Object node) {
        this.node = node;
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

    public RT get(String key) {
        Map map = asInstanceOf(Map.class);
        return RT.of(map.get(key));
    }

    public RT get(int index) {
        // RR: IDEA wrongly reports an error here
        //noinspection LoopStatementThatDoesntLoop
        for (List list : asInstanceOfOption(List.class)) {
            return RT.of(list.get(index));
        }
        throw new UnsupportedOperationException("This node can not accessed as a List/Set/etc.");
    }
}
