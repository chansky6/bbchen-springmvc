package com.bbedu.bbspringmvc.handler;

import java.lang.reflect.Method;

/**
 * 用于记录请求 URL 和控制器方法的映射关系
 */
public class BBHandler {
    private String url;
    private Object controller;
    private Method method;

    public BBHandler(String url, Object controller, Method method) {
        this.url = url;
        this.controller = controller;
        this.method = method;
    }

    @Override
    public String toString() {
        return "BBHandler{" +
                "url='" + url + '\'' +
                ", controller=" + controller +
                ", method=" + method +
                '}';
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
