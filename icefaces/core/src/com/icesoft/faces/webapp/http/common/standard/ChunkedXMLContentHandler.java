package com.icesoft.faces.webapp.http.common.standard;

import com.icesoft.faces.webapp.http.common.Response;
import com.icesoft.faces.webapp.http.common.ResponseHandler;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public abstract class ChunkedXMLContentHandler implements ResponseHandler {

    public abstract void writeTo(Writer writer) throws IOException;

    public void respond(Response response) throws Exception {
        response.setHeader("Content-Type", "text/xml; charset=UTF-8");
        OutputStreamWriter writer = new OutputStreamWriter(response.writeBody(), "UTF-8");
        writeTo(writer);
        writer.flush();
    }
}
