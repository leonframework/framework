package io.leon.samples.cometping.java_js.test;

import io.leon.samples.cometping.java_js.Config;
import io.leon.tests.browser.CometCallsMark;
import io.leon.tests.browser.LeonBrowserTester;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class CometPingBrowserTest {

    private static LeonBrowserTester LEON;

    @BeforeClass
    public static void init() throws Exception {
        LEON = new LeonBrowserTester(Config.class);
        LEON.start();
    }

    @AfterClass
    public static void destroy() throws Exception {
        LEON.stop();
    }

    @Test
    public void testPingService() throws InterruptedException {
        LEON.openPage("/");

        CometCallsMark mark = LEON.createCometCallsMark();
        LEON.findElementById("sendPing").click();
        mark.waitForCalls(10);

        String pingResult = LEON.findElementById("result").getText().replace("\n", "");
        Assert.assertEquals("10987654321", pingResult);
    }

    @Test
    public void testSimultaneousPingServiceRequests() throws InterruptedException {
        LEON.openPage("/");

        CometCallsMark mark = LEON.createCometCallsMark();
        LEON.findElementById("sendPing").click();
        LEON.findElementById("sendPing").click();
        LEON.findElementById("sendPing").click();
        mark.waitForCalls(30);

        String pingResult = LEON.findElementById("result").getText().replace("\n", "");
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
