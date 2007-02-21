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

import java.util.*;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
 * The ContextEventRepeater was designed to forward servlet events to different
 * parts of the ICEfaces framework. These events are typically of interest for
 * gracefully and/or proactively keeping track of valid sessions and allowing
 * for orderly shut down.
 *
 * This was deemed necessary since the Servlet specification does not allow a
 * programmatic way of adding and removing listeners for these events. So rather
 * than have the various ICEfaces pieces register listeners individually, we can
 * register this class and then add and remove listeners as required.
 *
 * The implementation is currently simple and broad. The class maintains a
 * static collection of listeners in a WeakHashMap and forwards all events to
 * all registered listeners.
 *
 * Future improvements might include:
 * - adapter implementations
 * - event filtering
 *
 * For now, anything that is interested in receiving events from the repeater
 * should simply implement the ContextEventListener interface and then add
 * itself to the ContextEventRepeater using the static addListener method.
 *
 * The limitation of adding a listener programmatically is that certain creation
 * events can occur before the class has a chance to add itself as a listener.
 * To mitigate this, the ContextEventRepeater buffers the creation events
 * temporarily.  When a ContextEventListener is added, the receiveBufferedEvents
 * method is called and, if it returns true, any buffered creation events are
 * sent to the listener after it has been added to the listener collection. The
 * timing of the events is off but the session information can still be useful.
 * Events are removed from the buffer when the corresponding destroy events are
 * received.  This means that that sessions that have already been created AND
 * destroyed are NOT in the buffer.
 */

/**
 *
 */
