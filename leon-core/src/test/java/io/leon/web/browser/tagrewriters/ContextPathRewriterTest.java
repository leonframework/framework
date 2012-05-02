package io.leon.web.browser.tagrewriters;

import io.leon.tests.browser.LeonBrowserTester;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Test(singleThreaded = true, groups = {"nodefault"})
public class ContextPathRewriterTest {

    private String baseUrl = "/" + getClass().getPackage().getName().replace('.', '/');

    private String getPortAndContextUrl(LeonBrowserTester leon) {
        return ":" + leon.getHttpPort() + leon.getContextPath();
    }

    private void compareStrings(String shouldEndWith, String toCompare) {
        toCompare = toCompare.substring(toCompare.length() - shouldEndWith.length());
        assertEquals(toCompare, shouldEndWith);
    }

    public void testRootContextPath() throws Exception {
        LeonBrowserTester leon = new LeonBrowserTester(new TagRewritersTestModule());
        try {
            leon.setContextPath("");
            leon.start();
            leon.openPage(getClass(), "severalTags.html");

            compareStrings(getPortAndContextUrl(leon) + baseUrl + "/url", leon.findElementById("a1").getAttribute("href"));
            compareStrings(getPortAndContextUrl(leon) + "/url", leon.findElementById("a2").getAttribute("href"));

            compareStrings(getPortAndContextUrl(leon) + baseUrl + "/url", leon.findElementById("script1").getAttribute("src"));
            compareStrings(getPortAndContextUrl(leon) + "/url", leon.findElementById("script2").getAttribute("src"));
        } finally {
            leon.stop();
        }
    }

    public void testDummyContextPath() throws Exception {
        LeonBrowserTester leon = new LeonBrowserTester(new TagRewritersTestModule());
        try {
            leon.setContextPath("/dummy");
            leon.start();
            leon.openPage(getClass(), "severalTags.html");

            compareStrings(getPortAndContextUrl(leon) + baseUrl + "/url", leon.findElementById("a1").getAttribute("href"));
            compareStrings(getPortAndContextUrl(leon) + "/url", leon.findElementById("a2").getAttribute("href"));

            compareStrings(getPortAndContextUrl(leon) + baseUrl + "/url", leon.findElementById("script1").getAttribute("src"));
            compareStrings(getPortAndContextUrl(leon) + "/url", leon.findElementById("script2").getAttribute("src"));
        } finally {
            leon.stop();
        }
    }


}
