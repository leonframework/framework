package io.leon.rt;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Test
public class NodeTest {

    private RelaxedTypes rt;

    @BeforeTest
    public void beforeTest() {
        rt = new RelaxedTypes();
    }

    public void val() {
        assertEquals(rt.node("test").val(), "test");
    }

    public void keepTypeWhenPossible() {
        String in = "test";
        String out = rt.node(in).val();
        assertTrue(in.equals(out), "Dummy test to ensure that the lines above compile.");
    }

}
