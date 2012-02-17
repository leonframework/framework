package io.leon.web.browser.ajax.basicjs;

import io.leon.tests.browser.LeonBrowserTester;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "nodefault")
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

    public void method1() throws InterruptedException {
        leon.openPage(getClass(), "index.html");
        leon.findElementById("method1").click();
        leon.waitForExpectedValue("result", "method1", 5);
    }

}
