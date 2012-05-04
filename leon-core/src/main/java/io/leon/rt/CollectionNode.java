package io.leon.rt;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;

public class CollectionNode<E> {

    private final Rt rt;
    private final Collection<E> collection;

    protected CollectionNode(Rt rt, Collection<E> collection) {
        this.rt = rt;
        this.collection = collection;
    }

    public Rt getRt() {
        return rt;
    }

    public Collection<E> getCollection() {
        return collection;
    }

    @SuppressWarnings("unchecked")
    public Node<E> get(int index) {
        List list = rt.getConverter().convert(getCollection().getClass(), List.class, getCollection())
                .getOrThrowException("This collection does not support random access.");

        return (Node<E>) getRt().of(list.get(index));
    }

    @SuppressWarnings("unchecked")
    public <A> Collection<A> map(Function<? super E, ? extends A> function) {
        Collection<A> newCollection;
        try {
            Constructor<? extends Collection> constructor = getCollection().getClass().getConstructor();
            newCollection = constructor.newInstance();
        } catch (Exception e) {
            newCollection = Lists.newArrayListWithCapacity(getCollection().size());
        }
        for (E e : getCollection()) {
            newCollection.add(function.apply(e));
        }
        return newCollection;
    }


    // TODO equals/hashCode

    @Override
    public String toString() {
        return "CollectionNode(" + getCollection() + ")";
    }
}
