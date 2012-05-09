package io.leon.dummyapp;

import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresRoles;

import static org.apache.shiro.SecurityUtils.getSubject;

@SuppressWarnings("UnusedDeclaration")
public class ShiroService {

    public String auth(String username, String password) {
        getSubject().login(new UsernamePasswordToken(username, password));
        return "auth done";
    }

    public String doPublic() {
        return "public";
    }

    public String doPrivate1() {
        getSubject().checkRole("role1");
        return "private1";
    }

    @RequiresRoles("role2")
    public String doPrivate2() {
        return "private2";
    }

}
