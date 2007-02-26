package com.icesoft.jsfmeta.util;

import com.sun.rave.jsfmeta.beans.ComponentBean;
import com.sun.rave.jsfmeta.beans.FacesConfigBean;
import com.sun.rave.jsfmeta.beans.FacetBean;
import com.sun.rave.jsfmeta.beans.RenderKitBean;
import com.sun.rave.jsfmeta.beans.RendererBean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class DebugUtil {
    
    private static Logger logger = Logger.getLogger("com.icesoft.jsfmeta.util.DebugUtil");
    
    /** Creates a new instance of DebugUtil */
    public DebugUtil() {
    }
    
    public static void print(FacesConfigBean config){
        		ComponentBean cbs[] = config.getComponents();
		for (int i = 0; i < cbs.length; i++) {
			ComponentBean cb = cbs[i];
			if (logger.isLoggable(Level.FINE)) {
				System.out.println("Component(componentType="
						+ cb.getComponentType() + ",componentFamily="
						+ cb.getComponentFamily() + ",rendererType="
						+ cb.getRendererType() + ",baseComponentType="
						+ cb.getBaseComponentType() + ")");
			}
			FacetBean fbs[] = cbs[i].getFacets();
			for (int j = 0; j < fbs.length; j++) {

				if (logger.isLoggable(Level.FINE)) {
					System.out.println("  Facet(facetName="
							+ fbs[j].getFacetName() + ",displayName="
							+ fbs[j].getDisplayName("") + ")");
				}
			}

		}

		RenderKitBean rkbs[] = config.getRenderKits();
		for (int i = 0; i < rkbs.length; i++) {
			RenderKitBean rkb = rkbs[i];
			RendererBean rbs[] = rkb.getRenderers();
			for (int j = 0; j < rbs.length; j++) {
				RendererBean rb = rbs[j];
				System.out.println("Renderer(renderKitId="
						+ rkb.getRenderKitId() + ",componentFamily="
						+ rb.getComponentFamily() + ",rendererType="
						+ rb.getRendererType() + ")");
				FacetBean fbs[] = rbs[j].getFacets();
				for (int k = 0; k < fbs.length; k++)
					System.out.println("  Facet(facetName="
							+ fbs[k].getFacetName() + ",displayName="
							+ fbs[k].getDisplayName("") + ")");

			}

		}
    }
    
}
