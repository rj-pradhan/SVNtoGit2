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

package com.icesoft.faces.util.event.servlet;

import java.util.EventObject;

import javax.servlet.http.HttpSession;

/**
 * The <code>AbstractSessionEvent</code> class provides a default implementation
 * of a session event. Standard behaviors like the get methods of HTTP session
 * and ICEfaces ID properties are defined here.
 */
public abstract class AbstractSessionEvent
        extends EventObject
        implements ContextEvent {
    protected String iceFacesId;

    /**
     * Constructs an <code>AbstractSessionEvent</code> with the specified
     * <code>source</code> and <code>iceFacesId</code>. </p>
     *
     * @param source     the source of this <code>AbstractSessionEvent</code>.
     * @param iceFacesId the ICEfaces ID.
     * @throws IllegalArgumentException if the one of the following happens:
     *                                  <ul> <li> the specified
     *                                  <code>source</code> is
     *                                  <code>null</code>. </li> <li> the
     *                                  specified <code>iceFacesId</code> is
     *                                  either <code>null</code> or empty. </li>
     *                                  </ul>
     */
    protected AbstractSessionEvent(HttpSession source, String iceFacesId)
            throws IllegalArgumentException {
        super(source);
        if (iceFacesId == null) {
            throw new IllegalArgumentException("iceFacesId is null");
        }
        if (iceFacesId.trim().length() == 0) {
            throw new IllegalArgumentException("iceFacesId is empty");
        }
        this.iceFacesId = iceFacesId;
    }

    /**
     * Gets the HTTP session of this <code>AbstractSessionEvent</code>. </p>
     *
     * @return the HTTP session.
     */
    public HttpSession getHttpSession() {
        return (HttpSession) source;
    }

    /**
     * Gets the ICEfaces ID of this <code>AbstractSessionEvent</code>. </p>
     *
     * @return the ICEfaces ID.
     */
    public String getICEfacesID() {
        return iceFacesId;
    }
}
