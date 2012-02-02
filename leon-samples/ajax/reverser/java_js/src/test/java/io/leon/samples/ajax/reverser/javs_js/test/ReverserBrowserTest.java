package io.leon.samples.ajax.reverser.javs_js.test;

import io.leon.samples.ajax.reverser.java_js.Config;
import io.leon.tests.AjaxCallsMark;
import io.leon.tests.AsyncTest;
import io.leon.tests.LeonBrowserTester;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

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

        LEON.doAjaxTest(1, new AsyncTest() {
            @Override
            public void init() {
                LEON.findElementById("reverse").click();
            }

            @Override
            public void callback(WebDriver webDriver) {
                Assert.assertEquals("cba", webDriver.findElement(By.id("text_reversed")).getText());
            }
        });
    }

    @Test
    public void testReverseTextUppercase() throws InterruptedException {
        LEON.openPage("/");
        LEON.setTextForElementWithName("text", "abc");
        LEON.setOnForElementWithName("toUpperCase");

        AjaxCallsMark mark = LEON.createAjaxCallsMark();
        LEON.findElementById("reverse").click();
        mark.waitForAjaxCalls(1);
        Assert.assertEquals("CBA", LEON.findElementById("text_reversed").getText());
    }

}
