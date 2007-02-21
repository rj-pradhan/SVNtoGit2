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

package com.icesoft.faces.webapp.xmlhttp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.webapp.FacesServlet;
import javax.portlet.PortletConfig;
import javax.servlet.ServletConfig;
import java.util.HashMap;

/**
 * This class is designed to hold all the common fields and methods of
 * PersistentFacesServlet and PersistentFacesPortlet.  Due to lack of multiple
 * inheritance in Java, we can't easily create a common superclass so instead,
 * the servlet and portlet will create an instance of this class and use it for
 * common stuff in a delegation-type way.
 */
public class PersistentFacesCommonlet {
    private static final Log log =
            LogFactory.getLog(PersistentFacesCommonlet.class);
    //FacesServlet stuff
    private Application application;
    private Lifecycle lifecycle;
    private FacesContextFactory facesContextFactory;
    //ICEfaces stuff
    public static final String SERVLET_KEY = "servletkey";
    public static final String REQUEST_PATH_KEY = "requestpathkey";
    public static final String PERSISTENT = "persistent";
    public static final String CONCURRENT_DOM_VIEWS =
            "com.icesoft.faces.concurrentDOMViews";
    public static final String REMOVE_SEAM_CONTEXTS =
            "com.icesoft.faces.removeSeamContexts";
    public static final String SEAM_LIFECYCLE_SHORTCUT =
            "com.icesoft.faces.shortcutLifecycle";
    boolean concurrentDOMViews = false;


    /**
     * Make sure we do all of the normal setup that is usually accomplished in
     * the standard FacesServlet - a concrete class that is part of the public
     * API.
     */
    void init(HashMap params) {
//
        try {
            //Get the FacesContextFactory implementation
            facesContextFactory = (FacesContextFactory)
                    FactoryFinder.getFactory
                            (FactoryFinder.FACES_CONTEXT_FACTORY);

            //Get the Application implementation
            ApplicationFactory applicationFactory = (ApplicationFactory)
                    FactoryFinder.getFactory(FactoryFinder.APPLICATION_FACTORY);
            application = applicationFactory.getApplication();

            //Get the Lifecycle implementation
            LifecycleFactory lifecycleFactory = (LifecycleFactory)
                    FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
            String lifecycleId =
                    (String) params.get(FacesServlet.LIFECYCLE_ID_ATTR);
            if (lifecycleId == null) {
                lifecycleId = LifecycleFactory.DEFAULT_LIFECYCLE;
            }
            lifecycle = lifecycleFactory.getLifecycle(lifecycleId);

        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("ICEfaces could not initialize JavaServer Faces. "
                          + "Please check that the JSF .jar files are "
                          + "installed correctly." + e.getMessage(), e);
            }
            throw new RuntimeException(e);
        }

    }

    /**
     * Release the various Faces resources
     */
    void destroy() {
        facesContextFactory = null;
        application = null;
        lifecycle = null;
    }

    /**
     * Another one of those lovely areas where the servlet and portlet APIs
     * don't share anything in common so we end up having two methods that do
     * pretty much the exact same thing but on a different object.
     *
     * @param config
     * @return map of init params
     */
    HashMap getInitParams(ServletConfig config) {
        HashMap initParams = new HashMap();
        initParams.put(FacesServlet.LIFECYCLE_ID_ATTR,
                       config.getInitParameter(FacesServlet.LIFECYCLE_ID_ATTR));
        return initParams;
    }

    HashMap getInitParams(PortletConfig config) {
        HashMap initParams = new HashMap();
        initParams.put(FacesServlet.LIFECYCLE_ID_ATTR,
                       config.getInitParameter(FacesServlet.LIFECYCLE_ID_ATTR));
        return initParams;
    }

    public Lifecycle getLifecycle() {
        return lifecycle;
    }

    public Application getApplication() {
        return application;
    }

    public FacesContextFactory getFacesContextFactory() {
        return facesContextFactory;
    }

    public FacesContext getFacesContext(Object context, Object request,
                                        Object response) {
        return facesContextFactory
                .getFacesContext(context, request, response, lifecycle);
    }

}
