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

package com.icesoft.faces.component.datapaginator;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.webapp.UIComponentTag;
import javax.servlet.jsp.JspException;

/**
 * <p>Auto-generated component tag class. Do <strong>NOT</strong> modify; all
 * changes <strong>will</strong> be lost!</p>
 */

public class DataPaginatorTag extends UIComponentTag {

    /**
     * <p>Return the requested component type.</p>
     */
    public String getComponentType() {
        return "com.icesoft.faces.DataScroller";
    }

    /**
     * <p>Return the requested renderer type.</p>
     */
    public String getRendererType() {
        return "com.icesoft.faces.DataScroller";
    }

    /**
     * <p>Release any allocated tag handler attributes.</p>
     */
    public void release() {
        super.release();
        actionListener = null;
        renderFacetsIfSinglePage = null;
        disabled = null;
        displayedRowsCountVar = null;
        enabledOnUserRole = null;
        fastStep = null;
        firstRowIndexVar = null;
        _for = null;
        immediate = null;
        lastRowIndexVar = null;
        pageCountVar = null;
        pageIndexVar = null;
        paginator = null;
        paginatorMaxPages = null;
        renderedOnUserRole = null;
        rowsCountVar = null;
        style = null;
        styleClass = null;
        vertical = null;
    }

    /**
     * <p>Transfer tag attributes to component properties.</p>
     */
    protected void setProperties(UIComponent _component) {
        super.setProperties(_component);
        if (disabled != null) {
            if (isValueReference(disabled)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(disabled);
                _component.setValueBinding("disabled", _vb);
            } else {
                _component.getAttributes()
                        .put("disabled", Boolean.valueOf(disabled));
            }
        }
        if (displayedRowsCountVar != null) {
            if (isValueReference(displayedRowsCountVar)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(displayedRowsCountVar);
                _component.setValueBinding("displayedRowsCountVar", _vb);
            } else {
                _component.getAttributes()
                        .put("displayedRowsCountVar", displayedRowsCountVar);
            }
        }
        if (enabledOnUserRole != null) {
            if (isValueReference(enabledOnUserRole)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(enabledOnUserRole);
                _component.setValueBinding("enabledOnUserRole", _vb);
            } else {
                _component.getAttributes()
                        .put("enabledOnUserRole", enabledOnUserRole);
            }
        }
        if (fastStep != null) {
            if (isValueReference(fastStep)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(fastStep);
                _component.setValueBinding("fastStep", _vb);
            } else {
                _component.getAttributes()
                        .put("fastStep", Integer.valueOf(fastStep));
            }
        }
        if (firstRowIndexVar != null) {
            if (isValueReference(firstRowIndexVar)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(firstRowIndexVar);
                _component.setValueBinding("firstRowIndexVar", _vb);
            } else {
                _component.getAttributes()
                        .put("firstRowIndexVar", firstRowIndexVar);
            }
        }
        if (_for != null) {
            if (isValueReference(_for)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(_for);
                _component.setValueBinding("for", _vb);
            } else {
                _component.getAttributes().put("for", _for);
            }
        }
        if (immediate != null) {
            if (isValueReference(immediate)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(immediate);
                _component.setValueBinding("immediate", _vb);
            } else {
                _component.getAttributes()
                        .put("immediate", Boolean.valueOf(immediate));
            }
        }
        if (lastRowIndexVar != null) {
            if (isValueReference(lastRowIndexVar)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(lastRowIndexVar);
                _component.setValueBinding("lastRowIndexVar", _vb);
            } else {
                _component.getAttributes()
                        .put("lastRowIndexVar", lastRowIndexVar);
            }
        }
        if (pageCountVar != null) {
            if (isValueReference(pageCountVar)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(pageCountVar);
                _component.setValueBinding("pageCountVar", _vb);
            } else {
                _component.getAttributes().put("pageCountVar", pageCountVar);
            }
        }
        if (pageIndexVar != null) {
            if (isValueReference(pageIndexVar)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(pageIndexVar);
                _component.setValueBinding("pageIndexVar", _vb);
            } else {
                _component.getAttributes().put("pageIndexVar", pageIndexVar);
            }
        }
        if (paginator != null) {
            if (isValueReference(paginator)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(paginator);
                _component.setValueBinding("paginator", _vb);
            } else {
                _component.getAttributes()
                        .put("paginator", Boolean.valueOf(paginator));
            }
        }
        if (paginatorMaxPages != null) {
            if (isValueReference(paginatorMaxPages)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(paginatorMaxPages);
                _component.setValueBinding("paginatorMaxPages", _vb);
            } else {
                _component.getAttributes().put("paginatorMaxPages",
                                               Integer.valueOf(
                                                       paginatorMaxPages));
            }
        }
        if (renderedOnUserRole != null) {
            if (isValueReference(renderedOnUserRole)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(renderedOnUserRole);
                _component.setValueBinding("renderedOnUserRole", _vb);
            } else {
                _component.getAttributes()
                        .put("renderedOnUserRole", renderedOnUserRole);
            }
        }
        if (rowsCountVar != null) {
            if (isValueReference(rowsCountVar)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(rowsCountVar);
                _component.setValueBinding("rowsCountVar", _vb);
            } else {
                _component.getAttributes().put("rowsCountVar", rowsCountVar);
            }
        }
        if (style != null) {
            if (isValueReference(style)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(style);
                _component.setValueBinding("style", _vb);
            } else {
                _component.getAttributes().put("style", style);
            }
        }
        if (styleClass != null) {
            if (isValueReference(styleClass)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(styleClass);
                _component.setValueBinding("styleClass", _vb);
            } else {
                _component.getAttributes().put("styleClass", styleClass);
            }
        }
        if (vertical != null) {
            if (isValueReference(vertical)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(vertical);
                _component.setValueBinding("vertical", _vb);
            } else {
                _component.getAttributes()
                        .put("vertical", Boolean.valueOf(vertical));
            }
        }
        if (actionListener != null) {
            if (isValueReference(actionListener)) {
                MethodBinding _mb = getFacesContext().getApplication()
                        .createMethodBinding(actionListener,
                                             actionListenerArgs);
                _component.getAttributes().put("actionListener", _mb);
            } else {
                throw new IllegalArgumentException(actionListener);
            }
        }
        if (renderFacetsIfSinglePage != null) {
            if (isValueReference(renderFacetsIfSinglePage)) {
                ValueBinding _vb = getFacesContext().getApplication()
                        .createValueBinding(displayedRowsCountVar);
                _component.setValueBinding("renderFacetsIfSinglePage", _vb);
            } else {
                _component.getAttributes().put("renderFacetsIfSinglePage",
                                               Boolean.valueOf(
                                                       renderFacetsIfSinglePage));
            }
        }
    }

