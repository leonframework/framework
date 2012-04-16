package com.company.appname;

public class DemoService {

    public String getExampleMessage() {
    	final int random = (int) (Math.random() * 1000);
        return "Example message form DemoService with random int " + random;
    }

}
