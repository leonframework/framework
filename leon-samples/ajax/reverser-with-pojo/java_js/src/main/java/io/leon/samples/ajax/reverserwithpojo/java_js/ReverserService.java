package io.leon.samples.ajax.reverserwithpojo.java_js;


public class ReverserService {

    public ReverserResponse reverse(ReverserRequest request) {
        String reversed = new StringBuffer(request.getText()).reverse().toString();
        if (request.isToUpperCase())
            return new ReverserResponse(reversed.toUpperCase());
        else
            return new ReverserResponse(reversed);
    }
}
