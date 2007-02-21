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

package com.icesoft.faces.context;

import com.icesoft.faces.application.D2DViewHandler;
import com.icesoft.faces.el.ELContextImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.el.ELContext;
import javax.faces.FactoryFinder;
import javax.faces.application.Application;
import javax.faces.application.ApplicationFactory;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;
import javax.faces.render.RenderKitFactory;
import java.util.*;

/**
 * This is the ICEfaces implementation of the JSF FacesContext API.
 */
public class BridgeFacesContext extends FacesContext
        implements java.io.Serializable {


    public BridgeFacesContext(ExternalContext externalContext) {
        super();
        setCurrentInstance(this);
        setExternalContext(externalContext);
    }


    public void setCurrentInstance() {
        setCurrentInstance(this);
    }


    private Application application;

    public Application getApplication() {
        if (null != application) {
            return application;
        }
        ApplicationFactory aFactory =
                (ApplicationFactory) FactoryFinder.getFactory(
                        FactoryFinder.APPLICATION_FACTORY);
        application = aFactory.getApplication();
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }


    public Iterator getClientIdsWithMessages() {
        return (faceMessages.keySet().iterator());
    }

    private ExternalContext externalContext;

    public ExternalContext getExternalContext() {
        return (this.externalContext);
    }

    public void setExternalContext(ExternalContext externalContext) {
        this.externalContext = externalContext;
    }


    private Object elContext;

    public ELContext getELContext() {
        if (null == elContext) {
            elContext = new ELContextImpl(getApplication());
            ((ELContext) elContext).putContext(FacesContext.class, this);
            UIViewRoot root = getViewRoot();
            if (null != root) {
                ((ELContext) elContext).setLocale(root.getLocale());
            }
        }
        return ((ELContext) elContext);
    }


    public Severity getMaximumSeverity() {
        throw new UnsupportedOperationException();
    }


    //storage for association between clientId and facesMessage
    private HashMap faceMessages = new HashMap();

    /**
     * gets all FacesMessages whether or not associatted with clientId.
     *
     * @return list of FacesMessages
     */
    public Iterator getMessages() {
        Vector vector = new Vector();
        Iterator iterator = faceMessages.values().iterator();
        while (iterator.hasNext()) {
            vector.addAll((Vector) iterator.next());
        }
        return vector.iterator();
    }

    /**
     * returns list of FacesMessages associated with a clientId. If client id is
     * null, then return all FacesMessages which are not assocaited wih any
     * clientId
     *
     * @param clientId
     * @return list of FacesMessages
     */
    public Iterator getMessages(String clientId) {
        try {
            return ((Vector) faceMessages.get(clientId)).iterator();
        } catch (NullPointerException e) {
            if (log.isDebugEnabled()) {
                log.debug("Cannot find clientId " + clientId +
                          "from facesMessages");
            }
            return Collections.EMPTY_LIST.iterator();
        }
    }

    public RenderKit getRenderKit() {
        UIViewRoot viewRoot = getViewRoot();
        if (null == viewRoot) {
            return (null);
        }
        String renderKitId = viewRoot.getRenderKitId();
        if (null == renderKitId) {
            return (null);
        }

        RenderKitFactory renderKitFactory = (RenderKitFactory)
                FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
        RenderKit renderKit = renderKitFactory.getRenderKit(this, renderKitId);
        return (renderKit);
    }


    private boolean renderResponse = false;

    public boolean getRenderResponse() {
        return (this.renderResponse);
    }


    private boolean responseComplete = false;

    public boolean getResponseComplete() {
        return (this.responseComplete);
    }


    private ResponseStream responseStream;

    public ResponseStream getResponseStream() {
        return (this.responseStream);
    }

    public void setResponseStream(ResponseStream responseStream) {
        this.responseStream = responseStream;
    }


    private ResponseWriter responseWriter;

    public ResponseWriter getResponseWriter() {
        return (responseWriter);
    }

    public void setResponseWriter(ResponseWriter responseWriter) {
        this.responseWriter = responseWriter;
    }


    private UIViewRoot viewRoot;

    public UIViewRoot getViewRoot() {
        if (null == viewRoot) {
            Map contextServletTable = getContextServletTable();
            if (null != contextServletTable) {
                viewRoot = (UIViewRoot) contextServletTable
                        .get(DOMResponseWriter.RESPONSE_VIEWROOT);
            }
        }

        return (this.viewRoot);
    }

    public void setViewRoot(UIViewRoot viewRoot) {
        //pointing this FacesContext to the new view
        Map contextServletTable = getContextServletTable();
        if (null != contextServletTable) {
            if (viewRoot != null) {
                contextServletTable
                        .put(DOMResponseWriter.RESPONSE_VIEWROOT, viewRoot);
            } else {
                contextServletTable.remove(DOMResponseWriter.RESPONSE_VIEWROOT);
            }
        }
        responseWriter = null;
        this.viewRoot = viewRoot;
    }

    private static final Log log = LogFactory.getLog(BridgeFacesContext.class);
    String iceFacesId;

    public String getIceFacesId() {
        if (iceFacesId == null) {
            iceFacesId = (String) ((BridgeExternalContext) externalContext)
                    .getApplicationSessionMap().get("icefacesID");
        }
        return iceFacesId;
    }

    String viewNumber;

    /**
     * Return the unique identifier associated with each browser window
     * associated with a single user.
     */
    public String getViewNumber() {
        if (null == viewNumber) {
            viewNumber = (String) externalContext.getRequestParameterMap()
                    .get("viewNumber");
        }
        return viewNumber;
    }

    /**
     * Return the id of the Element that currently has focus in the browser.
     *
     * @return String
     */
    public String getFocusId() {
        String focusId = "";
        focusId =
                (String) externalContext.getRequestParameterMap().get("focus");
        return focusId;
    }

    /**
     * Sets the id of the Element that should get focus in the browser.
     */
    public void setFocusId(String focusId) {
        externalContext.getRequestParameterMap().put("focus", focusId);
    }

    Map getContextServletTable() {
        return D2DViewHandler.getContextServletTable(this);
    }

    /**
     * add a FacesMessage to the set of message associated with the clientId, if
     * clientId is not null.
     *
     * @param clientId
     * @param message
     */
    public void addMessage(String clientId, FacesMessage message) {
        if (message == null) {
            throw new NullPointerException("Message is null");
        }
        if (faceMessages.containsKey(clientId)) {
            ((Vector) faceMessages.get(clientId)).addElement(message);
        } else {
            Vector vector = new Vector();
            vector.add(message);
            faceMessages.put(clientId, vector);
        }
    }

    /**
     * The release() found in FacesContextImpl is more comprehensive: since they
     * blow away the context instance after a response, they null/false out much
     * more than we do. We chose to keep the context instance around across
     * requests so we need to keep some of our state intact.
     */
    public void release() {
        faceMessages.clear();
        renderResponse = false;
        responseComplete = false;
        ((BridgeExternalContext) externalContext).resetRequestMap();
        setCurrentInstance(null);
    }


    public void renderResponse() {
        this.renderResponse = true;
    }


    public void responseComplete() {
        this.responseComplete = true;
    }

}
