package com.icesoft.faces.webapp.http.common.standard;

import com.icesoft.faces.webapp.http.common.Response;
import com.icesoft.faces.webapp.http.common.ResponseHandler;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public abstract class FixedXMLContentHandler implements ResponseHandler {

    public abstract void writeTo(Writer writer) throws IOException;

    public void respond(Response response) throws Exception {
        StringWriter writer = new StringWriter();
        writeTo(writer);
        writer.write("\n\n");
        byte[] content = writer.getBuffer().toString().getBytes("UTF-8");
        response.setHeader("Content-Type", "text/xml; charset=UTF-8");
        response.setHeader("Content-Length", content.length);
        response.writeBody().write(content);
    }
}
