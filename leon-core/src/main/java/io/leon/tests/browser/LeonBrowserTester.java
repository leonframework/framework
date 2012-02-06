package io.leon.tests.browser;

import io.leon.AbstractLeonConfiguration;
import io.leon.web.LeonFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;

public class LeonBrowserTester {

    private final Class<? extends AbstractLeonConfiguration> config;

    private ServerSocket lockSocket;

    private Server server;

    private WebDriver webDriver;

    private int httpPort = 8090;

    private int lockPort = 8091;

    public LeonBrowserTester(Class<? extends AbstractLeonConfiguration> config) {
        this.config = config;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    public int getLockPort() {
        return lockPort;
    }

    public void setLockPort(int lockPort) {
        this.lockPort = lockPort;
    }

    public void start() {
        try {
            lockSocket = new ServerSocket();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int numberOfFails = 0;
        while (!lockSocket.isBound()) {
            try {
                lockSocket.bind(new InetSocketAddress(lockPort));
            } catch (Exception e) {
                numberOfFails++;
                if (numberOfFails >= 500) { // every 5 seconds
                    numberOfFails = 0;
                    System.out.println("Could not bind lock socket for test synchronisation ("
                            + e.getClass().getName() + ":" + e.getMessage()
                            + "). Waiting...");
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e1) {
                    throw new RuntimeException(e1);
                }
            }
        }

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
                context.setContextPath("/");
                server.setHandler(context);

                FilterHolder filterHolder = new FilterHolder(new LeonFilter());
                filterHolder.setInitParameter("module", config.getName());
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
        lockSocket.close();
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }

    public void openPage(String url) {
        // Possible Selenium bug: Opening e.g. http://localhost:8080// causes a RuntimeException
        String _url = url == "/" ? "" : url;
        webDriver.get("http://localhost:" + httpPort + "/" + _url);
    }

    public int getAjaxCallsCount() {
        JavascriptExecutor jse = (JavascriptExecutor) webDriver;
        return Integer.parseInt(jse.executeScript("return leon.getAjaxCallsCount()").toString());
    }

    public int getCometCallsCount() {
        JavascriptExecutor jse = (JavascriptExecutor) webDriver;
        return Integer.parseInt(jse.executeScript("return leon.comet.getCometCallsCount()").toString());
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

    public AjaxCallsMark createAjaxCallsMark() {
        return new AjaxCallsMark(this);
    }

    public CometCallsMark createCometCallsMark() {
        return new CometCallsMark(this);
    }

}
