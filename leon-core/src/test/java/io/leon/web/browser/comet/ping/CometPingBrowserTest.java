package io.leon.web.browser.comet.ping;

import io.leon.tests.browser.CometCallsMark;
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

        CometCallsMark mark = leon.createCometCallsMark();
        leon.findElementById("sendPing").click();
        mark.waitForCalls(10);

        String pingResult = leon.findElementById("result").getText().replace("\n", "");
        Assert.assertEquals(pingResult, "10987654321");
    }

    public void testSimultaneousPingServiceRequests() throws InterruptedException {
        leon.openPage(getClass(), "/");

        CometCallsMark mark = leon.createCometCallsMark();
        leon.findElementById("sendPing").click();
        leon.findElementById("sendPing").click();
        leon.findElementById("sendPing").click();
        mark.waitForCalls(30);

        String pingResult = leon.findElementById("result").getText().replace("\n", "");
        Assert.assertTrue(pingResult.matches(".*10.*10.*10.*"));
        Assert.assertTrue(pingResult.matches(".*9.*9.*9.*"));
        Assert.assertTrue(pingResult.matches(".*8.*8.*8.*"));
        Assert.assertTrue(pingResult.matches(".*7.*7.*7.*"));
        Assert.assertTrue(pingResult.matches(".*6.*6.*6.*"));
        Assert.assertTrue(pingResult.matches(".*5.*5.*5.*"));
        Assert.assertTrue(pingResult.matches(".*4.*4.*4.*"));
        Assert.assertTrue(pingResult.matches(".*3.*3.*3.*"));
        Assert.assertTrue(pingResult.matches(".*2.*2.*2.*"));
        Assert.assertTrue(pingResult.matches(".*1.*1.*1.*"));
    }

}
