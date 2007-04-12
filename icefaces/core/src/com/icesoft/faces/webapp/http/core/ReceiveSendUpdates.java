package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.application.D2DViewHandler;
import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Server;

import javax.faces.FactoryFinder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIInput;
import javax.faces.el.ValueBinding;
import javax.faces.context.FacesContext;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ReceiveSendUpdates implements Server {
    private static final String REQUIRED = "required";
    private static final LifecycleFactory LifecycleFactory = (LifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
    private Lifecycle lifecycle = LifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);
    private Map commandQueues;
    private Collection synchronouslyUpdatedViews;

    public ReceiveSendUpdates(Map commandQueues, Collection synchronouslyUpdatedViews) {
        this.commandQueues = commandQueues;
        this.synchronouslyUpdatedViews = synchronouslyUpdatedViews;
    }

    public void service(final Request request) throws Exception {
        List viewIdentifiers = Arrays.asList(request.getParameterAsStrings("viewNumber"));
        synchronouslyUpdatedViews.addAll(viewIdentifiers);

        FacesContext context = FacesContext.getCurrentInstance();
        if (request.getParameterAsBoolean("partial", false)) {
            String componentID = request.getParameter("ice.event.captured");
            UIComponent component = D2DViewHandler.findComponent(componentID, context.getViewRoot());
            renderCyclePartial(context, component);
        } else {
            renderCycle(context);
        }

        request.respondWith(new SendUpdates.Handler(commandQueues, request));
    }

    public void shutdown() {
    }

    private void renderCycle(FacesContext context) {
        synchronized (context) {
            com.icesoft.util.SeamUtilities.removeSeamDebugPhaseListener(lifecycle);
            lifecycle.execute(context);
            lifecycle.render(context);
        }
    }

    private void renderCyclePartial(FacesContext context,
                                    UIComponent component) {
        synchronized (context) {
            Map alteredRequiredComponents =
                    setRequiredFalseInFormContaining(component);
            com.icesoft.util.SeamUtilities.removeSeamDebugPhaseListener(lifecycle);
            lifecycle.execute(context);
            lifecycle.render(context);
            setRequiredTrue(alteredRequiredComponents);
        }
    }

    private void setRequiredTrue(Map requiredComponents) {
        Iterator i = requiredComponents.keySet().iterator();
        UIInput next = null;
        while (i.hasNext()) {
            next = (UIInput) i.next();
            ValueBinding valueBinding = (ValueBinding) 
                    requiredComponents.get(next);
            if (null != valueBinding) {
                next.setValueBinding(REQUIRED, valueBinding);
            } else {
                next.setRequired(true);
            }
        }
    }

    private Map setRequiredFalseInFormContaining(UIComponent component) {
        Map alteredComponents = new HashMap();
        UIComponent form = getContainingForm(component);
        setRequiredFalseOnAllChildrenExceptOne(form, component,
                alteredComponents);
        return alteredComponents;
    }

    private void setRequiredFalseOnAllChildrenExceptOne(UIComponent parent,
                                                        UIComponent componentToAvoid,
                                                        Map alteredComponents) {
        ValueBinding FALSE_BINDING = FacesContext.getCurrentInstance()
                .getApplication().createValueBinding("#{false}");
        int length = parent.getChildCount();
        UIComponent next = null;
        for (int i = 0; i < length; i++) {
            next = (UIComponent) parent.getChildren().get(i);
            if (next instanceof UIInput && next != componentToAvoid) {
                UIInput input = (UIInput) next;
                if (input.isRequired()) {
                    ValueBinding valueBinding = 
                            input.getValueBinding(REQUIRED);
                    if (null != valueBinding) {
                        input.setValueBinding(REQUIRED, FALSE_BINDING);
                    } else {
                        input.setRequired(false);
                    }
                    alteredComponents.put(input, valueBinding);
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
