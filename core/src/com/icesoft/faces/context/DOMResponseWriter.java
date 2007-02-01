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

package com.icesoft.faces.context;

import com.icesoft.faces.application.D2DViewHandler;
import com.icesoft.faces.context.effects.JavascriptContext;
import com.icesoft.faces.renderkit.ApplicationBaseLocator;
import com.icesoft.faces.util.DOMUtils;
import com.icesoft.faces.webapp.xmlhttp.ResponseState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.beans.Beans;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p><strong>DOMResponseWriter</strong> is a DOM specific implementation of
 * <code>javax.faces.context.ResponseWriter</code>.
 */
public class DOMResponseWriter extends ResponseWriter {
    private static final Log log = LogFactory.getLog(DOMResponseWriter.class);
    public static final String STREAM_WRITING =
            "com.icesoft.faces.streamWriting";
    //DOM and current node being written to for this ResponseWriter
    public static final String DOCTYPE_PUBLIC = "com.icesoft.doctype.public";
    public static final String DOCTYPE_SYSTEM = "com.icesoft.doctype.system";
    public static final String DOCTYPE_ROOT = "com.icesoft.doctype.root";
    public static final String DOCTYPE_OUTPUT = "com.icesoft.doctype.output";
    public static final String DOCTYPE_PRETTY_PRINTING =
            "com.icesoft.doctype.prettyprinting";

    public static final String RESPONSE_DOM = "com.icesoft.domResponseDocument";
    public static final String RESPONSE_DOM_ID =
            "com.icesoft.domResponseDocumentID";
    public static final String OLD_DOM = "com.icesoft.oldDocument";
    public static final String RESPONSE_VIEWROOT =
            "com.icesoft.domResponseViewRoot";
    //Hashtable of DOMContext objects associated with each component
    public static final String RESPONSE_CONTEXTS_TABLE =
            "com.icesoft.domResponseContexts";
    //Hashtable of DOMContext tables to be cached per page in each session
    public static final String RESPONSE_CONTEXT_SERVLET_TABLE =
            "com.icesoft.domResponseContextServlet";
    //Set of nodes that have changed since the last rendering pass
    public static final String RESPONSE_MODIFIED_NODES =
            "com.icesoft.domResponseContextModNodes";
    public static final String USE_DOM_DIFF = "com.icesoft.faces.useDOMDiff";
    private static DocumentBuilder DOCUMENT_BUILDER;

    static {
        try {
            DOCUMENT_BUILDER =
                    DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            log.error("Cannot acquire a DocumentBuilder", e);
        }
    }

    private static boolean isStreamWritingFlag = false;
    private String contentType;
    private String encoding;
    private Writer writer;
    private Document document;
    private Document oldDocument;
    private Node cursor;
    private Map domResponseContexts;
    private Map contextServletTable;
    private FacesContext context;

    public DOMResponseWriter(Writer writer, FacesContext context, String contentType,
                             String encoding) {
        this.writer = writer;
        this.context = context;
        this.initialize();
        this.contentType = contentType == null ? "text/html" : contentType;
        checkEncoding(encoding);
        this.encoding = encoding;
    }

    Map getDomResponseContexts() {
        return domResponseContexts;
    }

    public Node getCursorParent() {
        return cursor;
    }

    Document getDocument() {
        return document;
    }

    public String getContentType() {
        return contentType;
    }

    public String getCharacterEncoding() {
        return encoding;
    }

    public void startDocument() throws IOException {
    }

