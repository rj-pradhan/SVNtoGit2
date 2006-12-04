/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * "The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is ICEfaces 1.5 open source software code, released
 * November 5, 2006. The Initial Developer of the Original Code is ICEsoft
 * Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 * 2004-2006 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"
 * License), in which case the provisions of the LGPL License are
 * applicable instead of those above. If you wish to allow use of your
 * version of this file only under the terms of the LGPL License and not to
 * allow others to use your version of this file under the MPL, indicate
 * your decision by deleting the provisions above and replace them with
 * the notice and other provisions required by the LGPL License. If you do
 * not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the LGPL License."
 *
 */

/*
 * BridgeExternalContext.java
 */

package com.icesoft.faces.context;

import com.icesoft.faces.context.portlet.PortletApplicationMap;
import com.icesoft.faces.context.portlet.PortletSessionMap;
import com.icesoft.faces.env.PortletEnvironmentResponse;
import com.icesoft.faces.env.ServletEnvironmentResponse;
import com.icesoft.faces.util.EnumerationIterator;
import com.icesoft.util.SeamUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.PortletSession;
import javax.portlet.RenderResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * This class is supposed to provide a nice, generic interface to the
 * environment that we're running in (e.g. servlets, portlets).  The current
 * design has the type of environment identified during construction and any
 * subsequent method calls check the environment and call the underlying methods
 * appropriately.  I don't think this is the way we should do it but hopefully
 * we'll get this working and then refactor.
 */
public class BridgeExternalContext extends ExternalContext {

    //Attributes for JSP forwarded requests
    public static final String
            INCLUDE_REQUEST_URI = "javax.servlet.include.request_uri";
    public static final String
            INCLUDE_CONTEXT_PATH = "javax.servlet.include.context_path";
    public static final String
            INCLUDE_SERVLET_PATH = "javax.servlet.include.servlet_path";
    public static final String
            INCLUDE_PATH_INFO = "javax.servlet.include.path_info";
    public static final String
            INCLUDE_QUERY_STRING = "javax.servlet.include.query_string";

    //Init Parameter names
    public static final String
            PATH_INFO_HACK = "com.icesoft.faces.fakePathInfo";


    public static final int UNKNOWN_ENVIRONMENT = -1;
    public static final int SERVLET_ENVIRONMENT = 1;
    public static final int PORTLET_ENVIRONMENT = 2;
    public static final String REQUEST_MAP =
            "com.icesoft.faces.context.BridgeExternalContext.REQUEST_MAP";

    //By default, assume the current environment is plain old servlets
    private int currentEnvironment = SERVLET_ENVIRONMENT;

    //Servlet environment members
    private ServletContext servletContext;
    private HttpServletRequest servletRequest;
    private HttpServletResponse servletResponse;
    private HttpSession servletSession;

    //Portlet environment members
    private PortletContext portletContext;
    private PortletRequest portletRequest;
    private PortletResponse portletResponse;
    private PortletSession portletSession;

    //Common environment members
    private String requestURI;

    private static final Log log =
            LogFactory.getLog(BridgeExternalContext.class);


    public BridgeExternalContext(Object context, Object request,
                                 Object response) {
        init(context, request, response);
    }

    private void init(Object context, Object request, Object response) {

        if (context instanceof ServletContext) {

            currentEnvironment = SERVLET_ENVIRONMENT;
            servletContext = (ServletContext) context;
            servletRequest = (HttpServletRequest) request;
            servletResponse = (HttpServletResponse) response;
            servletSession = servletRequest.getSession();
            requestURI = servletRequest.getRequestURI();

        } else if (context instanceof PortletContext) {

            currentEnvironment = PORTLET_ENVIRONMENT;
            portletContext = (PortletContext) context;
            portletRequest = (PortletRequest) request;
            portletResponse = (PortletResponse) response;
            portletSession = portletRequest.getPortletSession();
            //TODO
            //There is no getRequestURI method in the RenderRequest so
            //I'm not sure if this is the currect replacement.
            requestURI = portletRequest.getContextPath();
        }
    }

