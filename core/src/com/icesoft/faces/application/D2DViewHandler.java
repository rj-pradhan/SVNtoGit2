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

package com.icesoft.faces.application;

import com.icesoft.faces.context.BridgeExternalContext;
import com.icesoft.faces.context.BridgeFacesContext;
import com.icesoft.faces.context.DOMResponseWriter;
import com.icesoft.faces.env.CommonEnvironmentResponse;
import com.icesoft.faces.webapp.parser.JspPageToDocument;
import com.icesoft.faces.webapp.parser.Parser;
import com.icesoft.faces.webapp.xmlhttp.BlockingServlet;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesCommonlet;
import com.icesoft.util.SeamUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.FacesException;
import javax.faces.application.StateManager;
import javax.faces.application.ViewHandler;
import javax.faces.component.NamingContainer;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKitFactory;
import javax.portlet.RenderResponse;
import javax.servlet.ServletResponse;
import java.beans.Beans;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * <B>D2DViewHandler</B> is the ICEfaces ViewHandler implementation
 *
 * @see javax.faces.application.ViewHandler
 */
public class D2DViewHandler extends ViewHandler {
    // Log instance for this class
    protected static Log log = LogFactory.getLog(D2DViewHandler.class);

    static {
        if (D2DViewHandler.log.isInfoEnabled()) {
            D2DViewHandler.log
                    .info(new ProductInfo().toString());//Announce ICEfaces
        }
    }

    private static final String CURRENT_VIEW_ROOT =
            "javax.faces.webapp.CURRENT_VIEW_ROOT";
    private final static String DELEGATE_NONIFACE =
            "com.icesoft.faces.delegateNonIface";
    private final static String ACTION_URL_SUFFIX =
            "com.icesoft.faces.actionURLSuffix";
    private final static String RELOAD_INTERVAL =
            "com.icesoft.faces.reloadInterval";
    private final static String DO_JSF_STATE_MANAGEMENT =
            "com.icesoft.faces.doJSFStateManagement";
    private final static String LAST_LOADED_KEY = "_lastLoaded";
    private final static String LAST_CHECKED_KEY = "_lastChecked";
    // Key for storing ICEfaces auxillary data in the session
    public static final String DOM_CONTEXT_TABLE =
            "com.icesoft.faces.sessionAuxiliaryData";
    public static final String CHAR_ENCODING = "UTF-8";
    public static final String HTML_CONTENT_TYPE =
            "text/html;charset=" + CHAR_ENCODING;

    private String actionURLSuffix;
    protected boolean delegateNonIface = false;
    protected boolean delegateNonIfaceDefault = false;
    protected boolean jsfStateManagement;
    //reloadInterval internally in milliseconds
    protected long reloadInterval;
    protected long reloadIntervalDefault = 2;
    private boolean parametersInitialized = false;

    protected Parser parser;
    private ViewHandler delegate;


    public D2DViewHandler() {
    }

    public D2DViewHandler(ViewHandler delegate) {
        super();
        this.delegate = delegate;
    }

    // Render the components
    public void renderView(FacesContext context, UIViewRoot viewToRender)
            throws IOException, FacesException {
        initializeParameters(context);
        if (delegateView(viewToRender.getViewId())) {
            delegate.renderView(context, viewToRender);
            return;
        }

        if (null == parser) {
            parser = new Parser(this.getClass().getResourceAsStream(
                    "serializedTagToComponentMapFull.ser"));
        }
        
        if( log.isTraceEnabled() ) {
            log.trace("renderView(FC,UIVR)  BEFORE  renderResponse  " +
                      "viewToRender.getViewId(): " + viewToRender.getViewId());
        }
        renderResponse(context);
        if( log.isTraceEnabled() ) {
            log.trace("renderView(FC,UIVR)  AFTER   renderResponse");
        }

        //If DOM rendering, DOM is now complete
        ResponseWriter responseWriter = context.getResponseWriter();
        if (responseWriter instanceof DOMResponseWriter) {
            if( log.isTraceEnabled() ) {
                log.trace("renderView(FC,UIVR)  BEFORE  domWriter");
            }
            DOMResponseWriter domWriter = (DOMResponseWriter) responseWriter;
            domWriter.writeDOM(context);
            if( log.isTraceEnabled() ) {
                log.trace("renderView(FC,UIVR)  AFTER   domWriter");
            }
        }

        if (jsfStateManagement) {
            StateManager stateMgr = context.getApplication().getStateManager();
            stateMgr.saveSerializedView(context);
        }
    }


