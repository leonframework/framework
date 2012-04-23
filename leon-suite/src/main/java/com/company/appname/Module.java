package com.company.appname;

import io.leon.LeonAppMainModule;

public class Module extends LeonAppMainModule {

    @Override
    protected void config() {
        exposeJavaService("/demoService", DemoService.class);
    }

}
