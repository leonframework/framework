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

    @Override
    public A get() {
        return element;
    }

    @Override
    public A getOrElse(A elseObject) {
        return get();
    }

    @Override
    public A getOrThrowException() {
        return getOrThrowException("");
    }

    @Override
    public A getOrThrowException(String message) {
        return get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Some other = (Some) o;
        return !(element != null ? !element.equals(other.element) : other.element != null);
    }

    @Override
    public int hashCode() {
        return element != null ? element.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Some(" + element + ")";
    }
}
