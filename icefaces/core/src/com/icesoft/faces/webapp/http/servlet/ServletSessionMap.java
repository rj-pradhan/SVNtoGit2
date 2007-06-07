package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.context.AbstractAttributeMap;

import javax.servlet.http.HttpSession;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
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
        Object value = httpSession.getAttribute(key);
        if (value == null) {
            return null;
        } else if (value instanceof Slot) {
            return ((Slot) value).value;
        } else {
            return value;
        }
    }

    /*
      * @see com.icesoft.faces.context.AbstractAttributeMap#setAttribute(java.lang.String, java.lang.Object)
      */
    protected void setAttribute(String key, Object value) {
        //wrap values to avoid Tomcat serialization warnings
        httpSession.setAttribute(key, new Slot(value));
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

    private static class Slot implements Externalizable {
        private Object value;

        public Slot(Object value) {
            this.value = value;
        }

        public void writeExternal(ObjectOutput objectOutput) throws IOException {
            //do nothing
        }

        public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
            //do nothing
        }
    }
}
