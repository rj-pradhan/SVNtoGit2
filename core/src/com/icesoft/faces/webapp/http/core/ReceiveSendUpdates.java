package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.application.D2DViewHandler;
import com.icesoft.faces.context.BridgeFacesContext;
import com.icesoft.faces.context.DOMResponseWriter;
import com.icesoft.faces.webapp.http.Request;
import com.icesoft.faces.webapp.http.Server;
import com.icesoft.faces.webapp.http.ResponseHandler;
import com.icesoft.faces.webapp.http.Response;

import javax.faces.FactoryFinder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.render.ResponseStateManager;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;

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

        request.respondWith(new ResponseHandler() {
            public void respond(Response response) throws Exception {
                String[] views = request.getParameterAsStrings("viewNumber");
                StringWriter writer = new StringWriter();
                updateManager.serialize(views, writer);

                byte[] content = writer.getBuffer().toString().getBytes("UTF-8");
                response.setHeader("Content-Type", "text/xml;charset=UTF-8");
                response.setHeader("Content-Length", content.length);
                response.writeBodyFrom(new ByteArrayInputStream(content));
            }
        });
    }

    private void renderCycle(BridgeFacesContext context) {
        synchronized (context) {
            context.setCurrentInstance();
            DOMResponseWriter.applyBrowserDOMChanges(context);
            if (null != postBackKey) {
                context.getExternalContext().getRequestParameterMap()
                        .put(postBackKey, "not reload");
            }
            lifecycle.execute(context);
            lifecycle.render(context);
            context.release();
        }
    }

    private void renderCyclePartial(BridgeFacesContext context,
                            UIComponent component) {
        synchronized (context) {
            context.setCurrentInstance();
            DOMResponseWriter.applyBrowserDOMChanges(context);
            List alteredRequiredComponents =
                    setRequiredFalseInFormContaining(component);
            if (null != postBackKey) {
                context.getExternalContext().getRequestParameterMap()
                        .put(postBackKey, "not reload");
            }
            lifecycle.execute(context);
            lifecycle.render(context);
            context.release();
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
}
