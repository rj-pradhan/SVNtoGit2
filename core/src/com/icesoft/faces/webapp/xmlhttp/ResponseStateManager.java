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

import javax.portlet.PortletSession;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The ResponseStateManager is responsible for creating and maintaining the
 * collection of response states. This implementation provides the basic
 * functionality for the ICEfaces framework by creating and handling
 * BlockingResponseStates as required.
 */
public class ResponseStateManager {

    public final static String ICEFACES_ID_KEY = "icefacesID";
    public final static String RESPONSE_STATE =
            "com.icesoft.faces.ResponseState";

    //TODO - It's highly questionable that this is the best way to determine which
    //manager to use.  We should discuss how we really want people to configure
    //the enterprise edition and make the appropriate changes.

    private static Log log =
            LogFactory.getLog(ResponseStateManager.class);

    private static final String ASYNC_SERVER_KEY =
            "com.icesoft.faces.async.server";
    private static final String ASYNC_RESPONSE_STATE_MANAGER_CLASS_NAME =
            "com.icesoft.faces.async.server.AsyncResponseStateManager";

    private static ResponseStateManager mgr;

    private static boolean hasContinuations = false;

    static {
        try {
            Class continuationClass =
                    Class.forName("org.mortbay.util.ajax.Continuation");
            if (null != continuationClass)  {
                hasContinuations = true;
            }
        } catch (Throwable t)  {
            if (log.isDebugEnabled()) {
                log.debug("continuations not supported " + t.getMessage());
            }
        }
    }

    public synchronized static ResponseStateManager getResponseStateManager(
            ServletContext context) {

        //Return the existing manager if it exists
        if (mgr != null) {
            return mgr;
        }

        boolean tryAsync = false;
        try {
            tryAsync =
                    Boolean.valueOf(
                            context.getInitParameter(ASYNC_SERVER_KEY))
                            .booleanValue();
        } catch (NullPointerException e) {
            tryAsync = false;
            if (log.isDebugEnabled()) {
                log.debug("com.icesoft.faces.async.server not defined.");
            }
        }

        //If we are supposed to try and use the Async server then reflectively try
        //and load it so we don't have any dependencies on the class.
        if (tryAsync) {
            try {
                mgr =
                        (ResponseStateManager)
                                Class.forName(
                                        ASYNC_RESPONSE_STATE_MANAGER_CLASS_NAME)
                                        .
                                                newInstance();
            } catch (Exception e) {
                mgr = new ResponseStateManager();
                if (log.isDebugEnabled()) {
                    log.debug("AsyncResponseStateManager class not found.");
                }
            }

        } else {
            mgr = new ResponseStateManager();
        }

        if (log.isInfoEnabled()) {
            log.info("using response state manager: " +
                     mgr.getClass().getName());
        }
        return mgr;
    }

    public ResponseState createState(HttpSession session, String iceID,
                                     String viewNumber) {
        if (hasContinuations)  {
            return new ContinuationResponseState( session, iceID, viewNumber );
        }
        return new BlockingResponseState(session, iceID, viewNumber);
    }

    public ResponseState createState(PortletSession session, String iceID,
                                     String viewNumber) {
        return new PortletBlockingResponseState(session, iceID, viewNumber);
    }

    synchronized ResponseState getState(HttpSession session,
                                        String viewNumber) {

        String iceID = (String) session
                .getAttribute(ResponseStateManager.ICEFACES_ID_KEY);
        ResponseState state = null;
        synchronized(iceID)  {
            state =
                    (ResponseState) session.getAttribute(getStateKey(viewNumber));
            if (null == state) {
                state = createState(session, iceID, viewNumber);
                session.setAttribute(getStateKey(viewNumber), state);
            }
        }
        return state;
    }

    ResponseState getState(PortletSession session,
                                        String viewNumber) {

        String iceID = (String) session.getAttribute(
                ResponseStateManager.ICEFACES_ID_KEY,
                PortletSession.APPLICATION_SCOPE);
        ResponseState state = null;
        synchronized(iceID) {
            state = (ResponseState) session.getAttribute(
                    getStateKey(viewNumber), PortletSession.APPLICATION_SCOPE);
            if (null == state) {
                state = createState(session, iceID, viewNumber);
                session.setAttribute(getStateKey(viewNumber), state,
                                     PortletSession.APPLICATION_SCOPE);
            }
        }
        return state;
    }

    String getStateKey(String viewNumber) {
        //TODO - might be better to reverse these for searching purposes
        return (viewNumber + "/" + RESPONSE_STATE);
    }
}
