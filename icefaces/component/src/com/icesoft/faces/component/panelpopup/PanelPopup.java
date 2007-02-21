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

package com.icesoft.faces.component.panelpopup;

import com.icesoft.faces.component.CSS_DEFAULT;
import com.icesoft.faces.component.ext.HtmlPanelGroup;
import com.icesoft.faces.component.ext.taglib.Util;
import com.icesoft.faces.context.effects.JavascriptContext;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * PanelPopup is a JSF component class that represents an ICEfaces popup panel.
 * The "header" and "body" named facets represent the components responsible for
 * rendering the header and body of the popup panel.
 * <p/>
 * The component extends the ICEfaces extended HTMLPanelGroup.
 * <p/>
 * By default the component is rendered by the "com.icesoft.faces.PanelPopup"
 * renderer type.
 *
 * @author Greg McCleary
 * @version beta 1.0
 */
public class PanelPopup
        extends HtmlPanelGroup {
    /**
     * The component type.
     */
    public static final String COMPONENT_TYPE = "com.icesoft.faces.PanelPopup";
    /**
     * The default renderer type.
     */
    public static final String DEFAULT_RENDERER_TYPE =
            "com.icesoft.faces.PanelPopup";
    /**
     * The default resizable property is false. This unused attribute is here
     * for future use.
     */
    private static final boolean DEFAULT_RESIZABLE = false;
    /**
     * The default modal property is false.
     */
    private static final boolean DEFAULT_MODAL = false;
    /**
     * The header facet name.
     */
    private static final String HEADER_FACET = "header";
    /**
     * The body facet name.
     */
    private static final String BODY_FACET = "body";

    /**
     * The current style class.
     */
    private String styleClass = null;
    /**
     * The current style.
     */
    private String style = null;
    /**
     * The current resizable state.
     */
    private Boolean resizable = null;
    /**
     * The current modal state.
     */
    private Boolean modal = null;

    /**
     * Creates an instance and sets renderer type to "com.icesoft.faces.PanelPopup".
     */
    public PanelPopup() {
        setRendererType(DEFAULT_RENDERER_TYPE);
        JavascriptContext.includeLib(JavascriptContext.ICE_EXTRAS,
                                     FacesContext.getCurrentInstance());
    }

    // typesafe facet getters

    /**
     * @return the "header" facet.
     */
    public UIComponent getHeader() {
        return (UIComponent) getFacet(HEADER_FACET);
    }

    /**
     * @return the "body" facet.
     */
    public UIComponent getBody() {
        return (UIComponent) getFacet(BODY_FACET);
    }

    /* (non-Javadoc)
     * @see javax.faces.component.UIComponent#isRendered()
     */
    public boolean isRendered() {
        if (!Util.isRenderedOnUserRole(this)) {
            return false;
        }
        return super.isRendered();
    }


    /**
     * @return the headerClass style class name.
     */
    public String getHeaderClass() {

        return Util.appendNewStyleClass(
                CSS_DEFAULT.POPUP_BASE,
                getStyleClass(),
                CSS_DEFAULT.POPUP_DEFAULT_HEADER_CLASS);
    }


    /**
     * @return the bodyClass style class name.
     */
    public String getBodyClass() {
        return Util.appendNewStyleClass(
                CSS_DEFAULT.POPUP_BASE,
                getStyleClass(),
                CSS_DEFAULT.POPUP_DEFAULT_BODY_CLASS);
    }


    /* (non-Javadoc)
     * @see javax.faces.component.html.HtmlPanelGroup#setStyleClass(java.lang.String)
     */
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    /* (non-Javadoc)
    * @see javax.faces.component.html.HtmlPanelGroup#getStyleClass()
    */
    public String getStyleClass() {
        if (styleClass != null) {
            return styleClass;
        }
        ValueBinding vb = getValueBinding("styleClass");
        return vb != null ? (String) vb.getValue(getFacesContext()) :
               CSS_DEFAULT.POPUP_BASE;
    }

    /* (non-Javadoc)
     * @see javax.faces.component.html.HtmlPanelGroup#getStyle()
     */
    public String getStyle() {
        if (style != null) {
            return style;
        }
        ValueBinding vb = getValueBinding("style");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /* (non-Javadoc)
    * @see javax.faces.component.html.HtmlPanelGroup#setStyle(java.lang.String)
    */
    public void setStyle(String style) {
        this.style = style;
    }

    /**
     * Returns true if component is resizable. This method will always return
     * false as the resizable functionality has not been implemented in the 1.0
     * version.
     *
     * @return false
     */
    public boolean isResizable() {
        return false; // resizable functionality is not added yet
    }

    /**
     * Sets the resizable attribute of the component. Note: The resizable
     * function has not been implemented in the Beta relase.
     *
     * @param resizable a value of true will set the component to be resizable
     */
    public void setResizable(boolean resizable) {
        this.resizable = Boolean.valueOf(resizable);
    }

    /**
     * @return true if the component is modal.
     */
    public boolean isModal() {
        if (modal != null) {
            return modal.booleanValue();
        }
        ValueBinding vb = getValueBinding("modal");
        Boolean boolVal =
                vb != null ? (Boolean) vb.getValue(getFacesContext()) : null;
        return boolVal != null ? boolVal.booleanValue() : DEFAULT_MODAL;
    }

    /**
     * @param modal a value of true sets the component to be modal
     */
    public void setModal(boolean modal) {
        this.modal = Boolean.valueOf(modal);
    }

    /* (non-Javadoc)
     * @see javax.faces.component.StateHolder#saveState(javax.faces.context.FacesContext)
     */
    public Object saveState(FacesContext context) {
        Object values[] = new Object[10];
        values[0] = super.saveState(context);
        values[5] = styleClass;
        values[6] = style;
        values[7] = resizable;
        values[8] = modal;
        values[9] = title;
        return ((Object) (values));
    }

    /* (non-Javadoc)
     * @see javax.faces.component.StateHolder#restoreState(javax.faces.context.FacesContext, java.lang.Object)
     */
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        styleClass = (String) values[5];
        style = (String) values[6];
        resizable = (Boolean) values[7];
        modal = (Boolean) values[8];
        title = (String) values[9];
    }

    private String title = null;

    /**
     * <p>Set the value of the <code>title</code> property.</p>
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * <p>Return the value of the <code>title</code> property.</p>
     */
    public String getTitle() {
        if (title != null) {
            return title;
        }
        ValueBinding vb = getValueBinding("title");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }
}