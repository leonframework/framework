package io.leon.rt.option;

import java.util.Iterator;

public class None<A> extends Option<A> {

    private final Iterator<A> iterator = new Iterator<A>() {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public A next() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    };


    @Override
    public Iterator<A> iterator() {
        return iterator;
    }

    @Override
    public boolean isDefined() {
        return false;
    }
}
