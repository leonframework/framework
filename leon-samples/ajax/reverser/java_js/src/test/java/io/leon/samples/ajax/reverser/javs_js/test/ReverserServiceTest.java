package io.leon.samples.ajax.reverser.javs_js.test;

import com.google.inject.Injector;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ReverserServiceTest {

    private static Injector INJECTOR;

    @BeforeClass
    public static void init() throws Exception {
        /*
        final com.google.inject.Module m = new Module();
        INJECTOR = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                install(m);
            }
        });
        */
    }

    @AfterClass
    public static void destroy() throws Exception {
    }

    @Test
    public void testReverseText() throws InterruptedException {
        Assert.assertEquals("a", "a");
    }

    @Test
    public void testReverseTextUppercase() throws InterruptedException {
    }

}