    /**
     * Create a new ViewRoot
     * @param context FacesContext
     * @param viewId ViewId identifying the root
     * @return A new viewRoot 
     */
    public UIViewRoot createView(FacesContext context, String viewId) {
        initializeParameters(context);

        if (delegateView(viewId)) {
            return delegate.createView(context, viewId);
        }

        UIViewRoot root = new UIViewRoot();
        root.setRenderKitId(RenderKitFactory.HTML_BASIC_RENDER_KIT);

         Map contextServletTable =
                D2DViewHandler.getContextServletTable(context);
        if (null == viewId) {
            root.setViewId("default");
            context.setViewRoot(root);
            contextServletTable
                    .put(DOMResponseWriter.RESPONSE_VIEWROOT, root);
            Locale locale = calculateLocale(context);
            root.setLocale(locale);
            return root;
        }

        root.setViewId(viewId);
        context.setViewRoot(root);
        contextServletTable.put(DOMResponseWriter.RESPONSE_VIEWROOT, root);

        return root;
//        return restoreView(context, viewId);
    }

    /**
     * Restore the view if possible. This method can return null if
     * no ViewRoot is available. The <code>LifeCycle</code> will call
     * createView in this case.
     *
     * @param context FacesContext
     * @param viewId ViewId identifying the view to restore
     * @return UIViewRoot instance if found, null if none yet created,
     * or if trying to model Seam JSF behaviour.
     */
    public UIViewRoot restoreView(FacesContext context, String viewId) {
        this.initializeParameters(context);


        if (delegateView(viewId)) {
            return delegate.restoreView(context, viewId);
        }

        UIViewRoot currentRoot = context.getViewRoot();
        //MyFaces expects path to match current view
        ExternalContext externalContext = context.getExternalContext();
        if (externalContext instanceof BridgeExternalContext) {

            BridgeExternalContext bridgeExternalContext =
                    (BridgeExternalContext) externalContext;

            bridgeExternalContext.setRequestServletPath(viewId);

            if (null != externalContext.getRequestPathInfo()) {
                //it's not null, so must be valid to keep in synch for MyFaces
                bridgeExternalContext.setRequestPathInfo(viewId);
            }

            if (SeamUtilities.isSeamEnvironment() ) {
                if (bridgeExternalContext.getRequestParameterMap().remove(
                        PersistentFacesCommonlet.SEAM_LIFECYCLE_SHORTCUT) != null) {
                     if (log.isTraceEnabled() ) {
                        log.trace("Seam Keyword shortcut found, new ViewRoot");
                    }
                    return null;
                } else {
                    if (log.isTraceEnabled() ) {
                        log.trace("No Seam Keyword shortcut found");
                    } 
                }
            }
        }

        if (null != currentRoot &&
            D2DViewHandler.mungeViewId(viewId)
                    .equals(D2DViewHandler.mungeViewId(
                            currentRoot.getViewId()))) {
            purgeSeamContexts(context, currentRoot);
            return currentRoot;
        }

        Map contextServletTable =
                D2DViewHandler.getContextServletTable(context);
        Map domResponseContexts;
        if (contextServletTable
                .containsKey(DOMResponseWriter.RESPONSE_CONTEXTS_TABLE)) {
            domResponseContexts = (Map) contextServletTable
                    .get(DOMResponseWriter.RESPONSE_CONTEXTS_TABLE);
        } else {
            domResponseContexts = new HashMap();
            contextServletTable.put(DOMResponseWriter.RESPONSE_CONTEXTS_TABLE,
                                    domResponseContexts);
        }

        UIViewRoot root = null;

        root = (UIViewRoot) contextServletTable
                .get(DOMResponseWriter.RESPONSE_VIEWROOT);

        if ( (null != root) && (null != viewId) &&
             (mungeViewId(viewId).equals(mungeViewId(root.getViewId()))) ) {
            //existing root is good, could just return but need post-processing
            //return root;
        } else {

            // Moved this to createView

//            root = new UIViewRoot();
//            root.setRenderKitId(RenderKitFactory.HTML_BASIC_RENDER_KIT);
//
//            if (null == viewId) {
//                root.setViewId("default");
//                context.setViewRoot(root);
//                contextServletTable
//                        .put(DOMResponseWriter.RESPONSE_VIEWROOT, root);
//                Locale locale = calculateLocale(context);
//                root.setLocale(locale);
//                return root;
//            }
//
//            root.setViewId(viewId);
//            context.setViewRoot(root);
//            contextServletTable.put(DOMResponseWriter.RESPONSE_VIEWROOT, root);

        }

        // Remove seam PageContext if REMOVE_SEAM_CONTEXTS key is found
        // Remove the value since this context may be shared between
        // subsequent requests.
        if (SeamUtilities.isSeamEnvironment()) {
            purgeSeamContexts(context, root);
        }
        return root;
    }


