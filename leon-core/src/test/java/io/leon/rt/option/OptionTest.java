package io.leon.rt.option;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Test
public class OptionTest {

    public void none() {
        boolean called = false;
        for (@SuppressWarnings("UnusedDeclaration") Object o : new None()) {
            called = true;
        }
        assertFalse(called, "Option.none() should not iterate.");
    }

    public void optionNone() {
        boolean called = false;
        for (@SuppressWarnings("UnusedDeclaration") Object o : Option.none()) {
            called = true;
        }
        assertFalse(called, "Option.none() should not iterate.");
    }

    public void some() {
        String val = "";
        for (String o : new Some<String>("abc")) {
            val = o;
        }
        assertEquals(val, "abc", "Option.some() should iterate.");
    }

    public void optionSome() {
        String val = "";
        for (String o : Option.some("abc")) {
            val = o;
        }
        assertEquals(val, "abc", "Option.some() should iterate.");
    }

    public void noneIsDefined() {
        assertFalse(Option.none().isDefined());
    }

    public void someIsDefined() {
        assertTrue(Option.some("abc").isDefined());
    }

}