    private void initialize() {
        contextServletTable = D2DViewHandler.getContextServletTable(context);
        // contexts for each component
        if (contextServletTable
                .containsKey(DOMResponseWriter.RESPONSE_CONTEXTS_TABLE)) {
            domResponseContexts = (Map) contextServletTable
                    .get(DOMResponseWriter.RESPONSE_CONTEXTS_TABLE);
        }
        if (null == domResponseContexts) {
            domResponseContexts = new HashMap();
            contextServletTable.put(DOMResponseWriter.RESPONSE_CONTEXTS_TABLE,
                    domResponseContexts);
        }
        // viewroot, application
        contextServletTable.put(DOMResponseWriter.RESPONSE_VIEWROOT,
                context.getViewRoot());
        cursor = document = DOCUMENT_BUILDER.newDocument();
        contextServletTable.put(DOMResponseWriter.RESPONSE_DOM, document);
        boolean streamWritingParam = "true".equalsIgnoreCase(
                context.getExternalContext().getInitParameter(
                        DOMResponseWriter.STREAM_WRITING));
        DOMResponseWriter.isStreamWritingFlag =
                Beans.isDesignTime() || streamWritingParam;
    }

    public void endDocument() throws IOException {
        writeDOM();
    }

    public void flush() throws IOException {
    }

    public void startElement(String name, UIComponent componentForElement)
            throws IOException {
        Node oldCursor = cursor;
        Element elem = document.createElement(name);
        cursor = cursor.appendChild(elem);
        if (log.isTraceEnabled()) {
            log.trace("startElement()  name: " + name + "  elem: " + elem +
                    "  oldCursor: " + oldCursor + "  newCursor: " + cursor);
        }
    }

    public void endElement(String name) throws IOException {
        Node oldCursor = cursor;
        cursor = cursor.getParentNode();
        if (log.isTraceEnabled()) {
            log.trace("endElement()  name: " + name + "  oldCursor: " +
                    oldCursor + "  newCursor: " + cursor);
        }
    }

    public void writeAttribute(String name, Object value,
                               String componentPropertyName)
            throws IOException {
        //name.trim() because cardemo had a leading space in an attribute name
        //which made the DOM processor choke
        ((Element) cursor).setAttribute(name.trim(), String.valueOf(value));
    }

    public void writeURIAttribute(String name, Object value,
                                  String componentPropertyName)
            throws IOException {
        String stringValue = String.valueOf(value);
        if (stringValue.startsWith("javascript:")) {
            ((Element) cursor).setAttribute(name, stringValue);
        } else {
            ((Element) cursor)
                    .setAttribute(name, stringValue.replace(' ', '+'));
        }
    }

    public void writeComment(Object comment) throws IOException {
        if (log.isTraceEnabled()) {
            log.trace("writeComment()  comment: " + comment);
        }
        cursor.appendChild(document.createComment(String.valueOf(comment)));
    }

    public void writeText(Object text, String componentPropertyName)
            throws IOException {
        if (log.isTraceEnabled()) {
            log.trace("writeText(O,S)  text: " + text);
        }
        cursor.appendChild(document.createTextNode(String.valueOf(text)));
    }

    public void writeText(char text[], int off, int len) throws IOException {
        if (log.isTraceEnabled()) {
            log.trace("writeText(c[],i,i)  text: " +
                    (new String(text, off, len)));
        }
        cursor.appendChild(document.createTextNode(new String(text, off, len)));
    }

    public ResponseWriter cloneWithWriter(Writer writer) {
        //FIXME: This is a hack for DOM rendering but JSF currently clones the writer
        //just as the components are complete
        if (null != document) {
            try {
                writeDOM();
            } catch (IOException e) {
                throw new IllegalStateException(e.toString());
            }
        }
        try {
            return new DOMResponseWriter(writer, context, getContentType(),
                    getCharacterEncoding());
        } catch (FacesException e) {
            throw new IllegalStateException();
        }
    }

    public void close() throws IOException {
        writer.close();
    }

    public void write(char[] cbuf, int off, int len) throws IOException {
        if (log.isTraceEnabled()) {
            log.trace("writeText(c[],i,i)  str: " +
                    (new String(cbuf, off, len)));
        }
        cursor.appendChild(document.createTextNode(new String(cbuf, off, len)));
    }