    public Object getSession(boolean create) {
        switch (currentEnvironment) {

            case SERVLET_ENVIRONMENT:
                if (servletSession != null) {
                    return servletSession;
                } else {
                    servletSession = servletRequest.getSession(create);
                }
                return servletSession;

            case PORTLET_ENVIRONMENT:
                if (portletSession != null) {
                    return portletSession;
                } else {
                    portletSession = portletRequest.getPortletSession(create);
                }
                return portletSession;

        }
        return null;
    }


    public void setSession(Object session) {

        switch (currentEnvironment) {

            case SERVLET_ENVIRONMENT:
                servletSession = (HttpSession) session;
                break;

            case PORTLET_ENVIRONMENT:
                portletSession = (PortletSession) session;
                break;

        }
        sessionMap = null;
    }


    public Object getContext() {
        switch (currentEnvironment) {

            case SERVLET_ENVIRONMENT:
                return servletContext;

            case PORTLET_ENVIRONMENT:
                return portletContext;

        }
        return null;
    }


    public Object getRequest() {
        switch (currentEnvironment) {

            case SERVLET_ENVIRONMENT:
                return servletRequest;

            case PORTLET_ENVIRONMENT:
                return portletRequest;

        }
        return null;
    }


    public Object getResponse() {

        //Only return a wrapped response if the original response was non-null.  We rely
        //on this to be true for further handling.
        switch (currentEnvironment) {

            case SERVLET_ENVIRONMENT:
                if (servletResponse != null) {
                    return new ServletEnvironmentResponse(servletResponse);
                } else {
                    return null;
                }

            case PORTLET_ENVIRONMENT:
                if (portletResponse != null) {
                    return new PortletEnvironmentResponse(portletResponse);
                } else {
                    return null;
                }

        }
        return null;
    }


    private Map applicationMap;

    public Map getApplicationMap() {
        if (null == applicationMap) {
            switch (currentEnvironment) {

                case SERVLET_ENVIRONMENT:
                    applicationMap = new ApplicationMap(servletContext);
                    break;

                case PORTLET_ENVIRONMENT:
                    applicationMap = new PortletApplicationMap(portletContext);
                    break;

            }
        }
        return applicationMap;
    }


    private Map sessionMap;

    public Map getSessionMap() {
        if (null != sessionMap) {
            return sessionMap;
        }
        switch (currentEnvironment) {

            case SERVLET_ENVIRONMENT:
                if (null != servletSession) {
                    sessionMap = new SessionMap(servletSession);
                } else {
                    sessionMap = new Hashtable();
                }
                break;

            case PORTLET_ENVIRONMENT:
                if (null != portletSession) {
                    sessionMap = new PortletSessionMap(portletSession);
                } else {
                    sessionMap = new Hashtable();
                }
                break;

        }
        return (sessionMap);

    }


    /**
     * If this is found to be a Seam environment, then we have to clear out any
     * left over request attributes. Otherwise, since this context is
     * incorporated into the Seam Contexts structure, things put into this
     * context linger beyond the scope of the request, which can cause problems.
     * This method should only be called from the blocking servlet, as it's the
     * handler for the Ajax requests that cause the issue.
     */
    public void clearRequestContext() {

        if (!SeamUtilities.isSeamEnvironment()) {
            return;
        }
        Enumeration e;
        String el;
        switch (currentEnvironment) {

            case SERVLET_ENVIRONMENT:
                e = servletRequest.getAttributeNames();
                while (e.hasMoreElements()) {
                    el = (String) e.nextElement();
                    log.debug("Removing named element: " + el +
                              " from Reused ExternalContext");
                    servletRequest.removeAttribute(el);
                }
                // skip portlets? What's in there?
        }
    }

    public void setSessionMap(Map sessionMap) {
        this.sessionMap = sessionMap;
    }

