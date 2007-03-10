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

package com.icesoft.faces.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import java.lang.reflect.Method;

/**
 * This is the ICEfaces implementation of the FacesContextFactory.  We take
 * advantage of the delegation design provided by the JSF spec to create our
 * factory with a delegate factory. We then check to see if requests are being
 * handled by one of our PersistentFaces classes. This is accomplished by
 * inserting a known attribute into the request.  If the attribute is present,
 * then we know that the request for a FacesContext originated from an ICEfaces
 * servlet (or portlet).  If not, we delegate the call to the "parent" factory.
 * This allows us to run in "plain" Faces mode while still making use of our own
 * ViewHandler and renders.
 */
public class FacesContextFactoryImpl extends FacesContextFactory {

    protected static Log log = LogFactory.getLog(FacesContextFactoryImpl.class);

    public static final String SERVLET_KEY = "servletkey";
    public static final String PERSISTENT = "persistent";

    private FacesContextFactory delegate;

    public FacesContextFactoryImpl() {

    }

    public FacesContextFactoryImpl(FacesContextFactory delegate) {
        this.delegate = delegate;
    }

    public FacesContext getFacesContext(Object context, Object request,
                                        Object response,
                                        Lifecycle lifecycle)
            throws FacesException {

        // If anything is null, we're not good to go.
        if (context == null || request == null ||
                response == null || lifecycle == null) {
            throw new NullPointerException();
        }

        //If ICEfaces should not be handling this, then delegate the responsibility of
        //providing the FacesContext to the delegated FacesContextFactory.
        if (shouldDelegate(request)) {
            return delegate
                    .getFacesContext(context, request, response, lifecycle);
        }

        // Create a new external context that wraps up the context for the environment that
        // we're currently running in as well as the request and response objects.  The
        // BridgeExternalContext is responsible for differentiating the type of environment
        // and delegating the calls appropriately.
        if (context instanceof ServletContext) {
            return null;
        } else {
            throw new IllegalStateException("Unknown environment");
        }
    }

    private boolean shouldDelegate(Object request) {
        if (delegate == null) {
            return false;
        }
        //We must run without portlet.jar being present, so cannot use
        //instanceof PortletRequest
        //if (request instanceof PortletRequest) {
        Method getAttributeMethod = null;
        Class portletRequestClass = null;
        try {
            portletRequestClass =
                    Class.forName("javax.portlet.PortletRequest");
            getAttributeMethod = portletRequestClass
                    .getMethod("getAttribute", new Class[]{String.class});
        } catch (Throwable t) {
            if (log.isDebugEnabled()) {
                log.debug("Portlet classes not available");
            }
        }

        if ((null != portletRequestClass) &&
                (portletRequestClass.isInstance(request))) {
            //now use our reflectively obtained method to get another attribute
            String portletType = null;
            try {
                portletType = (String) getAttributeMethod
                        .invoke(request, new Object[]{SERVLET_KEY});
            } catch (Exception e) {
                if (log.isWarnEnabled()) {
                    log.warn("Reflection failure: request.getAttribute", e);
                }
            }

            if (portletType != null &&
                    portletType.equalsIgnoreCase(PERSISTENT)) {
                return false;
            }
        } else if (request instanceof ServletRequest) {
            String servletType = (String) ((ServletRequest) request)
                    .getAttribute(SERVLET_KEY);
            if (servletType != null &&
                    servletType.equalsIgnoreCase(PERSISTENT)) {
                return false;
            }
        }

        return true;
    }

}