    public void write(int c) throws IOException {
        if (log.isTraceEnabled()) {
            log.trace("write(i)  hex: " + Integer.toHexString(c) +
                    "  decimal: " + c);
        }
        cursor.appendChild(document.createTextNode(String.valueOf((char) c)));
    }

    public void write(String str) throws IOException {
        if (log.isTraceEnabled()) {
            log.trace("write(S)  str: " + str);
        }
        cursor.appendChild(document.createTextNode(str));
    }

    public void write(String str, int off, int len) throws IOException {
        if (log.isTraceEnabled()) {
            log.trace("write(S,i,i)  str_sub: " + str.substring(off, len));
        }
        cursor.appendChild(document.createTextNode(str.substring(off, len)));
    }

    /**
     * This method writes the complete DOM to the current writer
     */
    public void writeDOM() throws IOException {
        if (isStreamWriting())
            return;//The DOM has already been written via the Renderer streamWrite calls

        if (!(context instanceof BridgeFacesContext)) {
            if (log.isErrorEnabled()) {
                log.error("ICEfaces requires the PersistentFacesServlet. " +
                        "Please check your web.xml servlet mappings");
            }
            throw new IllegalStateException(
                    "ICEfaces requires the PersistentFacesServlet. " +
                            "Please check your web.xml servlet mappings");
        }
        BridgeFacesContext bridgeFacesContext = (BridgeFacesContext) context;
        enhanceAndFixDocument(bridgeFacesContext);
        BridgeExternalContext externalContext =
                (BridgeExternalContext) context.getExternalContext();
        Map sessionMap = externalContext.getApplicationSessionMap();

        String viewNumber = bridgeFacesContext.getViewNumber();
        ResponseState nodeWriter =
                (ResponseState) sessionMap.get(
                        viewNumber + "/" + ResponseState.STATE);
        //We've changed IncrementalNodeWriter implementation from BlockingServlet
        //to the more generic and direct ResponseState.  This helps to support
        //running in basic mode (BlockingResponseState) or enterprise (AsyncResponseState)
        //configurations.
        if (null != nodeWriter) {
            oldDocument = (Document) sessionMap.get(getOldDOMKey());

            if (null != oldDocument) {
                Node[] changed = DOMUtils.domDiff(oldDocument, document);
                for (int i = 0; i < changed.length; i++) {
                    Element changeRoot =
                            DOMUtils.ascendToNodeWithID(changed[i]);
                    for (int j = 0; j < i; j++) {
                        if (changed[j] == null)
                            continue;
                        // If new is already in, then discard new
                        if (changeRoot == changed[j]) {
                            changeRoot = null;
                            break;
                        }
                        // If new is parent of old, then replace old with new
                        else if (isAncestor(changeRoot, changed[j])) {
                            changed[j] = null;
                        }
                        // If new is a child of old, then discard new
                        else if (isAncestor(changed[j], changeRoot)) {
                            changeRoot = null;
                            break;
                        }
                    }
                    changed[i] = changeRoot;
                }
                for (int i = 0; i < changed.length; i++) {
                    Element element = (Element) changed[i];
                    if (null != element) {
                        nodeWriter.writeElement(element);
                    }
                }
            }

            nodeWriter.flush();
        }

        sessionMap.put(getOldDOMKey(), document);

        if (null != writer) {
            writeDOM(writer);
        }

    }

    private void enhanceAndFixDocument(BridgeFacesContext context) {
        Element html = (Element) document.getDocumentElement();
        html = "html".equals(html.getTagName()) ? html : fixHtml();

        Element head = (Element) document.getElementsByTagName("head").item(0);
        enhanceHead(head == null ? fixHead(html) : head, context);

        Element body = (Element) document.getElementsByTagName("body").item(0);
        enhanceBody(body == null ? fixBody(html) : body, context);
    }