    /**
     * utility method to remove a Seam PageContext from the ViewRoot's attribute
     * map, if the persistent FacesContext has inserted the appropriate code in
     * the externalContext's attribute map
     *
     * @param context The current FacesContext
     * @param root    The UIViewRoot instance
     */
    private void purgeSeamContexts(FacesContext context, UIViewRoot root) {

        if (root == null) {
            return;
        }
        if (context.getExternalContext().getRequestMap().remove(
                PersistentFacesCommonlet.REMOVE_SEAM_CONTEXTS) != null) {

            Object key = SeamUtilities.getPageContextKey();
            Object o = root.getAttributes().remove(key);
            log.debug("Removed Seam PageContext from Request: " +
                      (o != null));
        }
    }

    private static Map getContextServletTables(FacesContext context) {
        Map sessionMap = D2DViewHandler.getSessionMap(context);
        String viewNumber = "-";
        if (context instanceof BridgeFacesContext) {
            viewNumber = ((BridgeFacesContext) context).getViewNumber();
        }

        String treeKey = viewNumber + "/" + D2DViewHandler.DOM_CONTEXT_TABLE;
        Map contextTable;
        if (sessionMap.containsKey(treeKey)) {
            contextTable = (Map) sessionMap.get(treeKey);
        } else {
            contextTable = new HashMap();
            sessionMap.put(treeKey, contextTable);
        }

        return contextTable;
    }

    public static Map getContextServletTable(FacesContext context) {
        Map domContextTables = D2DViewHandler.getContextServletTables(context);
        String servletRequestPath =
                D2DViewHandler.getServletRequestPath(context);
        if (domContextTables.containsKey(servletRequestPath)) {
            return (Map) domContextTables.get(servletRequestPath);
        } else {
            Map domContextTable = new HashMap();
            domContextTables.put(servletRequestPath, domContextTable);
            return domContextTable;
        }
    }

    public static String getServletRequestPath(FacesContext context) {
        if (Beans.isDesignTime()) {
            //IDE scenario requires artificial path
            return context.getViewRoot().getViewId();
        }
        ExternalContext externalContext = context.getExternalContext();
        if (externalContext instanceof BridgeExternalContext) {
            String uri = ((BridgeExternalContext) externalContext)
                    .getRequestURI();
            if (null == uri) {
                if (D2DViewHandler.log.isWarnEnabled()) {
                    D2DViewHandler.log
                            .warn("Failing over to default request path");
                }
                uri = "default";

            }
            return uri;
        }
        return (externalContext.getRequestContextPath() +
                externalContext.getRequestServletPath());
    }

    public static String getServletRequestPath(ExternalContext externalContext,
                                               String viewId) {
        if (externalContext == null) {
            throw new IllegalStateException("ExternalContext is null");
        }
        return externalContext.getRequestContextPath() + viewId;
    }

    public String getActionURL(FacesContext context, String viewId) {
        //Maybe should always use delegate
        if (delegateView(viewId)) {
            return delegate.getActionURL(context, viewId);
        }

        if (viewId.indexOf("://") >= 0) {
            return viewId;
        }

        if (viewId.charAt(0) != '/') {
            throw new IllegalArgumentException(
                    "viewId " + viewId + "does not begin with '/'");
        }

        //remove extension and replace with parametrized suffix
        if (null != actionURLSuffix) {
            viewId =
                    viewId.substring(0, viewId.lastIndexOf(".")) +
                    actionURLSuffix;
        }

        return context.getExternalContext().getRequestContextPath() + viewId;
    }

    public String getResourceURL(FacesContext context, String path) {
        if (path.startsWith("/")) {
            return context.getExternalContext().getRequestContextPath() + path;
        } else {
            return path;
        }
    }

    protected long getTimeAttribute(UIComponent root, String key) {
        Long timeLong = (Long) root.getAttributes().get(key);
        long time = (null == timeLong) ? 0 :
                    timeLong.longValue();
        return time;
    }

