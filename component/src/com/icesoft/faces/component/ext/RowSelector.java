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

package com.icesoft.faces.component.ext;

import com.icesoft.faces.context.effects.JavascriptContext;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;
import javax.faces.event.FacesEvent;

/**
 * Created by IntelliJ IDEA. User: rmayhew Date: Aug 28, 2006 Time: 12:45:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class RowSelector extends UIComponentBase {
    private Boolean value;
    // private Listener
    private Boolean multiple;
    private String mouseOverClass;
    private String selectedClass;
    private MethodBinding selectionListener;

    public static final String COMPONENT_TYPE = "com.icesoft.faces.RowSelector";
    public static final String RENDERER_TYPE =
            "com.icesoft.faces.RowSelectorRenderer";
    public static final String COMPONENT_FAMILY =
            "com.icesoft.faces.RowSelectorFamily";

    public RowSelector(){
       JavascriptContext
               .includeLib(JavascriptContext.ICE_EXTRAS, getFacesContext());
    }

    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    public Boolean getValue() {
        ValueBinding vb = getValueBinding("value");
        if (vb != null) {
            return (Boolean) vb.getValue(getFacesContext());
        }
        if (value != null) {
            return value;
        }
        return Boolean.FALSE;
    }

    public void setValue(Boolean value) {
        ValueBinding vb = getValueBinding("value");
        if (vb != null) {
            vb.setValue(getFacesContext(), value);
        } else {
            this.value = value;
        }
    }

    public Boolean getMultiple() {
        ValueBinding vb = getValueBinding("multiple");
        if (vb != null) {
            return (Boolean) vb.getValue(getFacesContext());
        }
        if (multiple != null) {
            return multiple;
        }
        return Boolean.FALSE;
    }

    public void setMultiple(Boolean multiple) {
        this.multiple = multiple;
    }

    public String getMouseOverClass() {
        ValueBinding vb = getValueBinding("mouseOverClass");
        if (vb != null) {
            return (String) vb.getValue(getFacesContext());
        }
        return mouseOverClass;
    }

    public void setMouseOverClass(String mouseOverClass) {
        this.mouseOverClass = mouseOverClass;
    }

    public String getSelectedClass() {
        ValueBinding vb = getValueBinding("selectedClass");
        if (vb != null) {
            return (String) vb.getValue(getFacesContext());
        }
        return selectedClass;
    }

    public void setSelectedClass(String selectedClass) {
        this.selectedClass = selectedClass;
    }

    public MethodBinding getSelectionListener() {
        return selectionListener;
    }

    public void setSelectionListener(MethodBinding selectionListener) {
        this.selectionListener = selectionListener;
    }

    public void broadcast(FacesEvent event) {
        super.broadcast(event);
        if (event instanceof RowSelectorEvent && selectionListener != null) {

            selectionListener.invoke(getFacesContext(),
                                     new Object[]{(RowSelectorEvent) event});

        }
    }

    public Object saveState(FacesContext context) {
        Object[] state = new Object[12];
        state[0] = super.saveState(context);
        state[1] = value;
        state[2] = multiple;
        state[3] = mouseOverClass;
        state[4] = selectedClass;
        state[5] = selectionListener;
        return state;
    }

    public void restoreState(FacesContext context, Object stateIn) {
        Object[] state = (Object[]) stateIn;
        super.restoreState(context, state[0]);
        value = (Boolean) state[1];
        multiple = (Boolean) state[2];
        mouseOverClass = (String) state[3];
        selectedClass = (String) state[4];
        selectionListener = (MethodBinding) state[5];
    }
}
