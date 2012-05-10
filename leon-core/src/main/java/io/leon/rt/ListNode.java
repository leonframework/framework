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

    @SuppressWarnings("unchecked")
    protected ListNode(RelaxedTypes rt, Object listLike) {
        this.rt = rt;
        this.list = rt.getConverter().convert(listLike.getClass(), List.class, listLike).get();
    }

    public Node<E> get(int index) {
        return rt.node(list.get(index));
    }

    @SuppressWarnings("unchecked")
    public <A> List<A> map(Function<? super E, ? extends A> function) {
        List<A> newCollection;
        try {
            Constructor<? extends List> constructor = list.getClass().getConstructor();
            newCollection = constructor.newInstance();
        } catch (Exception e) {
            newCollection = Lists.newArrayListWithCapacity(list.size());
        }
        for (E e : list) {
            newCollection.add(function.apply(e));
        }
        return newCollection;
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
