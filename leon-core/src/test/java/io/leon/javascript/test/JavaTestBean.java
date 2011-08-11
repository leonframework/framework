package io.leon.javascript.test;

import java.util.Arrays;

public class JavaTestBean {

    private String x;
    private int y;
    private JavaTestBean z;
    private String[] xs;

    public JavaTestBean() {
        // necessary for mapper
    }

    public JavaTestBean(String x, int y) {
        this.x = x;
        this.y = y;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public JavaTestBean getZ() {
        return z;
    }

    public void setZ(JavaTestBean z) {
        this.z = z;
    }

    public String[] getXs() {
        return xs;
    }

    public void setXs(String[] xs) {
        this.xs = xs;
    }

    @Override
    public String toString() {
        return "JavaTestBean{" +
                "x='" + x + '\'' +
                ", y=" + y +
                ", z=" + z +
                ", xs=" + (xs == null ? null : Arrays.asList(xs)) +
                '}';
    }
}
