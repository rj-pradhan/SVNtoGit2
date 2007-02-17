package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.application.D2DViewHandler;
import com.icesoft.faces.context.BridgeExternalContext;
import com.icesoft.faces.context.BridgeFacesContext;
import com.icesoft.faces.context.DOMResponseWriter;
import com.icesoft.faces.util.DOMUtils;
import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Response;
import com.icesoft.faces.webapp.http.common.Server;
import com.icesoft.faces.webapp.http.common.standard.FixedXMLContentHandler;

import javax.faces.FactoryFinder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.render.ResponseStateManager;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReceiveSendUpdates implements Server {
    private static final LifecycleFactory LifecycleFactory = (LifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
    private Lifecycle lifecycle = LifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
    private static String postBackKey;
    private UpdateManager updateManager;

    static {
        //We will place VIEW_STATE_PARAM in the requestMap so that
        //JSF 1.2 doesn't think the request is a postback and skip
        //execution
        try {
            Field field = ResponseStateManager.class.getField("VIEW_STATE_PARAM");
            if (null != field) {
                postBackKey = (String) field.get(ResponseStateManager.class);
            }
        } catch (Exception e) {
        }
    }

    public ReceiveSendUpdates(UpdateManager updateManager) {
        this.updateManager = updateManager;
    }

    public void service(final Request request) throws Exception {
        BridgeFacesContext context = (BridgeFacesContext) FacesContext.getCurrentInstance();
        if (request.getParameterAsBoolean("partial", false)) {
            String componentID = request.getParameter("ice.event.captured");
            UIComponent component = D2DViewHandler.findComponent(componentID, context.getViewRoot());
            renderCyclePartial(context, component);
        } else {
            renderCycle(context);
        }

        final BridgeExternalContext externalContext = (BridgeExternalContext) context.getExternalContext();
        if (externalContext.redirectRequested()) {
            request.respondWith(new SendRedirectHandler(externalContext));
        } else {
            String[] views = request.getParameterAsStrings("viewNumber");
            request.respondWith(new SendUpdatesHandler(views, externalContext));
        }
    }

    public void shutdown() {
    }

    private void renderCycle(BridgeFacesContext context) {
        synchronized (context) {
            DOMResponseWriter.applyBrowserDOMChanges(context);
            if (null != postBackKey) {
                context.getExternalContext().getRequestParameterMap()
                        .put(postBackKey, "not reload");
            }
            lifecycle.execute(context);
            lifecycle.render(context);
        }
    }

    private void renderCyclePartial(BridgeFacesContext context,
                            UIComponent component) {
        synchronized (context) {
            DOMResponseWriter.applyBrowserDOMChanges(context);
            List alteredRequiredComponents =
                    setRequiredFalseInFormContaining(component);
            if (null != postBackKey) {
                context.getExternalContext().getRequestParameterMap()
                        .put(postBackKey, "not reload");
            }
            lifecycle.execute(context);
            lifecycle.render(context);
            setRequiredTrue(alteredRequiredComponents);
        }
    }

    private void setRequiredTrue(List requiredComponents) {
        Iterator i = requiredComponents.iterator();
        UIInput next = null;
        while (i.hasNext()) {
            next = (UIInput) i.next();
            ((UIInput) next).setRequired(true);
        }
    }

    private List setRequiredFalseInFormContaining(UIComponent component) {
        List alteredComponents = new ArrayList();
        UIComponent form = getContainingForm(component);
        setRequiredFalseOnAllChildrenExceptOne(form, component,
                alteredComponents);
        return alteredComponents;
    }

    private void setRequiredFalseOnAllChildrenExceptOne(UIComponent parent,
                                                        UIComponent componentToAvoid,
                                                        List alteredComponents) {
        int length = parent.getChildCount();
        UIComponent next = null;
        for (int i = 0; i < length; i++) {
            next = (UIComponent) parent.getChildren().get(i);
            if (next instanceof UIInput && next != componentToAvoid) {
                if (((UIInput) next).isRequired()) {
                    ((UIInput) next).setRequired(false);
                    alteredComponents.add(next);
                }
            }
            setRequiredFalseOnAllChildrenExceptOne(next, componentToAvoid,
                    alteredComponents);
        }
    }

    private UIComponent getContainingForm(UIComponent component) {
        if (null == component) {
            return FacesContext.getCurrentInstance().getViewRoot();
        }
        UIComponent parent = component.getParent();
        while (parent != null) {
            if (parent instanceof UIForm) {
                break;
            }
            parent = parent.getParent();
        }
        return (UIForm) parent;
    }

    private class SendUpdatesHandler extends FixedXMLContentHandler {
        private String[] views;
        private BridgeExternalContext externalContext;

        public SendUpdatesHandler(String[] views, BridgeExternalContext externalContext) {
            this.views = views;
            this.externalContext = externalContext;
        }

        public void writeTo(Writer writer) throws IOException {
            updateManager.serialize(views, writer);
        }

        public void respond(Response response) throws Exception {
            //todo: replace this by a message
            Cookie[] cookies = externalContext.getResponseCookies();
            for (int i = 0; i < cookies.length; i++) {
                response.addCookie(cookies[i]);
            }
            super.respond(response);
        }
    }

    private static class SendRedirectHandler extends FixedXMLContentHandler {
        private final BridgeExternalContext externalContext;

        public SendRedirectHandler(BridgeExternalContext externalContext) {
            this.externalContext = externalContext;
        }

        public void writeTo(Writer writer) throws IOException {
            writer.write("<redirect url=\"" + DOMUtils.escapeAnsi(externalContext.redirectTo() ) + "\"/>");
            externalContext.redirectComplete();
        }
    }
}
