package com.icesoft.faces.component.panelcollapsible;

import com.icesoft.faces.context.effects.JavascriptContext;
import com.icesoft.faces.component.CSS_DEFAULT;
import com.icesoft.faces.component.ext.taglib.Util;

import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.ActionListener;
import javax.faces.component.UIComponentBase;
import javax.faces.component.ActionSource;

public class PanelCollapsible extends UIComponentBase implements ActionSource {
    public static final String COMPONENET_TYPE = "com.icesoft.faces.PanelCollapsible";
    public static final String DEFAULT_RENDERER_TYPE = "com.icesoft.faces.PanelCollapsibleRenderer";
    public static final String COMPONENT_FAMILY = "javax.faces.Command";
    private static final boolean DEFAULT_IMMEDIATE = false;

    private String label;
    private Boolean expanded;
    private MethodBinding actionListener;
    private String style;
    private String styleClass;
    private Boolean toggleOnClick;
    private Boolean disabled;
    private Boolean immediate;
    
     /**
     * The current enabledOnUserRole state.
     */
    private String enabledOnUserRole = null;
    /**
     * The current renderedOnUserRole state.
     */
    private String renderedOnUserRole = null;


    public PanelCollapsible() {
        super();
        JavascriptContext.includeLib(JavascriptContext.ICE_EXTRAS, FacesContext.getCurrentInstance());

    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public String getRendererType() {
        return DEFAULT_RENDERER_TYPE;
    }


    public String getStyle() {
        if (style != null) {
            return style;
        }
        ValueBinding vb = getValueBinding("style");
        if (vb != null) {
            return (String) vb.getValue(getFacesContext());
        }
        return null;
    }
    
    public void setStyle(String style) {
        this.style = style;
    }

    public String getStyleClass() {
        return Util.getQualifiedStyleClass(this, 
                getCollapsedStyle(styleClass),
                getCollapsedStyle(CSS_DEFAULT.PANEL_COLLAPSIBLE_DEFAULT_STYLE_CLASS),
                "styleClass",
                isDisabled());
    }

    public String getHeaderClass() {
        return Util.getQualifiedStyleClass(this, 
                CSS_DEFAULT.PANEL_COLLAPSIBLE_HEADER, 
                isDisabled());
    }
    
    public String getContentClass() {
        return Util.getQualifiedStyleClass(this, 
                CSS_DEFAULT.PANEL_COLLAPSIBLE_CONTENT, 
                isDisabled());        
    }
    
    private String getCollapsedStyle(String style) {
        if (!getExpanded().booleanValue() && style != null) {
            style += CSS_DEFAULT.PANEL_COLLAPSIBLE_STATE_COLLAPSED;
        }
        return style;
    }
    
    String getStyleClassForJs() {
        String[] styleClassArray = getStyleClass()
            .replaceAll(CSS_DEFAULT.PANEL_COLLAPSIBLE_STATE_COLLAPSED, "")
            .split(" ");
        String styleClass ="";
        if (styleClassArray.length >= 1) {
            styleClass = "'"+ styleClassArray[0] + "'";
        } 
        
        if (styleClassArray.length == 2) {
            styleClass += ", '"+ styleClassArray[1]+ "'";
        }
        return styleClass;
    }
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
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


    public Boolean getExpanded() {
        if (expanded!= null) {
            return expanded;
        }
        ValueBinding vb = getValueBinding("expanded");
        if (vb != null) {
            return (Boolean) vb.getValue(getFacesContext());
        }
        return Boolean.FALSE;
    }

    public void setExpanded(Boolean expanded) {
        this.expanded = expanded;
    }

    public Boolean getToggleOnClick() {
         ValueBinding vb = getValueBinding("toggleOnClick");
        if (vb != null) {
            return (Boolean) vb.getValue(getFacesContext());
        }
        if (toggleOnClick!= null) {
            return toggleOnClick;
        }
        return Boolean.TRUE;
    }

    public void setToggleOnClick(Boolean toggleOnClick) {
        ValueBinding vb = getValueBinding("toggleOnClick");
        if (vb != null) {
            vb.setValue(getFacesContext(), toggleOnClick);
        } else {
            this.toggleOnClick= toggleOnClick;
        }
    }

    public MethodBinding getAction() {
        return null;
    }

    public void setAction(MethodBinding methodBinding) {
        throw new UnsupportedOperationException(
                "Defining an action is not supported. Use an actionListener");
    }

    public MethodBinding getActionListener() {
        return actionListener;
    }

    public void setActionListener(MethodBinding actionListener) {
        this.actionListener = actionListener;
    }

    public boolean isImmediate() {
        if (immediate != null) {
            return immediate.booleanValue();
        }
        ValueBinding vb = getValueBinding("immediate");
        Boolean v =
                vb != null ? (Boolean) vb.getValue(getFacesContext()) : null;
        return v != null ? v.booleanValue() : DEFAULT_IMMEDIATE;
    }
    
    public void setImmediate(boolean immediate) {
        this.immediate = Boolean.valueOf(immediate);
    }
    
    public void addActionListener(ActionListener actionListener) {
        addFacesListener(actionListener);
    }

    public ActionListener[] getActionListeners() {
        return (ActionListener[]) getFacesListeners(ActionListener.class);
    }

    public void removeActionListener(ActionListener actionListener) {
        removeFacesListener(actionListener);
    }

    public void broadcast(FacesEvent event) {
        super.broadcast(event);
        if (event instanceof ActionEvent) {
            ActionEvent actionEvent = (ActionEvent) event;
            if(actionListener != null) {
                actionListener.invoke(
                    getFacesContext(), new Object[]{actionEvent});
            }
            // super.broadcast(event) does this itself
            //ActionListener[] actionListeners = getActionListeners();
            //if(actionListeners != null) {
            //    for(int i = 0; i < actionListeners.length; i++) {
            //        actionListeners[i].processAction(actionEvent);
            //    }
            //}
        }
    }

      /**
     * @param disabled
     */
    public void setDisabled(boolean disabled) {
        this.disabled = new Boolean(disabled);
        ValueBinding vb = getValueBinding("disabled");
        if (vb != null) {
            vb.setValue(getFacesContext(), this.disabled);
            this.disabled = null;
        }
    }

    /**
     * @return the value of disabled
     */
    public boolean isDisabled() {
        if (!Util.isEnabledOnUserRole(this)) {
            return true;
        }

        if (disabled != null) {
            return disabled.booleanValue();
        }
        ValueBinding vb = getValueBinding("disabled");
        Boolean v =
                vb != null ? (Boolean) vb.getValue(getFacesContext()) : null;
        return v != null ? v.booleanValue() : false;
    }

      /**
     * @param enabledOnUserRole
     */
    public void setEnabledOnUserRole(String enabledOnUserRole) {
        this.enabledOnUserRole = enabledOnUserRole;
    }

    /**
     * @return the value of enabledOnUserRole
     */
    public String getEnabledOnUserRole() {
        if (enabledOnUserRole != null) {
            return enabledOnUserRole;
        }
        ValueBinding vb = getValueBinding("enabledOnUserRole");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * @param renderedOnUserRole
     */
    public void setRenderedOnUserRole(String renderedOnUserRole) {
        this.renderedOnUserRole = renderedOnUserRole;
    }

    /**
     * @return the value of renderedOnUserRole
     */
    public String getRenderedOnUserRole() {
        if (renderedOnUserRole != null) {
            return renderedOnUserRole;
        }
        ValueBinding vb = getValueBinding("renderedOnUserRole");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }



    public Object saveState(FacesContext context) {
        Object[] state = new Object[10];
        state[0] = super.saveState(context);
        state[1] = label;
        state[2] = expanded;
        state[3] = saveAttachedState(context, actionListener);
        state[4] = style;
        state[5] = styleClass;
        state[6] = disabled;
        state[7] = enabledOnUserRole;
        state[8] = renderedOnUserRole;
        state[9] = immediate;
        return state;
    }

    public void restoreState(FacesContext context, Object stateIn) {
        Object[] state = (Object[]) stateIn;
        super.restoreState(context, state[0]);
        label = (String)state[1];
        expanded= (Boolean)state[2];
        actionListener = (MethodBinding) restoreAttachedState(context, state[3]);
        style = (String)state[4];
        styleClass = (String)state[5];
        disabled = (Boolean)state[6];
        enabledOnUserRole = (String)state[7];
        renderedOnUserRole = (String)state[8];
        immediate = (Boolean)state[9];
    }


    


    public Object processSaveState(FacesContext facesContext) {
        return super.processSaveState(facesContext);
    }

    public void processRestoreState(FacesContext facesContext, Object object) {
        super.processRestoreState(facesContext, object);
    }
}