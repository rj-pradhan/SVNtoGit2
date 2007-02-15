package com.icesoft.faces.component.accordion;

import com.icesoft.faces.context.DOMContext;
import com.icesoft.faces.context.effects.CurrentStyle;
import com.icesoft.faces.renderkit.dom_html_basic.DomBasicRenderer;
import com.icesoft.faces.renderkit.dom_html_basic.HTML;

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

public class AccordionRenderer extends DomBasicRenderer {

    private static Log log = LogFactory.getLog(AccordionRenderer.class);
    private static final String HEADER = "Header";
    private static final String CONTENT = "Content";
    private static final String CONTAINER = "Container";


    public boolean getRendersChildren() {
        return true;
    }

    public void decode(FacesContext context, UIComponent component) {
        super.decode(context, component);
         Map map = (Map) context.getExternalContext().getSessionMap()
                .get(CurrentStyle.class.getName());
        String baseId = component.getClientId(context);
        String contentId = baseId + "_content";
        String style = (String)map.get(contentId);
        Accordion accordion = (Accordion)component;
        if(style != null){
            Boolean newState = Boolean.TRUE;
            if(style.indexOf("display:none") != -1){
                newState = Boolean.FALSE;
            }
            Boolean currentState = accordion.getOpen();
            if(!newState.equals(currentState)){
                ActionEvent ae = new ActionEvent(component);
                component.queueEvent(ae);
                accordion.setOpen(newState);
            }
        }
    }

    public void encodeBegin(FacesContext facesContext, UIComponent uiComponent) throws IOException {
        Accordion accordion = (Accordion)uiComponent;
        String base = accordion.getStyleClass();
        boolean open = accordion.getOpen().booleanValue();
        String headerClass = base + HEADER;
        String contentClass = base + CONTENT;
        String containerClass = base + CONTAINER;

        DOMContext domContext = DOMContext.attachDOMContext(facesContext, uiComponent);
        if (!domContext.isInitialized()) {
            Element rootSpan = domContext.createElement(HTML.DIV_ELEM);
            domContext.setRootNode(rootSpan);
            setRootElementId(facesContext, rootSpan, uiComponent);
        }
        Element root = (Element)domContext.getRootNode();
        root.setAttribute(HTML.CLASS_ATTR, containerClass);
        String baseID = uiComponent.getClientId(facesContext);
        Element header = domContext.createElement(HTML.DIV_ELEM);
        Text text = domContext.createTextNode(((Accordion)uiComponent).getLabel());
        header.appendChild(text);
        header.setAttribute(HTML.CLASS_ATTR, headerClass);
        header.setAttribute(HTML.ONCLICK_ATTR, "Ice.Accordion.fire('" + baseID + "_content');");
        header.setAttribute(HTML.ID_ATTR, baseID + "_header");
        root.appendChild(header);
        //Text script = domContext.createTextNode(SCRIPT);
        //Element scriptEle = domContext.createElement(SCRIPT);
        //scriptEle.setAttribute(HTML.LANG_ATTR, "javascript");
        //scriptEle.appendChild(script);
        //header.appendChild(scriptEle);

        Element container = domContext.createElement(HTML.DIV_ELEM);
        container.setAttribute(HTML.ID_ATTR, baseID + "_content");
        root.appendChild(container);
        container.setAttribute(HTML.CLASS_ATTR, contentClass);

        if(!open){
            container.setAttribute(HTML.STYLE_ATTR, "display:none;");
        }
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

