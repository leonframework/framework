package io.leon.samples.ajax.reverser.javs_js.test;

import com.google.inject.Injector;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test
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

    public void testReverseText() throws InterruptedException {
        Assert.assertEquals("a", "a");
    }

    public void testReverseTextUppercase() throws InterruptedException {
    }

}
