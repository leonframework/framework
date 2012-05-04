package io.leon.web.browser.tagrewriters;

import io.leon.tests.browser.HtmlUnitLeonBrowserTester;
import io.leon.tests.browser.LeonBrowserTester;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

@Test(singleThreaded = true, groups = {"nodefault"})
public class LeonIncludeRewriterTest {

    private LeonBrowserTester leon;

    public void leonIncludeWithRelativePaths() throws Exception {
        try {
            leon = new HtmlUnitLeonBrowserTester(new TagRewritersTestModule());
            leon.start();
            leon.openPage(getClass(), "snippetRoot.html");
            String html = leon.getHtml();
            assertTrue(html.contains("child1"));
            assertTrue(html.contains("child2"));
        } finally {
            leon.stop();
        }
    }

}