    protected void renderResponse(FacesContext context) throws IOException {
        UIComponent root = context.getViewRoot();
        String viewId = ((UIViewRoot) root).getViewId();

        if (D2DViewHandler.log.isTraceEnabled()) {
            D2DViewHandler.log.trace("Rendering " + root + " with " +
                                     root.getChildCount() + " children");
        }

        clearSession(context);
        createResponseWriter(context);

        boolean reloadView = false;
        URLConnection viewConnection = null;

        if ((root.getChildCount() == 0) || (reloadInterval > -1)) {
            // We have not parsed the page yet;
            // Need an input stream for the page;
            if (viewId.startsWith("/faces")) {
                viewId = viewId.substring(6);
            }
            if (viewId.endsWith(".jpg") || viewId.endsWith(".gif") ||
                viewId.endsWith(".png")) {
                context.getExternalContext().dispatch(viewId);
                return;
            }
            try {
                URL viewURL = context.getExternalContext().getResource(viewId);
                if (null == viewURL) {
                    if (viewId.endsWith(".faces")) {
                        viewId = D2DViewHandler.truncate(".faces", viewId);
                    } else if (viewId.endsWith(".jsf")) {
                        viewId = D2DViewHandler.truncate(".jsf", viewId);
                    } else if (viewId.endsWith(".iface")) {
                        viewId = D2DViewHandler.truncate(".iface", viewId);
                    } else if (viewId.endsWith(".jsp")) {
                        //MyFaces thinks everything is a .jsp
                        viewId = D2DViewHandler.truncate(".jsp", viewId);
                    }

                    viewId = viewId + ".jspx";
                    viewURL = context.getExternalContext().getResource(viewId);
                }

                if (null == viewURL) {
                    if (viewId.endsWith(".jspx")) {
                        viewId = D2DViewHandler.truncate(".jspx", viewId) +
                                 ".jsp";
                    }
                    viewURL = context.getExternalContext().getResource(viewId);
                }


                long currentTime = System.currentTimeMillis();
                long lastLoaded = getTimeAttribute(root, LAST_LOADED_KEY);
                long lastChecked = getTimeAttribute(root, LAST_CHECKED_KEY);
                long lastModified = 0;

                //newly instantiated viewRoot will have lastChecked of 0
                //and lastLoaded of 0
                if (currentTime > lastChecked + reloadInterval) {
                    viewConnection = viewURL.openConnection();
                    lastModified = viewConnection.getLastModified();
                    root.getAttributes().put(LAST_CHECKED_KEY,
                                             new Long(currentTime));
                    if (lastModified > lastLoaded) {
                        reloadView = true;
                    }
                }

            } catch (Exception e) {
                throw new FacesException("Can't find stream for " + viewId, e);
            }
        }

        if (reloadView) {
            Reader viewInput = null;

            try {
                viewInput = new InputStreamReader(
                        viewConnection.getInputStream(), CHAR_ENCODING);

                if (null == viewInput) {
                    throw new NullPointerException();
                }
                if (viewId.endsWith(".jsp")) {
                    viewInput = JspPageToDocument.transform(viewInput);
                } else if (viewId.endsWith(".jspx")) {
                    viewInput =
                            JspPageToDocument.preprocessJspDocument(viewInput);
                }
            } catch (Throwable e) {
                throw new FacesException("Can't read stream for " + viewId, e);
            }

            // Parse the page;
            try {
                //TODO: pass viewInput as an InputStream in order to give to the XML parser a chance to
                //TODO: read the encoding type declared in the xml processing instruction (<?xml version="1.0" charset="..."?>)
                context.getExternalContext().getRequestMap()
                        .remove(CURRENT_VIEW_ROOT);
                parser.parse(viewInput, context);
                root.getAttributes().put(LAST_LOADED_KEY,
                                         new Long(System.currentTimeMillis()));

                ExternalContext externalContext = context.getExternalContext();
                if (externalContext instanceof BridgeExternalContext) {
                    BridgeExternalContext bridgeExternalContext =
                            (BridgeExternalContext) externalContext;
                    if (BlockingServlet.standardRequestScope) {
                        bridgeExternalContext.clearRequestMap();
                    } else {
                        bridgeExternalContext.resetRequestMap();
                    }
                }
            } catch (Throwable e) {
                throw new FacesException("Can't parse stream for " + viewId +
                                         " " + e.getMessage(), e);
            }
        } else {
            renderResponse(context, root);
            tracePrintComponentTree(context);            
        }
    }

