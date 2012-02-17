package io.leon.web.browser.comet.ping;

import com.google.inject.Inject;
import io.leon.web.comet.CometRegistry;

import java.util.HashMap;

public class PingService {

    private final CometRegistry cometRegistry;
    
    @Inject
    public PingService(CometRegistry cometRegistry) {
        this.cometRegistry = cometRegistry;
    }

    public void ping(final int start) {
        new Thread() {
            @Override
            public void run() {
                int i = start;
                while (i++ < start + (10)) {
                    cometRegistry.publish("ping", new HashMap<String, Object>(), String.valueOf(i));
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

}
