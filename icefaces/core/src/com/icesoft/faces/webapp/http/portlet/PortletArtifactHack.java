package com.icesoft.faces.webapp.http.portlet;

import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;

/**
 * WARNING! WARNING! WARNING!
 * <p/>
 * This class is temporary and should not be relied on as it is extremely likely
 * not to be the final solution.
 * <p/>
 * Because we use a dispatcher to pass requests from the MainPortlet to the
 * MainServlet, the resulting portlet artifacts (like request, response, etc.)
 * are wrapped to look like servlet artifacts and certain types and APIs that
 * are specific to portlets are not available to the developer.  As a temporary
 * solution, we provide this class stored as a request attribute that can be
 * retrieved.
 */
public class PortletArtifactHack {

	public static final String PORTLET_HACK_KEY =
			"com.icesoft.faces.portletHack";

	private PortletConfig portletConfig;
	private PortletRequest portletRequest;

	public PortletArtifactHack(PortletConfig portletConfig,
			PortletRequest portletRequest) {
		this.portletConfig = portletConfig;
		this.portletRequest = portletRequest;
	}

	public PortletRequest getPortletRequest() {
		return (this.portletRequest);
	}

	public PortletConfig getPortletConfig() {
		return (this.portletConfig);
	}
}
