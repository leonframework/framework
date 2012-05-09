package io.leon.dummyapp;

import io.leon.LeonAppMainModule;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.text.PropertiesRealm;

import java.util.Arrays;
import java.util.List;

public class Module extends LeonAppMainModule {

    @Override
    public List<? extends Realm> getShiroRealms() {
        PropertiesRealm realm = new PropertiesRealm();
        realm.setResourcePath("classpath:dummy-users.properties");
        return Arrays.asList(realm);
    }

    @Override
    public void config() {
        loadFile("/io/leon/dummyapp/server.js");

        bind(ReverserService.class).asEagerSingleton();
        exposeJavaService("/reverserService", ReverserService.class);
        addTopic("/reversed");

        bind(ChatService.class).asEagerSingleton();
        exposeJavaService("/chat", ChatService.class);
        addTopic("/chat");

        exposeJavaService("/shiro", ShiroService.class);
    }

}
