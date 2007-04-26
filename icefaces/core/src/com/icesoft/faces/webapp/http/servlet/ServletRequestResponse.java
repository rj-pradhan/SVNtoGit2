package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Response;
import com.icesoft.faces.webapp.http.common.ResponseHandler;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.TimeZone;

public class ServletRequestResponse implements Request, Response {
    private final static DateFormat DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");

    static {
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    protected HttpServletRequest request;
    protected HttpServletResponse response;

    public ServletRequestResponse(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public String getMethod() {
        return request.getMethod();
    }

    public URI getURI() {
        String query = request.getQueryString();
        URI uri = URI.create(request.getRequestURL().toString());
        return query == null ? uri : URI.create(uri + "?" + query);
    }

    public String getHeader(String name) {
        return request.getHeader(name);
    }

    public String[] getHeaderAsStrings(String name) {
        Enumeration e = request.getHeaders(name);
        ArrayList values = new ArrayList();
        while (e.hasMoreElements()) values.add(e.nextElement());
        return (String[]) values.toArray(new String[values.size()]);
    }

    public Date getHeaderAsDate(String name) {
        try {
            return DATE_FORMAT.parse(request.getHeader(name));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public int getHeaderAsInteger(String name) {
        return Integer.parseInt(request.getHeader(name));
    }

    public boolean containsParameter(String name) {
        return request.getParameter(name) != null;
    }

    public String getParameter(String name) {
        checkExistenceOf(name);
        return (String) request.getParameter(name);
    }

    public String[] getParameterAsStrings(String name) {
        checkExistenceOf(name);
        return request.getParameterValues(name);
    }

    public int getParameterAsInteger(String name) {
        return Integer.parseInt(getParameter(name));
    }

    public boolean getParameterAsBoolean(String name) {
        return Boolean.valueOf(getParameter(name)).booleanValue();
    }

    public String getParameter(String name, String defaultValue) {
        try {
            return getParameter(name);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public int getParameterAsInteger(String name, int defaultValue) {
        try {
            return getParameterAsInteger(name);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public boolean getParameterAsBoolean(String name, boolean defaultValue) {
        try {
            return getParameterAsBoolean(name);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public InputStream readBody() throws IOException {
        return request.getInputStream();
    }

    public void readBodyInto(OutputStream out) throws IOException {
        copy(readBody(), out);
    }

    public void respondWith(ResponseHandler handler) throws Exception {
        handler.respond(this);
    }

    public void setStatus(int code) {
        response.setStatus(code);
    }

    public void setHeader(String name, String value) {
        if ("Content-Type".equals(name)) {
            response.setContentType(value);
        } else if ("Content-Length".equals(name)) {
            response.setContentLength(Integer.parseInt(value));
        } else {
            response.setHeader(name, value);
        }
    }

    public void setHeader(String name, String[] values) {
        for (int i = 0; i < values.length; i++) {
            response.addHeader(name, values[i]);
        }
    }

    public void setHeader(String name, Date value) {
        response.setDateHeader(name, value.getTime());
    }

    public void setHeader(String name, int value) {
        response.setIntHeader(name, value);
    }

    public void addCookie(Cookie cookie) {
        response.addCookie(cookie);
    }

    public OutputStream writeBody() throws IOException {
        return response.getOutputStream();
    }

    public void writeBodyFrom(InputStream in) throws IOException {
        copy(in, writeBody());
    }

    private static void copy(InputStream input, OutputStream output) throws IOException {
        byte[] buf = new byte[4096];
        int len = 0;
        while ((len = input.read(buf)) > -1) output.write(buf, 0, len);
    }

    private void checkExistenceOf(String name) {
        if (request.getParameter(name) == null)
            throw new RuntimeException("Query does not contain parameter named: " + name);
    }
}
