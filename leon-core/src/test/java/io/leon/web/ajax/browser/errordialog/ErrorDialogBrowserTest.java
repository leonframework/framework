package io.leon.web.ajax.browser.errordialog;

import io.leon.tests.browser.LeonBrowserTester;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class ErrorDialogBrowserTest {

    private static LeonBrowserTester LEON;

    @BeforeClass
    public static void init() throws Exception {
        LEON = new LeonBrowserTester(Config.class);
        LEON.start();
    }

    @AfterClass
    public static void destroy() throws Exception {
        LEON.stop();
    }

    @Test
    public void testReverseText() throws InterruptedException {
        LEON.openPage(Config.class.getPackage().getName().replace('.', '/') + "/index.html");

        /*
        LEON.setTextForElementWithName("text", "abc");
        LEON.setOffForElementWithName("toUpperCase");

        AjaxCallsMark mark = LEON.createAjaxCallsMark();
        LEON.findElementById("reverse").click();
        mark.waitForCalls(1);
        Assert.assertEquals("cba", LEON.findElementById("text_reversed").getText());
        */

    }

}
