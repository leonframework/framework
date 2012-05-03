package io.leon.web.browser.selenium.cockpit;

import io.leon.tests.browser.FirefoxLeonBrowserTester;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "nodefault")
public class BasicCockpitTest {

    private FirefoxLeonBrowserTester leon;

    @BeforeClass
    public void beforeClass() throws Exception {
        leon = new FirefoxLeonBrowserTester(new BasicCockpitTestModule());
        leon.start();
    }

    @AfterClass
    public void afterClass() throws Exception {
        leon.stop();
    }

    public void method1() throws InterruptedException {
        leon.openPage(getClass(), "index.html");

        //Thread.sleep(1000000000);
    }

}
