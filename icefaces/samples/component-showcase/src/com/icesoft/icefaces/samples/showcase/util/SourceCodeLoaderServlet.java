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

package com.icesoft.icefaces.samples.showcase.util;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

/**
 * <p>The SourceCodeLoaderServlet class is responsible for displaying the JSF
 * source code for a particular example. </p>
 *
 * @since 0.3.0
 */
public class SourceCodeLoaderServlet extends HttpServlet {

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) {

        // contains the relative path to where the source code for the example
        // is on the server
        String sourcePath = request.getParameter("path");

        if (sourcePath != null) {
            InputStream sourceStream =
                    getServletContext().getResourceAsStream(sourcePath);

            if (sourceStream == null) {
                try {
                    // Work around for websphere
                    sourceStream = new FileInputStream(new File(
                            getServletContext().getRealPath(
                                    sourcePath.replaceFirst("./", ""))));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (sourceStream != null) {
                PrintWriter responseStream = null;
                try {
                    // Setting the context type to text/xml provides style
                    // attributes for most browsers which should make reading
                    // the code easier.
                    response.setContentType("text/plain");
                    responseStream = response.getWriter();
                    int ch;
                    while ((ch = sourceStream.read()) != -1) {
                        responseStream.print((char) ch);
                    }
                    responseStream.close();
                    sourceStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
