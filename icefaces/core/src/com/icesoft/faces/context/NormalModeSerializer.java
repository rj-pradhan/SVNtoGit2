package com.icesoft.faces.context;

import com.icesoft.faces.application.StartupTime;
import com.icesoft.faces.renderkit.ApplicationBaseLocator;
import com.icesoft.faces.util.DOMUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class NormalModeSerializer implements DOMSerializer {
    private BridgeFacesContext context;
    private Writer writer;

    public NormalModeSerializer(BridgeFacesContext context, Writer writer) {
        this.context = context;
        this.writer = writer;
    }

    public void serialize(Document document) throws IOException {
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
                String base = ApplicationBaseLocator.locate(context);
                writer.write("<script language='javascript' src='" + base +
                        "xmlhttp" + StartupTime.getStartupInc() + "icefaces-d2d.js'></script>");
                writer.write(DOMUtils.childrenToString(body));
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
                JAXPSerializer serializer =
                        new JAXPSerializer(writer, publicID, systemID);
                if ("xml".equals(output)) {
                    serializer.outputAsXML();
                } else {
                    serializer.outputAsHTML();
                }
                if (prettyPrinting) {
                    serializer.printPretty();
                }
                serializer.serialize(document);
            }
        }

        writer.flush();
    }
}
