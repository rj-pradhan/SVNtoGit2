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

package com.icesoft.faces.component.inputfile;

import com.icesoft.faces.webapp.xmlhttp.PersistentFacesState;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

/**
 * This servlet handles request for inputFile component.
 */
public class FileUploadServlet
        extends com.icesoft.faces.webapp.xmlhttp.FileUploadServlet {
    public static final String UPLOAD_DIRECTORY =
            "com.icesoft.faces.uploadDirectory";
    public static final String UPLOAD_MAX_FILE_SIZE =
            "com.icesoft.faces.uploadMaxFileSize";
    public static final String UPLOAD_DIRECTORY_ABSOLUTE  =
            "com.icesoft.faces.uploadDirectoryAbsolute";

    static long uploadMaxFileSize;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext servletContext = config.getServletContext();
        try {
            uploadMaxFileSize = Long.parseLong(
                    servletContext.getInitParameter(UPLOAD_MAX_FILE_SIZE));
        } catch (NumberFormatException e) {
            uploadMaxFileSize = 10 * 1048576;
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        sendOutput(response, getHtml(request));
    }

    private String getComponentId(HttpServletRequest request) {
        return request.getParameter(InputFile.FILE_UPLOAD_COMPONENT_ID)
                .toString();
    }

    private String getDisabled(HttpServletRequest request) {
        Object disabledParam = request.getParameter("disabled");
        if (disabledParam != null &&
            disabledParam.toString().equalsIgnoreCase("true")) {
            return "disabled";
        } else {
            return "";
        }
    }

    private String getCSSFile(HttpServletRequest request) {
        Object cssFile = request.getParameter("cssFile");
        if (cssFile != null) {
            return cssFile.toString();
        }
        return null;
    }

    private String getInputTextClass(HttpServletRequest request) {
        if (request.getParameter("inputTextClass") != null) {
            return "class='" +
                   request.getParameter("inputTextClass").toString() + "'";
        } else {
            return null;
        }

    }

    private String getButtonClass(HttpServletRequest request) {
        if (request.getParameter("buttonClass") != null) {
            return "class='" + request.getParameter("buttonClass").toString() +
                   "'";
        } else {
            return null;
        }
    }

    private String getLabel(HttpServletRequest request) {
        if (request.getParameter("label") != null) {
            return request.getParameter("label").toString();
        } else {
            return "Upload";
        }
    }

    private boolean isUniqueFolder(HttpServletRequest request) {
        if (request.getParameter("uniqueFolder") != null && request
                .getParameter("uniqueFolder").trim().equalsIgnoreCase("true")) {
            return true;
        } else {
            return false;
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //Test if request if for file upload

        boolean isMultipart = FileUpload.isMultipartContent(request);
        if (isMultipart) {
            processMultipartContent(request, response);
        }
        sendOutput(response, getHtml(request));
    }

    private void processMultipartContent(HttpServletRequest request,
                                         HttpServletResponse response) {
        PersistentFacesState state = PersistentFacesState.getInstance();
        HttpSession session = request.getSession(true);
        DiskFileUpload upload = new DiskFileUpload(session);
        upload.setSizeMax(uploadMaxFileSize);

        List items;
        try {
            items = upload.parseRequest(request);
            SaveFile(items, request, config, session);
        } catch (SizeLimitExceededException limitExcep) {
            if (log.isDebugEnabled()) {
                log.debug(limitExcep.getMessage());
            }
            execute(session, state);

        } catch (FileUploadException e) {
            if (log.isDebugEnabled()) {
                log.debug(e.getMessage());
            }
            execute(session, state);
        }
    }

    private void SaveFile(List itemList, HttpServletRequest request,
                          ServletConfig config, HttpSession session) {
        Iterator files = itemList.iterator();
        PersistentFacesState state = PersistentFacesState.getInstance();
        String componentId = null;
        InputFile inputFile = null;


        while (files.hasNext()) {
            FileItem item = (FileItem) files.next();

            //this is a hidden field, so get the componentId
            //and get the inputFile component from the session keyed as componentId. 

            if (item.isFormField()) {
                componentId = item.getString();
                if (session.getAttribute(componentId) != null) {
                    inputFile = (InputFile) session.getAttribute(componentId);
                    inputFile.fireEvent();
                }
            }

            //this is the actual file object.
            else if (!item.isFormField()) {
                if (item.getName() != null &&
                    item.getName().trim().length() > 0) {

                    String fileName = null;
                    String FILE_SEPARATOR =
                            System.getProperty("file.separator");
                    int fileSeparatorIndex =
                            item.getName().lastIndexOf(FILE_SEPARATOR);
                    if (fileSeparatorIndex < 0) {
                        //Here we have to make manuel check for IE, FILE_SEPARATOR not giving proper val with diffrent config
                        fileSeparatorIndex = item.getName().lastIndexOf("\\");
                    }
                    if (fileSeparatorIndex > 0) {
                        fileName = item.getName()
                                .substring(fileSeparatorIndex + 1);
                    } else {
                        fileName = item.getName();
                    }

                    inputFile.getFileInfo().setFileName(fileName);
                    inputFile.getFileInfo()
                            .setContentType(item.getContentType());
                    inputFile.getFileInfo().setPhysicalPath(getPath(request));

                    try {
                        File uploadFolder = new File(getPath(request));
                        if (!uploadFolder.exists()) {
                            uploadFolder.mkdirs();
                        }
                        File file = new File(uploadFolder, fileName);
                        item.write(file);
                        inputFile.setFile(file);
                        inputFile.setStatus(InputFile.SAVED);
                        execute(session, state);
                    } catch (Exception e) {
                        if (log.isErrorEnabled()) {
                            log.error(e);
                        }
                    }
                } else {

                }
            }
        }
        persistentStateClearInstance();

    }


    private String getPath(HttpServletRequest request) {
        String relativeDir =
                config.getServletContext().getInitParameter(UPLOAD_DIRECTORY);

        if (null == relativeDir) {
            relativeDir = "";
        }

        boolean absolute = false;
        String param = config.getServletContext().getInitParameter(UPLOAD_DIRECTORY_ABSOLUTE);
        if(param!=null){
            absolute = Boolean.valueOf(param).booleanValue();
        }
                     
        String sessionId = request.getRequestedSessionId();
        String FILE_SEPARATOR = System.getProperty("file.separator");
        String dir = relativeDir;
        if(!absolute)
                dir = config.getServletContext().getRealPath(relativeDir);
        if (isUniqueFolder(request)) {
            dir += FILE_SEPARATOR + sessionId;
        }
        return (dir);
    }

    private void sendOutput(HttpServletResponse response, String output) {
        try {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println(output);
        } catch (IOException e) {
            if (log.isDebugEnabled()) {
                log.debug(e.getMessage());
            }
            e.printStackTrace();
        }
    }

    private String getHtml(HttpServletRequest request) {
        String link = "";
        if (getCSSFile(request) != null) {
            String[] links = getCSSFile(request).split(",");
            for (int i = 0; i < links.length; i++) {
                link += "<link rel='stylesheet' type='text/css' href='" +
                        links[i] + "'/>";
            }
        }
        String html = "<HTML><HEAD>" + link +
                      "<SCRIPT LANGUAGE='javascript'>function mySubmit(frm) {frm.fileName.value = frm.inputFileField.value ; return true;} </SCRIPT>" +
                      "</HEAD><BODY>" +
                      "<FORM action='" + InputFile.ICE_UPLOAD_FILE + "?" +
                      request.getQueryString() +
                      "' enctype='multipart/form-data' id='fileUploadForm' method='post' onsubmit='return mySubmit(this)'>" +
                      "<DIV id='submit' style='position:absolute;'>" +
                      "<INPUT name='fileName' type='hidden' value='test'/>" +
                      "<INPUT name='" + InputFile.FILE_UPLOAD_COMPONENT_ID +
                      "' type='hidden' value='" + getComponentId(request) +
                      "'/>" +
                      "<INPUT name='inputFileField' " +
                      getInputTextClass(request) + " type='file'" +
                      getDisabled(request) + " />" +
                      "<INPUT type='submit' " + getButtonClass(request) +
                      " value='" + getLabel(request) + "' " +
                      getDisabled(request) + "/>" +
                      "</DIV>" +
                      "</FORM></BODY></HTML>";
        return html;
    }
}
