package io.leon.web.browser.selenium.ajax.errordialog;

import io.leon.tests.browser.FirefoxLeonBrowserTester;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "nodefault")
public class ErrorDialogBrowserTest {

    private FirefoxLeonBrowserTester leon;

    @BeforeClass
    public void beforeClass() throws Exception {
        leon = new FirefoxLeonBrowserTester(new io.leon.web.browser.selenium.ajax.errordialog.ErrorDialogModule());
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
