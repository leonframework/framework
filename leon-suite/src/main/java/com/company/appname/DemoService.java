package com.company.appname;

public class DemoService {

    public String getExampleMessage() {
    	final int random = (int) (Math.random() * 1000);
        return "DemoService.getExampleMessage() -> " + random;
    }

}
