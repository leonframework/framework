package com.acme.appname;

public class DemoService {

    public String getA() {
    	final int random = (int) (Math.random() * 1000);
        return "Message with random int " + random + " from ServiceA";
    }

}
