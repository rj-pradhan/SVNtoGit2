package com.icesoft.faces.webapp.http.portlet;

import javax.portlet.PortalContext;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.WindowState;
import java.security.Principal;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * WARNING! WARNING! WARNING!
 * <p/>
 * This class is temporary and should not be relied on as it is extremely likely
 * not to be the final solutions.
 * <p/>
 * Because we use a dispatcher to pass requests from the MainPortlet to the
 * MainServlet, the resulting portlet artifacts (like request, response, etc.)
 * are wrapped to look like servlet artifacts and certain types and APIs that
 * are specific to portlets are not available to the developer.  As a temporary
 * solution, we provide this class stored as a request attribute that can be
 * retrieved.
 * <p/>
 * It implements two interfaces:  PortletConfig and PortletRequest but many of
 * the exposed methods are not implemented.  For now, we are only exposing what
 * certain customers require.  Some of the information is exposed through normal
 * JSF mechanisms when the request is dispatched so should be accessed through
 * JSF APIs.
 */
public class PortletArtifactHack
        implements PortletConfig, PortletRequest {

    public static final String PORTLET_HACK_KEY =
            "com.icesoft.faces.portletHack";

    private PortletConfig portletConfig;
    private PortletRequest request;

    public PortletArtifactHack(PortletConfig portletConfig,
                               PortletRequest request) {
        this.portletConfig = portletConfig;
        this.request = request;
    }

    public boolean isWindowStateAllowed(WindowState windowState) {
        return request.isWindowStateAllowed(windowState);
    }

    public boolean isPortletModeAllowed(PortletMode portletMode) {
        return request.isPortletModeAllowed(portletMode);
    }

    public PortletMode getPortletMode() {
        return request.getPortletMode();
    }

    public WindowState getWindowState() {
        return request.getWindowState();
    }

    public PortletPreferences getPreferences() {
        return request.getPreferences();
    }

    public PortletSession getPortletSession() {
        return request.getPortletSession();
    }

    public PortletSession getPortletSession(boolean create) {
        return request.getPortletSession(create);
    }

    public String getProperty(String string) {
        throw new UnsupportedOperationException();
    }

    public Enumeration getProperties(String string) {
        throw new UnsupportedOperationException();
    }

    public Enumeration getPropertyNames() {
        throw new UnsupportedOperationException();
    }

    public PortalContext getPortalContext() {
        return request.getPortalContext();
    }

    public String getAuthType() {
        throw new UnsupportedOperationException();
    }

    public String getContextPath() {
        throw new UnsupportedOperationException();
    }

    public String getRemoteUser() {
        throw new UnsupportedOperationException();
    }

    public Principal getUserPrincipal() {
        throw new UnsupportedOperationException();
    }

    public boolean isUserInRole(String string) {
        throw new UnsupportedOperationException();
    }

    public Object getAttribute(String string) {
        throw new UnsupportedOperationException();
    }

    public Enumeration getAttributeNames() {
        throw new UnsupportedOperationException();
    }

    public String getParameter(String string) {
        throw new UnsupportedOperationException();
    }

    public Enumeration getParameterNames() {
        throw new UnsupportedOperationException();
    }

    public String[] getParameterValues(String string) {
        throw new UnsupportedOperationException();
    }

    public Map getParameterMap() {
        throw new UnsupportedOperationException();
    }

    public boolean isSecure() {
        throw new UnsupportedOperationException();
    }

    public void setAttribute(String string, Object object) {
        throw new UnsupportedOperationException();
    }

    public void removeAttribute(String string) {
        throw new UnsupportedOperationException();
    }

    public String getRequestedSessionId() {
        throw new UnsupportedOperationException();
    }

    public boolean isRequestedSessionIdValid() {
        throw new UnsupportedOperationException();
    }

    public String getResponseContentType() {
        throw new UnsupportedOperationException();
    }

    public Enumeration getResponseContentTypes() {
        throw new UnsupportedOperationException();
    }

    public Locale getLocale() {
        throw new UnsupportedOperationException();
    }

    public Enumeration getLocales() {
        throw new UnsupportedOperationException();
    }

    public String getScheme() {
        throw new UnsupportedOperationException();
    }

    public String getServerName() {
        throw new UnsupportedOperationException();
    }

    public int getServerPort() {
        throw new UnsupportedOperationException();
    }

    public String getPortletName() {
        return portletConfig.getPortletName();
    }

    public PortletContext getPortletContext() {
        return portletConfig.getPortletContext();
    }

    public ResourceBundle getResourceBundle(Locale locale) {
        throw new UnsupportedOperationException();
    }

    public String getInitParameter(String string) {
        throw new UnsupportedOperationException();
    }

    public Enumeration getInitParameterNames() {
        throw new UnsupportedOperationException();
    }
}
