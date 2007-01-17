package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.context.AbstractAttributeMap;

import javax.servlet.ServletContext;
import java.util.Enumeration;

public class ServletApplicationMap extends AbstractAttributeMap {

    final ServletContext servletContext;

    public ServletApplicationMap(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /*
      * @see com.icesoft.faces.context.AbstractAttributeMap#getAttribute(java.lang.String)
      */
    protected Object getAttribute(String key) {
        return servletContext.getAttribute(key);
    }

    /*
      * @see com.icesoft.faces.context.AbstractAttributeMap#setAttribute(java.lang.String, java.lang.Object)
      */
    protected void setAttribute(String key, Object value) {
        servletContext.setAttribute(key, value);
    }

    /*
      * @see com.icesoft.faces.context.AbstractAttributeMap#removeAttribute(java.lang.String)
      */
    protected void removeAttribute(String key) {
        servletContext.removeAttribute(key);
    }

    /*
      * @see com.icesoft.faces.context.AbstractAttributeMap#getAttributeNames()
      */
    protected Enumeration getAttributeNames() {
        return servletContext.getAttributeNames();
    }

}