    protected void renderResponse(FacesContext context, UIComponent component)
            throws IOException {
        component.encodeBegin(context);

        if (component.getRendersChildren()) {
            component.encodeChildren(context);
        } else {
            Iterator kids = component.getChildren().iterator();
            while (kids.hasNext()) {
                renderResponse(context, (UIComponent) kids.next());
            }
        }

        component.encodeEnd(context);

        //Workaround so that MyFaces UIData will apply values to
        //child components even if the tree is not restored via StateManager
        if (component instanceof javax.faces.component.UIData) {
            StateHolder stateHolder = (StateHolder) component;
            stateHolder.restoreState(context, stateHolder.saveState(context));
        }
    }
    
    protected void tracePrintComponentTree(FacesContext context) {
        if( log.isTraceEnabled() ) {
            log.trace("tracePrintComponentTree() vvvvvv");
            tracePrintComponentTree( context.getViewRoot(), context, 0 );
            log.trace("tracePrintComponentTree() ^^^^^^");
        }
    }
    
    private void tracePrintComponentTree(UIComponent component, FacesContext context, int levels) {
        StringBuffer prefix = new StringBuffer( 64 );
        for(int i = 0; i < levels; i++)
            prefix.append("  ");
        prefix.append("<");
        String compStr = component.toString();
        
        StringBuffer open = new StringBuffer( 512 );
        open.append( prefix );
        open.append( compStr );
        boolean hasKids = component.getChildCount() > 0;
        if( !hasKids )
            open.append("/");
        open.append(">");
        if( hasKids ) {
            open.append(" kids: ");
            open.append( Integer.toString(component.getChildCount()) );
        }
        log.trace( open );        
        
        if( hasKids ) {
            Iterator kids = component.getChildren().iterator();
            while( kids.hasNext() ) {
                tracePrintComponentTree(
                        (UIComponent) kids.next(), context, levels+1 );
            }
                
            StringBuffer close = new StringBuffer( 512 );
            close.append( prefix );
            close.append("/");
            close.append( compStr );
            close.append(">");
            log.trace( close );        
        }
    }

    protected void clearSession(FacesContext context) {
        Map contextServletTable =
                D2DViewHandler.getContextServletTable(context);
        contextServletTable.remove(DOMResponseWriter.RESPONSE_CONTEXTS_TABLE);
        contextServletTable.remove(DOMResponseWriter.RESPONSE_VIEWROOT);
        contextServletTable.remove(DOMResponseWriter.RESPONSE_DOM);
        contextServletTable.remove(DOMResponseWriter.RESPONSE_MODIFIED_NODES);
    }

    protected void createResponseWriter(FacesContext context)
            throws IOException {
        // TODO
        // Workaround to support running in both ICEfaces and plain Faces modes
        Object obj = context.getExternalContext().getResponse();
        Writer writer = null;

        // If the response is null, don't bother trying to do anything with it.
        // If it's a CommonEnvironmentResponse, then we are running ICEfaces with the
        // PersistentFacesServlet.  If not, we're likely running in plain Faces mode and just
        // have a ServletResponse or a RenderReponse (portlets).
        //TODO: detect and pick one of the browser's preferred character sets.
        if (obj != null) {
            if (obj instanceof CommonEnvironmentResponse) {
                CommonEnvironmentResponse response =
                        (CommonEnvironmentResponse) obj;
                response.setContentType(D2DViewHandler.HTML_CONTENT_TYPE);
                try {
                    writer = new OutputStreamWriter(response.getStream(),
                                                    D2DViewHandler.CHAR_ENCODING);
                } catch (IllegalStateException e) {
                    //jsp inclusion seems to have already called getWriter
                    writer = response.getWriter();
                }
            } else if (obj instanceof ServletResponse) {
                ServletResponse response = (ServletResponse) obj;
                response.setContentType(D2DViewHandler.HTML_CONTENT_TYPE);
                writer = new OutputStreamWriter(response.getOutputStream(),
                                                D2DViewHandler.CHAR_ENCODING);
            } else if (obj instanceof RenderResponse) {
                RenderResponse response = (RenderResponse) obj;
                response.setContentType(D2DViewHandler.HTML_CONTENT_TYPE);
                writer = new OutputStreamWriter(
                        response.getPortletOutputStream(),
                        D2DViewHandler.CHAR_ENCODING);
            } else {
                throw new FacesException("unknown type of response: " + obj);
            }
        }

        context.setResponseWriter(
                new DOMResponseWriter(writer, D2DViewHandler.HTML_CONTENT_TYPE,
                                      D2DViewHandler.CHAR_ENCODING));
    }

