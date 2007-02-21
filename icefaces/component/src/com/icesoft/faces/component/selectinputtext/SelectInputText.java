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

package com.icesoft.faces.component.selectinputtext;

import com.icesoft.faces.component.CSS_DEFAULT;
import com.icesoft.faces.component.ext.HtmlInputText;
import com.icesoft.faces.component.ext.KeyEvent;
import com.icesoft.faces.component.ext.renderkit.FormRenderer;
import com.icesoft.faces.component.ext.taglib.Util;
import com.icesoft.faces.context.effects.JavascriptContext;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * SelectInputText is a JSF component class that represents an ICEfaces
 * autocomplete input text. This component requires the application developer to
 * implement the matching list rearch algorithm in their backing bean.
 * <p/>
 * SelectInputText extends the ICEfaces extended HtmlInputText component.
 * <p/>
 * By default this component is rendered by the "com.icesoft.faces.SelectInputText"
 * renderer type.
 */
public class SelectInputText extends HtmlInputText implements NamingContainer {
    public static final String COMPONENT_TYPE =
            "com.icesoft.faces.SelectInputText";

    //default style classes

    //private properties for style classes
    private String styleClass;


    /**
     * A request-scope attribute under which the model data for the row selected
     * by the current value of the "rowIndex" property will be exposed.
     */
    private String listVar = null;

    /**
     * A state variable for number of rows to be matched.
     */
    private Integer rows;
    private final int DEFAULT_MAX_MATCHS = 10;

    /**
     * A state variable for width of both inputtext and dropdownlist
     */
    private String width;
    private final String DEFAULT_WIDTH = "150";

    /**
     * A property to store the selectedItem, after successfull match
     */
    private SelectItem selectedItem = null;

    /**
     * list of selectItems
     */
    List itemList;

    /**
     * Map for selectItems, where the key is the "SelectItem.getlabel()" and the
     * value is the selectItem object
     */
    Map itemMap = new HashMap();

    private int index = -1;

    public SelectInputText() {
        super();
        JavascriptContext.includeLib(JavascriptContext.ICE_EXTRAS,
                                     FacesContext.getCurrentInstance());
    }

    /*
    *  (non-Javadoc)
    * @see javax.faces.component.UIComponent#encodeBegin(javax.faces.context.FacesContext)
    */
    public void encodeBegin(FacesContext context) throws IOException {
        super.encodeBegin(context);
    }

    /*
    *  (non-Javadoc)
    * @see javax.faces.component.UIComponent#decode(javax.faces.context.FacesContext)
    */
    public void decode(FacesContext facesContext) {
        super.decode(facesContext);
        setSelectedItem(facesContext);
        if (hadFocus(facesContext)) {
            queueEventIfEnterKeyPressed(facesContext);
        }
    }

    /**
     * this method is responsible to setting the seletecdItem on the component
     *
     * @param facesContext
     */
    private void setSelectedItem(FacesContext facesContext) {
        Map requestMap =
                facesContext.getExternalContext().getRequestParameterMap();
        String clientId = getClientId(facesContext);
        String value = (String) requestMap.get(clientId);
        setSelectedItem(value);
    }


    /**
     * return true if I had focus when submitted
     *
     * @param facesContext
     * @return focus
     */
    private boolean hadFocus(FacesContext facesContext) {
        Object focusId = facesContext.getExternalContext()
                .getRequestParameterMap().get(FormRenderer.getFocusElementId());
        boolean focus = false;
        if (focusId != null) {
            if (focusId.toString().equals(getClientId(facesContext))) {
                focus = true;
            }
        }
        setFocus(focus);
        return focus;
    }

    //this list would be set in populateItemList()

    /**
     * <p>Return the value of the <code>itemList</code> property.</p>
     */
    public Iterator getItemList() {
        if (itemList == null) {
            return Collections.EMPTY_LIST.iterator();
        }
        return itemList.iterator();
    }

    /**
     * <p>Set the value of the <code>index</code> property.</p>
     */
    public void setIndex(int index) {
        this.index = index;

    }

    /**
     * <p>Return the value of the <code>clientId</code> property.</p>
     */
    public String getClientId(FacesContext context) {
        if (context == null) {
            throw new NullPointerException();
        }
        String baseClientId = super.getClientId(context);
        if (index >= 0) {
            return (baseClientId + NamingContainer.SEPARATOR_CHAR + index++);
        } else {
            return (baseClientId);
        }
    }

