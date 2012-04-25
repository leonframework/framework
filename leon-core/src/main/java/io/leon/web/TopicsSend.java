package io.leon.web;

import java.util.Map;

public interface TopicsSend {

    public void send(String topicId, Object data, Map<String, ?> filters);

    public void send(String topicId, Object data);

}
