package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.context.BridgeExternalContext;
import com.icesoft.faces.util.EnumerationIterator;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesCommonlet;
import com.icesoft.util.SeamUtilities;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
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
    private Map requestCookieMap;
    private Collection responseCookies;

    public ServletExternalContext(Object context, Object request, Object response) {
        this.context = (ServletContext) context;
        this.request = (HttpServletRequest) request;
        this.response = (HttpServletResponse) response;
        this.session = this.request.getSession();
        this.requestMap = new ServletRequestMap(this.request);
        this.applicationMap = new ServletApplicationMap(this.context);
        this.sessionMap = new ServletSessionMap(this.session);
        this.requestCookieMap = new HashMap();
        this.initParameterMap = new HashMap();
        this.responseCookies = new ArrayList();
        Enumeration names = this.context.getInitParameterNames();
        while (names.hasMoreElements()) {
            String key = (String) names.nextElement();
            initParameterMap.put(key, this.context.getInitParameter(key));
        }

        this.updateRequest(this.request);
        if (SeamUtilities.isSeamEnvironment() ) {
                   requestParameterMap.put(
                           PersistentFacesCommonlet.SEAM_LIFECYCLE_SHORTCUT,
                           Boolean.TRUE);
        }
                
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

        //update parameters
         boolean persistSeamKey = false; 
        if (requestParameterMap != null) {
            persistSeamKey = (requestParameterMap.containsKey(PersistentFacesCommonlet.SEAM_LIFECYCLE_SHORTCUT ));
        }

        requestParameterMap = new HashMap();
        requestParameterValuesMap = new HashMap();
        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = (String) parameterNames.nextElement();
            requestParameterMap.put(name, request.getParameter(name));
            requestParameterValuesMap.put(name, request.getParameterValues(name));
        }

        if (persistSeamKey) {
            requestParameterMap.put(
                    PersistentFacesCommonlet.SEAM_LIFECYCLE_SHORTCUT,
                    Boolean.TRUE);
        } 
        


        requestCookieMap = new HashMap();
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                requestCookieMap.put(cookie.getName(), cookie);
            }
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

    //todo: implement!
    public Map getRequestHeaderMap() {
        return Collections.EMPTY_MAP;
    }

    //todo: implement!
    public Map getRequestHeaderValuesMap() {
        return Collections.EMPTY_MAP;
    }

    public Map getRequestCookieMap() {
        return requestCookieMap;
    }

    public Locale getRequestLocale() {
        return request.getLocale();
    }

    public Iterator getRequestLocales() {
        return new EnumerationIterator(request.getLocales());
    }

    private String requestPathInfo;

    public void setRequestPathInfo(String viewId) {
        requestPathInfo = viewId;
    }

    public String getRequestPathInfo() {
        return requestPathInfo == null ? request.getPathInfo() : requestPathInfo;
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

    public void addCookie(Cookie cookie) {
        responseCookies.add(cookie);
    }

    public Cookie[] getResponseCookies() {
        return (Cookie[]) responseCookies.toArray(new Cookie[responseCookies.size()]);
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

    /**
     * Setup 
     */
    public void setupSeamEnvironment() {
        insertNewViewrootToken();
    }

    /**
     * Any GET request performed by the browser is a non-faces request to the framework.
     * (JSF-spec chapter 2, introduction). Given this, the framework must create a new
     * viewRoot for the request, even if the viewId has already been visited. (Spec
     * section 2.1.1) <p>   
     *
     * Only during GET's remember, not during partial submits, where the JSF framework must
     * be allowed to attempt to restore the view. There is a great deal of Seam related code
     * that depends on this happening. So put in a token that allows the D2DViewHandler
     * to differentiate between the non-faces request, and the postbacks, for this
     * request, which will allow the ViewHandler to make the right choice, since we keep
     * the view around for all types of requests
     *
     */
    private void insertNewViewrootToken() {
        if (SeamUtilities.isSeamEnvironment()) {
            requestParameterMap.put(
                    PersistentFacesCommonlet.SEAM_LIFECYCLE_SHORTCUT,
                    Boolean.TRUE);
        }
    }
}