    private void enhanceBody(Element body, BridgeFacesContext context) {
        //id required for forwarded (server-side) redirects
        body.setAttribute("id", "body");
        Element iframe = document.createElement("iframe");
        body.insertBefore(iframe, body.getFirstChild());
        iframe.setAttribute("id", "history-frame");
        Object request = context.getExternalContext().getRequest();

        final String frameURI;
        //another "workaround" to resolve the iframe URI
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            if (httpRequest.getRequestURI() == null) {
                frameURI = "about:blank";
            } else {
                frameURI = httpRequest.getContextPath() + "/xmlhttp/blank.iface";
            }
        } else {
            frameURI = "about:blank";
        }
        iframe.setAttribute("src", frameURI);
        iframe.setAttribute("frameborder", "0");
        iframe.setAttribute("style",
                "z-index: 10000; visibility: hidden; width: 0; height: 0; opacity: 0.22; filter: alpha(opacity=22);");

        // TODO This is only meant to be a transitional focus retention(management) solution.
        String focusId = context.getFocusId();
        if (focusId != null && !focusId.equals("null")) {
            JavascriptContext.focus(context, focusId);
        }

        Element script =
                (Element) body.appendChild(document.createElement("script"));
        script.setAttribute("id", JavascriptContext.DYNAMIC_CODE_ID);
        script.setAttribute("language", "javascript");
        String calls = JavascriptContext.getJavascriptCalls(context);
        script.appendChild(document.createTextNode(calls));

        Map session = context.getExternalContext().getSessionMap();
        ElementController.from(session).addInto(body);

