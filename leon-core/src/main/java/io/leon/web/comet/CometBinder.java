package io.leon.web.comet;

import com.google.inject.Binder;
import com.google.inject.name.Names;

public class CometBinder {

    private final Binder binder;

    public CometBinder(Binder binder) {
        this.binder = binder;
    }

    public void addTopic(String name) {
        Topic t = new Topic(name);
        binder.bind(Topic.class).annotatedWith(Names.named("topic:" + name)).toInstance(t);
    }

}
