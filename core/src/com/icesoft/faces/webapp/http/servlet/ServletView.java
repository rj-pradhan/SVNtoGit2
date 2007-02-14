package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.env.ServletEnvironmentRequest;
import com.icesoft.faces.webapp.xmlhttp.BlockingResponseState;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
import com.icesoft.faces.webapp.xmlhttp.ResponseStateManager;
import com.icesoft.faces.util.event.servlet.ContextEventRepeater;
import com.icesoft.util.IdGenerator;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Random;
import java.io.IOException;

//todo: refactor this structure into an object with behavior
public class ServletView {
    private ServletExternalContext externalContext;
    private ServletFacesContext facesContext;
    private BlockingResponseState responseState;
    private PersistentFacesState persistentFacesState;
    private Map bundles;

    public ServletView(String viewIdentifier, HttpServletRequest request, HttpServletResponse response, ResponseStateManager responseStateManager) {
        HttpSession session = request.getSession();
        ServletContext servletContext = session.getServletContext();
        externalContext = new ServletExternalContext(servletContext, new ServletEnvironmentRequest(request), response);
        facesContext = new ServletFacesContext(externalContext, viewIdentifier);
        //the call has the side effect of creating and setting up the state
        //todo: make this concept more visible and less subversive
        responseState = (BlockingResponseState) responseStateManager.getState(session, viewIdentifier);
        persistentFacesState = new PersistentFacesState(facesContext, responseState);
        //collect bundles put by Tag components when the page is parsed
        bundles = externalContext.collectBundles();
        externalContext.setupSeamEnvironment();
    }

    public void setAsCurrentDuring(HttpServletRequest request) {
        externalContext.updateRequest(request);
        externalContext.getRequestMap().putAll(bundles);
        persistentFacesState.setCurrentInstance();
        facesContext.setCurrentInstance();
    }

    public void switchToImmediateMode(HttpServletResponse response) {
        externalContext.updateResponse(response);        
    }

    public void switchToPushMode() {
        //by making the request null DOMResponseWriter will redirect its output to the coresponding ResponseState
        //todo: find better (less subversive) solution -- like creating two different implementions for DOMResponseWriter
        externalContext.updateResponse(null);
    }

    public void redirectIfRequired() throws IOException {
        // If the GET request handled by this servlet results in a
        // redirect (not likely under icefaces demo apps, but happens
        // all the time in Seam) then, we're stuck. We don't use the
        // X-REDIRECT mechanism in this servlet, so resort to some
        // good old fashioned manual redirect code.
        if (externalContext.redirectRequested()) {

            // Append 'rvn' parameter field to trigger new ServletView creation. Otherwise, Seam
            // based redirects wont find the new ViewId.
            ((HttpServletResponse) externalContext.getResponse()).sendRedirect(externalContext.redirectTo() +
                                             "&rvn="+facesContext.getViewNumber());
            externalContext.redirectComplete();
        }
    }

    public void release() {
        facesContext.release();
        persistentFacesState.release();        
    }
}