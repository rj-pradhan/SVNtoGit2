package com.icesoft.faces.component.panelcollapsible;

import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.context.effects.CurrentStyle;
import com.icesoft.faces.context.effects.JavascriptContext;
import com.icesoft.faces.renderkit.dom_html_basic.DomBasicRenderer;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;
import com.icesoft.faces.component.util.CustomComponentUtils;
import com.icesoft.faces.component.CSS_DEFAULT;
import com.icesoft.faces.component.ext.taglib.Util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import java.util.Iterator;
import java.util.Map;
import java.io.IOException;

import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

public class PanelCollapsibleRenderer extends DomBasicRenderer {

    private static Log log = LogFactory.getLog(PanelCollapsibleRenderer.class);


    public boolean getRendersChildren() {
        return true;
    }

    public void decode(FacesContext context, UIComponent component) {
        super.decode(context, component);
        PanelCollapsibleState state = PanelCollapsibleState.getState(context, component);
        state.setChangedViaDecode(false);
        try {
            PanelCollapsible panelCollapsible = (PanelCollapsible) component;

            Map map = (Map) context.getExternalContext().getSessionMap()
                    .get(CurrentStyle.class.getName());
            if (map != null) {
                String baseId = component.getClientId(context);
                String contentId = baseId + "_content";
                String style = (String) map.get(contentId);

                if (style != null) {
                    Boolean newState = Boolean.TRUE;
                    if (style.indexOf("display:none") != -1) {
                        newState = Boolean.FALSE;
                    }
                    Boolean currentState = panelCollapsible.getExpanded();
                    if (!newState.equals(currentState)) {
                        ActionEvent ae = new ActionEvent(component);
                        component.queueEvent(ae);
                        panelCollapsible.setExpanded(newState);

                        state.setChangedViaDecode(true);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent) throws IOException {
        PanelCollapsible panelCollapsible = (PanelCollapsible) uiComponent;
        PanelCollapsibleState state = PanelCollapsibleState.getState(facesContext, uiComponent);
        String base = panelCollapsible.getStyleClass();
        boolean open = panelCollapsible.getExpanded().booleanValue();
        boolean disabled = panelCollapsible.isDisabled();
        StringBuffer classModeSuffix = new StringBuffer(16);
        String style = panelCollapsible.getStyle();
        
        boolean fireEffect = false;


        String baseID = uiComponent.getClientId(facesContext);
        if( !state.isFirstTime() &&
            !state.isChangedViaDecode() &&
            state.isLastExpandedValue() != open){
            fireEffect = true;
        }


        DOMContext domContext = DOMContext.attachDOMContext(facesContext, uiComponent);
        if (!domContext.isInitialized()) {
            Element rootSpan = domContext.createElement(HTML.DIV_ELEM);
            domContext.setRootNode(rootSpan);
            setRootElementId(facesContext, rootSpan, uiComponent);
        }
        Element root = (Element) domContext.getRootNode();
        root.setAttribute(HTML.CLASS_ATTR, panelCollapsible.getStyleClass());
        root.setAttribute(HTML.STYLE_ATTR, style);

        Element header = domContext.createElement(HTML.DIV_ELEM);

        header.setAttribute(HTML.CLASS_ATTR, panelCollapsible.getHeaderClass());
        if(panelCollapsible.getToggleOnClick().equals(Boolean.TRUE)){
            if(!disabled)
                header.setAttribute(HTML.ONCLICK_ATTR, "Ice.PanelCollapsible." +
                        "fire('" + baseID + "_content', " +
                                "'"+ baseID +"_header', "+ panelCollapsible.getStyleClassForJs()+");");
        }
        String script = "Ice.PanelCollapsible.collapse('" + baseID + "_content');";
        if(panelCollapsible.getExpanded().booleanValue()){
            script = "Ice.PanelCollapsible.expand('" + baseID + "_content');";
        }

        if(!disabled)
            JavascriptContext.addJavascriptCall(facesContext, script);

        header.setAttribute(HTML.ID_ATTR, baseID + "_header");

        root.appendChild(header);
        UIComponent headerFacet = uiComponent.getFacet("header");
        if(headerFacet != null){
            try{

            //UIComponent headerComp = (UIComponent)headerFacet.getChildren().get(0);
            domContext.setCursorParent(header);
            domContext.streamWrite(facesContext, uiComponent,
                                   domContext.getRootNode(),header);

            CustomComponentUtils.renderChild(facesContext,headerFacet);
                //System.err.println("Header Renader Done");
            }catch(Exception e){
                e.printStackTrace();
            }
        }else{
            log.error("No Header facet found in Panel collapsible");
        }
        //Text script = domContext.createTextNode(SCRIPT);
        //Element scriptEle = domContext.createElement(SCRIPT);
        //scriptEle.setAttribute(HTML.LANG_ATTR, "javascript");
        //scriptEle.appendChild(script);
        //header.appendChild(scriptEle);

        Element container = domContext.createElement(HTML.DIV_ELEM);
        container.setAttribute(HTML.ID_ATTR, baseID + "_content");
        root.appendChild(container);
        container.setAttribute(HTML.CLASS_ATTR, panelCollapsible.getContentClass());


        if(fireEffect){
            if(!state.isLastExpandedValue()){
                container.setAttribute(HTML.STYLE_ATTR, "display:none;");
            }
        }else{
            if (!open) {
                container.setAttribute(HTML.STYLE_ATTR, "display:none;");
            }
        }
        state.setLastExpandedValue(open);
        state.setChangedViaDecode(false);
        state.setFirstTime(false);
        domContext.setCursorParent(container);

    }


    public void encodeEnd(FacesContext facesContext, UIComponent uiComponenet) throws IOException {

        DOMContext.getDOMContext(facesContext, uiComponenet).stepOver();

    }

    public void encodeChildren(FacesContext facesContext, UIComponent uiComponent)
            throws IOException {
        validateParameters(facesContext, uiComponent, null);
        DOMContext domContext =
                DOMContext.getDOMContext(facesContext, uiComponent);
        Iterator children = uiComponent.getChildren().iterator();
        while (children.hasNext()) {
            UIComponent nextChild = (UIComponent) children.next();
            if (nextChild.isRendered()) {
                encodeParentAndChildren(facesContext, nextChild);
            }
        }
        // set the cursor here since nothing happens in encodeEnd
        domContext.stepOver();
        domContext.streamWrite(facesContext, uiComponent);

    }

}

