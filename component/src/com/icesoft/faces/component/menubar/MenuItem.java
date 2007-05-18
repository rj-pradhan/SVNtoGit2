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

package com.icesoft.faces.component.menubar;

import com.icesoft.faces.component.ext.taglib.Util;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionListener;
import java.util.Iterator;
import java.util.List;


/**
 * MenuItem is a JSF component class that represent an ICEfaces menuItem.
 * <p>MenuItem components are the menu items contained by a menuBar. The action
 * and actionListener attributes operate in the same way as the standard
 * component attributes of the same name. The MenuItem component is only used in
 * the static approach to defining the heirarchy of menu items.
 * <p/>
 * This component extends the ICEfaces MenuItemBase component.
 * <p/>
 * By default the MenuItem is rendered by the "com.icesoft.faces.View" renderer
 * type.
 *
 * @author Chris Brown
 * @author gmccleary
 * @version 1.1
 */
public class MenuItem extends MenuItemBase {

    private String icon;
    private Object value;
    private String link;
    private String target;
    private MethodBinding action = null;
    private MethodBinding actionListener = null;
    private Boolean disabled = null;
    private String enabledOnUserRole = null;
    private String renderedOnUserRole = null;

    /**
     * String constant defining default menu icon img
     */
    public static final String DEFAULT_ICON =
            "./xmlhttp/css/xp/css-images/menu_blank_icon.gif";
    private static final String DEFAULT_VALUE = "menu item default";
    private static final String DEFAULT_LINK = "#";

    public MenuItem() {
    }

    /* (non-Javadoc)
     * @see javax.faces.component.UIComponent#getRendererType()
     */
    public String getRendererType() {
        return "com.icesoft.faces.View";
    }

    public String getComponentType() {
        return "com.icesoft.faces.MenuNode";
    }

    /**
     * <p>Return the value of the <code>COMPONENT_FAMILY</code> of this
     * component.</p>
     */
    public String getFamily() {
        return "com.icesoft.faces.MenuNode";
    }

    // convenience getters / setters

    /**
     * <p>Return the value of the <code>icon</code> property.</p>
     */
    public String getIcon() {
        if (icon != null) {
            return icon;
        }
        ValueBinding vb = getValueBinding("icon");
        if (vb != null) {
            return (String) vb.getValue(getFacesContext());
        }
        return DEFAULT_ICON;
    }

    /**
     * <p>Set the value of the <code>icon</code> property.</p>
     */
    public void setIcon(String iconValue) {
        icon = iconValue;
    }

    /**
     * <p>Return the value of the <code>value</code> property.</p>
     */
    public Object getValue() {
        if (value != null) {
            return value;
        }
        ValueBinding vb = getValueBinding("value");
        if (vb != null) {
            return (String) vb.getValue(getFacesContext());
        }
        return DEFAULT_VALUE;
    }

    /**
     * <p>Set the value of the <code>value</code> property.</p>
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * <p>Return the value of the <code>link</code> property.</p>
     */
    public String getLink() {
        if (link != null) {
            return link;
        }
        ValueBinding vb = getValueBinding("link");
        if (vb != null) {
            return (String) vb.getValue(getFacesContext());
        }
        return DEFAULT_LINK;
    }

    /**
     * <p>Set the value of the <code>link</code> property.</p>
     */
    public void setLink(String linkValue) {
        this.link = linkValue;
    }

    /**
     * <p>Return the value of the <code>target</code> property.</p>
     */
    public String getTarget() {
        if (target != null) {
            return target;
        }
        ValueBinding vb = getValueBinding("target");
        if (vb != null) {
            return (String) vb.getValue(getFacesContext());
        }
        return null;
    }

    /**
     * <p>Set the value of the <code>target</code> property.</p>
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * <p>Return the value of the <code>action</code> property.</p>
     */
    public MethodBinding getAction() {
        // Superclass getAction() is smart enough to handle JSF 1.1 and 1.2,
        //  getting Action MethodBinding or MethodExpression
        MethodBinding actionMB = super.getAction();
        if(actionMB != null)
            return actionMB;
        return action;
    }

    /**
     * <p>Set the value of the <code>action</code> property.</p>
     */
    public void setAction(MethodBinding action) {
        this.action = action;
    }

    /**
     * <p>Return the value of the <code>actionListener</code> property.</p>
     */
    public MethodBinding getActionListener() {
        return actionListener;
    }

    /**
     * <p>Set the value of the <code>actionListener</code> property.</p>
     */
    public void setActionListener(MethodBinding actionListener) {
        this.actionListener = actionListener;
    }
    
    public boolean hasActionOrActionListener() {
        if( getAction() != null )
            return true;
        if( getActionListener() != null )
            return true;
        ActionListener[] actionListeners = getActionListeners();
        if( actionListeners != null && actionListeners.length > 0 )
            return true;
        return false;
    }


    /**
     * <p>Set the value of the <code>disabled</code> property.</p>
     */
    public void setDisabled(boolean disabled) {
        this.disabled = new Boolean(disabled);
        ValueBinding vb = getValueBinding("disabled");
        if (vb != null) {
            vb.setValue(getFacesContext(), this.disabled);
            this.disabled = null;
        }
    }

    public boolean getDisabled() {
        return isDisabled();
    }

    /**
     * <p>Return the value of the <code>disabled</code> property.</p>
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

    void addParameter(UIComponent link) {
        List children = getChildren();

        for (int i = 0; i < children.size(); i++) {
            UIComponent nextChild = (UIComponent) children.get(i);
            if (nextChild instanceof UIParameter) {
                UIParameter param = new UIParameter();
                param.setName(((UIParameter) nextChild).getName());
                param.setValue(((UIParameter) nextChild).getValue());
                link.getChildren().add(param);
            }
        }
    }

    boolean isChildrenMenuItem() {
        Iterator children = getChildren().iterator();
        while (children.hasNext()) {
            UIComponent child = (UIComponent) children.next();
            if (child instanceof MenuItem) {
                return true;
            }
        }
        return false;
    }


    /**
     * <p>Set the value of the <code>enabledOnUserRole</code> property.</p>
     *
     * @param enabledOnUserRole
     */
    public void setEnabledOnUserRole(String enabledOnUserRole) {
        this.enabledOnUserRole = enabledOnUserRole;
    }

    /**
     * <p>Return the value of the <code>enabledOnUserRole</code> property.</p>
     *
     * @return String enabledOnUserRole
     */
    public String getEnabledOnUserRole() {
        if (enabledOnUserRole != null) {
            return enabledOnUserRole;
        }
        ValueBinding vb = getValueBinding("enabledOnUserRole");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>renderedOnUserRole</code> property.</p>
     *
     * @param renderedOnUserRole
     */
    public void setRenderedOnUserRole(String renderedOnUserRole) {
        this.renderedOnUserRole = renderedOnUserRole;
    }

    /**
     * <p>Return the value of the <code>renderedOnUserRole</code> property.</p>
     *
     * @return String renderedOnUserRole
     */
    public String getRenderedOnUserRole() {
        if (renderedOnUserRole != null) {
            return renderedOnUserRole;
        }
        ValueBinding vb = getValueBinding("renderedOnUserRole");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Return the value of the <code>rendered</code> property.</p>
     *
     * @return boolean rendered
     */
    public boolean isRendered() {
        if (!Util.isRenderedOnUserRole(this)) {
            return false;
        }
        return super.isRendered();
    }
   
}
