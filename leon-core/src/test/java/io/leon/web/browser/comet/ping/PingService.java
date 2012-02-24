package io.leon.web.browser.comet.ping;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.inject.Inject;
import io.leon.web.TopicsService;

import java.util.Map;

public class PingService {

    private final TopicsService topicsService;

    private final Gson gson;
    
    @Inject
    public PingService(TopicsService topicsService, Gson gson) {
        this.topicsService = topicsService;
        this.gson = gson;
    }

    public void ping(final int start) {
        new Thread() {
            @Override
            public void run() {
                int i = start;
                while (i++ < start + (10)) {
                    Map<String, Object> map = Maps.newHashMap();
                    map.put("number", i);
                    topicsService.send("ping", map);
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Map<String, Object> map = Maps.newHashMap();
                map.put("done", true);
                topicsService.send("ping", map);
            }
        }.start();
    }

}
