package io.leon.dummyapp;

import com.google.common.collect.Lists;
import io.leon.LeonAppMainModule;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.text.PropertiesRealm;

import java.util.List;

public class Module extends LeonAppMainModule {

    @Override
    public List<? extends Realm> getShiroRealms() {
        PropertiesRealm pr = new PropertiesRealm();
        pr.setResourcePath("classpath:dummy-users.properties");
        return Lists.newArrayList(pr);
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
