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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * I monitor all the sessions created within a context. Sessions not touched
 * (see {@link #touch} within the maxInactiveInterval (see {@link
 * javax.servlet.http.HttpSession#getMaxInactiveInterval()})  are expired. To
 * run me I need to be registered as a listener in the web.xml file.
 */
public class SessionLifetimeManager
        implements HttpSessionListener, ServletContextListener {
    private static Map sessions = new HashMap();
    private boolean run = false;

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        Thread poolingProcess = new Thread() {
            public void run() {
                while (run) {
                    Iterator trackers = sessions.values().iterator();
                    while (trackers.hasNext())
                        ((Tracker) trackers.next()).invalidateIfExpired();
                    try {
                        sleep(3000);
                    } catch (InterruptedException e) {
                        //ignore interrupts
                    }
                }
            }
        };
        run = true;
        poolingProcess.start();
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        run = false;
    }

    public void sessionCreated(HttpSessionEvent event) {
        sessions.put(event.getSession(), new Tracker(event.getSession()));
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        sessions.remove(event.getSession());
    }

    public static void touch(HttpSession session) {
        if (sessions.containsKey(session))
            ((Tracker) sessions.get(session)).touch();
    }

    private class Tracker {
        private final HttpSession session;
        private long lastAccess;

        public Tracker(HttpSession session) {
            this.session = session;
            touch();
        }

        public void touch() {
            lastAccess = System.currentTimeMillis();
        }

        public void invalidateIfExpired() {
            if (isExpired()) {
                try {
                    session.invalidate();
                } catch (IllegalStateException e) {
                    //session was expired already by the container
                }
            }
        }

        private boolean isExpired() {
            return session.getMaxInactiveInterval() * 1000 <
                   System.currentTimeMillis() - lastAccess;
        }
    }
}
