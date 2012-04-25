package io.leon.web;

public interface TopicsService extends TopicsSend {

    public TopicsSend toOthers();

    public TopicsSend toCurrent();

}
