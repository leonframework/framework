package io.leon.dummyapp;


public class AjaxService {

    public String method1(String param) {
        return new StringBuffer(param).reverse().toString();
    }
}
