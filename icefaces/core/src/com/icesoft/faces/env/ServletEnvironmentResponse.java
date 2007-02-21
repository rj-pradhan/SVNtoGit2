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

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Locale;

/**
 * A pretty straight-forward wrapper for HttpServletResponse.
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
public class ServletEnvironmentResponse
        extends CommonEnvironmentResponse
        implements HttpServletResponse {


    //Servlet members
    private HttpServletResponse response;


    public ServletEnvironmentResponse(HttpServletResponse response) {
        this.response = response;
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

    public PrintWriter getWriter() throws IOException {
        return response.getWriter();
    }

    public OutputStream getStream() throws IOException {
        return response.getOutputStream();
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
        return response.encodeURL(path);
    }

    //Servlet specific methods

    public ServletOutputStream getOutputStream() throws IOException {
        return response.getOutputStream();
    }

    public void setCharacterEncoding(String encoding) {
        response.setContentType(encoding);
    }

    public void setContentLength(int length) {
        response.setContentLength(length);
    }

    public void setLocale(Locale locale) {
        response.setLocale(locale);
    }

    public void addCookie(Cookie cookie) {
        response.addCookie(cookie);
    }

    public boolean containsHeader(String name) {
        return response.containsHeader(name);
    }

    public String encodeRedirectURL(String url) {
        return response.encodeRedirectURL(url);
    }

    public String encodeUrl(String url) {
        return encodeURL(url);
    }

    public String encodeRedirectUrl(String url) {
        return encodeRedirectURL(url);
    }

    public void sendError(int code, String message) throws IOException {
        response.sendError(code, message);
    }

    public void sendError(int code) throws IOException {
        response.sendError(code);
    }

    public void sendRedirect(String dest) throws IOException {
        response.sendRedirect(dest);
    }

    public void setDateHeader(String name, long date) {
        response.setDateHeader(name, date);
    }

    public void addDateHeader(String name, long date) {
        response.addDateHeader(name, date);
    }

    public void setHeader(String name, String val) {
        response.setHeader(name, val);
    }

    public void addHeader(String name, String val) {
        response.addHeader(name, val);
    }

    public void setIntHeader(String name, int val) {
        response.setIntHeader(name, val);
    }

    public void addIntHeader(String name, int val) {
        response.addIntHeader(name, val);
    }

    public void setStatus(int code) {
        response.setStatus(code);
    }

    /**
     * @deprecated
     */
    public void setStatus(int code, String message) {
        response.setStatus(code, message);
    }
}
