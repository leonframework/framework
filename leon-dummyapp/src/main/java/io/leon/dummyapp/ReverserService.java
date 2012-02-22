package io.leon.dummyapp;


import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.inject.Inject;
import io.leon.web.comet.CometRegistry;

import java.util.HashMap;
import java.util.Map;

public class ReverserService {

    private final CometRegistry cometRegistry;

    private final Gson gson;

    @Inject
    public ReverserService(CometRegistry cometRegistry, Gson gson) {
        this.cometRegistry = cometRegistry;
        this.gson = gson;
    }

    public String reverse(String param) {
        String reversed = new StringBuffer(param).reverse().toString();

        Map<String, String> data = Maps.newHashMap();
        data.put("original", param);
        data.put("reversed", reversed);
        String json = gson.toJson(data);

        cometRegistry.publish("reversed", new HashMap<String, Object>(), json);

        return reversed;
    }
}
