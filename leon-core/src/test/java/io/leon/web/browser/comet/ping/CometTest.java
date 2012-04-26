package io.leon.web.browser.comet.ping;

import io.leon.tests.browser.LeonBrowserTester;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test(groups = "nodefault")
public class CometTest {

    private static LeonBrowserTester leon;

    @BeforeClass
    public void beforeClass() throws Exception {
        leon = new LeonBrowserTester(new CometTestModule());
        leon.start();
    }

    @AfterClass
    public void afterClass() throws Exception {
        leon.stop();
    }

    public void testOneTopic() throws InterruptedException {
        leon.openPage(getClass(), "/ping.html");
        leon.waitForHtmlValue("status", "init");
        leon.getTopicSubscriptionsTester().waitForSubscription("ping");

        leon.findElementById("sendPing").click();
        leon.waitForHtmlValue("status", "pong");
    }

    public void testMultipleTopics() throws InterruptedException {
        leon.openPage(getClass(), "/multiPing.html");
        leon.waitForHtmlValue("init", "done");
        leon.getTopicSubscriptionsTester().waitForSubscription("numberPing");

        leon.findElementById("sendPing").click();
        leon.waitForHtmlValue("status1", "done");
        leon.waitForHtmlValue("status2", "done");
        leon.waitForHtmlValue("status3", "done");
        leon.waitForHtmlValue("status4", "done");
        leon.waitForHtmlValue("status5", "done");
    }

    public void testTopicWithFilters() throws InterruptedException {
        leon.openPage(getClass(), "/filterPing.html");
        leon.waitForHtmlValue("status", "init");
        leon.getTopicSubscriptionsTester().waitForSubscription("filterPing", "key1", "value1");
        leon.getTopicSubscriptionsTester().waitForSubscription("filterPing", "key2", "value2");
        leon.getTopicSubscriptionsTester().waitForSubscription("filterPing", "key3", "value3");
        leon.getTopicSubscriptionsTester().waitForSubscription("filterPing", "key4", "value4");
        leon.getTopicSubscriptionsTester().waitForSubscription("filterPing", "key5", "value5");

        leon.findElementById("start").click();

        final boolean[] t1ok = {false};
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                leon.waitForHtmlValue("status_key1", "value1");
                t1ok[0] = true;
            }
        });
        t1.start();

        final boolean[] t2ok = {false};
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                leon.waitForHtmlValue("status_key2", "value2");
                t2ok[0] = true;
            }
        });
        t2.start();

        final boolean[] t4ok = {false};
        Thread t4 = new Thread(new Runnable() {
            @Override
            public void run() {
                leon.waitForHtmlValue("status_key4", "value4");
                t4ok[0] = true;
            }
        });
        t4.start();

        t1.join();
        t2.join();
        t4.join();

        Assert.assertTrue(t1ok[0], "Status key1 should be true");
        Assert.assertTrue(t2ok[0], "Status key2 should be true");
        Assert.assertEquals(leon.findElementById("status_key3").getText(), "false", "Status key3 should be false");
        Assert.assertTrue(t4ok[0], "Status key4 should be true");
        Assert.assertEquals(leon.findElementById("status_key5").getText(), "false", "Status key5 should be false");
    }

}
