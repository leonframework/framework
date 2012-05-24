package io.leon.rt;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.lang.reflect.Constructor;
import java.util.List;

public class ListNode<E> {

    private final RelaxedTypes rt;
    private final List<E> list;

    protected ListNode(RelaxedTypes rt, List<E> list) {
        this.rt = rt;
        this.list = list;
    }

    public List<E> val() {
        return list;
    }

    public Node<E> get(int index) {
        return rt.node(list.get(index));
    }

    @SuppressWarnings("unchecked")
    public <A> List<A> map(Function<? super E, ? extends A> function) {
        List<A> newList;
        try {
            Constructor<? extends List> constructor = list.getClass().getConstructor();
            newList = constructor.newInstance();
        } catch (Exception e) {
            newList = Lists.newArrayListWithCapacity(list.size());
        }
        for (E e : list) {
            newList.add(function.apply(e));
        }
        return newList;
    }

    public <A> ListNode<A> asListOf(final Class<A> elementType) {
        return rt.listNode(map(new Function<E, A>() {
            @Override
            public A apply(E input) {
                return rt.getConverter().convert(input.getClass(), elementType, input).getOrThrowException();
            }
        }));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ListNode listNode = (ListNode) o;
        return list.equals(listNode.list) && rt.equals(listNode.rt);

    }

    @Override
    public int hashCode() {
        int result = rt.hashCode();
        result = 31 * result + list.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ListNode(" + list + ")";
    }
}
