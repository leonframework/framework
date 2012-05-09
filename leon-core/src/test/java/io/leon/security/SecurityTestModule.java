package io.leon.security;

import io.leon.LeonAppMainModule;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.text.TextConfigurationRealm;

import java.util.Arrays;
import java.util.List;

public class SecurityTestModule extends LeonAppMainModule {

    @Override
    public List<? extends Realm> getShiroRealms() {
        TextConfigurationRealm realm = new TextConfigurationRealm();
        StringBuilder lines = new StringBuilder();
        lines.append("user1 = pass1, role1 \n");
        lines.append("user2 = pass2, role2 \n");
        realm.setUserDefinitions(lines.toString());
        return Arrays.asList(realm);
    }

    @Override
    protected void config() {
    }

}
