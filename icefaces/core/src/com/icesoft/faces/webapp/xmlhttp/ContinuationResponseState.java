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
import org.mortbay.util.ajax.Continuation;
import org.mortbay.util.ajax.ContinuationSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Implementation of blocking functionality via Jetty Continuations
 */
public class ContinuationResponseState extends BlockingResponseState {
    protected static Log log = LogFactory.getLog(ContinuationResponseState.class);

    private Continuation continuation;

    protected ContinuationResponseState() {
    }

    public ContinuationResponseState(HttpSession session,
            String iceID, String viewNumber) {
        this.session = session;
        this.iceID = iceID;
        this.viewNumber = viewNumber;
        //A null iceID is an indication that the session has expired and
        //a new one has been created, but BlockingServlet needs to address this
        if ((null == iceID) || (remainingMillis() <= 0)) {
            throw new SessionExpiredException("Session timeout elapsed.");
        }
    }

    public void block(HttpServletRequest request)  {
        continuation = ContinuationSupport
                .getContinuation(request,request);
        continuation.suspend(remainingMillis());
    }

    public void flush() {
        if (log.isTraceEnabled()) {
            log.trace("Continuation DOMUpdate flushed");
        }
        if (null != continuation) {
            continuation.resume();
        }
    }

    //implemented so that cancel only cancels the current
    //block operation.  Possibly cancel should remain in effect
    //until cleared.  Possibly the block operation should throw
    //an exception when cancelled (like InterruptedException)
    public void cancel() {
        isCancelled = true;
        if (null != continuation)  {
            continuation.resume();
        }
    }

}

