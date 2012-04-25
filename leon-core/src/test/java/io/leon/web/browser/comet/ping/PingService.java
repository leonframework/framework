package io.leon.web.browser.comet.ping;

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

    public void filterPing(String key, String value) {
        Map<String, String> filter = Maps.newHashMap();
        filter.put(key, value);

        Map<String, String> pong = Maps.newHashMap();
        pong.put("key", key);
        pong.put("value", value);

        topicsService.send("filterPing", pong, filter);
    }

}
