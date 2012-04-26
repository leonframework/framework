package io.leon.web;

public interface TopicsService extends TopicsSend {

    public TopicsSend toOtherSessions();

    public TopicsSend toCurrentSession();

}
