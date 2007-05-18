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

import com.icesoft.faces.component.NamespacingViewRoot;
import com.icesoft.faces.context.BridgeExternalContext;
import com.icesoft.faces.context.BridgeFacesContext;
import com.icesoft.faces.context.DOMResponseWriter;
import com.icesoft.faces.webapp.http.servlet.ServletExternalContext;
import com.icesoft.faces.webapp.parser.JspPageToDocument;
import com.icesoft.faces.webapp.parser.Parser;
import com.icesoft.faces.webapp.xmlhttp.PersistentFacesCommonlet;
import com.icesoft.jasper.Constants;
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
import java.beans.Beans;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Arrays;
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
        if (log.isInfoEnabled()) {
            log.info(new ProductInfo().toString());//Announce ICEfaces
        }
    }

    private final static String DELEGATE_NONIFACE =
            "com.icesoft.faces.delegateNonIface";
    private final static String ACTION_URL_SUFFIX =
            "com.icesoft.faces.actionURLSuffix";
    private final static String RELOAD_INTERVAL =
            "com.icesoft.faces.reloadInterval";
    private final static String DO_JSF_STATE_MANAGEMENT =
            "com.icesoft.faces.doJSFStateManagement";
    public final static String INCLUDE_OPEN_AJAX_HUB =
            "com.icesoft.faces.openAjaxHub";
    private final static String LAST_LOADED_KEY = "_lastLoaded";
    private final static String LAST_CHECKED_KEY = "_lastChecked";
    // Key for storing ICEfaces auxillary data in the session
    public static final String DOM_CONTEXT_TABLE =
            "com.icesoft.faces.sessionAuxiliaryData";
    public static final String CHAR_ENCODING = "UTF-8";
    public static final String HTML_CONTENT_TYPE =
            "text/html;charset=" + CHAR_ENCODING;

    public static final String DEFAULT_VIEW_ID = "default";

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
        try {
            InputStream inputStream = this.getClass().getResourceAsStream("serializedTagToComponentMapFull.ser");
            parser = new Parser(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public D2DViewHandler(ViewHandler delegate) {
        this();
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

        if (log.isTraceEnabled()) {
            log.trace("renderView(FC,UIVR)  BEFORE  renderResponse  " +
                    "viewToRender.getViewId(): " + viewToRender.getViewId());
        }
        renderResponse(context);

        if (jsfStateManagement) {
            StateManager stateMgr = context.getApplication().getStateManager();
            stateMgr.saveSerializedView(context);
            // JSF 1.1 removes transient components here, but I don't think that 1.2 does
        }

       // This has been moved to the ServletView

    }


    /**
     * Create a new ViewRoot
     *
     * @param context FacesContext
     * @param viewId  ViewId identifying the root
     * @return A new viewRoot
     */
    public UIViewRoot createView(FacesContext context, String viewId) {
        initializeParameters(context);

        if (delegateView(viewId)) {
            return delegate.createView(context, viewId);
        }

        UIViewRoot root = new NamespacingViewRoot(context);
//        UIViewRoot root = new UIViewRoot();
        root.setRenderKitId(RenderKitFactory.HTML_BASIC_RENDER_KIT);

        Map contextServletTable =
                getContextServletTable(context);
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
    }

    /**
     * Restore the view if possible. This method can return null if
     * no ViewRoot is available. The <code>LifeCycle</code> will call
     * createView in this case.
     *
     * @param context FacesContext
     * @param viewId  ViewId identifying the view to restore
     * @return UIViewRoot instance if found, null if none yet created,
     *         or if trying to model Seam JSF behaviour.
     */
    public UIViewRoot restoreView(FacesContext context, String viewId) {
        this.initializeParameters(context);


        if (delegateView(viewId)) {
            return delegate.restoreView(context, viewId);
        }

        UIViewRoot currentRoot = context.getViewRoot();
        //MyFaces expects path to match current view
        ExternalContext externalContext = context.getExternalContext();

        if (externalContext instanceof ServletExternalContext) {

            ServletExternalContext servletExternalContext =
                    (ServletExternalContext) externalContext;

            servletExternalContext.setRequestServletPath(viewId);

            if (null != externalContext.getRequestPathInfo()) {
                //it's not null, so must be valid to keep in synch for MyFaces
                servletExternalContext.setRequestPathInfo(viewId);
            }

            if (SeamUtilities.isSeamEnvironment()) {
                if (servletExternalContext.getRequestParameterMap().remove(
                        PersistentFacesCommonlet.SEAM_LIFECYCLE_SHORTCUT) != null) {

                    if (log.isTraceEnabled()) {
                        log.trace("Seam Keyword shortcut found, new ViewRoot");
                    }
                    return null;
                } else {
                    if (log.isTraceEnabled()) {
                        log.trace("No Seam Keyword shortcut found");
                    }
                }
            }
        }

        if (null != currentRoot &&
                mungeViewId(viewId)
                        .equals(mungeViewId(
                                currentRoot.getViewId()))) {
//            purgeSeamContexts(context, currentRoot);
            return currentRoot;
        }

        Map contextServletTable =
                getContextServletTable(context);
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

        if ((null != root) && (null != viewId) &&
                (mungeViewId(viewId).equals(mungeViewId(root.getViewId())))) {
        }

        return root;
    }


    private static Map getContextServletTables(FacesContext context) {
        Map sessionMap = context.getExternalContext().getSessionMap();
        String viewNumber = "-";
        if (context instanceof BridgeFacesContext) {
            viewNumber = ((BridgeFacesContext) context).getViewNumber();
        }

        String treeKey = viewNumber + "/" + DOM_CONTEXT_TABLE;
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
        Map domContextTables = getContextServletTables(context);
        String servletRequestPath =
                getServletRequestPath(context);
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
                if (log.isWarnEnabled()) {
                    log
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
        //Context and resource must be non-null
        if (context == null) {
            throw new NullPointerException("context cannot be null");
        }

        if (path == null) {
            throw new NullPointerException("path cannot be null");
        }

        //The URI class doesn't like resource with illegal characters so
        //we need to do our own encoding.
        path = encode(path);

        ExternalContext extCtxt = context.getExternalContext();

        // Components that render out links to resources like images, CSS,
        // JavaScript, etc. must do it correctly.  In a normal web app, there
        // may not be much to do but in a portlet environment, we have to
        // resolve these correctly.
        if (isPortlet(extCtxt)) {
            path = resolveFully(extCtxt, path);
        } else {
            //is it an absolute path?
            if (path.startsWith("/")) {
                //resolve the path to the application's context
                StringBuffer dir = new StringBuffer();
                int atoms = extCtxt.getRequestServletPath().split("/").length;
                while (atoms-- > 2) dir.append("../");
                path = URI.create(dir + "." + path).normalize().toString();                
            }
            //else don't resolve (see: ViewHandler.getResourceURL javadocs) 
        }

        return path;
    }

    private String encode(String path){
        return path.trim().replaceAll(" ","%20");
    }

    private boolean isPortlet(ExternalContext extCtxt) {
        return extCtxt.getRequestMap().get(Constants.PORTLET_KEY) != null;
    }

    /**
     * Resolves references fragements to the full resource reference including
     * the context path and the servlet path.
     */
    private String resolveFully(ExternalContext extCtxt, String resource) {

        String context = extCtxt.getRequestContextPath();

        //Absolute references are handled differently.  For portlets, we just
        //need to stick the context in front.
        if( resource.startsWith("/") ){
            return context + resource;
        }

        //For relative paths, we need to resolve them to the full path
        String base = context + extCtxt.getRequestServletPath();
        try {
            URI baseURI = new URI(base);
            URI resourceURI = new URI(resource);
            URI resolvedURI = baseURI.resolve(resourceURI);
            return resolvedURI.toString();

        } catch (URISyntaxException e) {
            if( log.isWarnEnabled() ){
                log.warn( "could not resolve URI's based on" +
                          "\n  context : " + extCtxt.getRequestContextPath() +
                          "\n  path    : " + extCtxt.getRequestServletPath() +
                          "\n  resource: " + resource, e );
            }
            return resource;
        }
    }    

    protected long getTimeAttribute(UIComponent root, String key) {
        Long timeLong = (Long) root.getAttributes().get(key);
        long time = (null == timeLong) ? 0 :
                timeLong.longValue();
        return time;
    }

    protected void renderResponse(FacesContext facesContext) throws IOException {
        BridgeFacesContext context = (BridgeFacesContext) facesContext;
        UIViewRoot root = context.getViewRoot();
        String viewId = root.getViewId();

        if (log.isTraceEnabled()) {
            log.trace("Rendering " + root + " with " +
                    root.getChildCount() + " children");
        }

        clearSession(context);
        ResponseWriter responseWriter = context.createAndSetResponseWriter();

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
                        viewId = truncate(".faces", viewId);
                    } else if (viewId.endsWith(".jsf")) {
                        viewId = truncate(".jsf", viewId);
                    } else if (viewId.endsWith(".iface")) {
                        viewId = truncate(".iface", viewId);
                    } else if (viewId.endsWith(".jsp")) {
                        //MyFaces thinks everything is a .jsp
                        viewId = truncate(".jsp", viewId);
                    }

                    viewId = viewId + ".jspx";
                    viewURL = context.getExternalContext().getResource(viewId);
                }

                if (null == viewURL) {
                    if (viewId.endsWith(".jspx")) {
                        viewId = truncate(".jspx", viewId) +
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
                parser.parse(viewInput, context);
                root.getAttributes().put(LAST_LOADED_KEY,
                        new Long(System.currentTimeMillis()));


            } catch (Throwable e) {
                throw new FacesException("Can't parse stream for " + viewId +
                        " " + e.getMessage(), e);
            }
        } else {
            responseWriter.startDocument();
            renderResponse(context, root);
            responseWriter.endDocument();
            tracePrintComponentTree(context);
        }

    }

    protected void renderResponse(FacesContext context, UIComponent component)
            throws IOException {
        if (!component.isRendered())
            return;

        // UIViewRoot.encodeBegin(FacesContext) resets its counter for
        //   createUniqueId(), which we don't want, or else we get
        //   duplicate ids
        boolean isUIViewRoot = component instanceof UIViewRoot;
        if( !isUIViewRoot )
            component.encodeBegin(context);

        if (component.getRendersChildren()) {
            component.encodeChildren(context);
        } else {
            Iterator kids = component.getChildren().iterator();
            while (kids.hasNext()) {
                renderResponse(context, (UIComponent) kids.next());
            }
        }

        if( !isUIViewRoot )
            component.encodeEnd(context);

        //Workaround so that MyFaces UIData will apply values to
        //child components even if the tree is not restored via StateManager
        if (component instanceof javax.faces.component.UIData) {
            StateHolder stateHolder = (StateHolder) component;
            stateHolder.restoreState(context, stateHolder.saveState(context));
        }
    }

    protected void tracePrintComponentTree(FacesContext context) {
        tracePrintComponentTree( context, context.getViewRoot() );
    }

    protected void tracePrintComponentTree(
        FacesContext context, UIComponent component)
    {
        if (log.isTraceEnabled()) {
            StringBuffer sb = new StringBuffer(4096);
            sb.append("tracePrintComponentTree() vvvvvv\n");
            tracePrintComponentTree(context, component, 0, sb, null);
            log.trace(sb.toString());
            log.trace("tracePrintComponentTree() ^^^^^^");
        }
    }

    private void tracePrintComponentTree(
        FacesContext context, UIComponent component,
        int levels, StringBuffer sb, String facetName)
    {
        if( component == null ) {
            sb.append("null\n");
            return;
        }
        final String PREFIX_WHITESPACE = "  ";
        StringBuffer prefix = new StringBuffer(64);
        for (int i = 0; i < levels; i++)
            prefix.append(PREFIX_WHITESPACE);
        prefix.append("<");
        String compStr = component.toString();

        StringBuffer open = new StringBuffer(512);
        open.append(prefix);
        open.append(compStr);
        Map facetsMap = component.getFacets();
        boolean hasKids = component.getChildCount() > 0;
        boolean hasFacets = (facetsMap != null) && (facetsMap.size() > 0);
        boolean hasUnderlings = hasKids | hasFacets;
        if (!hasUnderlings)
            open.append("/");
        open.append(">");
        if( facetName != null ) {
            open.append(" facetName: ");
            open.append(facetName);
        }
        open.append(" id: ");
        open.append(component.getId());
        if( component.getParent() != null ) {
            open.append(" clientId: ");
            open.append(component.getClientId(context));
        }
        if(hasKids) {
            open.append(" kids: ");
            open.append(Integer.toString(component.getChildCount()));
        }
        if(hasFacets) {
            open.append(" facets: ");
            open.append(Integer.toString(facetsMap.size()));
        }
        if(component.isTransient())
            open.append(" TRANSIENT ");
        sb.append(open.toString());
        sb.append('\n');

        if (hasUnderlings) {
            if( hasFacets ) {
                Object[] facetKeys = facetsMap.keySet().toArray();
                Arrays.sort(facetKeys);
                for(int i = 0; i < facetKeys.length; i++) {
                    tracePrintComponentTree(
                        context, (UIComponent) facetsMap.get(facetKeys[i]),
                        levels + 1, sb, facetKeys[i].toString());
                }
            }
            if( hasKids ) {
                Iterator kids = component.getChildren().iterator();
                while (kids.hasNext()) {
                    tracePrintComponentTree(
                        context, (UIComponent) kids.next(), levels + 1, sb, null);
                }
            }

            StringBuffer close = new StringBuffer(512);
            close.append(prefix);
            close.append("/");
            close.append(compStr);
            close.append(">");
            sb.append(close);
            sb.append('\n');
        }
    }

    protected void clearSession(FacesContext context) {
        Map contextServletTable =
                getContextServletTable(context);
        contextServletTable.remove(DOMResponseWriter.RESPONSE_CONTEXTS_TABLE);
        contextServletTable.remove(DOMResponseWriter.RESPONSE_VIEWROOT);
        contextServletTable.remove(DOMResponseWriter.RESPONSE_DOM);
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
     */
    public static UIComponent findComponent(String clientId, UIComponent base) {
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
            result = findComponent(base, id);
            if ((result == null) || (clientId.length() == 0)) {
                break; // Missing intermediate node or this is the last node
            }
            if (result instanceof NamingContainer) {
                result = findComponent(clientId, result);
                break;
            }
        }

        return result;
    }

    private static String truncate(String remove, String input) {
        return input.substring(0, input.length() - remove.length());
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

        ExternalContext ec = context.getExternalContext();
        String delegateNonIfaceParameter = ec.getInitParameter(DELEGATE_NONIFACE);
        String reloadIntervalParameter = ec.getInitParameter(RELOAD_INTERVAL);
        String jsfStateManagementParameter = ec.getInitParameter(DO_JSF_STATE_MANAGEMENT);
        actionURLSuffix = ec.getInitParameter(ACTION_URL_SUFFIX);
        delegateNonIface = delegateNonIfaceParameter == null ? delegateNonIfaceDefault : Boolean.valueOf(delegateNonIfaceParameter).booleanValue();
        try {
            reloadInterval = Long.parseLong(reloadIntervalParameter) * 1000;
        } catch (NumberFormatException e) {
            reloadInterval = reloadIntervalDefault * 1000;
        }
        jsfStateManagement = Boolean.valueOf(jsfStateManagementParameter).booleanValue();
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
            log
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
        UIComponent child = null;

        if (componentId.equals(uiComponent.getId())) {
            return uiComponent;
        }
        Iterator children = uiComponent.getFacetsAndChildren();
        while (children.hasNext() && (component == null)) {
            child = (UIComponent) children.next();
            if (!(child instanceof NamingContainer)) {
                component = findComponent(child, componentId);
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
