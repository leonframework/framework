package io.leon.web.browser.comet;

import com.beust.jcommander.internal.Maps;
import com.google.inject.Inject;
import io.leon.web.TopicsService;

import java.util.Map;

public class PingService {

    private final TopicsService topicsService;

    @Inject
    public PingService(TopicsService topicsService) {
        this.topicsService = topicsService;
    }

    public void ping() {
        topicsService.send("ping", "pong");
    }

    public void numberPing(int number) {
        topicsService.send("numberPing", String.valueOf(number));
    }

    public void multipleTopicsPing() {
        topicsService.send("pingTopic1", 1);
        topicsService.send("pingTopic2", 2);
        topicsService.send("pingTopic3", 3);
        topicsService.send("pingTopic4", 4);
        topicsService.send("pingTopic5", 5);
        topicsService.send("pingTopic6", 6);
        topicsService.send("pingTopic7", 7);
        topicsService.send("pingTopic8", 8);
        topicsService.send("pingTopic9", 9);
    }

    public void filterPing(String key, String value) {
        Map<String, String> filter = Maps.newHashMap();
        filter.put(key, value);

        Map<String, String> pong = Maps.newHashMap();
        pong.put("key", key);
        pong.put("value", value);

        topicsService.send("filterPing", pong, filter);
    }

}
