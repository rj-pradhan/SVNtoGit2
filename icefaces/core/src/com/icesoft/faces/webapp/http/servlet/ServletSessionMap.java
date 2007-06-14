package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.context.AbstractAttributeMap;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;

public class ServletSessionMap extends AbstractAttributeMap {
    private final HttpSession httpSession;

    public ServletSessionMap(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    /*
      * @see com.icesoft.faces.context.AbstractAttributeMap#getAttribute(java.lang.String)
      */
    protected Object getAttribute(String key) {
        return httpSession.getAttribute(key);
    }

    /*
      * @see com.icesoft.faces.context.AbstractAttributeMap#setAttribute(java.lang.String, java.lang.Object)
      */
    protected void setAttribute(String key, Object value) {
        httpSession.setAttribute(key, value);
    }

    /*
      * @see com.icesoft.faces.context.AbstractAttributeMap#removeAttribute(java.lang.String)
      */
    protected void removeAttribute(String key) {
        httpSession.removeAttribute(key);
    }

    /*
      * @see com.icesoft.faces.context.AbstractAttributeMap#getAttributeNames()
      */
    protected Enumeration getAttributeNames() {
        return httpSession.getAttributeNames();
    }

}
