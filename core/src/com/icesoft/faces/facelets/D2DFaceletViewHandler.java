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

package com.icesoft.faces.facelets;

import com.icesoft.faces.application.D2DViewHandler;
import com.sun.facelets.Facelet;
import com.sun.facelets.FaceletFactory;
import com.sun.facelets.compiler.Compiler;
import com.sun.facelets.compiler.SAXCompiler;
import com.sun.facelets.compiler.TagLibraryConfig;
import com.sun.facelets.impl.DefaultFaceletFactory;
import com.sun.facelets.impl.DefaultResourceResolver;
import com.sun.facelets.impl.ResourceResolver;
import com.sun.facelets.tag.TagDecorator;
import com.sun.facelets.tag.TagLibrary;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

/**
 * <B>D2DViewHandler</B> is the ICEfaces Facelet ViewHandler implementation
 *
 * @see javax.faces.application.ViewHandler
 */
public class D2DFaceletViewHandler extends D2DViewHandler {

    //Facelets parameter constants
    public final static long DEFAULT_REFRESH_PERIOD = 2;
    public final static String PARAM_REFRESH_PERIOD = "facelets.REFRESH_PERIOD";
    public final static String PARAM_SKIP_COMMENTS = "facelets.SKIP_COMMENTS";
    public final static String PARAM_VIEW_MAPPINGS = "facelets.VIEW_MAPPINGS";
    public final static String PARAM_LIBRARIES = "facelets.LIBRARIES";
    public final static String PARAM_DECORATORS = "facelets.DECORATORS";
    public final static String PARAM_RESOURCE_RESOLVER =
            "facelets.RESOURCE_RESOLVER";

    // Log instance for this class
    private static Log log = LogFactory.getLog(D2DFaceletViewHandler.class);

    protected FaceletFactory faceletFactory;

    public D2DFaceletViewHandler() {
    }

    public D2DFaceletViewHandler(ViewHandler delegate) {
        super(delegate);
    }

    protected void faceletInitialize() {
        try {
            if (faceletFactory == null) {
                com.sun.facelets.compiler.Compiler c = new SAXCompiler();
                initializeCompiler(c);
                faceletFactory = createFaceletFactory(c);
            }
        }
        catch (Throwable t) {
            if (log.isErrorEnabled()) {
                log.error("Failed initializing facelet instance", t);
            }
        }
    }


    protected void initializeCompiler(Compiler c) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        ExternalContext ext = ctx.getExternalContext();

        // Load libraries
        String paramLibraries = ext.getInitParameter(PARAM_LIBRARIES);
        if (paramLibraries != null) {
            paramLibraries = paramLibraries.trim();
            String[] paramLibrariesArray = paramLibraries.split(";");
            for (int i = 0; i < paramLibrariesArray.length; i++) {
                try {
                    URL url = ext.getResource(paramLibrariesArray[i]);
                    if (url == null) {
                        throw new FileNotFoundException(paramLibrariesArray[i]);
                    }
                    TagLibrary tagLibrary = TagLibraryConfig.create(url);
                    c.addTagLibrary(tagLibrary);
                    if (log.isDebugEnabled()) {
                        log.debug("Loaded library: " + paramLibrariesArray[i]);
                    }
                }
                catch (IOException e) {
                    if (log.isWarnEnabled()) {
                        log.warn("Problem loading library: " + paramLibrariesArray[i], e);
                    }
                }
            }
        }

        // Load decorators
        String paramDecorators = ext.getInitParameter(PARAM_DECORATORS);
        if (paramDecorators != null) {
            paramDecorators = paramDecorators.trim();
            String[] paramDecoratorsArray = paramDecorators.split(";");
            for (int i = 0; i < paramDecoratorsArray.length; i++) {
                try {
                    Class tagDecoratorClass = Class.forName(paramDecoratorsArray[i]);
                    TagDecorator tagDecorator = (TagDecorator)
                            tagDecoratorClass.newInstance();
                    c.addTagDecorator(tagDecorator);
                    if (log.isDebugEnabled()) {
                        log.debug("Loaded decorator: " +
                                paramDecoratorsArray[i]);
                    }
                }
                catch (Exception e) {
                    if (log.isWarnEnabled()) {
                        log.warn("Problem loading decorator: " +
                                paramDecoratorsArray[i], e);
                    }
                }
            }
        }

        // Load whether to skip comments or not. For our hierarchial
        //  UIComponent tree, we have to throw away most useless text nodes,
        //  so this is a bit redundant getting the parameter. But who knows,
        //  things might change later, so best to preserve this code.
        String paramSkipComments =
                ext.getInitParameter(PARAM_SKIP_COMMENTS);
        // Default is true.  I think this behaviour has changed over time
        //  is stock Facelets builds from 1.0.x to 1.1.x
        if (paramSkipComments != null && paramSkipComments.equals("false")) {
            c.setTrimmingComments(false);
        }

        // This has to be true, otherwise table or other container
        //   UIComponents will have text children, when they're
        //   expecting only real UIComponents
        c.setTrimmingWhitespace(true);
        c.setTrimmingComments(true);

