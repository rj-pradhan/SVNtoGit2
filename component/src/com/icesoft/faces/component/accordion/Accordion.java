package com.icesoft.faces.component.accordion;

import com.icesoft.faces.context.effects.JavascriptContext;

import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesEvent;
import javax.faces.component.UIComponentBase;

public class Accordion extends UIComponentBase {
    public static final String COMPONENET_TYPE = "com.icesoft.faces.Accordion";
    public static final String DEFAULT_RENDERER_TYPE = "com.icesoft.faces.AccordionRenderer";
    public static final String COMPONENT_FAMILY = "com.icesoft.faces.AccordionFamily";

    private String label;
    private Boolean open;
    private MethodBinding actionListener;
    private String styleClass;

    public Accordion() {
        super();
        JavascriptContext.includeLib(JavascriptContext.ICE_EXTRAS, FacesContext.getCurrentInstance());
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getRendererType() {
        return DEFAULT_RENDERER_TYPE;
    }


    public String getStyleClass() {
        ValueBinding vb = getValueBinding("styleClass");
       if (vb != null) {
           return (String) vb.getValue(getFacesContext());
       }
       if (styleClass != null) {
           return styleClass;
       }
       return null;

    }

    public void setStyleClass(String styleClass) {
        ValueBinding vb = getValueBinding("styleClass");
        if (vb != null) {
            vb.setValue(getFacesContext(), styleClass);
        } else {
            this.styleClass = styleClass;
        }
    }

    public String getLabel() {
         ValueBinding vb = getValueBinding("label");
        if (vb != null) {
            return (String) vb.getValue(getFacesContext());
        }
        if (label != null) {
            return label;
        }
        return null;
    }

    public void setLabel(String label) {
        ValueBinding vb = getValueBinding("label");
        if (vb != null) {
            vb.setValue(getFacesContext(), label);
        } else {
            this.label = label;
        }
    }


    public Boolean getOpen() {
         ValueBinding vb = getValueBinding("open");
        if (vb != null) {
            return (Boolean) vb.getValue(getFacesContext());
        }
        if (open!= null) {
            return open;
        }
        return Boolean.FALSE;
    }

    public void setOpen(Boolean open) {
        ValueBinding vb = getValueBinding("open");
        if (vb != null) {
            vb.setValue(getFacesContext(), open);
        } else {
            this.open = open;
        }
    }

    public MethodBinding getActionListener() {
        return actionListener;
    }

    public void setActionListener(MethodBinding actionListener) {
        this.actionListener = actionListener;
    }

    public void broadcast(FacesEvent event) {
        super.broadcast(event);
        if (event instanceof ActionEvent && actionListener != null) {

            actionListener.invoke(getFacesContext(),
                                     new Object[]{(ActionEvent) event});

        }
    }

    public Object saveState(FacesContext context) {
        Object[] state = new Object[12];
        state[0] = super.saveState(context);
        state[1] = label;
        state[2] = open;
        state[3] = actionListener;
        state[4] = styleClass;
        return state;
    }

    public void restoreState(FacesContext context, Object stateIn) {
        Object[] state = (Object[]) stateIn;
        super.restoreState(context, state[0]);
        label = (String)state[1];
        open= (Boolean)state[2];
        actionListener = (MethodBinding)state[3];
        styleClass = (String)state[4];
    }
}