public class ContextEventRepeater
        implements HttpSessionListener, ServletContextListener {
    private static final String ASYNC_SERVER_KEY =
            "com.icesoft.faces.async.server";
    private static final String MESSAGING_CONTEXT_EVENT_PUBLISHER_CLASS_NAME =
            "com.icesoft.faces.util.event.servlet.MessagingContextEventPublisher";

    private static Log log = LogFactory.getLog(ContextEventRepeater.class);

    private static Map bufferedContextEvents = new HashMap();
    private static ContextEventPublisher contextEventPublisher;
    private static Map listeners = new WeakHashMap();

    /**
     * Adds the specified <code>listener</code> to this
     * <code>ContextEventRepeater</code>. </p>
     *
     * @param listener the listener to be added.
     */
    public synchronized static void addListener(ContextEventListener listener) {
        if (listener == null || listeners.containsKey(listener)) {
            return;
        }
        listeners.put(listener, null);
        if (listener.receiveBufferedEvents()) {
            sendBufferedEvents(listener);
        }
    }

    /**
     * Fires a new <code>ContextDestroyedEvent</code>, based on the received
     * <code>event</code>, to all registered listeners, and cleans itself
     * up. </p>
     *
     * @param event the servlet context event.
     */
    public synchronized void contextDestroyed(ServletContextEvent event) {
        ContextDestroyedEvent contextDestroyedEvent =
                new ContextDestroyedEvent(event);
        Iterator it = listeners.keySet().iterator();
        while (it.hasNext()) {
            ((ContextEventListener) it.next()).
                    contextDestroyed(contextDestroyedEvent);
        }
        listeners.clear();
        bufferedContextEvents.clear();
        if (contextEventPublisher != null) {
            contextEventPublisher.publish(contextDestroyedEvent);
        }
        if (log.isInfoEnabled()) {
            ServletContext servletContext =
                    contextDestroyedEvent.getServletContext();
            log.info(
                    "Servlet Context Name: " +
                    servletContext.getServletContextName() + ", " +
                    "Server Info: " + servletContext.getServerInfo());
        }
    }

    public synchronized void contextInitialized(ServletContextEvent event) {
        boolean _asyncServer;
        String _asyncServerValue =
                event.getServletContext().getInitParameter(ASYNC_SERVER_KEY);
        if (_asyncServerValue != null) {
            _asyncServer = Boolean.valueOf(_asyncServerValue).booleanValue();
        } else {
            _asyncServer = false;
            if (log.isDebugEnabled()) {
                log.debug("com.icesoft.faces.async.server not defined.");
            }
        }
        if (_asyncServer) {
            try {
                contextEventPublisher =
                        (ContextEventPublisher)
                                Class.forName(
                                        MESSAGING_CONTEXT_EVENT_PUBLISHER_CLASS_NAME)
                                        .
                                                newInstance();
                contextEventPublisher.setContextEventRepeater(this);
                contextEventPublisher.publish(
                        new ContextInitializedEvent(event));
            } catch (ClassNotFoundException exception) {
                if (log.isDebugEnabled()) {
                    log.debug("MessagingContextEventPublisher is not found!");
                }
            } catch (IllegalAccessException exception) {
                if (log.isFatalEnabled()) {
                    log.fatal(
                            "Failed to access constructor of " +
                            "MessagingContextEventPublisher!",
                            exception);
                }
            } catch (InstantiationException exception) {
                if (log.isFatalEnabled()) {
                    log.fatal(
                            "Failed to " +
                            "instantiate MessagingContextEventPublisher!",
                            exception);
                }
            }
        }
    }

    /**
     * Fires a new <code>ICEfacesIDRetrievedEvent</code>, with the specified
     * <code>source</code> and </code>iceFacesId</code>, to all registered
     * listeners. </p>
     *
     * @param source the source of the event.
     * @param iceFacesId the ICEfaces ID.
     */
    public synchronized static void iceFacesIdRetrieved(
            HttpSession source, String iceFacesId) {

        ICEfacesIDRetrievedEvent iceFacesIdRetrievedEvent =
                new ICEfacesIDRetrievedEvent(source, iceFacesId);
        bufferedContextEvents.put(iceFacesIdRetrievedEvent, source);
        Iterator _listeners = listeners.keySet().iterator();
        while (_listeners.hasNext()) {
            ((ContextEventListener) _listeners.next()).
                    iceFacesIdRetrieved(iceFacesIdRetrievedEvent);
        }
        if (contextEventPublisher != null) {
            contextEventPublisher.publish(iceFacesIdRetrievedEvent);
        }
        if (log.isTraceEnabled()) {
            log.trace(
                    "ICEfaces ID: " + iceFacesIdRetrievedEvent.getICEfacesID());
        }
    }

    /**
     * Removes the specified <code>listener</code> from this
     * <code>ContextEventRepeater</code>. </p>
     *
     * @param listener the listener to be removed.
     */
    public synchronized static void removeListener(
            ContextEventListener listener) {

        if (listener == null) {
            return;
        }
        listeners.remove(listener);
    }

    public synchronized void sessionCreated(HttpSessionEvent event) {
        // do nothing.
    }

    /**
     * Fires a new <code>SessionDestroyedEvent</code>, based on the received
     * <code>event</code>, to all registered listeners. </p>
     *
     * @param event the HTTP session event.
     */
    public synchronized void sessionDestroyed(HttpSessionEvent event) {
        //It's possible to have a valid session that does not contain an
        //icefacesID.  We should not bail out completely.  Simply log a message
        //and return quietly, but do not broadcast this to the listeners.  We
        //may have to change this behaviour in the future if this becomes a more
        //general purpose utility.
        String icefacesID = (String) ((HttpSession) event.getSource())
                .getAttribute("icefacesID");
        if (icefacesID == null || icefacesID.trim().length() < 1) {
            if (log.isDebugEnabled()) {
                log.debug("session does not contain and icefacesID");
            }
            return;
        }

        SessionDestroyedEvent sessionDestroyedEvent =
                new SessionDestroyedEvent(event, icefacesID);

        Iterator _listeners = listeners.keySet().iterator();
        while (_listeners.hasNext()) {
            ((ContextEventListener) _listeners.next()).
                    sessionDestroyed(sessionDestroyedEvent);
        }

        removeBufferedEvents(event.getSession());

        if (contextEventPublisher != null) {
            contextEventPublisher.publish(sessionDestroyedEvent);
        }
        if (log.isTraceEnabled()) {
            log.trace("ICEfaces ID: " + sessionDestroyedEvent.getICEfacesID());
        }
    }

    /**
     * Fires a new <code>ViewNumberRetrievedEvent</code>, with the specified
     * <code>source</code> and </code>viewNumber</code>, to all registered
     * listeners. </p>
     *
     * @param source the source of the event.
     * @param viewNumber the view number.
     */
    public synchronized static void viewNumberRetrieved(
            HttpSession source, int viewNumber) {

        ViewNumberRetrievedEvent viewNumberRetrievedEvent =
                new ViewNumberRetrievedEvent(
                        source, (String) source.getAttribute("icefacesID"),
                        viewNumber);

        bufferedContextEvents.put(viewNumberRetrievedEvent, source);

        Iterator _listeners = listeners.keySet().iterator();
        while (_listeners.hasNext()) {
            ((ContextEventListener) _listeners.next()).
                    viewNumberRetrieved(viewNumberRetrievedEvent);
        }
        if (contextEventPublisher != null) {
            contextEventPublisher.publish(viewNumberRetrievedEvent);
        }
        if (log.isTraceEnabled()) {
            log.trace(
                    "View Number: " + viewNumberRetrievedEvent.getViewNumber());
        }
    }

    ContextEvent[] getBufferedContextEvents() {
        Set _contextEventSet = bufferedContextEvents.keySet();
        return
                (ContextEvent[])
                        _contextEventSet.toArray(
                                new ContextEvent[_contextEventSet.size()]);
    }

    private synchronized static void removeBufferedEvents(HttpSession session) {
        Iterator it = bufferedContextEvents.keySet().iterator();
        Object event = null;
        HttpSession bufferedSession = null;
        while (it.hasNext()) {
            event = it.next();
            bufferedSession = (HttpSession) bufferedContextEvents.get(event);
            if (bufferedSession.equals(session)) {
                //bufferedContextEvents.remove(event);
                it.remove();
            }
        }
    }

    private synchronized static void sendBufferedEvents(
            ContextEventListener listener) {

        Iterator it = bufferedContextEvents.keySet().iterator();
        while (it.hasNext()) {
            Object event = it.next();
            if (event instanceof ICEfacesIDRetrievedEvent) {
                listener.iceFacesIdRetrieved(
                        (ICEfacesIDRetrievedEvent) event);
            } else if (event instanceof ViewNumberRetrievedEvent) {
                listener.viewNumberRetrieved(
                        (ViewNumberRetrievedEvent) event);
            }
        }
    }
}
