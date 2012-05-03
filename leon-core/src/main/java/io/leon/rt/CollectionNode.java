package io.leon.rt;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.lang.reflect.Constructor;
import java.util.Collection;

public class CollectionNode<E> extends Node<Collection<E>> {

    protected CollectionNode(Rt rt, Collection<E> element) {
        super(rt, element);
    }

    @SuppressWarnings("unchecked")
    public <A> Collection<A> map(Function<? super E, ? extends A> function) {
        Collection<A> newCollection;
        try {
            Constructor<? extends Collection> constructor = val().getClass().getConstructor();
            newCollection = constructor.newInstance();
        } catch (Exception e) {
            newCollection = Lists.newArrayListWithCapacity(val().size());
        }
        for (E e : val()) {
            newCollection.add(function.apply(e));
        }
        return newCollection;
    }


    // TODO equals/hashCode

    @Override
    public String toString() {
        return "CollectionNode(" + val() + ")";
    }
}
