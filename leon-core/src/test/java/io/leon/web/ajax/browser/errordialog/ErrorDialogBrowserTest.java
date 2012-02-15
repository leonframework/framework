package io.leon.web.ajax.browser.errordialog;

import io.leon.tests.browser.AjaxCallsMark;
import io.leon.tests.browser.LeonBrowserTester;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "nodefault")
public class ErrorDialogBrowserTest {

    private static LeonBrowserTester LEON;

    @BeforeClass
    public void beforeClass() throws Exception {
        LEON = new LeonBrowserTester(Config.class);
        LEON.start();
    }

    @AfterClass
    public void afterClass() throws Exception {
        LEON.stop();
    }

    public void testWithoutError() throws InterruptedException {
        LEON.openPage(Config.class.getPackage().getName().replace('.', '/') + "/index.html");

        AjaxCallsMark mark = LEON.createAjaxCallsMark();
        LEON.findElementById("withoutError").click();
        mark.waitForCalls(1);
        Assert.assertEquals("throwError==false", LEON.findElementById("result").getText());
    }

    public void testWithError() throws InterruptedException {
        LEON.openPage(Config.class.getPackage().getName().replace('.', '/') + "/index.html");

        AjaxCallsMark mark = LEON.createAjaxCallsMark();
        LEON.findElementById("withError").click();
        mark.waitForCalls(1);

        // TODO improve test
        Assert.assertEquals("", LEON.findElementById("result").getText());
    }

}
