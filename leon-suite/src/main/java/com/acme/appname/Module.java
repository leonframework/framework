package com.acme.appname;

import io.leon.LeonAppMainModule;

public class Module extends LeonAppMainModule {

    @Override
    protected void config() {
        setApplicationName("AcmeAppName");

        bind(ServiceA.class).asEagerSingleton();
        bind(ServiceB.class).asEagerSingleton();

        loadFile("index.server.js");
        exposeJavaScript("/indexService", "indexService");
    }

}
