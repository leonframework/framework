package io.leon.samples.ajax.reverser.javs_js.test;

import io.leon.samples.ajax.reverser.java_js.Module;
import io.leon.tests.AjaxCallsMark;
import io.leon.tests.AsyncTest;
import io.leon.tests.LeonBrowserTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class ReverserBrowserTest {

    private static LeonBrowserTest LEON;

    @BeforeClass
    public static void init() throws Exception {
        LEON = new LeonBrowserTest(Module.class);
        LEON.start();
    }

    @AfterClass
    public static void destroy() throws Exception {
        LEON.stop();
    }

    @Test
    public void testReverseText() throws InterruptedException {
        LEON.get("http://localhost:8080");
        WebElement text = LEON.findElementByName("text");
        text.clear();
        text.sendKeys("abc");

        LEON.doAjaxTest(1, new AsyncTest() {
            @Override
            public void init() {
                WebElement submit = LEON.findElementById("reverse");
                submit.click();
            }
            @Override
            public void callback(WebDriver webDriver) {
                WebElement textReversed = webDriver.findElement(By.id("text_reversed"));
                Assert.assertEquals("cba", textReversed.getText());
            }
        });
    }

    @Test
    public void testReverseTextUppercase() throws InterruptedException {
        LEON.get("http://localhost:8080");
        WebElement text = LEON.findElementByName("text");
        text.clear();
        text.sendKeys("abc");

        WebElement toUpperCase = LEON.findElementByName("toUpperCase");
        toUpperCase.click();

        WebElement submit = LEON.findElementById("reverse");
        AjaxCallsMark mark = LEON.createAjaxCallsMark();
        submit.click();
        mark.waitForAjaxCalls(1);
        WebElement textReversed = LEON.findElementById("text_reversed");
        Assert.assertEquals("CBA", textReversed.getText());
    }

}
