package io.leon.web.browser.tagrewriters;

import io.leon.tests.browser.HtmlUnitLeonBrowserTester;
import io.leon.tests.browser.LeonBrowserTester;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

@Test(singleThreaded = true, groups = {"nodefault"})
public class ContextPathRewriterTest {

    private void compareStrings(String shouldEndWith, String toCompare) {
        if (toCompare.length() <= shouldEndWith.length()) {
            assertEquals(toCompare, shouldEndWith);
        }
        toCompare = toCompare.substring(toCompare.length() - shouldEndWith.length());
        assertEquals(toCompare, shouldEndWith);
    }

    public void testDummyContextPath() throws Exception {
        LeonBrowserTester leon = new HtmlUnitLeonBrowserTester(new TagRewritersTestModule());
        try {
            leon.setContextPath("/dummy");
            leon.start();
            leon.openPage(getClass(), "contextPathRewriterTest.html");

            assertFalse(leon.getAttributeValueOfElementById("a1", "href").endsWith("dummy/url"), "Relative URLs must not be changed");
            compareStrings("/dummy/url", leon.getAttributeValueOfElementById("a2", "href"));
        } finally {
            leon.stop();
        }
    }

}
