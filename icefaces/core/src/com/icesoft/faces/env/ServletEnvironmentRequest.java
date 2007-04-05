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

package com.icesoft.faces.env;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * A wrapper for HttpServletRequest.
 * <p/>
 * It is up to the user to ensure that casts to this specific type and use the
 * specific methods if you are running in the appropriate environment.  Also,
 * since we wrap real requests, the state of those requests can get changed by
 * the application server, so it's possible that certain calls may result in
 * exceptions being thrown.
 * <p/>
 * Note:  This class may not be completely finished.  We have only included what
 * we've needed to this point. Also, the proper operations of some methods may
 * not make sense (like getting the reader or the input stream) so they simply
 * return null and we don't call them on the underlying instance either.
 */
public class ServletEnvironmentRequest
        extends CommonEnvironmentRequest
        implements HttpServletRequest {

    protected static Log log =
            LogFactory.getLog(ServletEnvironmentRequest.class);
    private static String ACEGI_AUTH_CLASS = "org.acegisecurity.Authentication";
    HttpServletRequest servletRequest;
    private String localName;
    private String localAddr;
    private int localPort;
    private Hashtable headers;
    private Cookie[] cookies;
    private String method;
    private String pathInfo;
    private String pathTranslated;
    private String queryString;
    private String requestURI;
    private StringBuffer requestURL;
    private String servletPath;
    private HttpSession servletSession;
    private boolean isRequestedSessionIdFromCookie;
    private boolean isRequestedSessionIdFromURL;
    private String characterEncoding;
    private int contentLength;
    private String contentType;
    private String protocol;
    private String remoteAddr;
    private String remoteHost;
    private int remotePort;
    private AcegiAuthWrapper acegiAuthWrapper;
    private static Class acegiAuthClass;

    static {
        try {
            acegiAuthClass = Class.forName(ACEGI_AUTH_CLASS);
            if (log.isDebugEnabled()) {
                log.debug("Acegi Security engaged.");
            }
        } catch (Throwable t) {
            if (log.isDebugEnabled()) {
                log.debug("Acegi Security not detected.");
            }
        }
    }

    public ServletEnvironmentRequest() {
    }

    public ServletEnvironmentRequest(HttpServletRequest servletRequest) {
        copyServletRequestData(servletRequest);
    }

    private void copyServletRequestData(HttpServletRequest req) {

        servletRequest = req;

        //Copy common data
        authType = req.getAuthType();
        contextPath = req.getContextPath();
        remoteUser = req.getRemoteUser();
        userPrincipal = req.getUserPrincipal();
        if (null != acegiAuthClass) {
            if (acegiAuthClass.isInstance(userPrincipal)) {
                acegiAuthWrapper = new AcegiAuthWrapper(userPrincipal);
            }
        }
        requestedSessionId = req.getRequestedSessionId();
        isRequestedSessionIdValid = req.isRequestedSessionIdValid();

        attributes = new Hashtable();
        Enumeration items = req.getAttributeNames();
        String name = null;
        while (items.hasMoreElements()) {
            name = (String) items.nextElement();
            Object attribute = req.getAttribute(name);
            if ((null != name) && (null != attribute)) {
                attributes.put(name, attribute);
            }
        }

        headers = new Hashtable();
        items = req.getHeaderNames();
        name = null;
        while (items.hasMoreElements()) {
            name = (String) items.nextElement();
            headers.put(name, req.getHeaders(name));
        }

        parameters = new Hashtable();
        items = req.getParameterNames();
        while (items.hasMoreElements()) {
            name = (String) items.nextElement();
            attributes.put(name, req.getParameterValues(name));
        }

        scheme = req.getScheme();
        serverName = req.getServerName();
        serverPort = req.getServerPort();
        locale = req.getLocale();

        locales = new Vector();
        items = req.getLocales();
        while (items.hasMoreElements()) {
            locales.add(items.nextElement());
        }

        isSecure = req.isSecure();

        //Copy servlet specific data
        cookies = req.getCookies();
        method = req.getMethod();
        pathInfo = req.getPathInfo();
        pathTranslated = req.getPathTranslated();
        queryString = req.getQueryString();
        requestURI = req.getRequestURI();
        requestURL = req.getRequestURL();
        servletPath = req.getServletPath();
        servletSession = req.getSession();
        isRequestedSessionIdFromCookie = req.isRequestedSessionIdFromCookie();
        isRequestedSessionIdFromURL = req.isRequestedSessionIdFromURL();
        characterEncoding = req.getCharacterEncoding();
        contentLength = req.getContentLength();
        contentType = req.getContentType();
        protocol = req.getProtocol();
        remoteAddr = req.getRemoteAddr();
        remoteHost = req.getRemoteHost();

        //Servlet 2.4 Stuff
//        localName = req.getLocalName();
//        localAddr = req.getLocalAddr();
//        localPort = req.getLocalPort();
//        remotePort = req.getRemotePort();

    }


    /**
     * Common HttpServletRequest/PortletRequest methods
     */

    public boolean isUserInRole(String role) {
        if (null != acegiAuthWrapper) {
            return acegiAuthWrapper.isUserInRole(role);
        }
        return servletRequest.isUserInRole(role);
    }


    /**
     * HttpServletRequest methods
     */

    public Cookie[] getCookies() {
        return cookies;
    }

    public long getDateHeader(String name) {
        String header = getHeader(name);
        if (header == null) {
            return -1;
        }
        //TODO
        //Convert header string to a date
        return -1;
    }

    public String getHeader(String name) {
        Enumeration allProps = (Enumeration) headers.get(name);
        if (allProps.hasMoreElements()) {
            return (String) allProps.nextElement();
        }
        return null;
    }

    public Enumeration getHeaders(String name) {
        return (Enumeration) headers.get(name);
    }

    public Enumeration getHeaderNames() {
        return headers.keys();
    }

    public int getIntHeader(String name) {
        String header = getHeader(name);
        if (header == null) {
            return -1;
        }
        return Integer.parseInt(name, -1);
    }

    public String getMethod() {
        return method;
    }

    public String getPathInfo() {
        return pathInfo;
    }

    public String getPathTranslated() {
        return pathTranslated;
    }

    public String getQueryString() {
        return queryString;
        //return (contextServletPath.substring(contextServletPath.indexOf("/")));
    }

    public String getRequestURI() {
        return requestURI;
    }

    public StringBuffer getRequestURL() {
        return requestURL;
        //return new StringBuffer(contextServletPath);
    }

    public String getServletPath() {
        return servletPath;
        //return (contextServletPath.substring(contextServletPath.lastIndexOf("/")));
    }

    public HttpSession getSession(boolean create) {
        return servletRequest.getSession(create);
    }

    public HttpSession getSession() {
        return servletSession;
    }

    public boolean isRequestedSessionIdFromCookie() {
        return isRequestedSessionIdFromCookie;
    }

    public boolean isRequestedSessionIdFromURL() {
        return isRequestedSessionIdFromURL;
    }

    public boolean isRequestedSessionIdFromUrl() {
        return isRequestedSessionIdFromURL();
    }

    public String getCharacterEncoding() {
        return characterEncoding;
    }

    public void setCharacterEncoding(String encoding)
            throws java.io.UnsupportedEncodingException {
        characterEncoding = encoding;
    }

    public int getContentLength() {
        return contentLength;
    }

    public String getContentType() {
        return contentType;
    }

    public javax.servlet.ServletInputStream getInputStream()
            throws java.io.IOException {
        return null;
    }

    public String getProtocol() {
        return protocol;
//        return "javascript";
    }

    public java.io.BufferedReader getReader() throws java.io.IOException {
        return null;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public javax.servlet.RequestDispatcher getRequestDispatcher(String name) {
        return null;
    }

    /**
     * @deprecated
     */
    public String getRealPath(String path) {
        return servletRequest.getRealPath(path);
    }

    public int getRemotePort() {
        return remotePort;
    }

    public String getLocalName() {
        return localName;
    }

    public String getLocalAddr() {
        return localAddr;
    }

    public int getLocalPort() {
        return localPort;
    }

}
