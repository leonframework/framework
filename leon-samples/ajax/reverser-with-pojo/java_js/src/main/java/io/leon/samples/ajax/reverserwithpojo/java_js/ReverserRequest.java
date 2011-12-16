package io.leon.samples.ajax.reverserwithpojo.java_js;

import java.util.LinkedList;
import java.util.List;

public class ReverserRequest {

    private List<Word> words = new LinkedList<Word>();

    public List<Word> getWords() {
        return words;
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }

}
