package io.leon.dummyapp;

import io.leon.LeonAppMainModule;

public class Module extends LeonAppMainModule {

    @Override
    public void config() {
        loadFile("/io/leon/dummyapp/server.js");

        bind(ReverserService.class).asEagerSingleton();
        exposeJavaService("/reverserService", ReverserService.class);

        addTopic("reversed");
    }

}
