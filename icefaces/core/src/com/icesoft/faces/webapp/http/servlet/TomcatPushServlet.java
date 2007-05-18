
package com.icesoft.faces.webapp.http.servlet;


import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.catalina.CometEvent;
import org.apache.catalina.CometProcessor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.icesoft.faces.webapp.http.core.SendUpdatedViews;
import com.icesoft.faces.webapp.http.core.ViewQueue;


public class TomcatPushServlet
    extends HttpServlet implements CometProcessor {

    public void init() throws ServletException {
    }

    public void destroy() {
    }

    /**
     * Process the given Comet event.
     * 
     * @param event The Comet event that will be processed
     * @throws IOException
     * @throws ServletException
     */
    public void event(CometEvent event)
        throws IOException, ServletException {

        HttpServletRequest request = event.getHttpServletRequest();
        HttpServletResponse response = event.getHttpServletResponse();
        
        if (event.getEventType() == CometEvent.EventType.BEGIN) {
            begin(event, request, response);
        } else if (event.getEventType() == CometEvent.EventType.ERROR) {
            error(event, request, response);
        } else if (event.getEventType() == CometEvent.EventType.END) {
            end(event, request, response);
        } else if (event.getEventType() == CometEvent.EventType.READ) {
            read(event, request, response);
        }
    }

    protected void begin(CometEvent event, HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        //We need access to the CometEvent, but this is not passed down through
        //our dispatcher chain, so we obtain the queues directly here
        MainSessionBoundServlet mainBound = (MainSessionBoundServlet) 
                SessionDispatcher.getSingletonSessionServlet(request.getSession());
        ViewQueue allUpdatedViews = mainBound.getAllUpdatedViews();
        Collection synchronouslyUpdatedViews = 
                mainBound.getSynchronouslyUpdatedViews();
        allUpdatedViews.onPut( new EventResponder(
                event, synchronouslyUpdatedViews, allUpdatedViews) );
    }
    
    protected void end(CometEvent event, HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        event.close();
    }
    
    protected void error(CometEvent event, HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
    }
    
    protected void read(CometEvent event, HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
    }

    protected void service(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        // Not used by Tomcat6
    }
    

}


class EventResponder implements Runnable {
    ViewQueue allUpdatedViews;
    Collection synchronouslyUpdatedViews;
    CometEvent event;
    Writer writer;

    public EventResponder(CometEvent event, 
            Collection synchronouslyUpdatedViews, 
            ViewQueue allUpdatedViews) {
        this.event = event;
        this.synchronouslyUpdatedViews = synchronouslyUpdatedViews;
        this.allUpdatedViews = allUpdatedViews;
        try {
            HttpServletResponse response =  event.getHttpServletResponse();
            writer = response.getWriter();
            response.setContentType("text/xml;charset=UTF-8"); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run()  {
        try {
            allUpdatedViews.removeAll(synchronouslyUpdatedViews);
            synchronouslyUpdatedViews.clear();
            Set viewIdentifiers = new HashSet(allUpdatedViews);
            if (!viewIdentifiers.isEmpty()) {
                Iterator identifiers = viewIdentifiers.iterator();
                writer.write("<updated-views>");
                while (identifiers.hasNext()) {
                    writer.write(identifiers.next().toString());
                    writer.write(' ');
                }
                writer.write("</updated-views>");
                event.close();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    
}