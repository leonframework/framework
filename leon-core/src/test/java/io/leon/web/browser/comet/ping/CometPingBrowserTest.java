package io.leon.web.browser.comet.ping;

import io.leon.tests.browser.LeonBrowserTester;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "nodefault")
public class CometPingBrowserTest {

    private static LeonBrowserTester leon;

    @BeforeClass
    public void beforeClass() throws Exception {
        leon = new LeonBrowserTester(CometPingModule.class);
        leon.start();
    }

    @AfterClass
    public void afterClass() throws Exception {
        leon.stop();
    }

    public void testPingService() throws InterruptedException {
        leon.openPage(getClass(), "/");

        //Thread.sleep(100000000);

        leon.findElementById("sendPing").click();

        leon.waitForExpectedValue("isDone", "true", 5);

        String pingResult = leon.findElementById("result").getText();
        Assert.assertEquals(pingResult, "12345678910");
    }

}
