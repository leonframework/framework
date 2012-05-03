package io.leon.tests.browser;

import com.google.inject.Module;
import io.leon.web.LeonFilter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.*;

public abstract class LeonBrowserTester {

    private Server server;

    private Module module;

    private LeonFilter leonFilter;

    private String contextPath = "";

    private int httpPort = 8090;

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
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
        Thread taskBrowser = new Thread(new Runnable() {
            public void run() {
                startBrowser();
            }
        });
        taskBrowser.start();

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
            taskBrowser.join();
            taskJetty.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() throws Exception {
        server.stop();
        stopBrowser();
    }

    public TopicSubscriptionsTester getTopicSubscriptionsTester() {
        return new TopicSubscriptionsTester(this);
    }

    public void openPage(Class<?> basePackage, String name) {
        openPage(basePackage.getPackage().getName().replace('.', '/') + "/" + name);
    }

    abstract void startBrowser();

    abstract void stopBrowser();

    public abstract void openPage(String url);

    public abstract String getAttributeValueOfElementById(String id, String attribute);

    public abstract void setTextForElementWithId(String id, String text);

    public abstract void setTextForElementWithName(String name, String text);

    public abstract void setOnForElementWithId(String id);

    public abstract void setOffForElementWithId(String id);

    public abstract void setOnForElementWithName(String name);

    public abstract void setOffForElementWithName(String name);

}
