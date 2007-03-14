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
 * BridgeExternalContext.java
 */

package com.icesoft.faces.context;

import javax.faces.context.ExternalContext;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * This class is supposed to provide a nice, generic interface to the
 * environment that we're running in (e.g. servlets, portlets).  The current
 * design has the type of environment identified during construction and any
 * subsequent method calls check the environment and call the underlying methods
 * appropriately.  I don't think this is the way we should do it but hopefully
 * we'll get this working and then refactor.
 */
public abstract class BridgeExternalContext extends ExternalContext {

    public static final String
            INCLUDE_SERVLET_PATH = "javax.servlet.include.servlet_path";

    public abstract void clearRequestContext();

    public abstract void resetRequestMap();

    public abstract String getRequestURI();

    public abstract Map getApplicationSessionMap();

    public abstract void setRequestServletPath(String viewId);

    public abstract void setRequestPathInfo(String viewId);

    public abstract void addCookie(Cookie cookie);

    public abstract Map collectBundles();

    public abstract Writer getWriter(String encoding) throws IOException;
}
