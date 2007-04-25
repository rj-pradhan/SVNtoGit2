package com.icesoft.faces.webapp.http.common.standard;

import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.RequestProxy;
import com.icesoft.faces.webapp.http.common.Response;
import com.icesoft.faces.webapp.http.common.ResponseHandler;
import com.icesoft.faces.webapp.http.common.ResponseProxy;
import com.icesoft.faces.webapp.http.common.Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

public class CompressingServer implements Server {
    private Server server;

    public CompressingServer(Server server) {
        this.server = server;
    }

    public void service(Request request) throws Exception {
        String acceptEncodingHeader = request.getHeader("Accept-Encoding");
        if (acceptEncodingHeader != null && (acceptEncodingHeader.indexOf("gzip") >= 0 || acceptEncodingHeader.indexOf("compress") >= 0)) {
            server.service(new CompressingRequest(request));
        } else {
            server.service(request);
        }
    }

    public void shutdown() {
    }

    private class CompressingRequest extends RequestProxy {
        public CompressingRequest(Request request) {
            super(request);
        }

        public void respondWith(final ResponseHandler handler) throws Exception {
            request.respondWith(new ResponseHandler() {
                public void respond(Response response) throws Exception {
                    CompressingResponse compressingResponse = new CompressingResponse(response);
                    handler.respond(compressingResponse);
                    compressingResponse.finishCompression();
                }
            });
        }
    }

    private class CompressingResponse extends ResponseProxy {
        private GZIPOutputStream output;

        public CompressingResponse(Response response) {
            super(response);
            response.setHeader("Content-Encoding", "gzip");
        }

        public OutputStream writeBody() throws IOException {
            return output = new GZIPOutputStream(response.writeBody());
        }

        public void writeBodyFrom(InputStream in) throws IOException {
            copy(in, writeBody());
        }

        public void finishCompression() throws IOException {
            if (output != null) {
                output.finish();
            }
        }
    }

    private static void copy(InputStream input, OutputStream output) throws IOException {
        byte[] buf = new byte[4096];
        int len = 0;
        while ((len = input.read(buf)) > -1) output.write(buf, 0, len);
    }
}
