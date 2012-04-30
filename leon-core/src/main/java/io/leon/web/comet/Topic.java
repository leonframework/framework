package io.leon.web.comet;

public class Topic {

    private final String name;

    public Topic(String name) {
        checkTopicName(name);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static void checkTopicName(String name) {
        if (!name.startsWith("/")) {
            throw new IllegalArgumentException("A topic name must start with '/'.");
        }
        if (name.endsWith("/")) {
            throw new IllegalArgumentException("A topic name must not end with '/'.");
        }
    }

}
