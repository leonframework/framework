package io.leon.dummyapp;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import io.leon.web.TopicsService;

import java.util.Map;

public class ChatService {

    @Inject
    private TopicsService topicsService;

    public void newMessage(String user, String message) {
        Map<String, Object> msg = Maps.newHashMap();
        msg.put("user", user);
        msg.put("message", message);
        msg.put("time", (int) (System.currentTimeMillis() / 1000.0));
        topicsService.toOthers().send("chat", msg);
    }

}
