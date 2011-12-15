package io.leon.samples.ajax.reverserwithpojo.java_js;

public class ReverserRequest {

    private String text;
    private boolean toUpperCase;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isToUpperCase() {
        return toUpperCase;
    }

    public void setToUpperCase(boolean toUpperCase) {
        this.toUpperCase = toUpperCase;
    }
}
