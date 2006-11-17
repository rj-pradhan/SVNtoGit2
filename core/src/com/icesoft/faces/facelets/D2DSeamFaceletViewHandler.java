package com.icesoft.faces.facelets;


import com.sun.facelets.compiler.Compiler;
import com.sun.facelets.compiler.SAXCompiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author ICEsoft Technologies, Inc.
 */
public class D2DSeamFaceletViewHandler extends D2DFaceletViewHandler {

    private static final String SEAM_EXPRESSION_FACTORY = "org.jboss.seam.ui.facelet.SeamExpressionFactory";

     // Log instance for this class
    private static Log log = LogFactory.getLog(D2DFaceletViewHandler.class);

    protected void faceletInitialize() {


        try {
            if( faceletFactory == null ) {

                com.sun.facelets.compiler.Compiler c = new SAXCompiler();
                c.setFeature( Compiler.EXPRESSION_FACTORY, SEAM_EXPRESSION_FACTORY );
                initializeCompiler( c );
                faceletFactory = createFaceletFactory( c );
            }
        }
        catch (Throwable t) {
            if( log.isErrorEnabled() ) {
                log.error("Failed initializing facelet instance", t);
            }
        }
    }
}
