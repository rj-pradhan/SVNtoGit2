package com.icesoft.faces.webapp.http;

import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public interface Response {

    void setStatus(int code);

    void setHeader(String name, String value);

    void setHeader(String name, String[] values);

    void setHeader(String name, Date value);

    void setHeader(String name, int value);

    OutputStream writeBody() throws IOException;

    void writeBodyFrom(InputStream in) throws IOException;
}
