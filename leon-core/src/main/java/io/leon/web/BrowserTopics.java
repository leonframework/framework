package io.leon.web;

import java.util.Map;

// TODO
public interface BrowserTopics {

    public void send(String topicId, Map<String, Object> filters, String data);

    public void send(String topicId, String data);

}
