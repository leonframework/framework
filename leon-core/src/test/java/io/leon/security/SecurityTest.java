package io.leon.security;

import io.leon.tests.browser.FirefoxLeonBrowserTester;
import io.leon.tests.browser.LeonBrowserTester;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

@Test
public class SecurityTest {

    private LeonBrowserTester tester;

    @BeforeTest
    public void beforeTest() {
        tester = new FirefoxLeonBrowserTester(new SecurityTestModule());
        tester.start();
    }

    @AfterTest()
    public void afterTest() {
        tester.stop();

    }

    public void val1() {
    }

    public void val2() {
    }

}
