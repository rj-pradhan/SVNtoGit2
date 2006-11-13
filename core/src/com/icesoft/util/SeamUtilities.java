package com.icesoft.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Field;

/**
 * @author ICEsoft Technologies, Inc.
 *
 * Purpose of this class is to localize Seam Introspection code
 * in one place, and get rid of the variables cluttering up a few
 * of the ICEfaces classes 
 *
 *
 */
public class SeamUtilities {

     private static final Log log =
            LogFactory.getLog(SeamUtilities.class);

    // Seam vars

    private static Class seamManagerClass;
    
    private static Class[] seamClassArgs = new Class[0];
    private static Object[] seamInstanceArgs = new Object[0];
    private static Class[] seamGetEncodeMethodArgs = {String.class};
    private static Object[] seamEncodeMethodArgs = new Object[1];

    private static Object[] seamMethodNoArgs = new Object[0];

    private static Method seamConversationIdMethodInstance;
    private static Method seamLongRunningMethodInstance;
    private static Method seamEncodeMethodInstance;
    private static Method seamInstanceMethod;
    private static Method seamPageContextGetPrefixInstance;

    private static Object pageContextInstance;

    static {
        loadSeamEnvironment();
    }


    /**
     * Utility method to determine if the Seam classes can be loaded.
     *
     * @return true if Seam classes can be loaded
     */
    public static boolean isSeamEnvironment() {
        return seamManagerClass != null;
    }


    /**
     * Called on a redirect to invoke any Seam redirection code. Seam uses
     * the sendRedirect method to preserve temporary conversations for the
     * duration of redirects. ICEfaces does not call this method, so this
     * method attempts to call the same Seam code introspectively. Seam will
     * encode the <code>conversationId</code> to the end of the argument URI.
     *
     * @param uri the redirect URI to redirect to, before the
     * conversationId is encoded
     * @return the URI, with the conversationId if Seam is detected
     */
    public static String encodeSeamConversationId(String uri) {

        // If Seam's not loaded, no changes necessary
        if (seamManagerClass == null) {
            return uri;
        }

        String cleanedUrl = uri;

        // IF the URI already contains a conversationId, strip it out,
        // and start again.
        int spos = uri.indexOf("?conversationId");
        if (spos > 0) {
            // extract anything up to the conversationId field
            cleanedUrl = uri.substring(0, spos);

            int epos = uri.indexOf("?", spos + 1);
            // append anything that came after the conversationId
            if (epos > 0) {
                cleanedUrl += uri.substring(epos);
            }
        }

        // The manager instance is a singleton, but it's continuously destroyed
        // after each request and thus must be re-obtained during each redirect.
        try {

            // Get the singleton instance of the Seam Manager each time through
            Object seamManagerInstance =
                    seamInstanceMethod.invoke(null, seamInstanceArgs);

            if (seamEncodeMethodInstance != null) {
                seamEncodeMethodArgs[0] = cleanedUrl;

                cleanedUrl = (String) seamEncodeMethodInstance
                        .invoke(seamManagerInstance, seamEncodeMethodArgs);
                if (log.isDebugEnabled()) {
                    log.debug("Enabled redirect from: " +
                            uri + ", to: " + cleanedUrl);
                }
            }
        } catch (Exception e) {
            seamInstanceMethod = null;
            seamManagerClass = null;
            log.error("Exception encoding seam conversationId: ", e);

        }
        return cleanedUrl;
    }

    /**
     * Retrieve the current Seam conversationId (if any) by introspection from
     * the SeamManager. The seam Conversation must be a long running
     * conversation, otherwise it isn't useful to encode it in the form. Long
     * running conversations are started by Seam components at various parts of
     * the application lifecycle, and their Id is necessary during a partial
     * submit to continue the thread of the conversation.
     *
     * @return The current conversation id, or null if not a seam environment,
     *         or there is no current long running conversation.
     */
    public static String getSeamConversationId() {

        String returnVal = null;
        if (seamManagerClass == null) {
            return returnVal;
        }

        try {
            // The manager instance is a singleton, but it's continuously
            // destroyed after each request and thus must be re-obtained.
            Object seamManagerInstance =
                    seamInstanceMethod.invoke(null, seamMethodNoArgs);

            if (seamConversationIdMethodInstance != null) {

                String conversationId =
                        (String) seamConversationIdMethodInstance.invoke(
                                seamManagerInstance, seamMethodNoArgs);

                Boolean is = (Boolean) seamLongRunningMethodInstance
                        .invoke(seamManagerInstance,
                                seamMethodNoArgs);               

                if (is.booleanValue()) {
                    returnVal = conversationId;
                }
            }

        } catch (Exception e) {
            seamInstanceMethod = null;
            seamManagerClass = null;
            log.error("Exception determining Seam ConversationId: ", e);

        }
        return returnVal;
    }

    /**
     * Retrieve the PageContext key. Equivalent to
     * <code>ScopeType.PAGE.getPrefix()</code>
     *
     * This String is used as the key to store the PageContext in the
     * ViewRoot attribute map, and does not equal the string
     *  "org.jboss.seam.PAGE" 
     *
     * @return
     */
    public static String getPageContextKey() {

        String returnVal = "";
        if (seamManagerClass == null) {
            return returnVal;
        }
        try {

            if (seamConversationIdMethodInstance != null) {
                returnVal = (String) seamPageContextGetPrefixInstance.invoke(
                        pageContextInstance, seamMethodNoArgs);
            }

        } catch (Exception e) {
            log.error("Exception fetching Page from ScopeType: ", e);

        }
        return returnVal;
    }



    /**
     * Attempt to load the classes from the Seam jars
     */
    private static void loadSeamEnvironment() {

        try {

            // load classes
            seamManagerClass = Class.forName("org.jboss.seam.core.Manager");
            Class seamScopeTypeClass = Class.forName("org.jboss.seam.ScopeType");

            // load method instances
            seamInstanceMethod = seamManagerClass.getMethod("instance",
                                                            seamClassArgs);

            Field fieldInstance = seamScopeTypeClass.getField("PAGE");

            pageContextInstance = fieldInstance.get(seamScopeTypeClass);


            seamPageContextGetPrefixInstance = seamScopeTypeClass.getMethod(
                    "getPrefix", seamClassArgs);


            seamEncodeMethodInstance =
                    seamManagerClass.getMethod("encodeConversationId",
                                               seamGetEncodeMethodArgs);

            seamConversationIdMethodInstance =
                    seamManagerClass.getMethod("getCurrentConversationId",
                                               seamClassArgs);
            seamLongRunningMethodInstance =
                    seamManagerClass.getMethod("isLongRunningConversation",
                                               seamClassArgs);

            log.info("Seam environment detected");

        } catch (ClassNotFoundException cnf) {
//            log.info ("Seam environment not detected ");
        } catch (Exception e) {
            seamInstanceMethod = null;
            seamManagerClass = null;
            log.error("Exception loading seam environment: ", e);
        }
    }        
}
