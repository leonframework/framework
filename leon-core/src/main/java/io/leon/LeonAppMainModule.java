/*
 * Copyright (c) 2011 WeigleWilczek and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon;

import com.google.common.collect.Lists;
import io.leon.config.ConfigMap;
import io.leon.config.ConfigMapHolder;
import io.leon.web.StaticServletContextHolder$;
import org.apache.shiro.guice.aop.ShiroAopModule;
import org.apache.shiro.guice.web.ShiroWebModule;
import org.apache.shiro.realm.Realm;

import javax.servlet.ServletContext;
import java.util.List;

abstract public class LeonAppMainModule extends LeonModule {

    private static final class SecurityModule extends ShiroWebModule {

        private final LeonAppMainModule leonAppMainModule;

        public SecurityModule(LeonAppMainModule leonAppMainModule, ServletContext servletContext) {
            super(servletContext);
            this.leonAppMainModule = leonAppMainModule;
        }

        @Override
        protected void configureShiroWeb() {
            if (leonAppMainModule.getShiroRealms() != null) {
                for (Realm realm : leonAppMainModule.getShiroRealms()) {
                    bindRealm().toInstance(realm);
                }
            }
        }
    }

    private final ConfigMap configMap = ConfigMapHolder.getInstance().getConfigMap();

    private boolean useLeonShiroIntegration = true;

    public void setApplicationName(String appName) {
        ConfigMap configMap = ConfigMapHolder.getInstance().getConfigMap();
        configMap.put(ConfigMap.APPLICATION_NAME_KEY, appName);
    }

    public ConfigMap getConfigMap() {
        return configMap;
    }

    public boolean isUseLeonShiroIntegration() {
        return useLeonShiroIntegration;
    }

    public void setUseLeonShiroIntegration(boolean useLeonShiroIntegration) {
        this.useLeonShiroIntegration = useLeonShiroIntegration;
    }

    public void setUseWebSocket(boolean useWebSocket) {
        configMap.put(ConfigMap.USE_WEBSOCKET_KEY, Boolean.toString(useWebSocket));
    }

    public List<? extends Realm> getShiroRealms() {
        return Lists.newLinkedList();
    }

    public ShiroWebModule getShiroWebModule(ServletContext servletContext) {
        return new SecurityModule(this, servletContext);
    }

    @Override
    protected void configureServlets() {
        exposeUrl(".*/$");
        exposeUrl(".*html$");
        exposeUrl(".*png$");
        exposeUrl(".*jpg$");
        exposeUrl(".*jpeg$");
        exposeUrl(".*gif$");
        exposeUrl(".*css$");
        exposeUrl("favicon.ico$");
        exposeUrl(".*\\.client\\.js$");

        if (useLeonShiroIntegration &&
                getShiroRealms() != null &&
                getShiroRealms().size() > 0) {

            // TODO avoid usage of static ref. staging should remove this problem
            install(getShiroWebModule(StaticServletContextHolder$.MODULE$.SERVLET_CONTEXT()));
            install(ShiroWebModule.guiceFilterModule());
            install(new ShiroAopModule());
        }

        super.configureServlets();
    }

}
