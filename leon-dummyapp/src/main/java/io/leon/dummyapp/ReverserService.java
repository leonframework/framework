package io.leon.dummyapp;


import com.google.common.collect.Maps;
import com.google.inject.Inject;
import io.leon.web.TopicsService;

import java.util.Map;

public class ReverserService {

    private final TopicsService topicsService;

    @Inject
    public ReverserService(TopicsService topicsService) {
        this.topicsService = topicsService;
    }

    public String reverse(String param) {
        String reversed = new StringBuffer(param).reverse().toString();

        final Map<String, String> data = Maps.newHashMap();
        data.put("original", param);
        data.put("reversed", reversed);

        (new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    topicsService.send("reversed", data);
                } catch (InterruptedException e) {
                    //
                }
            }
        }).start();

        return reversed;
    }
}
