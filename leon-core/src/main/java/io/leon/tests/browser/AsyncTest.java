package io.leon.tests.browser;

import org.openqa.selenium.WebDriver;

public interface AsyncTest {

    public void init();

    public void callback(WebDriver webDriver);

}
