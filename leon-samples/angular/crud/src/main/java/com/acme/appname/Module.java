package com.acme.appname;

import io.leon.LeonAppMainModule;
import io.leon.persistence.mongo.LeonMongoModule;

public class Module extends LeonAppMainModule {

    @Override
    protected void config() {
        
        setApplicationName("AcmeAppName");

        install(new LeonMongoModule());

        loadFile("index.server.js");
        exposeJavaScript("/indexService", "indexService");
    }

}
