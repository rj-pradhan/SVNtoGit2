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

package com.icesoft.faces.webapp.parser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import javax.servlet.jsp.PageContext;
import java.util.List;

/**
 * For ICEfaces to support JSF-RI, MyFaces, or any other future JSF
 * implementations, it may require some logic specific to the implementation.
 * Obviously this is not a good thing but it may be unavoidable if we need to
 * access something under the hood that isn't available through public API
 * calls.  This class is available to encapsulate implementation specific
 * anomalies.
 */
public class ImplementationUtil {

    /**
     * Logging instance for this class.
     */
    protected static Log log = LogFactory.getLog(ImplementationUtil.class);


    /**
     * Boolean values to track which implementation we are running under.
     */
    private static boolean isRI = false;
    private static boolean isMyFaces = false;

    /**
     * Marker classes whose presence we used to detect which implementation we
     * are running under.
     */
    private static String RI_MARKER =
            "com.sun.faces.application.ApplicationImpl";
    private static String MYFACES_MARKER =
            "org.apache.myfaces.application.ApplicationImpl";

    /**
     * In a couple of places, we need to access the component stack from the
     * PageContext and the key used to do this is implemenation dependent.  So
     * here is where we track the keys and provide appropriate value depending
     * on the implementation we are running under.
     */
    private static String RI_COMPONENT_STACK_KEY =
            "javax.faces.webapp.COMPONENT_TAG_STACK";

    private static String MYFACES_COMPONENT_STACK_KEY =
            "javax.faces.webapp.UIComponentTag.COMPONENT_STACK";


    static {
        try {
            Class.forName(RI_MARKER);
            isRI = true;
        } catch (ClassNotFoundException e) {
        }

        try {
            Class.forName(MYFACES_MARKER);
            isMyFaces = true;
        } catch (ClassNotFoundException e) {
        }

        if (log.isTraceEnabled()) {
            log.trace("JSF-RI: " + isRI + "  MyFaces: " + isMyFaces);
        }

    }

    /**
     * Identifies if the JSF implementation we are running in is Sun's JSF
     * reference implemenation (RI).
     *
     * @return true if the JSF implementation is Sun's JSF reference
     *         implemenation
     */
    public static boolean isRI() {
        return isRI;
    }

    /**
     * Identifies if the JSF implementation we are running in is Apache's
     * MyFaces implementation.
     *
     * @return true if the JSF implementation is Apache MyFaces.
     */
    public static boolean isMyFaces() {
        return isMyFaces;
    }

    /**
     * Returns the key used to get the component stack from the PageContext. The
     * key is a private member of UIComponentTag class but differs between the
     * known JSF implementations.  We detect the correct implementation in this
     * class and provide the proper key.
     *
     * @return String
     */
    public static String getComponentStackKey() {
        String key = null;
        if (isRI) {
            key = RI_COMPONENT_STACK_KEY;
        } else if (isMyFaces) {
            key = MYFACES_COMPONENT_STACK_KEY;
        }

        if (key != null) {
            return key;
        }

        if (log.isFatalEnabled()) {
            log.fatal(
                    "cannot detect JSF implementation so cannot determine component stack key");
        }

        throw new UnknownJSFImplementationException(
                "cannot determine component stack key");
    }

    /**
     * Returns the component tag stack, including checking in the current
     * request as is the strategy of JSF 1.1_02
     * @param pageContext
     * @return list being the component tag stack
     */
    public static List getComponentStack(PageContext pageContext) {
        List list = (List) pageContext.getAttribute(
                getComponentStackKey(), PageContext.REQUEST_SCOPE);
        if (null == list) {
            list = (List) FacesContext.getCurrentInstance()
                    .getExternalContext().getRequestMap().get(
                    getComponentStackKey());
        }
        return list;

    }
}

class UnknownJSFImplementationException extends FacesException {

    public UnknownJSFImplementationException() {
    }

    public UnknownJSFImplementationException(Throwable cause) {
        super(cause);
    }

    public UnknownJSFImplementationException(String message) {
        super(message);
    }

    public UnknownJSFImplementationException(String message, Throwable cause) {
        super(message, cause);
    }

}
