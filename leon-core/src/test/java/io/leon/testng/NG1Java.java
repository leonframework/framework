package io.leon.testng;

import org.testng.annotations.Test;

public class NG1Java {

    @Test(groups = { "nodefault", "requires_jetty_server" })
    public void test1jetty() {
        System.out.println("###### 1-1 start ###### (jetty)");
        NGUtils.sleep(2000);
        System.out.println("###### 1-1 ende ###### (jetty)");
    }

    @Test(groups = { "nodefault", "requires_jetty_server" })
    public void test2jetty() {
        System.out.println("###### 1-2 start ###### (jetty)");
        NGUtils.sleep(2000);
        System.out.println("###### 1-2 ende ###### (jetty)");
    }

    @Test
    public void test3() {
        System.out.println("###### 1-3 start ######");
        NGUtils.sleep(2000);
        System.out.println("###### 1-3 ende ######");
    }

    @Test
    public void test4() {
        System.out.println("###### 1-4 start ######");
        NGUtils.sleep(2000);
        System.out.println("###### 1-4 ende ######");
    }

}
