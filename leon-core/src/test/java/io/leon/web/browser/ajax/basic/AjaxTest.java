package io.leon.web.browser.ajax.basic;

import io.leon.tests.browser.LeonBrowserTester;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = {"nodefault"})
public class AjaxTest {

    private LeonBrowserTester leon;

    @BeforeClass
    public void beforeClass() throws Exception {
        leon = new LeonBrowserTester(AjaxTestModule.class);
        leon.start();
    }

    @AfterClass
    public void afterClass() throws Exception {
        leon.stop();
    }

    public void testJava() throws InterruptedException {
        leon.openPage(getClass(), "java.html");
        leon.findElementById("method1").click();
        leon.waitForExpectedValue("result", "java", 5);
    }

    public void testJavaScript() throws InterruptedException {
        leon.openPage(getClass(), "javascript.html");
        leon.findElementById("method1").click();
        leon.waitForExpectedValue("result", "javascript", 5);
    }

}
