package io.leon.dummyapp;


import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.inject.Inject;
import io.leon.web.TopicsService;

import java.util.Map;

public class ReverserService {

    private final TopicsService topicsService;

    private final Gson gson;

    @Inject
    public ReverserService(TopicsService topicsService, Gson gson) {
        this.topicsService = topicsService;
        this.gson = gson;
    }

    public String reverse(String param) {
        String reversed = new StringBuffer(param).reverse().toString();

        Map<String, String> data = Maps.newHashMap();
        data.put("original", param);
        data.put("reversed", reversed);
        topicsService.send("reversed", data);

        return reversed;
    }
}
