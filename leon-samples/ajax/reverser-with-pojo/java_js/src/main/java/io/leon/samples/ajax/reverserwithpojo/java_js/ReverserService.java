package io.leon.samples.ajax.reverserwithpojo.java_js;


import java.util.LinkedList;
import java.util.List;

public class ReverserService {

    public List<String> reverse(ReverserRequest request) {
        List<String> wordsReversed = new LinkedList<String>();
        for (Word w : request.getWords()) {
            String reversed = new StringBuffer(w.getText()).reverse().toString();
            if (w.isToUpperCase()) {
                wordsReversed.add(reversed.toUpperCase());
            }
            else {
                wordsReversed.add(reversed);
            }
        }
        return wordsReversed;
    }

}
