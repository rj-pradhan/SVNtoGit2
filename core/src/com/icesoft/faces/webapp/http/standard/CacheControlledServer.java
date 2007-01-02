package com.icesoft.faces.webapp.http.standard;

import com.icesoft.faces.webapp.http.ResponseHandler;
import com.icesoft.faces.webapp.http.Server;
import com.icesoft.faces.webapp.http.RequestProxy;
import com.icesoft.faces.webapp.http.Response;
import com.icesoft.faces.webapp.http.Request;

import java.util.Date;
import java.util.Collection;
import java.util.HashSet;

public class CacheControlledServer implements Server {
    private static final Date ExpirationDate = new Date(System.currentTimeMillis() + 2629743830l);
    private static final Collection cache = new HashSet();
    private static final Date StartupTime  = new Date();
    private Server server;
    private boolean served;

    public CacheControlledServer(Server server) {
        this.server = server;
        //todo: run a thread to clean up the cache.
    }

    public void service(Request request) throws Exception {
        if (served) {
            //tell to IE to cache these resources
            //see: http://mir.aculo.us/articles/2005/08/28/internet-explorer-and-ajax-image-caching-woes
            //see: http://www.bazon.net/mishoo/articles.epl?art_id=958
            //see: http://support.microsoft.com/default.aspx?scid=kb;en-us;319546
            if (cache.contains(request.getHeader("If-None-Match"))) {
                request.respondWith(new NotModifiedHandler(ExpirationDate));
            } else {
                try {
                    Date modifiedSince = request.getHeaderAsDate("If-Modified-Since");
                    if (StartupTime.getTime() - modifiedSince.getTime() > 1000) {
                        server.service(new EnhancedRequest(request));
                    } else {
                        request.respondWith(new NotModifiedHandler(ExpirationDate));
                    }
                } catch (Exception e) {
                    server.service(new EnhancedRequest(request));
                }
            }
        } else {
            server.service(new EnhancedRequest(request));
            served = true;
        }
    }

    private class EnhancedRequest extends RequestProxy {

        public EnhancedRequest(Request request) {
            super(request);
        }

        public void respondWith(final ResponseHandler handler) throws Exception {
            request.respondWith(new ResponseHandler() {
                public void respond(Response response) throws Exception {
                    String eTag = String.valueOf(Math.abs(request.getURI().hashCode()));
                    cache.add(eTag);
                    response.setHeader("ETag", eTag);
                    response.setHeader("Cache-Control", new String[] { "private", "max-age=86400" });
                    response.setHeader("Last-Modified", StartupTime);
                    handler.respond(response);
                }
            });
        }
    }
}
