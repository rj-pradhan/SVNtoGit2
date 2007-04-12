package com.icesoft.faces.webapp.http.servlet;

import javax.servlet.ServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CopyingRequestAttributesMap extends HashMap {
    private ServletRequest request;

    public CopyingRequestAttributesMap(ServletRequest request) {
        this.request = request;
        Enumeration e = request.getAttributeNames();
        while (e.hasMoreElements()) {
            String key = String.valueOf(e.nextElement());
            Object value = request.getAttribute(key);
            super.put(key, value);
        }
    }

    public Object put(Object o, Object o1) {
        request.setAttribute(String.valueOf(o), o1);
        return super.put(o, o1);
    }

    public void putAll(Map map) {
        Iterator i = map.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            request.setAttribute(String.valueOf(entry.getKey()), entry.getValue());
        }
        super.putAll(map);
    }

    public Object remove(Object o) {
        request.removeAttribute((String) o);
        return super.remove(o);
    }

    public void clear() {
        Enumeration e = request.getAttributeNames();
        while (e.hasMoreElements()) {
            request.removeAttribute((String.valueOf(e.nextElement())));
        }
        super.clear();
    }
}
