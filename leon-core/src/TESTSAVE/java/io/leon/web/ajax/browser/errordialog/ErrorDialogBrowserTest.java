package io.leon.web.ajax.browser.errordialog;

import io.leon.tests.browser.AjaxCallsMark;
import io.leon.tests.browser.LeonBrowserTester;
import org.junit.AfterClass;
import org.junit.Assert;
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
    public void testWithoutError() throws InterruptedException {
        LEON.openPage(Config.class.getPackage().getName().replace('.', '/') + "/index.html");

        AjaxCallsMark mark = LEON.createAjaxCallsMark();
        LEON.findElementById("withoutError").click();
        mark.waitForCalls(1);
        Assert.assertEquals("throwError==false", LEON.findElementById("result").getText());
    }

    @Test
    public void testWithError() throws InterruptedException {
        LEON.openPage(Config.class.getPackage().getName().replace('.', '/') + "/index.html");

        AjaxCallsMark mark = LEON.createAjaxCallsMark();
        LEON.findElementById("withError").click();
        mark.waitForCalls(1);

        // TODO improve test
        Assert.assertEquals("", LEON.findElementById("result").getText());
    }

}
