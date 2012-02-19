package io.leon.samples.ajax.reverser.javs_js.test;

import io.leon.samples.ajax.reverser.java_js.Config;
import io.leon.tests.browser.LeonBrowserTester;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class ReverserBrowserTest {

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
    public void testReverseText() throws InterruptedException {
        LEON.openPage("/");
        LEON.setTextForElementWithName("text", "abc");
        LEON.setOffForElementWithName("toUpperCase");

        AjaxCallsMark mark = LEON.createAjaxCallsMark();
        LEON.findElementById("reverse").click();
        mark.waitForCalls(1);
        Assert.assertEquals("cba", LEON.findElementById("text_reversed").getText());
    }

    @Test
    public void testReverseTextUppercase() throws InterruptedException {
        LEON.openPage("/");
        LEON.setTextForElementWithName("text", "abc");
        LEON.setOnForElementWithName("toUpperCase");

        AjaxCallsMark mark = LEON.createAjaxCallsMark();
        LEON.findElementById("reverse").click();
        mark.waitForCalls(1);
        Assert.assertEquals("CBA", LEON.findElementById("text_reversed").getText());
    }

}
