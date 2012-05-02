package io.leon.web.browser.tagrewriters;

import io.leon.LeonAppMainModule;

public class TagRewritersTestModule extends LeonAppMainModule {

    @Override
    public void config() {
        exposeUrl(".*");
    }

}
