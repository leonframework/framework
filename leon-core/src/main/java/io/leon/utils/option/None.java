package io.leon.utils.option;

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

    @Override
    public A get() {
        return null;
    }

    @Override
    public A getOrElse(A elseObject) {
        return elseObject;
    }

    @Override
    public A getOrThrowException() {
        getOrThrowException("Can not get the value of a None()");
        return null;
    }

    @Override
    public A getOrThrowException(String message) {
        throw new RuntimeException(message);
    }

    @Override
    public boolean equals(Object o) {
        return this == o || !(o == null || getClass() != o.getClass());
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return "None()";
    }
}
