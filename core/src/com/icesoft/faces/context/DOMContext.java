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

/*
 * $Id: DOMContext.java,v 1.0 2004/07/20 14:02:36 tedg Exp $
 */
package com.icesoft.faces.context;

import com.icesoft.faces.application.D2DViewHandler;
import com.icesoft.faces.util.HalterDump;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p><strong>DOMContext</strong> provides a component specific interface to the
 * DOM renderer
 */
public class DOMContext implements java.io.Serializable {
    private transient DOMResponseWriter writer;
    private Node cursor;
    private Document document;
    private Node rootNode;
    private Node parentElement;
    private boolean initialized;

    protected DOMContext(DOMResponseWriter writer, Document document,
                         Node parentElement) {
        this.writer = writer;
        this.document = document;
        this.cursor = parentElement;
        this.parentElement = parentElement;
        this.initialized = false;
    }

    /**
     * <p>Determine whether this instance is initialized. An initialized
     * instance is guaranteed to have a root node.</p>
     *
     * @return boolean reflecting whether this instance is initialized.
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * <p>This method returns the DOMContext associated with the specified
     * component.</p>
     *
     * @param facesContext an instance of {@link FacesContext} associated with
     *                     the lifecycle
     * @param component    component associated with this {@link DOMContext}
     * @return the attached {@link DOMContext}
     */
    public static DOMContext attachDOMContext(FacesContext facesContext,
                                              UIComponent component) {
        ResponseWriter responseWriter = facesContext.getResponseWriter();
        DOMResponseWriter domWriter;
        if (responseWriter instanceof DOMResponseWriter) {
            domWriter = (DOMResponseWriter) responseWriter;
        } else {
            domWriter = createTemporaryDOMResponseWriter(responseWriter);
        }
        Node cursorParent = domWriter.getCursorParent();
        Document doc = domWriter.getDocument();
        Map domContexts = domWriter.getDomResponseContexts();

        DOMContext context = null;
        String clientId =
                component.getClientId(FacesContext.getCurrentInstance());
        if (clientId != null && domContexts.containsKey(clientId)) {
            context = (DOMContext) domContexts.get(clientId);
        }
        if (null == context) {
            context = new DOMContext(domWriter, doc, cursorParent);
            domContexts.put(clientId, context);
        }
        //context may have been severed from the tree at some point
        if (context.isInitialized()) {
            if (!(cursorParent instanceof Element)) {
                context.stepOver();
                return context;
            }
            context.attach((Element) cursorParent);
        }
        context.stepOver();

        return context;
    }

    private static DOMResponseWriter createTemporaryDOMResponseWriter(
            ResponseWriter responseWriter) {
        DOMResponseWriter domWriter;
        domWriter = new DOMResponseWriter(responseWriter,
                                          D2DViewHandler.HTML_CONTENT_TYPE,
                                          D2DViewHandler.CHAR_ENCODING);
        Document doc = domWriter.getDocument();
        Element html = doc.createElement("html");
        doc.appendChild(html);
        Element body = doc.createElement("body");
        html.appendChild(body);
        domWriter.setCursorParent(body);
        return domWriter;
    }

    /**
     * <p>Get the DOMContext associated with the component. Do not attach the
     * DOMContext instance to its parent element.</p>
     *
     * @param facesContext
     * @param component    the {@link UIComponent} instance whose DOMContext we
     *                     are retrieving
     * @return {@link DOMContext}
     */
    public static DOMContext getDOMContext(FacesContext facesContext,
                                           UIComponent component) {
        ResponseWriter responseWriter = facesContext.getResponseWriter();
        DOMResponseWriter domWriter;
        if (responseWriter instanceof DOMResponseWriter) {
            domWriter = (DOMResponseWriter) responseWriter;
        } else {
            domWriter = createTemporaryDOMResponseWriter(responseWriter);
        }
        Document doc = domWriter.getDocument();
        Map domContexts = domWriter.getDomResponseContexts();

        DOMContext context = null;
        String clientId =
                component.getClientId(FacesContext.getCurrentInstance());
        if (domContexts.containsKey(clientId)) {
            context = (DOMContext) domContexts.get(clientId);
        }
        if (null == context) {
            Node cursorParent = domWriter.getCursorParent();
            context = new DOMContext(domWriter, doc, cursorParent);
            domContexts.put(clientId, context);
        }
        return context;
    }

