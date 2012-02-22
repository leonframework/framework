package io.leon.web;

import java.util.Map;

public interface BrowserTopics {

    public void send(String topicId, Map<String, Object> filters, Object data);

    public void send(String topicId, Object data);

}
