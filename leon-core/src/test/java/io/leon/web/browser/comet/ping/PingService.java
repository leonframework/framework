package io.leon.web.browser.comet.ping;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.inject.Inject;
import io.leon.web.comet.CometRegistry;

import java.util.HashMap;
import java.util.Map;

public class PingService {

    private final CometRegistry cometRegistry;

    private final Gson gson;
    
    @Inject
    public PingService(CometRegistry cometRegistry, Gson gson) {
        this.cometRegistry = cometRegistry;
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
                    cometRegistry.publish("ping", new HashMap<String, Object>(), gson.toJson(map));
                    System.out.println("##### " + gson.toJson(map));
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Map<String, Object> map = Maps.newHashMap();
                map.put("done", true);
                cometRegistry.publish("ping", new HashMap<String, Object>(), gson.toJson(map));
                System.out.println("##### " + gson.toJson(map));
            }
        }.start();
    }

}
