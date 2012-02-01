package io.leon.tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AjaxCallsMark {

    private LeonBrowserTest leonBrowserTest;
    private int ajaxCallsCount;

    public AjaxCallsMark(LeonBrowserTest leonBrowserTest) {
        this.leonBrowserTest = leonBrowserTest;
        this.ajaxCallsCount = leonBrowserTest.getAjaxCallsCount();
    }

    public WebDriver waitForAjaxCalls(int numberOfAjaxCalls) {
        long startTime = System.currentTimeMillis();
        while ((ajaxCallsCount + numberOfAjaxCalls) > leonBrowserTest.getAjaxCallsCount()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if ((startTime + 10000) < System.currentTimeMillis()) {
                throw new RuntimeException("Timeout while waiting for AJAX call results.");
            }
        }

        final WebDriver[] handle = new WebDriver[1];
        (new WebDriverWait(leonBrowserTest.getWebDriver(), 10)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                handle[0] = d;
                return true;
            }
        });
        return handle[0];
    }
}
