package io.leon.web;

import java.util.Map;

public interface TopicsService {

    public void send(String topicId, Object data, Map<String, Object> filters);

    public void send(String topicId, Object data);

}
