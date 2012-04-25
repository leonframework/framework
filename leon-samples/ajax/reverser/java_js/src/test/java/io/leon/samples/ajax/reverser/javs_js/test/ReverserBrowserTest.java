package io.leon.samples.ajax.reverser.javs_js.test;

import io.leon.samples.ajax.reverser.java_js.Config;
import io.leon.tests.browser.LeonBrowserTester;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Test
public class ReverserBrowserTest {

    private LeonBrowserTester leon;

    @BeforeClass
    public void init() throws Exception {
        leon = new LeonBrowserTester(Config.class);
        leon.start();
    }

    @AfterClass
    public void destroy() throws Exception {
        leon.stop();
    }

    public void testReverseText() throws InterruptedException {
        leon.openPage("/");
        leon.setTextForElementWithName("text", "abc");
        leon.setOffForElementWithName("toUpperCase");

        leon.findElementById("reverse").click();
        leon.waitForHtmlValue("text_reversed", "cba", 5);
    }

    public void testReverseTextUppercase() throws InterruptedException {
        leon.openPage("/");
        leon.setTextForElementWithName("text", "abc");
        leon.setOnForElementWithName("toUpperCase");

        leon.findElementById("reverse").click();
        leon.waitForHtmlValue("text_reversed", "CBA", 5);
    }

}
