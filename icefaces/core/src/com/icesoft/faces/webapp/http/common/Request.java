package com.icesoft.faces.webapp.http.common;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.net.URI;

public interface Request {

    String getMethod();

    URI getURI();
    
    String getHeader(String name);

    String[] getHeaderAsStrings(String name);

    Date getHeaderAsDate(String name);

    int getHeaderAsInteger(String name);    

    String getParameter(String name);

    String[] getParameterAsStrings(String name);

    int getParameterAsInteger(String name);

    boolean getParameterAsBoolean(String name);

    String getParameter(String name, String defaultValue);

    int getParameterAsInteger(String name, int defaultValue);

    boolean getParameterAsBoolean(String name, boolean defaultValue);

    InputStream readBody() throws IOException;

    void readBodyInto(OutputStream out) throws IOException;

    void respondWith(ResponseHandler handler) throws Exception;
}
