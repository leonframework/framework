package io.leon.tests.browser;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import io.leon.LeonAppMainModule;

import java.io.IOException;
import java.util.List;

public class HtmlUnitLeonBrowserTester extends LeonBrowserTester {

    private WebClient webClient;

    private HtmlPage currentPage;

    public HtmlUnitLeonBrowserTester(LeonAppMainModule module) {
        setModule(module);
    }

    @Override
    void startBrowser() {
        webClient = new WebClient();
    }

    @Override
    void stopBrowser() {
        webClient.closeAllWindows();
    }

    @Override
    public void openPage(String url) {
        try {
            currentPage = webClient.getPage("http://localhost:" + getHttpPort() + getContextPath() + "/" + url);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getHtml() {
        return currentPage.asText();
    }

    @Override
    public String getAttributeValueOfElementById(String id, String attribute) {
        return currentPage.getElementById(id).getAttribute(attribute);
    }

    @Override
    public void setTextForElementWithId(String id, String text) {
        currentPage.getElementById(id).setTextContent(text);
    }

    @Override
    public void setTextForElementWithName(String name, String text) {
        List<HtmlElement> elementsByName = currentPage.getElementsByName(name);
        elementsByName.get(0).setTextContent(text);
    }

    @Override
    public void setOnForElementWithId(String id) {
        throw new RuntimeException("Method not implemented!");
    }

    @Override
    public void setOffForElementWithId(String id) {
        throw new RuntimeException("Method not implemented!");
    }

    @Override
    public void setOnForElementWithName(String name) {
        throw new RuntimeException("Method not implemented!");
    }

    @Override
    public void setOffForElementWithName(String name) {
        throw new RuntimeException("Method not implemented!");
    }

}
