package io.leon.tests.browser;

import io.leon.LeonAppMainModule;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FirefoxLeonBrowserTester extends LeonBrowserTester {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private WebDriver webDriver;

    public FirefoxLeonBrowserTester(LeonAppMainModule module) {
        setModule(module);
    }

    @Override
    void startBrowser() {
        webDriver = new FirefoxDriver();
        webDriver.get("about:blank");
    }

    @Override
    void stopBrowser() {
        webDriver.quit();
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }

    @Override
    public String getAttributeValueOfElementById(String id, String attribute) {
        return findElementById(id).getAttribute(attribute);
    }

    @Override
    public void openPage(String url) {
        String uri = "http://localhost:" + getHttpPort() + getContextPath() + "/" + url;
        logger.info("Opening URL '{}'", uri);
        webDriver.get(uri);
    }

    @Override
    public String getHtml() {
        return webDriver.getPageSource();
    }

    public WebElement findElementById(String id) {
        return webDriver.findElement(By.id(id));
    }

    public WebElement findElementByName(String name) {
        return webDriver.findElement(By.name(name));
    }

    @Override
    public void setTextForElementWithId(String id, String text) {
        WebElement e = findElementById(id);
        e.clear();
        e.sendKeys(text);
    }

    @Override
    public void setTextForElementWithName(String name, String text) {
        WebElement e = findElementByName(name);
        e.clear();
        e.sendKeys(text);
    }

    public void setOnForElement(WebElement element) {
        if (!element.isSelected()) {
            element.click();
        }
    }

    public void setOffForElement(WebElement element) {
        if (element.isSelected()) {
            element.click();
        }
    }

    @Override
    public void setOnForElementWithId(String id) {
        setOnForElement(findElementById(id));
    }

    @Override
    public void setOffForElementWithId(String id) {
        setOffForElement(findElementById(id));
    }

    @Override
    public void setOnForElementWithName(String name) {
        setOnForElement(findElementByName(name));
    }

    @Override
    public void setOffForElementWithName(String name) {
        setOffForElement(findElementByName(name));
    }

    public void waitForHtmlValue(String id, String expectedValue) {
        waitForHtmlValue(id, expectedValue, 5);
    }

    public void waitForHtmlValue(String id, String expectedValue, int timeOutSeconds) {
        long startTime = System.currentTimeMillis();
        while (!findElementById(id).getText().equals(expectedValue)) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if ((startTime + (timeOutSeconds * 1000)) < System.currentTimeMillis()) {
                throw new RuntimeException("Timeout while waiting for the value ["
                        + expectedValue
                        + "] in element ["
                        + id
                        + "]");
            }
        }
    }

}
