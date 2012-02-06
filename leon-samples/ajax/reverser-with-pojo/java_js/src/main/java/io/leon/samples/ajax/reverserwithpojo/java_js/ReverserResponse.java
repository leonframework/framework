package io.leon.samples.ajax.reverserwithpojo.java_js;

import java.util.LinkedList;
import java.util.List;

public class ReverserResponse {

    private List<String> wordsReversed = new LinkedList<String>();

    public List<String> getWordsReversed() {
        return wordsReversed;
    }

    public void setWordsReversed(List<String> wordsReversed) {
        this.wordsReversed = wordsReversed;
    }

}
