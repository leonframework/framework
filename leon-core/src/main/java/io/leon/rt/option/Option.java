package io.leon.rt.option;

import java.util.concurrent.Callable;

public abstract class Option<A> implements Iterable<A> {

    public static <B> Option<B> none() {
        return new None<B>();
    }

    public static <B> Option<B> some(B object) {
        if (object == null) {
            return none();
        } else {
            return new Some<B>(object);
        }
    }

    public static <B> Option<B> someApply(Callable<B> callable) {
        try {
            return some(callable.call());
        } catch (Exception e) {
            return none();
        }
    }

    public abstract boolean isDefined();

    public abstract A get();

    public abstract A getOrElse(A elseObject);

    public abstract A getOrThrowException();

    public abstract A getOrThrowException(String message);


}
