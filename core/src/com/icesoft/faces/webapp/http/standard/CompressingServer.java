package com.icesoft.faces.webapp.http.standard;

import com.icesoft.faces.webapp.http.ResponseHandler;
import com.icesoft.faces.webapp.http.Response;
import com.icesoft.faces.webapp.http.Server;
import com.icesoft.faces.webapp.http.Request;
import com.icesoft.faces.webapp.http.RequestProxy;
import com.icesoft.faces.webapp.http.ResponseProxy;

import java.util.zip.GZIPOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CompressingServer implements Server {
    private Server server;
    private GZIPOutputStream output;

    public CompressingServer(Server server) {
        this.server = server;
    }

    public void service(Request request) throws Exception {
        String acceptEncodingHeader = request.getHeader("Accept-Encoding");
        if (acceptEncodingHeader.indexOf("gzip") >= 0 || acceptEncodingHeader.indexOf("compress") >= 0) {
            server.service(new CompressingRequest(request));
        } else {
            server.service(request);
        }
    }

    private class CompressingRequest extends RequestProxy {
        public CompressingRequest(Request request) {
            super(request);
        }

        public void respondWith(final ResponseHandler handler) throws Exception {
            request.respondWith(new ResponseHandler() {
                public void respond(Response response) throws Exception {
                    handler.respond(new CompressingResponse(response));
                    output.finish();
                }
            });
        }
    }

    private class CompressingResponse extends ResponseProxy {
        public CompressingResponse(Response response) {
            super(response);
            response.setHeader("Content-Encoding", "gzip");
        }

        public OutputStream writeBody() throws IOException {
            output = new GZIPOutputStream(response.writeBody());
            return output;
        }

        public void writeBodyFrom(InputStream in) throws IOException {
            copy(in, writeBody());
        }
    }

    private static void copy(InputStream input, OutputStream output) throws IOException {
        byte[] buf = new byte[4096];
        int len = 0;
        while ((len = input.read(buf)) > -1) output.write(buf, 0, len);
    }
}