        // Use our special hierarchial subclasses of the stock Facelets classes
        c.setTextUnitFactory(new D2DTextUnitFactory());
        c.setFallbackTagCompilationUnitFactory(
                new D2DTagCompilationUnitFactory());
    }

    protected FaceletFactory createFaceletFactory(Compiler c) {
        long refreshPeriod = DEFAULT_REFRESH_PERIOD;
        FacesContext ctx = FacesContext.getCurrentInstance();
        String paramRefreshPeriod = ctx.getExternalContext().getInitParameter(
                PARAM_REFRESH_PERIOD);
        if (paramRefreshPeriod != null && paramRefreshPeriod.length() > 0) {
            try {
                refreshPeriod = Long.parseLong(paramRefreshPeriod);
            }
            catch (NumberFormatException nfe) {
                if (log.isWarnEnabled()) {
                    log.warn("Problem parsing refresh period: " +
                            paramRefreshPeriod, nfe);
                }
            }
        }

        ResourceResolver resourceResolver = null;
        String paramResourceResolver = ctx.getExternalContext().getInitParameter(
                PARAM_RESOURCE_RESOLVER);
        if (paramResourceResolver != null && paramResourceResolver.length() > 0) {
            try {
                Class resourceResolverClass = Class.forName(
                        paramResourceResolver,
                        true,
                        Thread.currentThread().getContextClassLoader());
                resourceResolver = (ResourceResolver)
                        resourceResolverClass.newInstance();
            }
            catch (Exception e) {
                throw new FacesException("Problem initializing ResourceResolver: " +
                        paramResourceResolver, e);
            }
        }
        if (resourceResolver == null)
            resourceResolver = new DefaultResourceResolver();

        return new DefaultFaceletFactory(c, resourceResolver, refreshPeriod);
    }


    protected String getRenderedViewId(FacesContext context, String actionId) {
        ExternalContext extCtx = context.getExternalContext();
        String viewId = actionId;
        if (extCtx.getRequestPathInfo() == null) {
            String facesSuffix = actionId.substring(actionId.lastIndexOf('.'));
            String viewSuffix = context.getExternalContext()
                    .getInitParameter(ViewHandler.DEFAULT_SUFFIX_PARAM_NAME);
            viewId = actionId.replaceFirst(facesSuffix, viewSuffix);
        }
        return viewId;
    }


    protected void renderResponse(FacesContext context) throws IOException {
        if (log.isTraceEnabled()) {
            log.trace("renderResponse(FC)");
        }
        try {
            clearSession(context);

            UIViewRoot viewToRender = context.getViewRoot();
            String renderedViewId =
                    getRenderedViewId(context, viewToRender.getViewId());
            viewToRender.setViewId(renderedViewId);
            if (viewToRender.getId() == null) {
                viewToRender.setId(viewToRender.createUniqueId());
            }

            if (viewToRender.getChildCount() == 0) {
                // grab our FaceletFactory and create a Facelet
                faceletInitialize();
                Facelet f = null;
                FaceletFactory.setInstance(faceletFactory);
                try {
                    f = faceletFactory.getFacelet(viewToRender.getViewId());
                } finally {
                    FaceletFactory.setInstance(null);
                }

                // Populate UIViewRoot
                f.apply(context, viewToRender);

                context.setViewRoot(viewToRender);

                verifyUniqueComponentIds(viewToRender, new HashMap());

                // Uses D2DViewHandler logging
                tracePrintComponentTree(context);
            }

            ResponseWriter responseWriter = context.getResponseWriter();
            responseWriter.startDocument();
            renderResponse(context, viewToRender);
            responseWriter.endDocument();
        }
        catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Problem in renderResponse: " + e.getMessage(), e);
            }
        }
    }

    protected static void removeTransient(UIComponent c) {
        UIComponent d, e;
        if (c.getChildCount() > 0) {
            for (Iterator itr = c.getChildren().iterator(); itr.hasNext();) {
                d = (UIComponent) itr.next();
                if (d.getFacets().size() > 0) {
                    for (Iterator jtr = d.getFacets().values().iterator(); jtr
                            .hasNext();) {
                        e = (UIComponent) jtr.next();
                        if (e.isTransient()) {
                            jtr.remove();
                        } else {
                            D2DFaceletViewHandler.removeTransient(e);
                        }
                    }
                }
                if (d.isTransient()) {
                    itr.remove();
                } else {
                    D2DFaceletViewHandler.removeTransient(d);
                }
            }
        }
        if (c.getFacets().size() > 0) {
            for (Iterator itr = c.getFacets().values().iterator(); itr
                    .hasNext();) {
                d = (UIComponent) itr.next();
                if (d.isTransient()) {
                    itr.remove();
                } else {
                    D2DFaceletViewHandler.removeTransient(d);
                }
            }
        }
    }

    protected static void verifyUniqueComponentIds(UIComponent comp, HashMap ids) {
        if (!log.isErrorEnabled())
            return;
        String id = comp.getId();
        if (id == null) {
            log.error("UIComponent has null id: " + comp);
        } else {
            if (ids.containsKey(id)) {
                log.error("Multiple UIComponents found, which are wrongfully using the same id: " + id + ".  Most recent UIComponent: " + comp);
            } else {
                ids.put(id, id);
            }
        }
        Iterator children = comp.getFacetsAndChildren();
        while (children.hasNext()) {
            UIComponent child = (UIComponent) children.next();
            verifyUniqueComponentIds(child, ids);
        }
    }
}