    // disabled
    private String disabled = null;

    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }

    // displayedRowsCountVar
    private String displayedRowsCountVar = null;

    public void setDisplayedRowsCountVar(String displayedRowsCountVar) {
        this.displayedRowsCountVar = displayedRowsCountVar;
    }

    // enabledOnUserRole
    private String enabledOnUserRole = null;

    public void setEnabledOnUserRole(String enabledOnUserRole) {
        this.enabledOnUserRole = enabledOnUserRole;
    }

    // fastStep
    private String fastStep = null;

    public void setFastStep(String fastStep) {
        this.fastStep = fastStep;
    }

    // firstRowIndexVar
    private String firstRowIndexVar = null;

    public void setFirstRowIndexVar(String firstRowIndexVar) {
        this.firstRowIndexVar = firstRowIndexVar;
    }

    // for
    private String _for = null;

    public void setFor(String _for) {
        this._for = _for;
    }

    // immediate
    private String immediate = null;

    public void setImmediate(String immediate) {
        this.immediate = immediate;
    }

    // lastRowIndexVar
    private String lastRowIndexVar = null;

    public void setLastRowIndexVar(String lastRowIndexVar) {
        this.lastRowIndexVar = lastRowIndexVar;
    }

    // pageCountVar
    private String pageCountVar = null;

    public void setPageCountVar(String pageCountVar) {
        this.pageCountVar = pageCountVar;
    }

    // pageIndexVar
    private String pageIndexVar = null;

    public void setPageIndexVar(String pageIndexVar) {
        this.pageIndexVar = pageIndexVar;
    }

    // paginator
    private String paginator = null;

    public void setPaginator(String paginator) {
        this.paginator = paginator;
    }

    // paginatorMaxPages
    private String paginatorMaxPages = null;

    public void setPaginatorMaxPages(String paginatorMaxPages) {
        this.paginatorMaxPages = paginatorMaxPages;
    }

    // renderedOnUserRole
    private String renderedOnUserRole = null;

    public void setRenderedOnUserRole(String renderedOnUserRole) {
        this.renderedOnUserRole = renderedOnUserRole;
    }

    // rowsCountVar
    private String rowsCountVar = null;

    public void setRowsCountVar(String rowsCountVar) {
        this.rowsCountVar = rowsCountVar;
    }

    // style
    private String style = null;

    public void setStyle(String style) {
        this.style = style;
    }

    // styleClass
    private String styleClass = null;

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    // vertical
    private String vertical = null;

    public void setVertical(String vertical) {
        this.vertical = vertical;
    }

    // actionListener
    private String actionListener = null;

    public void setActionListener(String actionListener) {
        this.actionListener = actionListener;
    }

    // actionListener
    private String renderFacetsIfSinglePage = null;

    public void setRenderFacetsIfSinglePage(String renderFacetsIfSinglePage) {
        this.renderFacetsIfSinglePage = renderFacetsIfSinglePage;
    }


    private static Class actionArgs[] = new Class[0];
    private static Class actionListenerArgs[] = {ActionEvent.class};
    private static Class validatorArgs[] =
            {FacesContext.class, UIComponent.class, Object.class};
    private static Class valueChangeListenerArgs[] = {ValueChangeEvent.class};

    //
    // Methods From TagSupport
    // 

    public int doStartTag() throws JspException {
        int rc = 0;
        try {
            rc = super.doStartTag();
        } catch (JspException e) {
            throw e;
        } catch (Throwable t) {
            throw new JspException(t);
        }
        return rc;
    }


    public int doEndTag() throws JspException {
        int rc = 0;
        try {
            rc = super.doEndTag();
        } catch (JspException e) {
            throw e;
        } catch (Throwable t) {
            throw new JspException(t);
        }
        return rc;
    }

}
