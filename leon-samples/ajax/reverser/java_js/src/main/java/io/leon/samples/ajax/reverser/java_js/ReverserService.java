package io.leon.samples.ajax.reverser.java_js;


public class ReverserService {

    public String reverse(String text, boolean toUpperCase) {
        String reversed = new StringBuffer(text).reverse().toString();
        if (toUpperCase)
            return reversed.toUpperCase();
        else
            return reversed;
    }
}
