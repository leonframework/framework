package io.leon.tests.browser;

import com.google.inject.Module;
import io.leon.web.LeonFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class LeonBrowserTester {

    private final Module module;

    private Server server;

    private WebDriver webDriver;

    private int httpPort = 8090;

    private LeonFilter leonFilter;

    private String contextPath = "";

    public LeonBrowserTester(Module module) {
        this.module = module;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public String getContextPath() {
        return contextPath;
    }

    public void setContextPath(String contextPath) {
        this.contextPath = contextPath.equals("/") ? "" : contextPath;
    }

    public LeonFilter getLeonFilter() {
        return leonFilter;
    }

    public void start() {
        Thread taskFirefox = new Thread(new Runnable() {
            public void run() {
                webDriver = new FirefoxDriver();
            }
        });
        taskFirefox.start();

        Thread taskJetty = new Thread(new Runnable() {
            public void run() {
                server = new Server(httpPort);
                ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
                context.setContextPath(contextPath);
                server.setHandler(context);

                leonFilter = new LeonFilter(module);
                FilterHolder filterHolder = new FilterHolder(leonFilter);
                context.addFilter(filterHolder, "/*", FilterMapping.ALL);

                ServletHolder servletHolder = new ServletHolder(new DefaultServlet());
                context.addServlet(servletHolder, "/*");

                try {
                    server.start();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        taskJetty.start();

        try {
            taskFirefox.join();
            taskJetty.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() throws Exception {
        server.stop();
        webDriver.quit();
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }

    public void openPage(Class<?> basePackage, String name) {
        openPage(basePackage.getPackage().getName().replace('.', '/') + "/" + name);
    }

    public void openPage(String url) {
        // TODO this check may not be required anymore
        // // Possible Selenium bug: Opening e.g. http://localhost:8080// causes a RuntimeException
        // String _url = url.equals("/") ? "" : url;

        String _url = url;
        webDriver.get("http://localhost:" + httpPort + getContextPath() + "/" + _url);
    }

    public WebElement findElementById(String id) {
        return webDriver.findElement(By.id(id));
    }

    public WebElement findElementByName(String name) {
        return webDriver.findElement(By.name(name));
    }

    public void setTextForElementWithId(String id, String text) {
        WebElement e = findElementById(id);
        e.clear();
        e.sendKeys(text);
    }

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

    public void setOnForElementWithId(String id) {
        setOnForElement(findElementById(id));
    }

    public void setOffForElementWithId(String id) {
        setOffForElement(findElementById(id));
    }

    public void setOnForElementWithName(String name) {
        setOnForElement(findElementByName(name));
    }

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

    public TopicSubscriptionsTester getTopicSubscriptionsTester() {
        return new TopicSubscriptionsTester(this);
    }

}
