package com.icesoft.faces.context;

/**
 * @author ICEsoft Technologies, Inc.
 *
 * This exception conveys a message from the depths of the BridgeExternalContext
 * to the BlockingServlet, telling it to perform a normal redirect in the
 * case of a logoff, for example. In the case of a Seam logoff, the logic
 * of the Seam application logs off, killing the session, typically, ending
 * the usefulness of all the framework contexts, but still a response must
 * be generated. This exception must be caught, and <code>sendRedirect()</code>
 * must be called by the BlockingServlet when caught.  
 */
public class RedirectException extends RuntimeException {
    public RedirectException(String url) {
        super(url);
    } 
}
