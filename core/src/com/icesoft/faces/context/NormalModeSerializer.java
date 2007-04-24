package com.icesoft.faces.context;

import com.icesoft.faces.application.StartupTime;
import com.icesoft.faces.renderkit.LocationUtil;
import com.icesoft.faces.util.DOMUtils;
import com.icesoft.jasper.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

public class NormalModeSerializer implements DOMSerializer {

    private static Log log = LogFactory.getLog(NormalModeSerializer.class);

    private BridgeFacesContext context;
    private Writer writer;

    public NormalModeSerializer(BridgeFacesContext context, Writer writer) {
        this.context = context;
        this.writer = writer;
    }

    public void serialize(Document document) throws IOException {
        Map requestMap = context.getExternalContext().getRequestMap();

        if( isFragment(requestMap) ){
            if( log.isInfoEnabled() ){
                log.info( "treating request as a fragment" );
            }

            Node body = DOMUtils.getChildByNodeName(
                    document.getDocumentElement(), "body");
            if (null != body) {
                //TODO This should not be done here, we need to manage
                //it with the form renderer.  But we also need to fix
                //viewNumber so that it is not set with cookies.  Cookie
                //communication gives no way for multiple included
                //views on one page
                String base = LocationUtil.getAppBase(context);
                writer.write("<script language='javascript' src='" + base +
                        "xmlhttp" + StartupTime.getStartupInc() + "icefaces-d2d.js'></script>");
                writer.write(DOMUtils.childrenToString(body));
            }
        } else {
            if (log.isInfoEnabled()) {
                log.info("treating request as a whole page (not a fragment)");
            }

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

    private boolean isFragment(Map requestMap){
        //TODO - assuming we can handle portlets just like includes, then
        //we can probably reduce the attributes that we check for.  We need
        //to be specific about when to use request URI and when to use servlet
        //path.

        String frag = (String) requestMap.get(Constants.INC_REQUEST_URI);
        if( log.isInfoEnabled() ){
            log.info( Constants.INC_REQUEST_URI + " = " + frag );
        }
        if( frag != null ){
            return true;
        }

        frag = (String)requestMap.get(Constants.INC_SERVLET_PATH);
        if( log.isInfoEnabled() ){
            log.info( Constants.INC_SERVLET_PATH + " = " + frag );
        }
        if( frag != null ){
            return true;
        }

        //This type of check should no longer be required.  If we need
        //to put a portlet specific attribute back in, then we should
        //define our own.
        frag = (String) requestMap.get("com.sun.faces.portlet.INIT");
        if( frag != null ){
            return true;
        }

        return false;
    }
}