    /**
     * get the sessionMap, or in portlet mode, get the APPLICATION_SCOPE session
     * map
     */
    public Map getApplicationSessionMap() {

        switch (currentEnvironment) {
            case SERVLET_ENVIRONMENT:
                return getSessionMap();

            case PORTLET_ENVIRONMENT:
                if (null != portletSession) {
                    return new PortletSessionMap(portletSession,
                                                 PortletSession.APPLICATION_SCOPE);
                }
                break;
        }
        return null;
    }

    private Map requestMap;

    public Map getRequestMap() {
        BridgeFacesContext facesContext = ((BridgeFacesContext)
                FacesContext.getCurrentInstance());
        requestMap =
                (Map) facesContext.getContextServletTable().get(REQUEST_MAP);
        if (null != requestMap) {
            return requestMap;
        }
        //This map should really be a wrapper for the actual
        //request getAttribute/setAttribute.  For now just
        //preload our map with the request values
        requestMap = new HashMap();
        if (null != servletRequest) {
            Enumeration names = servletRequest.getAttributeNames();
            while (names.hasMoreElements()) {
                String name = (String) names.nextElement();
                requestMap.put(name, servletRequest.getAttribute(name));
            }
        } else if (null != portletRequest) {
            Enumeration names = portletRequest.getAttributeNames();
            while (names.hasMoreElements()) {
                String name = (String) names.nextElement();
                requestMap.put(name, portletRequest.getAttribute(name));
            }
        }
        facesContext.getContextServletTable()
                .put(REQUEST_MAP, requestMap);
        return (requestMap);
    }

    public void setRequestMap(Map requestMap) {
        this.requestMap = requestMap;
    }


    public void resetRequestMap() {

        try {
            Map map = getRequestMap();
            Iterator keys = map.keySet().iterator();
            while (keys.hasNext()) {
                Object key = keys.next();
                Object value = map.get(key);
                if (value instanceof Boolean) {
                    keys.remove();
                }
            }
        } catch (IllegalStateException ise) {

            // Can be thrown in Seam example applications as a result of
            // eg. logout, which has already invalidated the session.
            if (SeamUtilities.isSeamEnvironment()){
                if (redirect) {
                    throw new RedirectException ( redirectTo );
                }
            }
        }
    } 

    public void clearRequestMap() {
        Map map = getRequestMap();
        map.clear();
    }

    /**
     * The requestParameterMap and requestParameterValuesMap are interfaces
     * to the same underlying hashtable. The requestParameterMap is a filtered
     * version - it returns only String-String (name-value) pairs and not
     * the String-List pairs. For the String-List pairs only the first entry
     * in the List is returned.
     */

    /**
     * populate the underlying hashtable
     */
    public synchronized void populateRequestParameters(Map requestParameters) {
        Map valuesMap = getRequestParameterValuesMap();
        if (valuesMap == null) {
            valuesMap = requestParameterValuesMap =
                    new RequestParameterValuesMap();
        }
        valuesMap.clear();
        valuesMap.putAll(requestParameters);
        filterRequestParameterMap();
    }

    private Map requestParameterMap;

    public Map getRequestParameterMap() {
        if (requestParameterMap != null) {
            return requestParameterMap;
        }
        return requestParameterMap = new Hashtable();
    }

    private void filterRequestParameterMap() {
        if (requestParameterMap == null) {
            requestParameterMap = new Hashtable();
        } else {
            requestParameterMap.clear();
        }
        Map requestParameterValuesMap = getRequestParameterValuesMap();
        Iterator parameterNames = requestParameterValuesMap.keySet().iterator();
        Object nextParameterName = null, nextParameterValue = null;
        while (parameterNames.hasNext()) {
            nextParameterName = parameterNames.next();
            nextParameterValue =
                    requestParameterValuesMap.get(nextParameterName);
            nextParameterValue = ((String[]) nextParameterValue)[0];
            requestParameterMap.put(nextParameterName, nextParameterValue);
        }
    }

