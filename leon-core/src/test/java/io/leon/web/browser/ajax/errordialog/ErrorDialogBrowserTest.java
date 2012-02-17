package io.leon.web.browser.ajax.errordialog;

import io.leon.tests.browser.AjaxCallsMark;
import io.leon.tests.browser.LeonBrowserTester;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "nodefault")
public class ErrorDialogBrowserTest {

    private LeonBrowserTester leon;

    @BeforeClass
    public void beforeClass() throws Exception {
        leon = new LeonBrowserTester(ErrorDialogModule.class);
        leon.start();
    }

    @AfterClass
    public void afterClass() throws Exception {
        leon.stop();
    }

    public void testWithoutError() throws InterruptedException {
        leon.openPage(getClass(), "index.html");

        AjaxCallsMark mark = leon.createAjaxCallsMark();
        leon.findElementById("withoutError").click();
        mark.waitForCalls(1);
        Assert.assertEquals("throwError==false", leon.findElementById("result").getText());
    }

    public void testWithError() throws InterruptedException {
        leon.openPage(getClass(), "index.html");

        AjaxCallsMark mark = leon.createAjaxCallsMark();
        leon.findElementById("withError").click();
        mark.waitForCalls(1);

        // TODO improve test
        Assert.assertEquals("", leon.findElementById("result").getText());
    }

}
