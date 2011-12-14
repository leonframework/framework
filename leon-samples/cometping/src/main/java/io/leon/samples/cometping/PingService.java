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
        /*
        System.out.println("------------------------------");
        Person p1 = new Person();
        p1.setFirstname("Roman");
        p1.setLastname("Roelofsen");

        final String p1out = JsonUtils.object2Json(p1);

        System.out.println(p1out);
        System.out.println("------------------------------");
        */

        // TODO threads rauf und runter zählen
        // thread nummer übertragen
        // eventuell muss der client gelockt werden beim senden

        new Thread() {
            @Override
            public void run() {
                int i = start;
                int requestNo = ++requests;
                while (i++ < start + (5 * 10)) {
                    cometRegistry.publish("ping", new HashMap<String, Object>(), String.valueOf(i));
                    //cometRegistry.publish("ping", new HashMap<String, Object>(), p1out);
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