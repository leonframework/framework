package io.leon.tests;

import io.leon.AbstractLeonConfiguration;
import io.leon.web.LeonFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LeonBrowserTestUtil {

    private final Class<? extends AbstractLeonConfiguration> config;

    private Server server;

    private WebDriver webDriver;

    private int port;

    public LeonBrowserTestUtil(Class<? extends AbstractLeonConfiguration> config) {
        this(config, 51234);
    }

    public LeonBrowserTestUtil(Class<? extends AbstractLeonConfiguration> config, int port) {
        this.config = config;
        this.port = port;
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
                server = new Server(port);
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
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }

    public void openPage(String url) {
        // Possible Selenium bug: Opening e.g. http://localhost:8080// causes a RuntimeException
        String _url = url == "/" ? "" : url;
        webDriver.get("http://localhost:" + port + "/" + _url);
    }

    public int getAjaxCallsCount() {
        JavascriptExecutor jse = (JavascriptExecutor) webDriver;
        return Integer.parseInt(jse.executeScript("return leon.getAjaxCallsCount()").toString());
    }

    public WebElement findElementById(String id) {
        return webDriver.findElement(By.id(id));
    }

    public WebElement findElementByName(String name) {
        return webDriver.findElement(By.name(name));
    }

    public AjaxCallsMark createAjaxCallsMark() {
        return new AjaxCallsMark(this);
    }

    public void doAjaxTest(int requiredAjaxOperations, final AsyncTest asyncTest) {
        int startCount = getAjaxCallsCount();
        long startTime = System.currentTimeMillis();
        asyncTest.init();
        while ((startCount + requiredAjaxOperations) > getAjaxCallsCount()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if ((startTime + 10000) < System.currentTimeMillis()) {
                throw new RuntimeException("Timeout while waiting for AJAX call results.");
            }
        }

        (new WebDriverWait(webDriver, 10)).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver d) {
                asyncTest.callback(d);
                return true;
            }
        });
    }


}
