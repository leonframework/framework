package io.leon.samples.cometping;

import com.google.inject.Inject;
import io.leon.web.comet.CometRegistry;

import java.util.HashMap;

public class PingService {

    private final CometRegistry cometRegistry;
    
    private int requests = 0;

    @Inject
    public PingService(CometRegistry cometRegistry) {
        this.cometRegistry = cometRegistry;
    }

    public void ping(final int start) {
        new Thread() {
            @Override
            public void run() {
                int i = start;
                int requestNo = ++requests;
                while (i++ < start + (10)) {
                    cometRegistry.publish("ping", new HashMap<String, Object>(), String.valueOf(i));
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.run();
    }

}
