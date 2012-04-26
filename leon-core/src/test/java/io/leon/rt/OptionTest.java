package io.leon.rt;

import io.leon.rt.option.None;
import io.leon.rt.option.Option;
import io.leon.rt.option.Some;
import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class OptionTest {

    public void none() {
        boolean called = false;
        for (@SuppressWarnings("UnusedDeclaration") Object o : new None()) {
            called = true;
        }
        Assert.assertFalse(called, "Option.none() should not iterate.");
    }

    public void optionNone() {
        boolean called = false;
        for (@SuppressWarnings("UnusedDeclaration") Object o : Option.none()) {
            called = true;
        }
        Assert.assertFalse(called, "Option.none() should not iterate.");
    }

    public void some() {
        String val = "";
        for (String o : new Some<String>("abc")) {
            val = o;
        }
        Assert.assertEquals(val, "abc", "Option.some() should iterate.");
    }

    public void optionSome() {
        String val = "";
        for (String o : Option.some("abc")) {
            val = o;
        }
        Assert.assertEquals(val, "abc", "Option.some() should iterate.");
    }

}
