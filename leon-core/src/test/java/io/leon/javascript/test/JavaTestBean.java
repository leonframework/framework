package io.leon.javascript.test;

public class JavaTestBean {

    private String x;
    private int y;
    private JavaTestBean z;

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

    @Override
    public String toString() {
        return "JavaTestBean{" +
                "x='" + x + '\'' +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
