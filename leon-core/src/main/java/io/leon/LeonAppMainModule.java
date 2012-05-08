/*
 * Copyright (c) 2011 WeigleWilczek and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon;

import io.leon.config.ConfigMap;
import io.leon.config.ConfigMapHolder;

abstract public class LeonAppMainModule extends LeonModule {

    private final ConfigMap configMap = ConfigMapHolder.getInstance().getConfigMap();

    public void setApplicationName(String appName) {
        ConfigMap configMap = ConfigMapHolder.getInstance().getConfigMap();
        configMap.put(ConfigMap.APPLICATION_NAME_KEY, appName);
    }

    public void setConfigProperty(String propertyName, String propertyValue) {
        configMap.put(propertyName, propertyValue);
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

        super.configureServlets();
    }

}
