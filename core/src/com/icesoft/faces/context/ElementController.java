package com.icesoft.faces.context;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import java.util.Random;
import java.util.Map;

public class ElementController {
    private final static Random RANDOM = new Random();
    private String focusCode = "";
    private String selectCode = "";
    private String clickCode = "";

    public static ElementController from(Map session) {
        String key = ElementController.class.toString();
        if (!session.containsKey(key)) {
            session.put(key, new ElementController());
        }

        return (ElementController) session.get(key);
    }

    public void focus(String elementID) {
        focusCode = "'" + elementID + "'.asExtendedElement().focus();" + randomComment();
    }

    public void select(String elementID) {
        selectCode = "'" + elementID + "'.asExtendedElement().select();" + randomComment();
    }

    public void click(String elementID) {
        clickCode = "'" + elementID + "'.asExtendedElement().click();" + randomComment();
    }

    private String randomComment() {
        return "//" + RANDOM.nextInt(99999);
    }

    public void addInto(Element element) {
        Document document = element.getOwnerDocument();

        Element focusElement = (Element) element.appendChild(document.createElement("script"));
        focusElement.setAttribute("id", "focus-code");
        focusElement.appendChild(document.createTextNode(focusCode));

        Element selectElement = (Element) element.appendChild(document.createElement("script"));
        selectElement.setAttribute("id", "select-code");
        selectElement.appendChild(document.createTextNode(selectCode));

        Element clickElement = (Element) element.appendChild(document.createElement("script"));
        clickElement.setAttribute("id", "click-code");
        clickElement.appendChild(document.createTextNode(clickCode));
    }
}
