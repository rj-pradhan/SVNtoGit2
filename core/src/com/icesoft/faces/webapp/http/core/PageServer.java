package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Response;
import com.icesoft.faces.webapp.http.common.ResponseHandler;
import com.icesoft.faces.webapp.http.common.Server;

import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;

public class PageServer implements Server {
    private final static LifecycleFactory LIFECYCLE_FACTORY = (LifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
    private Lifecycle lifecycle = LIFECYCLE_FACTORY.getLifecycle(LIFECYCLE_FACTORY.DEFAULT_LIFECYCLE);

    private ResponseHandler responseHandler = new ResponseHandler() {
        public void respond(Response response) throws Exception {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            response.setHeader("Cache-Control", new String[]{"no-cache", "no-store", "must-revalidate"});//HTTP 1.1
            response.setHeader("Pragma", "no-cache");//HTTP 1.0
            response.setHeader("Expires", 0);//prevents proxy caching
            com.icesoft.util.SeamUtilities.removeSeamDebugPhaseListener(lifecycle);
            lifecycle.execute(facesContext);
            lifecycle.render(facesContext);
        }
    };

    public void service(Request request) throws Exception {
        request.respondWith(responseHandler);
    }

    public void shutdown() {
    }
}
