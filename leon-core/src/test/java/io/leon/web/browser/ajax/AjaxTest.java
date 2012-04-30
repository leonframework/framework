package io.leon.web.browser.ajax;

import io.leon.tests.browser.LeonBrowserTester;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = {"nodefault"})
public class AjaxTest {

    private LeonBrowserTester leon;

    @BeforeClass
    public void beforeClass() throws Exception {
        leon = new LeonBrowserTester(new AjaxTestModule());
        leon.start();
    }

    @AfterClass
    public void afterClass() throws Exception {
        leon.stop();
    }

    public void testJava() throws InterruptedException {
        leon.openPage(getClass(), "java.html");
        leon.findElementById("method1").click();
        leon.waitForHtmlValue("result", "java");
    }

    public void testJavaScript() throws InterruptedException {
        leon.openPage(getClass(), "javascript.html");
        leon.findElementById("method1").click();
        leon.waitForHtmlValue("result", "javascript");
    }

}
