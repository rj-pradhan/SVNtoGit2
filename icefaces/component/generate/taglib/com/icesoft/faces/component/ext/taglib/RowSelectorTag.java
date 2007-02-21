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

package com.icesoft.faces.component.ext.taglib;

import com.icesoft.faces.component.ext.RowSelector;
import com.icesoft.faces.component.ext.RowSelectorEvent;

import javax.faces.component.UIComponent;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;
import javax.faces.webapp.UIComponentTag;

/**
 * Created by IntelliJ IDEA. User: rmayhew Date: Aug 28, 2006 Time: 2:28:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class RowSelectorTag extends UIComponentTag {

    private String value;
    private String multiple;
    private String mouseOverClass;
    private String selectedClass;
    private String selectionListener;

    public void setValue(String value) {
        this.value = value;
    }

    public void setMultiple(String multiple) {
        this.multiple = multiple;
    }

    public void setMouseOverClass(String mouseOverClass) {
        this.mouseOverClass = mouseOverClass;
    }

    public void setSelectedClass(String selectedClass) {
        this.selectedClass = selectedClass;
    }

    public String getComponentType() {
        return RowSelector.COMPONENT_TYPE;
    }

    public String getRendererType() {
        return RowSelector.RENDERER_TYPE;
    }

    public void setSelectionListener(String selectionListener) {
        this.selectionListener = selectionListener;
    }

    protected void setProperties(UIComponent uiComponent) {
        try {
            super.setProperties(uiComponent);
            RowSelector series = (RowSelector) uiComponent;
            if (value != null) {
                if (isValueReference(value)) {
                    ValueBinding vb = Util.getValueBinding(value);
                    series.setValueBinding("value", vb);
                } else {
                    series.setValue(new Boolean(value));
                }
            }

            if (multiple != null) {
                if (isValueReference(multiple)) {
                    ValueBinding vb = Util.getValueBinding(multiple);
                    series.setValueBinding("multiple", vb);
                } else {

                    series.setMultiple(new Boolean(multiple));
                }
            }
            if (mouseOverClass != null) {
                if (isValueReference(mouseOverClass)) {
                    ValueBinding vb = Util.getValueBinding(mouseOverClass);
                    series.setValueBinding("mouseOverClass", vb);
                } else {

                    series.setMouseOverClass(mouseOverClass);
                }
            }

            if (selectedClass != null) {
                if (isValueReference(selectedClass)) {
                    ValueBinding vb = Util.getValueBinding(selectedClass);
                    series.setValueBinding("selectedClass", vb);
                } else {

                    series.setSelectedClass(selectedClass);
                }
            }

            if (selectionListener != null) {
                Class[] ca = {RowSelectorEvent.class};
                MethodBinding mb = getFacesContext().getApplication()
                        .createMethodBinding(selectionListener, ca);

                series.setSelectionListener(mb);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