    private static Map getSessionMap(FacesContext context) {
        if (null == context) {
            context = FacesContext.getCurrentInstance();
        }
        Map sessionMap = context.getExternalContext().getSessionMap();
        if (null == sessionMap) {
            context.getExternalContext().getSession(true);
            sessionMap = context.getExternalContext().getSessionMap();
        }
        return sessionMap;
    }

    public void writeState(FacesContext context) throws IOException {
        if (delegateView(context.getViewRoot().getViewId())) {
            delegate.writeState(context);
        }
    }

    public Locale calculateLocale(FacesContext context) {
        Iterator locales = context.getExternalContext().getRequestLocales();

        while (locales.hasNext()) {
            Locale locale = (Locale) locales.next();
            Iterator supportedLocales = context.getApplication()
                    .getSupportedLocales();

            while (supportedLocales.hasNext()) {
                Locale supportedLocale = (Locale) supportedLocales.next();
                if (locale.getLanguage()
                        .equals(supportedLocale.getLanguage())) {

                    if ((null == supportedLocale.getCountry()) ||
                        ("".equals(supportedLocale.getCountry()))) {
                        return supportedLocale;
                    }

                    if (locale.equals(supportedLocale)) {
                        return supportedLocale;
                    }

                }
            }
        }

        Locale defaultLocale = context.getApplication().getDefaultLocale();
        return (null == defaultLocale) ? Locale.getDefault() : defaultLocale;
    }

    public String calculateRenderKitId(FacesContext context) {
        return delegate.calculateRenderKitId(context);
    }

    public static boolean isValueReference(String value) {
        if (value == null) {
            throw new NullPointerException();
        }
        if ((value.indexOf("#{") != -1) &&
            (value.indexOf("#{") < value.indexOf('}'))) {
            return true;
        }
        return false;
    }

    /**
     * A dumber version (that can't find child components of the UIData
     * component) of this method resides in UIComponentBase. The same is true
     * for the private findComoponent in UIComponentBase - it is duplicated
     * here.
     *
     * @param clientId
     * @param base
     * @throws NullPointerException {@inheritDoc}
     */
    public static UIComponent findComponent(String clientId, UIComponent base) {

        if (clientId == null) {
            throw new NullPointerException();
        }
        if (base == null) {
            throw new NullPointerException();
        }

        // Set base, the parent component whose children are searched, to be the
        // nearest parent that is either 1) the view root if the id expression
        // is absolute (i.e. starts with the delimiter) or 2) the nearest parent
        // NamingContainer if the expression is relative (doesn't start with
        // the delimiter)
        String delimeter = String.valueOf(NamingContainer.SEPARATOR_CHAR);
        if (clientId.startsWith(delimeter)) {
            // Absolute searches start at the root of the tree
            while (base.getParent() != null) {
                base = base.getParent();
            }
            // Treat remainder of the expression as relative
            clientId = clientId.substring(1);
        } else {
            // Relative expressions start at the closest NamingContainer or root
            while (base.getParent() != null) {
                if (base instanceof NamingContainer) {
                    break;
                }
                base = base.getParent();
            }
        }
        // Evaluate the search expression (now guaranteed to be relative)
        String id = null;
        UIComponent result = null;
        while (clientId.length() > 0) {
            int separator = clientId.indexOf(NamingContainer.SEPARATOR_CHAR);
            if (base instanceof UIData) {
                if (separator >= 0) {
                    clientId = clientId.substring(separator + 1);
                }
                separator = clientId.indexOf(NamingContainer.SEPARATOR_CHAR);
            }
            if (separator >= 0) {
                id = clientId.substring(0, separator);
                clientId = clientId.substring(separator + 1);
            } else {
                id = clientId;
                clientId = "";
            }
            result = D2DViewHandler.findComponent(base, id);
            if ((result == null) || (clientId.length() == 0)) {
                break; // Missing intermediate node or this is the last node
            }
            if (result instanceof NamingContainer) {
                result = D2DViewHandler.findComponent(clientId, result);
                break;
            } else {
                throw new IllegalArgumentException(id);
            }
        }

        return result;
    }

