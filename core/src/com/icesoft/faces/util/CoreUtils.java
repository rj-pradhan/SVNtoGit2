package com.icesoft.faces.util;

import javax.faces.context.FacesContext;

public class CoreUtils {
    public static String resolveResourceURL(FacesContext facesContext, String path) {
        return facesContext.getApplication().getViewHandler().getResourceURL(facesContext, path);
    }
}
