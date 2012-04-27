package io.leon.web.browser.ajax.errordialog;

import io.leon.tests.browser.LeonBrowserTester;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "nodefault")
public class ErrorDialogBrowserTest {

    private LeonBrowserTester leon;

    @BeforeClass
    public void beforeClass() throws Exception {
        leon = new LeonBrowserTester(new ErrorDialogModule());
        leon.start();
    }

    @AfterClass
    public void afterClass() throws Exception {
        leon.stop();
    }

    public void testWithoutError() throws InterruptedException {
        leon.openPage(getClass(), "index.html");
        leon.findElementById("withoutError").click();
        leon.waitForHtmlValue("result", "throwError==false");
    }

    //public void testWithError() throws InterruptedException {
    //    leon.openPage(getClass(), "index.html");
    //    leon.findElementById("withError").click();
    //}

}