    private RequestParameterValuesMap requestParameterValuesMap;

    /**
     * returns null if you didn't first call populateRequestParameters.
     */
    public Map getRequestParameterValuesMap() {
        return requestParameterValuesMap;
    }


    public Iterator getRequestParameterNames() {
        if (requestParameterMap != null) {
            return (requestParameterMap.keySet().iterator());
        } else {
            return Collections.EMPTY_LIST.iterator();

        }
    }

    private Map requestHeaderMap;

    public Map getRequestHeaderMap() {
        if (requestHeaderMap != null) {
            return (requestHeaderMap);
        } else {
            requestHeaderMap = new Hashtable();
            return (requestHeaderMap);
        }
    }


    public Map getRequestHeaderValuesMap() {
        throw new UnsupportedOperationException();
    }


    public Map getRequestCookieMap() {
        throw new UnsupportedOperationException();
    }


    private Map responseCookieMap;

    public Map getResponseCookieMap() {
        if (null != responseCookieMap) {
            return (responseCookieMap);
        } else {
            responseCookieMap = new Hashtable();
            return (responseCookieMap);
        }
    }

    public void addCookie(Cookie cookie)  {
        responseCookieMap.put(cookie.getName(), cookie);
    }


    public Locale getRequestLocale() {

        Locale loc = null;

        switch (currentEnvironment) {

            case SERVLET_ENVIRONMENT:
                loc = servletRequest.getLocale();
                break;

            case PORTLET_ENVIRONMENT:
                loc = portletRequest.getLocale();
                break;

        }
        return loc;
    }


    public Iterator getRequestLocales() {
        Iterator iter = null;

        switch (currentEnvironment) {

            case SERVLET_ENVIRONMENT:
                iter = new EnumerationIterator(servletRequest.getLocales());
                break;

            case PORTLET_ENVIRONMENT:
                iter = new EnumerationIterator(portletRequest.getLocales());
                break;

        }
        return iter;
    }

    String requestPathInfo;

    public void setRequestPathInfo(String info) {
        requestPathInfo = info;
    }

    public String getRequestPathInfo() {
        String pathInfo = null;

        //Workaround for MyFaces remapping viewId before sending
        //to ViewHandler
        if ("true".equalsIgnoreCase(getInitParameter(PATH_INFO_HACK))) {
            return servletRequest.getServletPath();
        }

        switch (currentEnvironment) {

            case SERVLET_ENVIRONMENT:
                if (null != requestPathInfo) {
                    //over-ride with current value for MyFaces
                    pathInfo = requestPathInfo;
                } else {
                    pathInfo = servletRequest.getPathInfo();
                }
                break;

            case PORTLET_ENVIRONMENT:
                pathInfo = null;
                break;

        }
        return pathInfo;
    }

