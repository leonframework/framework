package io.leon.rt.option;

import io.leon.utils.option.None;
import io.leon.utils.option.Option;
import io.leon.utils.option.Some;
import org.testng.annotations.Test;

import java.util.concurrent.Callable;

import static io.leon.utils.option.Option.none;
import static io.leon.utils.option.Option.some;
import static io.leon.utils.option.Option.someApply;
import static org.testng.Assert.*;

@Test
public class OptionTest {

    public void newNone() {
        boolean called = false;
        for (@SuppressWarnings("UnusedDeclaration") Object o : new None()) {
            called = true;
        }
        assertFalse(called, "Option.none() should not iterate.");
    }

    public void optionNone() {
        boolean called = false;
        for (@SuppressWarnings("UnusedDeclaration") Object o : none()) {
            called = true;
        }
        assertFalse(called, "Option.none() should not iterate.");
    }

    public void newSome() {
        String val = "";
        for (String o : new Some<String>("abc")) {
            val = o;
        }
        assertEquals(val, "abc", "Option.some() should iterate.");
    }

    public void optionSome() {
        String val = "";
        for (String o : some("abc")) {
            val = o;
        }
        assertEquals(val, "abc", "Option.some() should iterate.");
    }

    public void noneEquals() {
        assertEquals(none(), none());
    }

    public void someEquals() {
        assertEquals(some("abc"), some("abc"));
    }

    public void noneHashCode() {
        assertEquals(none().hashCode(), none().hashCode());
    }

    public void someHashCode() {
        assertEquals(some("abc").hashCode(), some("abc").hashCode());
    }

    public void optionSomeWithNull() {
        assertEquals(some(null), none());
    }

    public void optionSomeWithCallable() {
        Option<String> option = someApply(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "abc";
            }
        });
        assertEquals(option.get(), "abc");
    }

    public void optionSomeWithCallableWithException() {
        Option<String> option = someApply(new Callable<String>() {
            @Override
            public String call() throws Exception {
                throw new RuntimeException();
            }
        });
        assertEquals(option, none());
    }

    public void noneIsDefined() {
        assertFalse(none().isDefined());
    }

    public void someIsDefined() {
        assertTrue(some("abc").isDefined());
    }

    public void noneGet() {
        assertNull(none().get());
    }

    public void noneGetOrElse() {
        assertEquals(none().getOrElse("abc"), "abc");
    }

    public void someGetOrElse() {
        assertEquals(some("abc").getOrElse("def"), "abc");
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void noneGetOrThrowException() {
        none().getOrThrowException();
    }

    public void someGetOrThrowException() {
        assertEquals(some("abc").getOrThrowException(), "abc");
    }
}