    /**
     * reset parent's and its children's ids
     */
    void resetId(UIComponent component) {
        String id = component.getId();
        component.setId(id); // Forces client id to be reset
        Iterator kids = component.getChildren().iterator();
        while (kids.hasNext()) {
            UIComponent kid = (UIComponent) kids.next();
            resetId(kid);
        }

    }

    //this method generating the list of selectItems "itemList",  which can be bounded 
    //with the bean, or could be static on jsf page 
    //matches list can be change after value change event, so we are calling
    //this method after value change event in broadcast(), where the method bounded 
    //with valueChangeListner is being called and updates the data model.
    void populateItemList() {
        if (getSelectFacet() != null) {
            //facet being used on jsf page, so get the list from the value binding
            itemList = getListValue();
        } else {
            //selectItem or selectItems has been used on jsf page, so get the selectItems
            itemList = Util.getSelectItems(FacesContext.getCurrentInstance(),
                                           this);
        }
        Iterator items = itemList.iterator();
        SelectItem item = null;
        itemMap.clear();
        while (items.hasNext()) {
            item = (SelectItem) items.next();
            itemMap.put(item.getLabel(), item);
        }
    }

    /* (non-Javadoc)
    * @see javax.faces.component.UIComponent#broadcast(javax.faces.event.FacesEvent)
    */
    public void broadcast(FacesEvent event) throws AbortProcessingException {
        //keyevent should be process by this component
        super.broadcast(event);
        if ((event instanceof ValueChangeEvent)) {
            populateItemList();
            setChangedComponentId(
                    this.getClientId(FacesContext.getCurrentInstance()));
        }
    }

    /**
     * <p>Return the value of the <code>selectInputText</code> property.</p>
     */
    public UIComponent getSelectFacet() {
        return (UIComponent) getFacet("selectInputText");
    }

    /**
     * <p>Set the value of the <code>selectedItem</code> property.</p>
     */
    public void setSelectedItem(String key) {

        this.selectedItem = (SelectItem) itemMap.get(key);
    }

    /**
     * <p>Return the value of the <code>selectedItem</code> property.</p>
     */
    public SelectItem getSelectedItem() {
        return selectedItem;
    }

    //property to set the max matches to be displayed

    /**
     * <p>Set the value of the <code>rows</code> property.</p>
     */
    public void setRows(int rows) {
        this.rows = new Integer(rows);
    }

    /**
     * <p>Return the value of the <code>rows</code> property.</p>
     */
    public int getRows() {
        if (rows != null) {
            if (itemMap != null) {
                return itemMap.size() > 0 && itemMap.size() < rows.intValue() ?
                       itemMap.size() : rows.intValue();
            } else {
                return rows.intValue();
            }
        }

        ValueBinding vb = getValueBinding("rows");
        return vb != null ?
               Integer.parseInt(vb.getValue(getFacesContext()).toString()) :
               DEFAULT_MAX_MATCHS;
    }

    /**
     * <p>Set the value of the <code>width</code> property.</p>
     */
    public void setWidth(String width) {
        this.width = width;
    }

    /**
     * <p>Set the value of the <code>listVar</code> property.</p>
     */
    public void setListVar(String listVar) {
        this.listVar = listVar;
    }

