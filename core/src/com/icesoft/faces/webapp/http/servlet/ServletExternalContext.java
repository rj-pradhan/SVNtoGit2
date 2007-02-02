package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.context.BridgeExternalContext;
import com.icesoft.faces.util.EnumerationIterator;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesCommonlet;
import com.icesoft.util.SeamUtilities;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

//for now extend BridgeExternalContext since there are so many bloody 'instanceof' tests
public class ServletExternalContext extends BridgeExternalContext {
    private ServletContext context;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private Map applicationMap;
    private Map sessionMap;
    private Map requestParameterMap;
    private Map requestParameterValuesMap;
    private Map initParameterMap;
    private Map requestMap;

    public ServletExternalContext(Object context, Object request, Object response) {
        this.context = (ServletContext) context;
        this.request = (HttpServletRequest) request;
        this.response = (HttpServletResponse) response;
        this.session = this.request.getSession();
        this.requestMap = new ServletRequestMap(this.request);
        this.applicationMap = new ServletApplicationMap(this.context);
        this.sessionMap = new ServletSessionMap(this.session);

        this.initParameterMap = new HashMap();
        Enumeration names = this.context.getInitParameterNames();
        while (names.hasMoreElements()) {
            String key = (String) names.nextElement();
            initParameterMap.put(key, this.context.getInitParameter(key));
        }

        this.updateRequest(this.request);
    }

    public Object getSession(boolean create) {
        return session;
    }

    public Object getContext() {
        return context;
    }

    public Object getRequest() {
        return request;
    }

    public Object getResponse() {
        return response;
    }

    public Map getApplicationMap() {
        return applicationMap;
    }

    public Map getSessionMap() {
        return sessionMap;
    }

    public Map getApplicationSessionMap() {
        return sessionMap;
    }

    public Map getRequestMap() {
        return requestMap;
    }

    public void updateRequest(HttpServletRequest request) {
        //requestMap = new ServletRequestMap(request);
        //update parameters
        requestParameterMap = new HashMap();
        requestParameterValuesMap = new HashMap();
        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = (String) parameterNames.nextElement();
            requestParameterMap.put(name, request.getParameter(name));
            requestParameterValuesMap.put(name, request.getParameterValues(name));
        }
    }

    public Map getRequestParameterMap() {
        return requestParameterMap;
    }

    public Map getRequestParameterValuesMap() {
        return requestParameterValuesMap;
    }

    public Iterator getRequestParameterNames() {
        return requestParameterMap.keySet().iterator();
    }

    public Map getRequestHeaderMap() {
        return Collections.EMPTY_MAP;
    }

    public Map getRequestHeaderValuesMap() {
        return Collections.EMPTY_MAP;
    }

    public Map getRequestCookieMap() {
        return Collections.EMPTY_MAP;
    }

    public Locale getRequestLocale() {
        return request.getLocale();
    }

    public Iterator getRequestLocales() {
        return new EnumerationIterator(request.getLocales());
    }

    public String getRequestPathInfo() {
        return request.getPathInfo();
    }

    public String getRequestURI() {
        return request.getRequestURI();
    }

    public String getRequestContextPath() {
        return request.getContextPath();
    }

    private String requestServletPath;

    public void setRequestServletPath(String path) {
        requestServletPath = path;
    }

    public String getRequestServletPath() {
        return null == requestServletPath ? request.getServletPath() : requestServletPath;
    }

    public String getInitParameter(String name) {
        return context.getInitParameter(name);
    }

    public Map getInitParameterMap() {
        return initParameterMap;
    }

    public Set getResourcePaths(String path) {
        return context.getResourcePaths(path);
    }

    public URL getResource(String path) throws MalformedURLException {
        return context.getResource(path);
    }

    public InputStream getResourceAsStream(String path) {
        return context.getResourceAsStream(path);
    }

    public String encodeActionURL(String url) {
        return url;
    }

    public String encodeResourceURL(String url) {
        try {
            return response.encodeURL(url);
        } catch (Exception e) {
            return url;
        }
    }

    public String encodeNamespace(String name) {
        return name;
    }

    public void dispatch(String path) throws IOException, FacesException {
        try {
            request.getRequestDispatcher(path).forward(request, response);
        } catch (ServletException se) {
            throw new FacesException(se);
        }
    }

    String redirectTo;

    public void redirect(String requestURI) throws IOException {
        redirectTo = SeamUtilities.encodeSeamConversationId(requestURI);
        redirect = true;
        FacesContext.getCurrentInstance().responseComplete();
    }

    public String redirectTo() {
        return redirectTo;
    }

    boolean redirect;

    public boolean redirectRequested() {
        return redirect;
    }

    public void redirectComplete() {
        this.redirect = false;
    }

    public void log(String message) {
        context.log(message);
    }

    public void log(String message, Throwable throwable) {
        context.log(message, throwable);
    }

    public String getAuthType() {
        return request.getAuthType();
    }

    public String getRemoteUser() {
        return request.getRemoteUser();
    }

    public java.security.Principal getUserPrincipal() {
        return request.getUserPrincipal();
    }

    public boolean isUserInRole(String role) {
        return request.isUserInRole(role);
    }

    public void updateResponse(HttpServletResponse response) {
        this.response = response;
    }

    public void setRequestPathInfo(String viewId) {
        //do nothing
    }

    //todo: see if we can execute full JSP cycle all the time (not only when page is parsed)
    //todo: this way the bundles are put into the request map every time, so we don't have to carry
    //todo: them between requests
    public Map collectBundles() {
        Map result = new HashMap();
        Iterator entries = requestMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();
            Object value = entry.getValue();
            if (value != null) {
                String className = value.getClass().getName();
                if ((className.indexOf("LoadBundleTag") > 0) ||  //Sun RI
                        (className.indexOf("BundleMap") > 0)) {     //MyFaces
                    result.put(entry.getKey(), value);
                }
            }
        }

        return result;
    }

    public void setupSeamEnvironment() {
        doClearSeamContexts();
        checkSeamRequestParameters();
    }

    /**
     * Any request handled by the PersistentFacesServlet should have the Seam
     * PageContext removed from our internal context complex. But we cannot do
     * it here, since the machinery is not yet in place, so put a flag into
     * the external context, allowing someone else to do it later.
     */
    private void doClearSeamContexts() {
        requestMap.put(PersistentFacesCommonlet.REMOVE_SEAM_CONTEXTS, Boolean.TRUE);
    }

    /**
     * Check to see if Seam specific keywords are in the request. If so,
     * then flag that a new ViewRoot is to be constructed.
     *
     * @param externalContextMap Map to insert Flag
     */
    private void checkSeamRequestParameters() {
        // Always on a GET request, create a new ViewRoot. New theory.
        // This works now that the ViewHandler only calls restoreView once,
        // as opposed to calling it again from createView
        if (SeamUtilities.isSeamEnvironment()) {
            requestParameterMap.put(
                    PersistentFacesCommonlet.SEAM_LIFECYCLE_SHORTCUT,
                    Boolean.TRUE);
        }
    }
}
