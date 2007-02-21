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

import com.icesoft.faces.context.SessionMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;

public abstract class FileUploadServlet extends HttpServlet {
    protected static final Log log = LogFactory.getLog(FileUploadServlet.class);
    protected ServletConfig config;

    public void init(ServletConfig config) throws ServletException {
        this.config = config;
    }

    protected void render(HttpSession session, PersistentFacesState state) {

        if (state == null) {
            state = getState(session, state);
        }

        if (state != null) {
            try {
                state.render();
            } catch (RenderingException e) {
                if (log.isDebugEnabled()) {
                    log.debug(e.getMessage());
                }
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("null PersistentFacesState");
            }
        }
    }

    protected void persistentStateClearInstance() {
        PersistentFacesState.clearLocalInstance();
    }

    protected void execute(HttpSession session, PersistentFacesState state) {
        if (state == null) {
            state = getState(session, state);
        }

        if (state != null) {
            try {
                state.execute();
            } catch (RenderingException e) {
                if (log.isDebugEnabled()) {
                    log.debug(e.getMessage());
                }
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("null PersistentFacesState");
            }
        }
    }

    private PersistentFacesState getState(HttpSession session,
                                          PersistentFacesState state) {
        Object current_view_number = session.getAttribute(
                PersistentFacesServlet.CURRENT_VIEW_NUMBER);
        if (current_view_number != null) {
            PersistentFacesState.setLocalInstance(new SessionMap(session),
                                                  String.valueOf(
                                                          current_view_number));
        }
        state = PersistentFacesState.getInstance();
        return state;
    }
}