    private void attach(Element cursorParent) {
        if (null == rootNode) { //nothing to attach
            return;
        }
        if (rootNode.equals(cursorParent)) {
            return;
        }

        //TODO needs proper fix
        //Quick & temp fix for ICEfacesWebPresentation application
        //This exception only happens when "rootNode" is ancestor of "cursor"
        if (rootNode.getParentNode() != cursorParent) {
            try {
                //re-attaching on top of another node
                //replace them and assume they will re-attach later
                cursorParent.appendChild(rootNode);
            } catch (DOMException e) {
                //this happens in strea-write mode only.
            }
        }
    }


    /**
     * <p>Creates an element of the type specified. Note that the instance
     * returned implements the <code>Element</code> interface, so attributes can
     * be specified directly on the returned object. <br>In addition, if there
     * are known attributes with default values, <code>Attr</code> nodes
     * representing them are automatically created and attached to the
     * element.</p>
     *
     * @param name the specified Element type to create
     * @return the created element
     */
    public Element createElement(String name) {
        return document.createElement(name);
    }

    /**
     * <p/>
     * Creates a <code>Text</code> node given the specified string. </p>
     *
     * @param cData The data for the node.
     * @return The new <code>Text</code> object.
     */
    public Text createTextNode(String cData) {
        return document.createTextNode(cData);
    }

    /**
     * <p/>
     * Set the rootNode member variable to the parameter Node. </p>
     *
     * @param rootNode
     */
    public void setRootNode(Node rootNode) {
        this.rootNode = rootNode;
        parentElement.appendChild(rootNode);
        initialized = true;
    }

    /**
     * <p/>
     * Creates an element of the type specified. Note that the instance returned
     * implements the <code>Element</code> interface, so attributes can be
     * specified directly on the returned object. <br>In addition, if there are
     * known attributes with default values, <code>Attr</code> nodes
     * representing them are automatically created and attached to the element.
     * Set the rootNode member variable of this instance to the newly-created
     * Element. </p>
     *
     * @param name
     * @return Element
     */
    public Element createRootElement(String name) {
        Element rootElement = createElement(name);
        setRootNode(rootElement);
        return rootElement;
    }

    void setIsolatedRootNode(Node rootElement) {
        this.rootNode = rootElement;
        initialized = true;
    }

    /**
     * <p>Get the rootNode member variable.</p>
     *
     * @return rootNode the root node of this <code>DOMContext</code> instance
     */
    public Node getRootNode() {
        return rootNode;
    }

    /**
     * Set the position at which the next rendered node will be appended
     *
     * @param cursorParent
     */
    public void setCursorParent(Node cursorParent) {
        this.cursor = cursorParent;
        writer.setCursorParent(cursorParent);
    }

    /**
     * Get the position in the document where the next DOM node will be
     * rendererd.
     */
    public Node getCursorParent() {
        return cursor;
    }


    /**
     * Maintain the cursor and cursor position; step to the position where the
     * next sibling should be rendered.
     */
    public void stepOver() {
        if (null != rootNode && rootNode.getParentNode() != null) {
            setCursorParent(rootNode.getParentNode());
        }
    }

    /**
     * Maintain the cursor and cursor such that the next rendered component will
     * be rendered as a child of the parameter component.
     *
     * @param component
     */
    public void stepInto(UIComponent component) {
        if (rootNode != null) {
            // default behaviour;
            // just like calling setCursorParent at the end of encode begin
            setCursorParent(rootNode);
        }
    }


    /**
     * Retrieve the org.w3c.dom.Document instance associated with this
     * DOMContext
     *
     * @return Document
     */
    public Document getDocument() {
        return document;
    }

    /**
     * Remove all children from Node parent
     *
     * @param parent - the root node to remove
     */
    public static void removeChildren(Node parent) {
        while (parent.hasChildNodes()) {
            parent.removeChild(parent.getFirstChild());
        }
    }

    /**
     * Removes from the root element all children with node name equal to the
     * nodeName parameter
     *
     * @param rootElement
     * @param name
     */
    public static void removeChildrenByTagName(Element rootElement,
                                               String name) {

        Node nextChildToRemove = null;
        while (rootElement.hasChildNodes()
               && ((nextChildToRemove = findChildWithNodeName(rootElement,
                                                              name)) != null)) {
            rootElement.removeChild(nextChildToRemove);
        }
    }