    private static String truncate(String remove, String input) {
        return input.substring(0, input.length() - remove.length());
    }

    public void setActionURLSuffix(String param) {
        actionURLSuffix = param;
    }


    public void setDelegateNonIface(String param) {
        delegateNonIface = D2DViewHandler
                .getStringAsBoolean(param, delegateNonIfaceDefault);
    }

    public void setReloadInterval(String param) {
        reloadInterval = D2DViewHandler.getStringAsLong(
                param, reloadIntervalDefault);
        if (-1 != reloadInterval) {
            //convert user input in seconds into milliseconds internally
            reloadInterval = reloadInterval * 1000;
        }
    }

    private static boolean getStringAsBoolean(String value,
                                              boolean defaultValue) {

        if (value == null) {
            return defaultValue;
        }

        if (value.equalsIgnoreCase("false") ||
            value.equalsIgnoreCase("off") ||
            value.equalsIgnoreCase("no")) {
            return false;
        }

        if (value.equalsIgnoreCase("true") ||
            value.equalsIgnoreCase("on") ||
            value.equalsIgnoreCase("yes")) {
            return true;
        }

        return defaultValue;
    }

    private static long getStringAsLong(String param, long defaultValue) {
        if (param == null) {
            return defaultValue;
        }

        long value = defaultValue;

        try {
            value = Long.parseLong(param);
        } catch (NumberFormatException e) {
            if (log.isWarnEnabled()) {
                log.warn("Unable to parse string as long " + param);
            }
        }

        return value;

    }

    //Determine whether handling of the view should be delegated to
    //the delegate ViewHandler
    private boolean delegateView(String viewId) {
        Map requestMap = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestMap();
        //If it's served by the PersistentFacesServlet, do not
        //delegate
        if (PersistentFacesCommonlet.PERSISTENT.equals(
                requestMap.get(PersistentFacesCommonlet.SERVLET_KEY))) {
            return false;
        }
        if (delegateNonIface) {
            return (!viewId.endsWith(".iface"));
        }
        return false;
    }

    private void initializeParameters(FacesContext context) {
        if (parametersInitialized) {
            return;
        }

        ExternalContext externalContext = context.getExternalContext();
        setDelegateNonIface(externalContext.getInitParameter(
                D2DViewHandler.DELEGATE_NONIFACE));
        setActionURLSuffix(externalContext.getInitParameter(
                D2DViewHandler.ACTION_URL_SUFFIX));
        setReloadInterval(externalContext.getInitParameter(
                D2DViewHandler.RELOAD_INTERVAL));
        jsfStateManagement = Boolean.valueOf(
                externalContext.getInitParameter(DO_JSF_STATE_MANAGEMENT))
                .booleanValue();
        if (!jsfStateManagement) {
            log.debug("JSF State Management not provided");
        }
        parametersInitialized = true;
    }

    //MyFaces processes the viewId before passing it to the ViewHandler
    //so we have no idea of knowing what it really is. This function
    //mimics the MyFaces process so we can compare ids modulo it
    private static String mungeViewId(String viewId) {
        String defaultSuffix = FacesContext.getCurrentInstance()
                .getExternalContext()
                .getInitParameter(ViewHandler.DEFAULT_SUFFIX_PARAM_NAME);
        String suffix = defaultSuffix != null ?
                        defaultSuffix : ViewHandler.DEFAULT_SUFFIX;

        int dot = viewId.lastIndexOf('.');
        if (dot == -1) {
            D2DViewHandler.log
                    .error("Assumed extension mapping, but there is no extension in " +
                           viewId);
        } else {
            viewId = viewId.substring(0, dot) + suffix;
        }

        return viewId;
    }
    
    private static UIComponent findComponent(UIComponent uiComponent, 
                                                        String componentId) {
        UIComponent component = null;
        UIComponent child = null;;
        
        if (componentId.equals(uiComponent.getId())) {
            return uiComponent;
        }
        Iterator children = uiComponent.getFacetsAndChildren();
        while (children.hasNext() && (component == null)) {
            child = (UIComponent) children.next();
            if (!(child instanceof NamingContainer)) {
                component = D2DViewHandler.findComponent(child, componentId);
                if (component != null) {
                    break;
                }
            } else if (componentId.endsWith(child.getId())) {
                component = child;
                break;
            }
        }
        return component;
    }
}
