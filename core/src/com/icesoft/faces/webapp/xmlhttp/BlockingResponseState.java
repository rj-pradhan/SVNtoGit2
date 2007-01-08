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
import com.icesoft.faces.webapp.http.core.UpdateManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;

import edu.emory.mathcs.backport.java.util.concurrent.Semaphore;

/**
 * Originally this class was named BlockingServletState and was an inner class
 * of the BlockingServlet.  In order to support a different state implementation
 * for use with the AsyncXmlHttpServer, we've pulled it out on it's own and
 * implemented it as an interface - ResponseState.  This class has the same
 * basic responsibility as before - it handles special incoming XmlHttpRequests
 * and blocks until something on the server triggers a render call and the
 * updated information is passed back.
 */
public class BlockingResponseState implements ResponseState, Serializable {
    protected static Log log = LogFactory.getLog(BlockingResponseState.class);
    protected int maxNumberOfUpdates = 50;

    private Collection updates = new ArrayList();
    protected String iceID;
    protected String viewNumber;
    private Semaphore semaphore;

    private final int maxUnflushed = 10;
    protected int unflushed = 0;

    /*
    Bug 1010:  Added emptry constructor so that the extending class is not
    required to call the "real" constructor.  This was agreed upon as the
    least disruptive solution even though it breaks strict backwards
    compatibility with 1.5 Open Source version.
    */
    protected BlockingResponseState() {
    }

    public BlockingResponseState(HttpSession session, String iceID,
                                 String viewNumber) {
        if (iceID == null || viewNumber == null) {
            throw new IllegalArgumentException(
                    "iceID and viewNumber must be set");
        }
        this.semaphore = (Semaphore) session.getAttribute(UpdateManager.class.toString());
        if (semaphore == null) {
            System.out.println("BlockingServlet needs to be registered as a listener class.");
            System.out.println("Add this lines to your web.xml:");
            System.out.println();
            System.out.println("<listener>");
            System.out.println("\t<listener-class>com.icesoft.faces.webapp.xmlhttp.BlockingServlet</listener-class>");
            System.out.println("</listener>");
        }
        this.iceID = iceID;
        this.viewNumber = viewNumber;
    }

    public void block(HttpServletRequest request)  {
        //do nothing
    }

    public void flush() {
        semaphore.release();
    }


    public boolean hasUpdates() {
        return !updates.isEmpty();
    }

    public String getViewNumber() {
        return viewNumber;
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

    public synchronized void serialize(Writer writer) throws IOException {
        Iterator i = this.updates.iterator();
        while (i.hasNext()) {
            ((Update) i.next()).serialize(writer);
        }
        this.updates.clear();
        unflushed = 0;
    }

    private synchronized void addUpdate(String address, String content) {
        updates.add(new Update(address, content));
    }

    private static class Update implements Serializable {
        private final static Pattern START_CDATA =
                Pattern.compile("<\\!\\[CDATA\\[");
        private final static Pattern END_CDATA = Pattern.compile("\\]\\]>");
        private String address;
        private String content;

        public Update(String address, String content) {
            this.address = address;
            this.content = content;
            this.content =
                    START_CDATA.matcher(this.content).replaceAll("<!#cdata#");
            this.content = END_CDATA.matcher(this.content).replaceAll("##>");
        }

        public void serialize(Writer writer) throws IOException {
            if( log.isTraceEnabled() ) {
                log.trace("serialize()  address: " + address + "  content: " +
                          content);
            }
            writer.write("<update address=\"" + this.address + "\"><![CDATA[" +
                         this.content + "]]></update>");
        }
    }
}