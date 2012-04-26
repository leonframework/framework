package io.leon.rt.option;

public abstract class Option<A> implements Iterable<A> {

    public static <B> Option<B> none() {
        return new None<B>();
    }

    public static <B> Option<B> some(B object) {
        return new Some<B>(object);
    }


}
