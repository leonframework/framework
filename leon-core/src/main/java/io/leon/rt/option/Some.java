package io.leon.rt.option;

import java.util.Iterator;

public class Some<A> extends Option<A> {

    private final A element;

    private class SomeIterator implements Iterator<A> {

        private boolean done = false;

        @Override
        public boolean hasNext() {
            return !done;
        }

        @Override
        public A next() {
            done = true;
            return element;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public Some(A element) {
        this.element = element;
    }

    @Override
    public Iterator<A> iterator() {
        return new SomeIterator();
    }

    @Override
    public boolean isDefined() {
        return true;
    }
}
