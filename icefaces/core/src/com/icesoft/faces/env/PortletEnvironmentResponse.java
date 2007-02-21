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

package com.icesoft.faces.env;

import javax.portlet.PortletResponse;
import javax.portlet.PortletURL;
import javax.portlet.RenderResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Locale;

/**
 * A pretty straight-forward wrapper for RenderResponse.
 * <p/>
 * It is up to the user to ensure that casts to this specific type and use the
 * specific methods if you are running in the appropriate environment.  Also,
 * since we wrap real responses, the state of those responses can get changed by
 * the application server, so it's possible that certain calls may result in
 * exceptions being thrown.
 * <p/>
 * Note:  This class may not be completely finished.  We have only included what
 * we've needed to this point.
 */
public class PortletEnvironmentResponse
        extends CommonEnvironmentResponse
        implements RenderResponse {


    //Portlet members
    private RenderResponse response;
    private PortletResponse portletResponse;


    public PortletEnvironmentResponse(PortletResponse response) {
        this.portletResponse = response;
        if (response instanceof RenderResponse) {
            this.response = (RenderResponse) response;
        }
    }

    //Common methods

    public String getContentType() {
        return response.getContentType();
    }

    public void setContentType(String type) {
        response.setContentType(type);
    }

    public String getCharacterEncoding() {
        return response.getCharacterEncoding();
    }

    public OutputStream getStream() throws IOException {
        return response.getPortletOutputStream();
    }

    public PrintWriter getWriter() throws IOException {
        return response.getWriter();
    }

    public Locale getLocale() {
        return response.getLocale();
    }

    public void setBufferSize(int size) {
        response.setBufferSize(size);
    }

    public int getBufferSize() {
        return response.getBufferSize();
    }

    public void flushBuffer() throws IOException {
        response.flushBuffer();
    }

    public void resetBuffer() {
        response.resetBuffer();
    }

    public boolean isCommitted() {
        return response.isCommitted();
    }

    public void reset() {
        response.reset();
    }

    public String encodeURL(String path) {
        return portletResponse.encodeURL(path);
    }

    //Portlet specific methods

    public PortletURL createRenderURL() {
        return response.createRenderURL();
    }

    public PortletURL createActionURL() {
        return response.createActionURL();
    }

    public String getNamespace() {
        return response.getNamespace();
    }

    public void setTitle(String title) {
        response.setTitle(title);
    }

    public OutputStream getPortletOutputStream() throws IOException {
        return response.getPortletOutputStream();
    }

    public void addProperty(String key, String value) {
        portletResponse.addProperty(key, value);
    }

    public void setProperty(String key, String value) {
        portletResponse.setProperty(key, value);
    }


}