    /**
     * Find and return root's child Node with name nodeName or null if no such
     * child Node exists.
     */
    private static Node findChildWithNodeName(Element root, String nodeName) {
        NodeList children = root.getChildNodes();
        int length = children.getLength();
        for (int i = 0; i < length; i++) {
            Node nextChildNode = children.item(i);
            String name = nextChildNode.getNodeName();
            if (name.equalsIgnoreCase(nodeName)) {
                return nextChildNode;
            }
        }
        return null;
    }

    public static List findChildrenWithNodeName(Element root, String nodeName) {
        NodeList children = root.getChildNodes();
        int length = children.getLength();
        List foundItems = new ArrayList();
        for (int i = 0; i < length; i++) {
            Node nextChildNode = children.item(i);
            String name = nextChildNode.getNodeName();
            if (name.equalsIgnoreCase(nodeName)) {
                foundItems.add(nextChildNode);
            }
        }
        return foundItems;
    }


    private HashMap halterDumps = new HashMap();

    /**
     * <p>Writes the DOM subtree anchored at <code>root</code> to the current
     * ResponseWriter. Serialization is halted at the node <code>halter</code>
     * (writing only the opening tag) and will be resumed from this node on the
     * next call to this function.</p>
     *
     * @param facesContext current FacesContext
     * @param component    JSF component being rendered
     * @param root         node indicating subtree of DOM to eventually
     *                     serialize
     * @param halter       node upon which to halt serialization on this pass
     */
    public void streamWrite(FacesContext facesContext, UIComponent component,
                            Node root, Node halter) throws IOException {
        if (!DOMResponseWriter.isStreamWriting()) {
            return;
        }
        HalterDump halterDump = (HalterDump) halterDumps.get(root);
        if (null == halterDump) {
            halterDump = new HalterDump(facesContext.getResponseWriter(),
                                        component, root);
            halterDumps.put(root, halterDump);
        }
        halterDump.streamWrite(halter);
    }

    /**
     * <p>Convenience method for <code>streamWrite(facesContext, component,
     * this.rootNode, null)</code>.</p>
     *
     * @param facesContext current FacesContext
     * @param component    JSF component being rendered
     */
    public void streamWrite(FacesContext facesContext, UIComponent component)
            throws IOException {
        streamWrite(facesContext, component, rootNode, null);
    }

    /**
     * <p>Convenience method used by renderers to determine if Stream writing is
     * enabled.
     */
    public boolean isStreamWriting() {
        return DOMResponseWriter.isStreamWriting();
    }

    /**
     * This method can be used as an alternative to the streamWrite method. When
     * using this method you must also use the endNode method.
     */
    public void startNode(FacesContext facesContext, UIComponent component,
                          Node node)
            throws IOException {
        if (!isStreamWriting()) {
            return;
        } else {
            // get writer
            ResponseWriter writer = facesContext.getResponseWriter();

            // write by node type
            switch (node.getNodeType()) {

                case Node.DOCUMENT_NODE:
                    break;

                case Node.ELEMENT_NODE:
                    // start the element
                    writer.startElement(node.getNodeName().toLowerCase(),
                                        component);
                    // write attributes
                    NamedNodeMap attributes = node.getAttributes();

                    for (int i = 0; i < attributes.getLength(); i++) {
                        Node current = attributes.item(i);
                        writer.writeAttribute(current.getNodeName(),
                                              current.getNodeValue(),
                                              current.getNodeName());
                    }
                    break;

                case Node.TEXT_NODE:
                    writer.writeText(node.getNodeValue(), "text");
                    break;
            }
        }
    }

    /**
     * This method can be used as an alternative to the streamWrite method. When
     * using this method you must also use the startNode method.
     */
    public void endNode(FacesContext facesContext, UIComponent component,
                        Node node)
            throws IOException {
        if (!isStreamWriting()) {
            return;
        } else {
            // get writer
            ResponseWriter writer = facesContext.getResponseWriter();
            switch (node.getNodeType()) {

                case Node.DOCUMENT_NODE:
                    break;

                case Node.ELEMENT_NODE:
                    writer.endElement(node.getNodeName().toLowerCase());
                    break;

                case Node.TEXT_NODE:
                    break;
            }
        }
    }

}
