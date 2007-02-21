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

import com.icesoft.faces.util.DOMUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import javax.portlet.PortletSession;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;

/*



NEED A COMMON ABSTRACTION; THIS CLASS ONLY A PLACEHOLDER



*/


/**
 * Originally this class was named BlockingServletState and was an inner class
 * of the BlockingServlet.  In order to support a different state implementation
 * for use with the AsyncXmlHttpServer, we've pulled it out on it's own and
 * implemented it as an interface - ResponseState.  This class has the same
 * basic responsibility as before - it handles special incoming XmlHttpRequests
 * and blocks until something on the server triggers a render call and the
 * updated information is passed back.
 */
public class PortletBlockingResponseState
        implements ResponseState, Serializable {
    protected static Log log = LogFactory.getLog(BlockingResponseState.class);
    protected int maxNumberOfUpdates = 50;

    protected PortletKicker kicker;
    protected Collection updates = new ArrayList();
    protected String focusID;
    protected boolean isCancelled = false;

    protected String iceID;
    protected String viewNumber;
    private final int maxUnflushed = 10;
    private int unflushed = 0;
    private PortletSession session;

    public PortletBlockingResponseState(PortletSession session, String iceID,
                                        String viewNumber) {
        if (iceID == null || viewNumber == null) {
            throw new IllegalArgumentException(
                    "iceID and viewNumber must be set");
        }
        this.session = session;
        this.iceID = iceID;
        synchronized (iceID) {
            kicker = (PortletKicker) session.getAttribute(iceID + "/kicker",
                                                          PortletSession.APPLICATION_SCOPE);
            if (null == kicker) {
                kicker = new PortletKicker();
                session.setAttribute(iceID + "/kicker", kicker,
                                     PortletSession.APPLICATION_SCOPE);
            }
        }
        this.viewNumber = viewNumber;
    }

    public String getFocusID() {
        return focusID;
    }

    public void block() throws Exception {
        long left = 0;

        synchronized (kicker) {
            kicker.notifyAll(); //experimental fix for IE connection limit
            while ((!isCancelled) && (!kicker.isKicked) &&
                   ((left = remainingMillis()) > 0)) {
                kicker.wait(left);
            }
            kicker.isKicked = false;
        }

        if (remainingMillis() <= 0) {
            throw new RuntimeException("BlockingResponseState.block() Expired");
        }

        if (isCancelled) {
            try {
                //sleeping here to give the browser req.abort a chance to
                //work.  By sleeping, the browser should never actually
                //process the canceled response
                Thread.sleep(500);
                log.debug("Cancelled block slept for browser abort");
            } catch (InterruptedException e) {
            }
        }
        isCancelled = false;
    }

    public void flush() {
        if (log.isTraceEnabled()) {
            log.trace("DOMUpdate flushed");
        }
        synchronized (kicker) {
            unflushed++;
            kicker.isKicked = true;
            kicker.notifyAll();
        }
    }

    //implemented so that cancel only cancels the current
    //block operation.  Possibly cancel should remain in effect
    //until cleared.  Possibly the block operation should throw
    //an exception when cancelled (like InterruptedException)
    public void cancel() {
        synchronized (kicker) {
            kicker.isKicked = true;
            isCancelled = true;
            kicker.notifyAll();
        }
    }

    private long remainingMillis() {
        long currentMillis = Calendar.getInstance().getTime().getTime();
        long accessedMillis = 0;
        long maxInactiveInterval = 1;
        if (session instanceof PortletSession) {
            accessedMillis = ((PortletSession) session).getLastAccessedTime();
        } else {
            accessedMillis = ((HttpSession) session).getLastAccessedTime();
        }

        if (session instanceof PortletSession) {
            maxInactiveInterval =
                    ((PortletSession) session).getMaxInactiveInterval();
        } else {
            maxInactiveInterval =
                    ((HttpSession) session).getMaxInactiveInterval();
        }
        return (maxInactiveInterval * 1000 -
                (currentMillis - accessedMillis));
    }

    public void setFocusID(String focusID) {
        this.focusID = focusID;
    }

    public boolean hasHandler() {
        return false;
    }

    public void writeElement(Element element) {
        if (null == element) {
            return;
        }

        if (unflushed > maxUnflushed) {
            throw new RuntimeException("viewNumber " + viewNumber +
                                       " update queue exceeded " + unflushed);
        }
        String nodeString = DOMUtils.nodeToString(element);
        this.addUpdate(element.getAttribute("id"), nodeString);
        if (log.isTraceEnabled()) {
            log.trace(nodeString);
        }
    }

    /**
     * @deprecated Replaced by {@link #flush()}
     */
    public void flush(HttpSession session) {
        //TODO fix interface and calls in DOMResponseWriter
        flush();
    }

    /**
     * @deprecated Replaced by {@link #getFocusID()}
     */
    public String getFocusID(HttpSession session) {
        return getFocusID();
    }

    /**
     * @deprecated Replaced by {@link #writeElement(Elementelement)}
     */
    public void writeElement(HttpSession session, Element element) {
       //TODO fix interface and calls in DOMResponseWriter
        writeElement(element);
    }

    public void serialize(Writer writer) throws IOException {
        Iterator i = this.updates.iterator();
        while (i.hasNext()) {
            ((Update) i.next()).serialize(writer);
        }
        this.updates.clear();
        unflushed = 0;
    }

    private void addUpdate(String address, String content) {
        updates.add(new Update(address, content));
    }

    private static class Update {
        private String address;
        private String content;

        public Update(String address, String content) {
            this.address = address;
            this.content = content;
        }

        public void serialize(Writer writer) throws IOException {
            writer.write("<update address=\"" + this.address + "\"><![CDATA[" +
                         this.content + "]]></update>");
        }
    }
}

class PortletKicker implements Serializable {
    boolean isKicked = false;
}
