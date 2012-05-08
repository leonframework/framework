package io.leon.dummyapp;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresRoles;

public class ShiroService {

    public String auth(String username, String password) {
        SecurityUtils.getSubject().login(new UsernamePasswordToken(username, password));
        return "auth done";
    }

    public String doPublic() {
        return "public";
    }

    public String doPrivate1() {
        SecurityUtils.getSubject().checkRole("role1");
        return "private1";
    }

    @RequiresRoles("role2")
    public String doPrivate2() {
        return "private2";
    }

}
