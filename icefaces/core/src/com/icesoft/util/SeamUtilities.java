package com.icesoft.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.StringTokenizer;

import javax.faces.event.PhaseListener;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.context.FacesContext;

/**
 * @author ICEsoft Technologies, Inc.
 *
 * Purpose of this class is to localize Seam Introspection code
 * in one place, and get rid of the variables cluttering up a few
 * of the ICEfaces classes 
 *
 *  Jun 2007 - removed reference to ConversationIsLongRunningParameter
 *      since seam1.3.0.ALPHA has removed all reference to it in Manager Class
 *
 */
public class SeamUtilities {

     private static final Log log =
            LogFactory.getLog(SeamUtilities.class);

    // Seam vars

    private static Class seamManagerClass;
    
    private static Class[] seamClassArgs = new Class[0];
    private static Object[] seamInstanceArgs = new Object[0];
    private static Class[] seamGetEncodeMethodArgs = {String.class, String.class};
    private static Object[] seamEncodeMethodArgs = new Object[2];

    private static Object[] seamMethodNoArgs = new Object[0];

    private static Method seamConversationIdMethodInstance;
    private static Method seamLongRunningMethodInstance;
    private static Method seamAppendConversationMethodInstance;
    private static Method seamInstanceMethod;
    private static Method seamPageContextGetPrefixInstance;

    // The method for getting the conversationId parameter name
    private static Method seamConversationIdParameterMethod;

    // The method for getting the parent conversationId parameter name
//    private static Method seamParentConversationIdParameterMethod;

    // the method for getting the longRunningConversation parameter name
//    private static Method seamLongRunningConversationParameterMethod;

//    private static Method seamBeforeRedirectMethodInstance;

    private static Object pageContextInstance;

    // This is just convenience, to avoid rebuilding the String
    private static String conversationIdParameter;
    private static String conversationParentParameter = "parentConversationId";
//    private static String conversationLongRunningParameter;
    
    // since seam1.3.0Alpha has api changes, detect which version we are using
    private static String seamVersion = "none";
    private static Method seamVersionMethod;
    
    private static String SPRING_CLASS_NAME = 
            "org.springframework.webflow.executor.jsf.FlowVariableResolver";

    private static boolean isSpringLoaded;

