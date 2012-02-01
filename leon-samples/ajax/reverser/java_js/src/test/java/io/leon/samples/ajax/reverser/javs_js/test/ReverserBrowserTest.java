package io.leon.samples.ajax.reverser.javs_js.test;

import io.leon.samples.ajax.reverser.java_js.Module;
import io.leon.tests.LeonBrowserTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

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
        text.sendKeys("text");

        WebElement submit = LEON.findElementById("reverse");
        submit.click();

        (new WebDriverWait(LEON.getWebDriver(), 10)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                WebElement textReversed = d.findElement(By.id("text_reversed"));
                Assert.assertEquals("txet", textReversed.getText());
                return true;
            }
        });
    }

    @Test
    public void testReverseTextUppercase() throws InterruptedException {
        LEON.get("http://localhost:8080");
        WebElement text = LEON.findElementByName("text");
        text.clear();
        text.sendKeys("text");

        WebElement toUpperCase = LEON.findElementByName("toUpperCase");
        toUpperCase.click();

        WebElement submit = LEON.findElementById("reverse");
        submit.click();

        (new WebDriverWait(LEON.getWebDriver(), 10)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                WebElement textReversed = d.findElement(By.id("text_reversed"));
                Assert.assertEquals("TXET", textReversed.getText());
                return true;
            }
        });
    }

}
