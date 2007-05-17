package com.icesoft.faces.component;

import com.icesoft.jasper.Constants;

import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.util.Map;

/**
 * The Sun RI does not call encodeNamespace when it creates unique ID's whereas
 * MyFaces does.  In order to support portlets, component ids must have the
 * appropriate namespace applied.  This custom UIViewRoot class can be used by
 * our own custom ViewHandler.  It simply overrides the createUniqueId method
 * and, if necessary, prepends the namespace to each component id.
 */
public class NamespacingViewRoot extends UIViewRoot {

    private ExternalContext extCtxt;
    private String namespace;

    public NamespacingViewRoot(FacesContext context) {
        extCtxt = context.getExternalContext();
        Map requestMap = extCtxt.getRequestMap();
        namespace = (String)requestMap.get(Constants.NAMESPACE_KEY);
    }

    /**
     * Overrides the UIViewRoot.createUniqueId method. The parent method is
     * called and the resulting id has the namespace pre-pended if:
     * - a namespace has been made available (e.g. portlets)
     * - the namespace has not already been prepended (e.g. MyFaces)
     * 
     * @return a unique component id, potentially with a namespace prepended
     */
    public String createUniqueId() {
        String uniqueID = super.createUniqueId();

        if (namespace == null || uniqueID.startsWith(namespace)) {
            return uniqueID;
        }

        return extCtxt.encodeNamespace(uniqueID);
    }
}