    //TODO
    //The following two methods are not a standard part of the ExternalContext
    //interface and I'm not sure how they should be handled in a portlet
    //environment since PortletRequest does not have a corresponding method.
    public void setRequestURI(String path) {
        requestURI = path;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getRequestContextPath() {
        String contextPath = null;
        switch (currentEnvironment) {

            case SERVLET_ENVIRONMENT:
                contextPath = servletRequest.getContextPath();
                break;

            case PORTLET_ENVIRONMENT:
                contextPath = portletRequest.getContextPath();
                break;
        }
        return contextPath;
    }

    String requestServletPath;

    public void setRequestServletPath(String path) {
        requestServletPath = path;
    }

    public String getRequestServletPath() {
        String servletPath = null;
        switch (currentEnvironment) {

            case SERVLET_ENVIRONMENT:
                if (null != requestServletPath) {
                    //allow redirects to over-ride the initial path
                    servletPath = requestServletPath;
                } else {
                    servletPath = servletRequest.getServletPath();
                }
                break;

            case PORTLET_ENVIRONMENT:
                if (null != requestServletPath) {
                    //allow redirects to over-ride the initial path
                    servletPath = requestServletPath;
                } else {
                    servletPath = null;
                }
                break;
        }
        return servletPath;
    }


    public String getInitParameter(String name) {
        //TODO
        //Why do we return null for this special case?
        if (name.equals(
                javax.faces.application.StateManager.STATE_SAVING_METHOD_PARAM_NAME)) {
            return null;
        }

        String initParam = null;
        switch (currentEnvironment) {

            case SERVLET_ENVIRONMENT:
                initParam = servletContext.getInitParameter(name);
                break;

            case PORTLET_ENVIRONMENT:
                initParam = portletContext.getInitParameter(name);
                break;
        }
        return initParam;

    }

    public Map getInitParameterMap() {
        Map initParameterMap = new HashMap();
        Enumeration names = null;
        switch (currentEnvironment) {

            case SERVLET_ENVIRONMENT:
                names = servletContext.getInitParameterNames();
                break;

            case PORTLET_ENVIRONMENT:
                names = portletContext.getInitParameterNames();
                break;
        }
        while (names.hasMoreElements()) {
            Object key = names.nextElement();
            initParameterMap.put(key, getInitParameter(key.toString()));
        }
        return initParameterMap;
    }


    public Set getResourcePaths(String path) {

        if (path == null) {
            throw new NullPointerException();
        }

        Set paths = null;
        switch (currentEnvironment) {

            case SERVLET_ENVIRONMENT:
                paths = servletContext.getResourcePaths(path);
                break;

            case PORTLET_ENVIRONMENT:
                paths = portletContext.getResourcePaths(path);
                break;
        }
        return paths;
    }


    public URL getResource(String path) throws MalformedURLException {
        if (path == null) {
            throw new NullPointerException();
        }

        URL resource = null;
        switch (currentEnvironment) {

            case SERVLET_ENVIRONMENT:
                resource = servletContext.getResource(path);
                break;

            case PORTLET_ENVIRONMENT:
                resource = portletContext.getResource(path);
                break;
        }
        return resource;
    }


    public InputStream getResourceAsStream(String path) {
        if (path == null) {
            throw new NullPointerException();
        }

        InputStream resource = null;
        switch (currentEnvironment) {

            case SERVLET_ENVIRONMENT:
                resource = servletContext.getResourceAsStream(path);
                break;

            case PORTLET_ENVIRONMENT:
                resource = portletContext.getResourceAsStream(path);
                break;
        }
        return resource;
    }


    public String encodeActionURL(String url) {

        if (url == null) {
            throw new NullPointerException();
        }

        //TODO
        //Why do we not encode it?  I've put the commented out correct logic
        //below in the event we revert back to "proper" behaviour.
        return (url);

//
//        String encodedURL = null;
//        switch (currentEnvironment){
//
//            case SERVLET_ENVIRONMENT:
//                encodedURL = servletResponse.encodeURL( url );
//                break;
//
//            case PORTLET_ENVIRONMENT:
//                encodedURL = portletResponse.encodeURL( url );
//                break;
//        }
//        return encodedURL;
    }


    public String encodeResourceURL(String url) {
        if (url == null) {
            throw new NullPointerException();
        }

        //typically the URL is returned unchanged
        String encodedURL = url;
        switch (currentEnvironment) {

            case SERVLET_ENVIRONMENT:
                //we are currrently using a null servletResponse
                if (null != servletResponse) {
                    encodedURL = servletResponse.encodeURL(url);
                }
                break;

            case PORTLET_ENVIRONMENT:
                encodedURL = portletResponse.encodeURL(url);
                break;
        }
        return encodedURL;
    }


    public String encodeNamespace(String name) {
        //TODO
        //Not sure what or why we are doing this here. Hopefully more correct
        //logic is below.
        // return ("NAMESPACE: " + name);

        if (name == null) {
            throw new NullPointerException();
        }

        switch (currentEnvironment) {

            case SERVLET_ENVIRONMENT:
                //spec says don't do anything
                break;

            case PORTLET_ENVIRONMENT:
                if (portletResponse instanceof ActionResponse) {
                    throw new IllegalStateException();
                } else if (portletResponse instanceof RenderResponse) {
                    StringBuffer buf = new StringBuffer(
                            ((RenderResponse) portletResponse).getNamespace());
                    buf.append(name);
                    name = buf.toString();
                }
                break;
        }
        return name;
    }

    public void dispatch(String path)
            throws IOException, FacesException {
        RequestDispatcher requestDispatcher =
                servletRequest.getRequestDispatcher(path);
        try {
            requestDispatcher.forward(
                    this.servletRequest, this.servletResponse);
        } catch (IOException ioe) {
            throw ioe;
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
        if (message == null) {
            message = "(no message)";
        }

        switch (currentEnvironment) {

            case SERVLET_ENVIRONMENT:
                servletContext.log(message);
                break;

            case PORTLET_ENVIRONMENT:
                portletContext.log(message);
                break;
        }
    }


    public void log(String message, Throwable throwable) {
        if (message == null) {
            message = "(no message)";
        }

        if (throwable == null) {
            StringBuffer buff = new StringBuffer(message);
            buff.append(" ");
            buff.append("[Throwable was null]");
            log(buff.toString());
        } else {
            switch (currentEnvironment) {

                case SERVLET_ENVIRONMENT:
                    servletContext.log(message, throwable);
                    break;

                case PORTLET_ENVIRONMENT:
                    portletContext.log(message, throwable);
                    break;
            }
        }

    }


    public String getAuthType() {
        String authType = null;
        switch (currentEnvironment) {

            case SERVLET_ENVIRONMENT:
                authType = servletRequest.getAuthType();
                break;

            case PORTLET_ENVIRONMENT:
                authType = portletRequest.getAuthType();
                break;
        }
        return authType;
    }

    public String getRemoteUser() {
        String remoteUser = null;
        switch (currentEnvironment) {

            case SERVLET_ENVIRONMENT:
                remoteUser = servletRequest.getRemoteUser();
                break;

            case PORTLET_ENVIRONMENT:
                remoteUser = portletRequest.getRemoteUser();
                break;
        }
        return remoteUser;
    }

    public java.security.Principal getUserPrincipal() {
        java.security.Principal principal = null;
        switch (currentEnvironment) {

            case SERVLET_ENVIRONMENT:
                principal = servletRequest.getUserPrincipal();
                break;

            case PORTLET_ENVIRONMENT:
                principal = portletRequest.getUserPrincipal();
                break;
        }
        return principal;
    }

    public boolean isUserInRole(String role) {
        if (role == null) {
            throw new NullPointerException();
        }

        boolean inRole = false;
        switch (currentEnvironment) {

            case SERVLET_ENVIRONMENT:
                inRole = servletRequest.isUserInRole(role);
                break;

            case PORTLET_ENVIRONMENT:
                inRole = portletRequest.isUserInRole(role);
                break;
        }
        return inRole;
    }

}

class RequestParameterValuesMap extends AbstractMap {

    private Hashtable map = new Hashtable();

    public Object get(Object key) {
        if (key == null) {
            throw new NullPointerException();
        }
        return getParameterValues(key.toString());
    }

    private String[] getParameterValues(String key) {
        Object parameterValues = map.get(key);
        if (parameterValues instanceof String) {
            String[] singlePointArray = {(String) parameterValues};
            return singlePointArray;
        } else if (parameterValues instanceof List) {
            List parameterList = (List) parameterValues;
            int size = parameterList.size();
            String[] multiplePointArray = new String[size];
            for (int i = 0; i < size; i++) {
                multiplePointArray[i] = (String) parameterList.get(i);
            }
            return multiplePointArray;
        }
        return null;
    }

    public Set entrySet() {
        return map.entrySet();
    }

    public Object put(Object key, Object value) {
        Object put = map.put(key, value);
        return put;
    }

}
