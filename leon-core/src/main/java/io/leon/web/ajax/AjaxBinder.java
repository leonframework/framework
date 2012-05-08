/*
 * Copyright (c) 2010 WeigleWilczek and others.
 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package io.leon.web.ajax;

import com.google.inject.Binder;
import com.google.inject.Key;
import io.leon.web.servletwhiteboard.ServletBinder;
import io.leon.web.servletwhiteboard.ServletBinding;

import javax.servlet.Servlet;

public class AjaxBinder {

    private final Binder binder;

    public AjaxBinder(Binder binder) {
        this.binder = binder;
    }

    public void exposeAjaxHandler(String url, AjaxHandler ajaxHandler) {
        binder.requestInjection(ajaxHandler);

        Servlet servlet = new AjaxServlet(ajaxHandler);
        binder.requestInjection(servlet);

        ServletBinding servletBinding = new ServletBinding(url, servlet);
        binder.requestInjection(servletBinding);

        ServletBinder servletBinder = new ServletBinder(binder);
        servletBinder.registerServlet(servletBinding);
    }

    public void exposeJavaService(String url, Class<?> clazz) {
        exposeJavaService(url, Key.get(clazz));
    }

    public void exposeJavaService(String url, Key<?> key) {
        exposeAjaxHandler(url, new JavaObjectAjaxHandler(key));
    }

    public void exposeJavaScript(String url, String javaScriptObjectName) {
        exposeAjaxHandler(url, new JavaScriptAjaxHandler(javaScriptObjectName));
    }

}
