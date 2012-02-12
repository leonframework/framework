package io.leon.testng;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test(groups = { "nodefault", "requires_jetty_server" })
public class NG2Java {

    public void test1jetty() {
        System.out.println("###### 3-1 start ###### (jetty)");
        NGUtils.sleep(2000);
        System.out.println("###### 3-1 ende ###### (jetty)");
    }

}