    /**
     * <p>Return the value of the <code>listVar</code> property.</p>
     */
    public String getListVar() {
        if (listVar != null) {
            return listVar;
        }
        ValueBinding vb = getValueBinding("listVar");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>listValue</code> property.</p>
     */
    public void setListValue(List listValue) {
        this.itemList = listValue;
    }

    /**
     * <p>Return the value of the <code>listValue</code> property.</p>
     */
    public List getListValue() {
        ValueBinding vb = getValueBinding("listValue");
        return (List) vb.getValue(FacesContext.getCurrentInstance());
    }

    /**
     * <p>Return the value of the <code>width</code> property.</p>
     */
    public String getWidth() {
        if (width != null) {
            return width;
        }
        ValueBinding vb = getValueBinding("width");
        return vb != null ? vb.getValue(getFacesContext()).toString() :
               DEFAULT_WIDTH;
    }

    String getWidthAsStyle() {
        try {//no measurement unit defined, so add the px unit
            int width = Integer.parseInt(getWidth());
            return "width:" + width + "px;";
        } catch (NumberFormatException e) {
            return "width:" + getWidth().trim();
        }
    }

    /**
     * <p>Set the value of the <code>styleClass</code> property.</p>
     */
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    /**
     * <p>Return the value of the <code>styleClass</code> property.</p>
     */
    public String getStyleClass() {
        if (styleClass != null) {
            return styleClass;
        }
        ValueBinding vb = getValueBinding("styleClass");
        return vb != null ? (String) vb.getValue(getFacesContext()) :
               CSS_DEFAULT.DEFAULT_SELECT_INPUT;
    }

    /**
     * <p>Return the value of the <code>inputTextClass</code> property.</p>
     */
    public String getInputTextClass() {
        String base = getStyleClass();
        String className = CSS_DEFAULT.DEFAULT_SELECT_INPUT_TEXT_CLASS;
        if (isDisabled()) {
            className += "-dis";
        }
        return Util.appendNewStyleClass(CSS_DEFAULT.DEFAULT_SELECT_INPUT, base,
                                        className);
    }


    /**
     * <p>Return the value of the <code>listClass</code> property.</p>
     */
    public String getListClass() {
        String base = getStyleClass();
        String className = CSS_DEFAULT.DEFAULT_SELECT_INPUT_LIST_CLASS;
        if (isDisabled()) {
            className += "-dis";
        }
        return Util.appendNewStyleClass(CSS_DEFAULT.DEFAULT_SELECT_INPUT, base,
                                        className);
    }


    /**
     * <p>Return the value of the <code>rowClass</code> property.</p>
     */
    public String getRowClass() {
        String base = getStyleClass();
        String className = CSS_DEFAULT.DEFAULT_SELECT_INPUT_ROW_CLASS;
        if (isDisabled()) {
            className += "-dis";
        }
        return Util.appendNewStyleClass(CSS_DEFAULT.DEFAULT_SELECT_INPUT, base,
                                        className);
    }


    /**
     * <p>Return the value of the <code>selectedRowClass</code> property.</p>
     */
    public String getSelectedRowClass() {
        String base = getStyleClass();
        String className = CSS_DEFAULT.DEFAULT_SELECT_INPUT_SELECTED_ROW_CLASS;
        if (isDisabled()) {
            className += "-dis";
        }
        return Util.appendNewStyleClass(CSS_DEFAULT.DEFAULT_SELECT_INPUT, base,
                                        className);
    }

    //the following code is a fix for iraptor bug 347
    //on first page submit, all input elements gets valueChangeEvent (null to ""), 
    //so component's ids can be more then one
    private List changedComponentIds = new ArrayList();

    /**
     * <p>Set the value of the <code>selectedPanel</code> property.</p>
     */
    void setChangedComponentId(Object id) {
        if (id == null) {
            changedComponentIds.clear();
        } else {
            changedComponentIds.add(id);
        }
    }

    /**
     * <p>Return the value of the <code>selectedPanel</code> property.</p>
     */
    boolean hasChanged() {
        return changedComponentIds
                .contains(this.getClientId(FacesContext.getCurrentInstance()));
    }

    /**
     * queue the event if the enter key was pressed
     *
     * @param facesContext
     */
    private void queueEventIfEnterKeyPressed(FacesContext facesContext) {
        try {
            KeyEvent keyEvent =
                    new KeyEvent(this, facesContext.getExternalContext()
                .getRequestParameterMap());
            if (keyEvent.getKeyCode() == KeyEvent.CARRIAGE_RETURN) {
                queueEvent(new ActionEvent(this));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>Gets the state of the instance as a <code>Serializable</code>
     * Object.</p>
     */
    public Object saveState(FacesContext context) {
        Object values[] = new Object[7];
        values[0] = super.saveState(context);
        values[1] = styleClass;
        values[2] = listVar;
        values[3] = rows;
        values[4] = width;
        values[5] = selectedItem;
        values[6] = itemList;
        return ((Object) (values));
    }

    /**
     * <p>Perform any processing required to restore the state from the entries
     * in the state Object.</p>
     */
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        styleClass = (String) values[1];
        listVar = (String) values[2];
        rows = (Integer) values[3];
        width = (String) values[4];
        selectedItem = (SelectItem) values[5];
        itemList = (List) values[6];
    }
}
