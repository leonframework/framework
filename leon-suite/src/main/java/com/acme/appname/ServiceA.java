package com.acme.appname;

public class ServiceA {

    public String getA() {
        return "Message from ServiceA";
    }


    public void addressSaved(Address address) {
        System.out.println("address = " + address);
    }
}
