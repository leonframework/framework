package io.leon.samples.ajax.reverser.java_js;


public class ReverserService {

    public String reverse(String text, boolean toUpperCase) {


        /*
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        */


        String reversed = new StringBuffer(text).reverse().toString();
        if (toUpperCase)
            return reversed.toUpperCase();
        else
            return reversed;
    }
}
