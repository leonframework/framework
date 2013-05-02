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
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.LinkedBindingBuilder;
import com.google.inject.servlet.ServletModule;
import io.leon.javascript.LeonScriptEngine;
import io.leon.web.ajax.AjaxBinder;
import io.leon.web.ajax.JavaObjectAjaxHandler;
import io.leon.web.ajax.JavaScriptAjaxHandler;
import io.leon.web.comet.CometBinder;
import io.leon.web.resources.WebResourcesBinder;

import java.util.List;

abstract public class LeonModule extends ServletModule {

    protected List<String> javaScriptFilesToLoad = Lists.newLinkedList();

    protected List<String> exposedUrls = Lists.newLinkedList();

    public void exposeUrl(String regex) {
        exposedUrls.add(regex);
    }

    public void loadFile(String fileName) {
        javaScriptFilesToLoad.add(fileName);
    }

    public void loadFile(Class<?> basePackage, String fileName) {
        String base = "/" + basePackage.getPackage().getName().replace(".", "/");
        String file = fileName.startsWith("/") ? fileName : "/" + fileName;
        loadFile(base + file);
    }

    @Override
    protected void configureServlets() {
        config();

        WebResourcesBinder webResourcesBinder = new WebResourcesBinder(binder());
        for (String eu : exposedUrls) {
            webResourcesBinder.exposeUrl(eu);
        }

        if (javaScriptFilesToLoad.size() > 0) {
            requestInjection(new Object() {
                @Inject
                public void init(LeonScriptEngine engine) {
                    // Loading JavaScript files
                    engine.loadResources(javaScriptFilesToLoad);
                }
            });
        }
    }

    // --- Delegates for JavaScript modules ---
    @Override
    protected <T> AnnotatedBindingBuilder<T> bind(Class<T> clazz) {
        return super.bind(clazz);
    }

    @Override
    protected <T> LinkedBindingBuilder<T> bind(Key<T> key) {
        return super.bind(key);
    }

    @Override
    protected <T> AnnotatedBindingBuilder<T> bind(TypeLiteral<T> typeLiteral) {
        return super.bind(typeLiteral);
    }

    // -- AjaxBinder delegates ---
    public void exposeJavaService(String url, Class<?> clazz) {
        AjaxBinder b = new AjaxBinder(binder());
        b.exposeJavaService(url, Key.get(clazz));
    }

    public void exposeJavaService(String url, Key<?> key) {
        AjaxBinder b = new AjaxBinder(binder());
        b.exposeAjaxHandler(url, new JavaObjectAjaxHandler(key));
    }

    public void exposeJavaScript(String url, String javaScriptObjectName) {
        AjaxBinder b = new AjaxBinder(binder());
        b.exposeAjaxHandler(url, new JavaScriptAjaxHandler(javaScriptObjectName));
    }

    // --- CometBinder delegates ---
    public void addTopic(String name) {
        new CometBinder(binder()).addTopic(name);
    }

    // --- Abstract methods ---
    abstract protected void config();

}
