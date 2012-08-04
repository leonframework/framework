package io.leon.utils.option;

import java.util.concurrent.Callable;

/**
 * Option type.
 *
 * See http://en.wikipedia.org/wiki/Option_type for some background information.
 *
 * @param <A> The type of the wrapped value
 * @author Roman Roelofsen
 */
public abstract class Option<A> implements Iterable<A> {

    /**
     * @return Returns an instance of None().
     */
    public static <B> Option<B> none() {
        return new None<B>();
    }

    /**
     * @param object The value to wrap
     *
     * @return Some(object) if object is not null, None() if object is null.
     */
    public static <B> Option<B> some(B object) {
        if (object == null) {
            return none();
        } else {
            return new Some<B>(object);
        }
    }

    /**
     * @param callable The callable which result should be wrapped in Some(...).
     *
     * @return The result or calling the callable, wrapped in Some(). Returns None() if callable threw an
     * exception.
     */
    public static <B> Option<B> someApply(Callable<B> callable) {
        try {
            return some(callable.call());
        } catch (Exception e) {
            return none();
        }
    }

    /**
     * @return true, if this option is a Some(...), false, if this option is a None().
     */
    public abstract boolean isDefined();

    /**
     * @return The value wrapped by this option. Returns null if this option is a None().
     */
    public abstract A get();

    /**
     * @param elseObject The value to return if this option is a None():
     *
     * @return If this option is a Some(), this method returns the wrapped value.
     * If this option is a None(), this method return the elseObject.
     */
    public abstract A getOrElse(A elseObject);

    /**
     * @return If this option is a Some(), this method returns the wrapped value.
     * If this option is a None(), this method throws a RuntimeException.
     */
    public abstract A getOrThrowException();


    /**
     * @param message The message used for the RuntimeException
     *
     * @return If this option is a Some(), this method returns the wrapped value.
     * If this option is a None(), this method throws a RuntimeException with the specified message.
     */
    public abstract A getOrThrowException(String message);


}
