package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.context.AbstractAttributeMap;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

// todo: Iron out DirectRequestAttributeMap implementation
public class DirectRequestAttributeMap extends AbstractAttributeMap {
    private HttpServletRequest request;

    public DirectRequestAttributeMap(HttpServletRequest request) {
        this.request = request;
    }

    protected Object getAttribute(String key) {
        return request.getAttribute(key);
    }

    protected void setAttribute(String key, Object value) {
        request.setAttribute(key, value);
    }

    protected void removeAttribute(String key) {
        request.removeAttribute(key);
    }

    protected Enumeration getAttributeNames() {
        return request.getAttributeNames();
    }
}
