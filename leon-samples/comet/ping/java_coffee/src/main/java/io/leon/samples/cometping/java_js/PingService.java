package io.leon.samples.cometping.java_js;

import com.google.inject.Inject;
import io.leon.web.TopicsService;

public class PingService {

    private final TopicsService topicsService;

    @Inject
    public PingService(TopicsService topicsService) {
        this.topicsService = topicsService;
    }

    public void ping(final int start) {
        new Thread() {
            @Override
            public void run() {
                for (int i = start; i < (start + 10); i++) {
                    topicsService.send("ping", i);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

}