    static {
        loadSeamEnvironment();
        loadSpringEnvironment();
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
     * Utility method to determine if D2DSeamFaceletViewHandler requires
     * SeamExpressionFactory Class
     * @return false if Seam version 1.3.0.ALPHA
     *         false otherwise
     */
    public static boolean requiresSeamExpressionFactory(){
    	return (!seamVersion.startsWith("1.3.0"));
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
    public static String encodeSeamConversationId(String uri, String viewId) {

        // If Seam's not loaded, no changes necessary
        if (! isSeamEnvironment() ) {
            return uri;
        }

        String cleanedUrl = uri;

        if (conversationIdParameter == null) {
            getConversationIdParameterName();
        } 

        // IF the URI already contains a conversationId, the isLongRunning parameter, or the
        // parentConversationId parameter, strip it out, and start again.

        // Maybe all of this has changed. This used to be necessary because the
        // 

        if (log.isTraceEnabled()) {
            log.trace("SeamConversationURLParam: " + conversationIdParameter);
        }
        StringTokenizer st = new StringTokenizer( uri, "?&");
        StringBuffer builder = new StringBuffer();

        String token;
        boolean first = true; 
        while(st.hasMoreTokens() ){
            token = st.nextToken();
            if ( (token.indexOf(conversationIdParameter) > -1)  ||
                 (token.indexOf(conversationParentParameter) > -1) ||
//                 (token.indexOf(conversationLongRunningParameter) > -1) ||
                  token.indexOf("rvn") > -1 ) {
                continue;
            } 
            builder.append(token);

            if (st.hasMoreTokens() ) {
                if (first) {
                    builder.append('?');
                    first = false;
                } else {
                    builder.append('&');
                }
            }
        }

        if (builder.length() > 0) {
            cleanedUrl = builder.toString();
        } 
        // The manager instance is a singleton, but it's continuously destroyed
        // after each request and thus must be re-obtained during each redirect.
        try {

            // Get the singleton instance of the Seam Manager each time through
            Object seamManagerInstance =
                    seamInstanceMethod.invoke(null, seamInstanceArgs);

            if (seamAppendConversationMethodInstance != null) {
                seamEncodeMethodArgs[0] = cleanedUrl;
                if (seamEncodeMethodArgs.length == 2) {
                    seamEncodeMethodArgs[1] = viewId;
                }

//                seamBeforeRedirectMethodInstance.invoke(
//                        seamManagerInstance, seamMethodNoArgs);

                
               // This has to do what the Manager.redirect method does.
                cleanedUrl = (String) seamAppendConversationMethodInstance
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

        if ( !isSeamEnvironment()) {
            return null;
        }
        
        String returnVal = null;

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
     * <code>ScopeType.PAGE.getPrefix()</code>. Can be used to
     * manipulate the PageContext, without loading the class. 
     *
     * This String is used as the key to store the PageContext in the
     * ViewRoot attribute map, and does not equal the string
     *  "org.jboss.seam.PAGE" 
     *
     * @return The String Key that can be used to find the Seam PageContext
     */
    public static String getPageContextKey() {

        String returnVal = "";
        if (!isSeamEnvironment()) {
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
     * Attempt to load the classes from the Seam jars. The methods I
     * can locate and load here, but the values (for example, the
     * conversationIdParameter name which is not mutable) have to be
     * retrieved from a Manager instance when the Manager object is
     * available, and that is only during a valid EventContext. 
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
            
            // for D2DSeamFaceletViewHandler need to know version 
           try {
               Class seamClass = Class.forName("org.jboss.seam.Seam");
               seamVersionMethod = seamClass.getMethod("getVersion",null);
               if (seamVersionMethod!=null){
                   seamVersion = (String)seamVersionMethod.invoke(null,seamMethodNoArgs);
//            	   log.info("SeamUtilities: loadSeam.. seamVersion="+seamVersion);
               }
            } catch (NoSuchMethodException e){
                    /* no getVersion method exists for Seam1.2.1 or earlier */
            	    seamVersion="1.2.1.GA";
            } 
//        	log.info("\t ->>> seamVersion="+seamVersion);
            
            try {
                seamAppendConversationMethodInstance =
                        seamManagerClass.getMethod("encodeConversationId",
                                                   seamGetEncodeMethodArgs);
            } catch (NoSuchMethodException e)  {
                /* revert our reflectively discovered Seam method
                   to the Seam 1.2.0 API
                */
                seamGetEncodeMethodArgs = new Class[] {String.class};
                seamEncodeMethodArgs = new Object[1];
                seamAppendConversationMethodInstance =
                        seamManagerClass.getMethod("encodeConversationId",
                                                   seamGetEncodeMethodArgs);
            }

            seamConversationIdMethodInstance =
                    seamManagerClass.getMethod("getCurrentConversationId",
                                               seamClassArgs);
            seamLongRunningMethodInstance =
                    seamManagerClass.getMethod("isLongRunningConversation",
                                               seamClassArgs);


            seamConversationIdParameterMethod =
                    seamManagerClass.getMethod("getConversationIdParameter",
                                               seamClassArgs);

            // This method is protected
//             seamParentConversationIdParameterMethod =
//                    seamManagerClass.getMethod("getParentConversationIdParameter",
//                                               seamClassArgs);


//             seamLongRunningConversationParameterMethod =
//                    seamManagerClass.getMethod("getConversationIsLongRunningParameter",
//                                               seamClassArgs);

//            seamBeforeRedirectMethodInstance =
//                    seamManagerClass.getMethod("beforeRedirect",
//                                               seamClassArgs);
                    


            Class.forName("org.jboss.seam.util.Parameters");


        } catch (ClassNotFoundException cnf) {
//            log.info ("Seam environment not detected ");
        } catch (Exception e) {
            seamInstanceMethod = null;
            seamManagerClass = null;
            log.info("Exception loading seam environment: ", e);
        }
        
        if (seamManagerClass != null) {
            log.info("Seam environment detected ");
        }
    }


    /**
     * Seam 1.0.1 uses an element <code>'conversationId'</code> as the
     * parameter name, whereas Seam 1.1 has it as a configurable item. This method
     * will call the Manager instance to retrieve the current parameter name
     * defining containing the conversation ID. This method must only be called
     *  when the EventContext is valid (and thus the Manager
     * instance is retrievable). The parameter is configurable on a
     * per application basis, so it wont change at runtime.
     * 
     * <p>
     * Calling this method also fills in the conversationIdParameter,
     * the conversationIsLongRunningParameter, and the conversationParentIdParameter
     * fields, as they are all configurable, and used in the encoding conversation
     * id method
     * 
     * @return the appropriate parameter name for the application
     */
    public static String getConversationIdParameterName() {
        if (!isSeamEnvironment()) {
            return null;
        }
        if (conversationIdParameter != null ) {
            return conversationIdParameter;
        } 

        String returnVal = null;
        try {

            Object seamManagerInstance =
                    seamInstanceMethod.invoke(null, seamMethodNoArgs);

            // The method may not be available on all versions of Manager
            if (seamConversationIdParameterMethod != null) {

                returnVal = (String) seamConversationIdParameterMethod.
                        invoke(seamManagerInstance, seamMethodNoArgs);
                conversationIdParameter = returnVal;
            }

//            if (seamParentConversationIdParameterMethod != null) {
//                conversationParentParameter = (String)
//                        seamParentConversationIdParameterMethod.
//                                invoke(seamManagerInstance, seamMethodNoArgs);
//            }

//            if (seamLongRunningConversationParameterMethod != null) {
//                conversationLongRunningParameter = (String)
//                        seamLongRunningConversationParameterMethod.invoke(
//                        seamManagerInstance, seamMethodNoArgs);
//            }


        } catch (Exception e) {
            log.error("Exception fetching conversationId Parameter name: ", e);

        }
        return returnVal;
    }

    /**
     * ICE-1084 : We have to turn off Seam's PhaseListener that makes
     *  it's debug page appear, so that our SeamDebugResourceResolver
     *  can do its work.
     * 
     * @param lifecycle The Lifecycle maintains the list of PhaseListeners
     */
    public static void removeSeamDebugPhaseListener(Lifecycle lifecycle) {
        PhaseListener[] phaseListeners = lifecycle.getPhaseListeners();
// System.out.println("*** SeamUtilities.removeSeamDebugPhaseListener()");
// System.out.println("***   phaseListeners: " + phaseListeners.length);
        for(int i = 0; i < phaseListeners.length; i++) {
// System.out.println("***     phaseListeners["+i+"]: " + phaseListeners[i]);
            if( phaseListeners[i].getClass().getName().equals(
                "org.jboss.seam.debug.jsf.SeamDebugPhaseListener") )
            {
                lifecycle.removePhaseListener(phaseListeners[i]);
//System.out.println("***       REMOVED: " + phaseListeners[i]);
                seamDebugPhaseListenerClassLoader = phaseListeners[i].getClass().getClassLoader();
//System.out.println("******      SeamDebugPhaseListener.class.getClassLoader(): " + phaseListeners[i].getClass().getClassLoader());
            }
        }
    }
    
    public static ClassLoader getSeamDebugPhaseListenerClassLoader() {
        return seamDebugPhaseListenerClassLoader;
    }
    
    private static ClassLoader seamDebugPhaseListenerClassLoader;
    

    
    /**
     * Perform any needed Spring initialization.
     */
    private static void loadSpringEnvironment() {
        Class flowVariableResolver = null;
        try {
            flowVariableResolver = Class.forName(SPRING_CLASS_NAME);
        } catch (Throwable t)  {
            if (log.isDebugEnabled()) {
                log.debug("Spring webflow not detected: " + t);
            }
        }
        if (null != flowVariableResolver) {
            isSpringLoaded = true;
            if (log.isDebugEnabled()) {
                log.debug("Spring webflow detected: " + flowVariableResolver);
            }
        }

    }

    /**
     * Utility method to determine if Spring WebFlow is active.
     *
     * @return true if Spring WebFlow is enabled
     */
    public static boolean isSpringEnvironment() {
        return isSpringLoaded;
    }

    /**
     * Retrieve the current Spring flowId (if any).
     *
     * @return The current Spring flowId.
     */
    public static String getSpringFlowId() {
        if ( !isSpringEnvironment()) {
            return null;
        }
        FacesContext facesContext = FacesContext.getCurrentInstance();
        //obtain key by evaluating expression with Spring VariableResolver
        Object value = facesContext.getApplication()
                .createValueBinding("#{flowExecutionKey}").getValue(facesContext);
        if (null == value) {
            return null;
        }
        
        return value.toString();
    }
    /**
     * Return the parameter name for the Spring Flow Id
     *
     * @return the appropriate parameter name for the application
     */
    public static String getFlowIdParameterName() {
        return "_flowExecutionKey";
    }

}
