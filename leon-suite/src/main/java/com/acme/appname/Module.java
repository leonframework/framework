package com.acme.appname;

import io.leon.LeonAppMainModule;

public class Module extends LeonAppMainModule {

    @Override
    protected void config() {
        setApplicationName("AcmeAppName");
        exposeJavaService("/demoService", DemoService.class);
    }

}