        Element sessionID =
                (Element) body.appendChild(document.createElement("script"));
        sessionID.setAttribute("language", "javascript");
        sessionID.appendChild(document.createTextNode("window.session='" + context.getIceFacesId() + "';"));
        body.appendChild(sessionID);
    }

    private void enhanceHead(Element head, BridgeFacesContext context) {
        Element meta =
                (Element) head.appendChild(document.createElement("meta"));
        meta.setAttribute("name", "icefaces");
        meta.setAttribute("content", "Rendered by ICEFaces™ D2D");

        Element noscript =
                (Element) head.appendChild(document.createElement("noscript"));
        Element noscriptMeta =
                (Element) noscript.appendChild(document.createElement("meta"));
        noscriptMeta.setAttribute("http-equiv", "refresh");
        noscriptMeta
                .setAttribute("content", "0;url=./xmlhttp/javascript-blocked");

        //load libraries
        String base = ApplicationBaseLocator.locate(context);
        List libs = new ArrayList();
        libs.add("xmlhttp/icefaces-d2d.js");
        if (context.getExternalContext().getRequestMap()
                .get(BridgeExternalContext.INCLUDE_SERVLET_PATH) == null) {
            libs.addAll(Arrays.asList(
                    JavascriptContext.getIncludedLibs(context)));
        }
        String avoidCacheId = avoidCacheId(context);
        Iterator iterator = libs.iterator();
        while (iterator.hasNext()) {
            String lib = (String) iterator.next();
            if (avoidCacheId != null) {
                lib += "?" + avoidCacheId;
            }
            Element script = (Element) head
                    .appendChild(document.createElement("script"));
            script.setAttribute("language", "javascript");
            script.setAttribute("src", base + lib);
        }

        String sessionIdentifier = context.getIceFacesId();
        Element viewAndSessionScript = (Element) head.appendChild(document.createElement("script"));
        viewAndSessionScript.setAttribute("language", "javascript");
        viewAndSessionScript.appendChild(document.createTextNode(
                "window.session = '" + sessionIdentifier + "';"
        ));
    }

    private Element fixHtml() {
        Element root = document.getDocumentElement();
        Element html = document.createElement("html");
        document.replaceChild(html, root);
        html.appendChild(root);

        return html;
    }

    private Element fixBody(Element html) {
        Element body = document.createElement("body");
        NodeList children = html.getChildNodes();
        int length = children.getLength();
        Node[] nodes = new Node[length];
        //copy the children first, since NodeList is live
        for (int i = 0; i < nodes.length; i++) nodes[i] = children.item(i);
        for (int i = 0; i < nodes.length; i++) {
            Node node = nodes[i];
            if (!(node instanceof Element &&
                    "head".equals(((Element) node).getTagName())))
                body.appendChild(node);
        }
        html.appendChild(body);

        return body;
    }

    private Element fixHead(Element html) {
        Element head = document.createElement("head");
        html.insertBefore(head, html.getFirstChild());

        return head;
    }

    /**
     * This method writes the complete DOM to the specified writer
     *
     * @param writer destination of the DOM output
     */
    private void writeDOM(Writer writer) throws IOException {
        Map requestMap = context.getExternalContext().getRequestMap();
        String includeServletPath = (String) requestMap
                .get(BridgeExternalContext.INCLUDE_SERVLET_PATH);
        String portletParam =
                (String) requestMap.get("com.sun.faces.portlet.INIT");

        //if it's not a servlet, it must be a portlet, so we should return
        //a fragment
        if ((null != includeServletPath) || (null != portletParam)) {
            Node body = DOMUtils.getChildByNodeName(
                    document.getDocumentElement(), "body");
            if (null != body) {
                //TODO This should not be done here, we need to manage
                //it with the form renderer.  But we also need to fix
                //viewNumber so that it is not set with cookies.  Cookie
                //communication gives no way for multiple included
                //views on one page
                FacesContext facesContext = FacesContext.getCurrentInstance();
                String base = ApplicationBaseLocator.locate(facesContext);
                writer.write("<script language='javascript' src='" + base +
                        "xmlhttp/icefaces-d2d.js'></script>");
                writer.write(DOMUtils.childrenToString(body));
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("null body");
                    log.debug(DOMUtils.DOMtoString(document));
                }
            }
        } else {
            String publicID =
                    (String) requestMap.get(DOMResponseWriter.DOCTYPE_PUBLIC);
            String systemID =
                    (String) requestMap.get(DOMResponseWriter.DOCTYPE_SYSTEM);
            String root =
                    (String) requestMap.get(DOMResponseWriter.DOCTYPE_ROOT);
            String output =
                    (String) requestMap.get(DOMResponseWriter.DOCTYPE_OUTPUT);
            boolean prettyPrinting =
                    Boolean.valueOf((String) requestMap
                            .get(DOMResponseWriter.DOCTYPE_PRETTY_PRINTING))
                            .booleanValue();

            //todo: replace this with a complete new implementation that doesn't rely on xslt but can serialize xml, xhtml, and html. 
            if (output == null || ("html".equals(output) && !prettyPrinting)) {
                if (publicID != null && systemID != null && root != null) {
                    writer.write(DOMUtils.DocumentTypetoString(publicID, systemID,
                            root));
                }
                writer.write(DOMUtils.DOMtoString(document));
            } else {
                //use a serializer. not as performant.
                try {
                    DOMSerializer serializer =
                            new DOMSerializer(writer, publicID, systemID);
                    if ("xml".equals(output)) {
                        serializer.outputAsXML();
                    } else {
                        serializer.outputAsHTML();
                    }
                    if (prettyPrinting) {
                        serializer.printPretty();
                    }
                    serializer.serialize(document);
                } catch (TransformerException e) {
                    new IOException(e.getMessage());
                }
            }
        }
        writer.flush();
    }

    /**
     * This method sets the write cursor for DOM modifications.  Subsequent DOM
     * modifications will take place below the cursor element.
     *
     * @param cursorParent parent node for subsequent modifications to the DOM
     */
    protected void setCursorParent(Node cursorParent) {
        this.cursor = cursorParent;
    }

    boolean isAncestor(Node test, Node child) {
        if (test == null || child == null)
            return false;
        if (!test.hasChildNodes()) {
            return false;
        }
        Node parent = child;
        while ((parent = parent.getParentNode()) != null) {
            if (test.equals(parent)) {
                return true;
            }
        }
        return false;
    }

    //With multiple browser windows, there will be one old DOM per window,
    //hence keyed by viewNumber
    String getOldDOMKey() {
        FacesContext context = FacesContext.getCurrentInstance();
        String viewNumber = "-";
        if (context instanceof BridgeFacesContext) {
            viewNumber = ((BridgeFacesContext) context).getViewNumber();
        }
        return DOMResponseWriter.getOldDOMKey(viewNumber);
    }

    public static String getOldDOMKey(String viewNumber) {
        return viewNumber + "/" + DOMResponseWriter.OLD_DOM;
    }

    private static void checkEncoding(String encoding)
            throws IllegalArgumentException {
        try {
            new String(new byte[]{65}, encoding);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(
                    "Unsupported Encoding " + encoding);
        }
    }

    public static void applyBrowserDOMChanges(BridgeFacesContext facesContext) {
        ExternalContext externalContext = facesContext.getExternalContext();
        Document document = (Document) externalContext.getSessionMap()
                .get(DOMResponseWriter.getOldDOMKey(
                        facesContext.getViewNumber()));
        if (document == null) return;
        Map parameters = externalContext.getRequestParameterValuesMap();

        NodeList inputElements = document.getElementsByTagName("input");
        int inputElementsLength = inputElements.getLength();
        for (int i = 0; i < inputElementsLength; i++) {
            Element inputElement = (Element) inputElements.item(i);
            String id = inputElement.getAttribute("id");
            if (parameters.containsKey(id)) {
                String value = ((String[]) parameters.get(id))[0];
                inputElement.setAttribute("value", value);
            }
        }

        NodeList textareaElements = document.getElementsByTagName("textarea");
        int textareaElementsLength = textareaElements.getLength();
        for (int i = 0; i < textareaElementsLength; i++) {
            Element textareaElement = (Element) textareaElements.item(i);
            String id = textareaElement.getAttribute("id");
            if (parameters.containsKey(id)) {
                String value = ((String[]) parameters.get(id))[0];
                textareaElement.getFirstChild()
                        .setNodeValue(value);//set value on the Text node
            }
        }

        NodeList selectElements = document.getElementsByTagName("select");
        int selectElementsLength = selectElements.getLength();
        for (int i = 0; i < selectElementsLength; i++) {
            Element selectElement = (Element) selectElements.item(i);
            String id = selectElement.getAttribute("id");
            if (parameters.containsKey(id)) {
                List values = Arrays.asList((String[]) parameters.get(id));

                NodeList optionElements =
                        selectElement.getElementsByTagName("option");
                int optionElementsLength = optionElements.getLength();
                for (int j = 0; j < optionElementsLength; j++) {
                    Element optionElement = (Element) optionElements.item(j);
                    if (values.contains(optionElement.getAttribute("value"))) {
                        optionElement.setAttribute("selected", "selected");
                    } else {
                        optionElement.removeAttribute("selected");
                    }
                }
            }
        }
    }

    public static boolean isStreamWriting() {
        return isStreamWritingFlag;
    }

    private String avoidCacheId(FacesContext facesContext) {
        Object sessionObj = facesContext.getExternalContext().getSession(true);
        if (sessionObj != null) {
            if (sessionObj instanceof HttpSession) {
                HttpSession httpSession = (HttpSession) sessionObj;
                String id = httpSession.getId();
                if (id == null) {
                    log.error("HttpSession id is null. Can't get a unique script ID");
                } else {
                    int size = 4;
                    if (id.length() > size) {
                        int start = id.length() - size;
                        return id.substring(start);
                    } else {
                        return id;
                    }
                }

            } else {
                log.error("Session is not HttpSession its [" + sessionObj.getClass().getName() + "]. Can't get a unique script ID.");
            }
        } else {
            log.error("Session is null. Can't get a unique script ID");
        }
        return null;
    }
}
