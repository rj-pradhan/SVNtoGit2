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

import javax.portlet.PortalContext;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.WindowState;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;

/**
 * A wrapper for PortletRequest.
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
public class PortletEnvironmentRequest
        extends ServletEnvironmentRequest
        implements PortletRequest {

    private Hashtable properties;
    private PortletRequest portletRequest;
    private PortletMode portletMode;
    private WindowState windowState;
    private PortletPreferences portletPreferences;
    private PortletSession portletSession;
    private PortalContext portalContext;
    private String responseContentType;
    private Vector responseContentTypes;


    public PortletEnvironmentRequest(PortletRequest portletRequest) {
        copyPortletRequestData(portletRequest);
    }

    private void copyPortletRequestData(PortletRequest req) {

        portletRequest = req;

        //Copy common data
        authType = req.getAuthType();
        contextPath = req.getContextPath();
        remoteUser = req.getRemoteUser();
        userPrincipal = req.getUserPrincipal();
        requestedSessionId = req.getRequestedSessionId();
        isRequestedSessionIdValid = req.isRequestedSessionIdValid();

        attributes = new Hashtable();
        Enumeration items = req.getAttributeNames();
        String name = null;
        while (items.hasMoreElements()) {
            name = (String) items.nextElement();
            Object value = req.getAttribute(name);
            if (null != value) {
                attributes.put(name, value);
            }
        }

        parameters = new Hashtable();
        items = req.getParameterNames();
        while (items.hasMoreElements()) {
            name = (String) items.nextElement();
            Object value = req.getParameterValues(name);
            if (null != value) {
                parameters.put(name, req.getParameterValues(name));
            }
        }

        scheme = req.getScheme();
        serverName = req.getServerName();
        serverPort = req.getServerPort();
        locale = req.getLocale();

        locales = new Vector();
        items = req.getLocales();
        while (items.hasMoreElements()) {
            locales.add((Locale) items.nextElement());
        }

        isSecure = req.isSecure();

        //Copy portlet specific data
        properties = new Hashtable();
        items = req.getPropertyNames();
        while (items.hasMoreElements()) {
            name = (String) items.nextElement();
            properties.put(name, req.getProperties(name));
        }

        portletMode = req.getPortletMode();
        windowState = req.getWindowState();
        portletPreferences = req.getPreferences();
        portletSession = req.getPortletSession();
        portalContext = req.getPortalContext();
        responseContentType = req.getResponseContentType();

        responseContentTypes = new Vector();
        items = req.getResponseContentTypes();
        while (items.hasMoreElements()) {
            responseContentTypes.add((String) items.nextElement());
        }
    }


    /**
     * Common HttpServletRequest/PortletRequest methods
     */

    public boolean isUserInRole(String role) {
        return portletRequest.isUserInRole(role);
    }


    /**
     * PortletRequest methods
     */

    public boolean isWindowStateAllowed(WindowState state) {
        return portletRequest.isWindowStateAllowed(state);
    }

    public boolean isPortletModeAllowed(PortletMode mode) {
        return portletRequest.isPortletModeAllowed(mode);
    }

    public PortletMode getPortletMode() {
        return portletMode;
    }

    public WindowState getWindowState() {
        return windowState;
    }

    public PortletPreferences getPreferences() {
        return portletPreferences;
    }

    public PortletSession getPortletSession() {
        return portletSession;
    }

    public PortletSession getPortletSession(boolean create) {
        return portletRequest.getPortletSession(create);
    }

    public String getProperty(String name) {
        Enumeration allProps = (Enumeration) properties.get(name);
        if (allProps.hasMoreElements()) {
            return (String) allProps.nextElement();
        }
        return null;
    }

    public Enumeration getProperties(String name) {
        return (Enumeration) properties.get(name);
    }

    public Enumeration getPropertyNames() {
        return properties.keys();
    }

    public PortalContext getPortalContext() {
        return portalContext;
    }

    public String getResponseContentType() {
        return responseContentType;
    }

    public Enumeration getResponseContentTypes() {
        return responseContentTypes.elements();
    }

}
