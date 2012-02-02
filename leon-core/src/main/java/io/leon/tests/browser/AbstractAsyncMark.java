package io.leon.tests.browser;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class AbstractAsyncMark {

    private LeonBrowserTester leonBrowserTester;

    private int countStart;

    public AbstractAsyncMark(LeonBrowserTester leonBrowserTester) {
        this.leonBrowserTester = leonBrowserTester;
        this.countStart = getCount();
    }

    public LeonBrowserTester getLeonBrowserTester() {
        return leonBrowserTester;
    }

    public WebDriver waitForCalls(int numberOfCalls) {
        return waitForCalls(numberOfCalls, 10000);
    }

    public WebDriver waitForCalls(int numberOfCalls, int timeOutMillis) {
        long startTime = System.currentTimeMillis();
        while ((countStart + numberOfCalls) > getCount()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if ((startTime + timeOutMillis) < System.currentTimeMillis()) {
                throw new RuntimeException("Timeout while waiting for async calls to complete.");
            }
        }

        final WebDriver[] handle = new WebDriver[1];
        (new WebDriverWait(leonBrowserTester.getWebDriver(), 10)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                handle[0] = d;
                return true;
            }
        });
        return handle[0];
    }

    abstract public int getCount();
}
