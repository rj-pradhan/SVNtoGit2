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
/* Original Copyright
 * Copyright 2001-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.icesoft.faces.component.inputfile;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.MultipartStream;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class DiskFileUpload
        extends org.apache.commons.fileupload.DiskFileUpload {
    InputFile inputFile = null;
    HttpSession session = null;
    String fileName = null;

    DiskFileUpload(HttpSession session, InputFile inputFile) {
        this.session = session;
        this.inputFile = inputFile;
    }

    public List parseRequest(HttpServletRequest req)
            throws FileUploadException {
        if (null == req) {
            throw new NullPointerException("req parameter");
        }
        if(inputFile != null){
            inputFile.setStatus(InputFile.DEFAULT);
        }
 
        ArrayList items = new ArrayList();
        String contentType = req.getHeader(CONTENT_TYPE);

        if ((null == contentType) || (!contentType.startsWith(MULTIPART))) {
            throw new InvalidContentTypeException(
                    "the request doesn't contain a "
                    + MULTIPART_FORM_DATA
                    + " or "
                    + MULTIPART_MIXED
                    + " stream, content type header is "
                    + contentType);
        }
        int requestSize = req.getContentLength();

        ProgressOutputStream pos = null;
        try {
            int boundaryIndex = contentType.indexOf("boundary=");
            if (boundaryIndex < 0) {
                throw new FileUploadException(
                        "the request was rejected because "
                        + "no multipart boundary was found");
            }
            byte[] boundary = contentType.substring(
                    boundaryIndex + 9).getBytes();

            InputStream input = req.getInputStream();

            MultipartStream multi = new MultipartStream(input, boundary);
            multi.setHeaderEncoding(getHeaderEncoding());
            boolean nextPart = multi.skipPreamble();
            while (nextPart) {
                Map headers = parseHeaders(multi.readHeaders());
                String fieldName = getFieldName(headers);
                if (fieldName != null) {
                    String subContentType = getHeader(headers, CONTENT_TYPE);
                    if (subContentType != null && subContentType
                            .startsWith(MULTIPART_MIXED)) {
                        // Multiple files.
                        byte[] subBoundary =
                                subContentType.substring(                      
                                        subContentType
                                                .indexOf("boundary=") + 9)
                                        .getBytes();
                        multi.setBoundary(subBoundary);
                        boolean nextSubPart = multi.skipPreamble();
                        while (nextSubPart) {
                            headers = parseHeaders(multi.readHeaders());
                            if (getFileName(headers) != null) {
                                FileItem item =
                                        createItem(headers, false);
                                OutputStream os = item.getOutputStream();

                                try {
                                    multi.readBodyData(os);
                                }
                                finally {
                                    os.close();
                                }
                            } else {
                                // Ignore anything but files inside
                                // multipart/mixed.
                                multi.discardBodyData();
                            }
                            nextSubPart = multi.readBoundary();
                        }
                        multi.setBoundary(boundary);
                    } else {
                        if (getFileName(headers) != null) {
                            // A single file.
                        	
                            if (!inputFile.patternMatched(getFileName())) {
                                inputFile.setStatus(InputFile.INVALID);
                                inputFile.getFileInfo()
                                        .setFileName(getFileName());
                                inputFile.getFileInfo().setPercent(0);
                                FileUploadException exception =
                                        new FileUploadException(
                                                "The file name ["+ getFileName() +"] does not match with the file name pattern ["+ inputFile.getFileNamePattern() +"]");
                                inputFile.getFileInfo().setException(exception);
                                throw exception;
                            }
                            FileItem item = createItem(headers, false);

                            OutputStream os = item.getOutputStream();
                            pos = new ProgressOutputStream(os, inputFile,
                                                           requestSize,
                                                           session);
                            try {
                                multi.readBodyData(pos);
                            }
                            finally {
                                os.close();
                            }

                            items.add(item);
                            inputFile.getFileInfo().setSize(item.getSize());
                            if (item.getSize() == 0) {
                                inputFile.setStatus(InputFile.INVALID);
                                inputFile.getFileInfo()
                                        .setFileName(getFileName());
                                inputFile.getFileInfo().setPercent(0);                                
                                FileUploadException exception =
                                        new FileUploadException(
                                                "This is not a valid file");
                                inputFile.getFileInfo().setException(exception);
                                inputFile.fireEvent();
                                throw exception;
                            }
                            pos.setSaved(true);
                        } else {
                            // A form field.
                            FileItem item = createItem(headers, true);

                            OutputStream os = item.getOutputStream();
                            try {
                                multi.readBodyData(os);
                            }
                            finally {
                                os.close();
                            }

                            if (item.getFieldName()
                                    .equalsIgnoreCase("fileName")) {
                                fileName = item.getString();
                                nextPart = multi.readBoundary();
                                continue;
                            }
                            items.add(item);

                            inputFile.getFileInfo().reset();
                            if (getSizeMax() >= 0 &&
                                requestSize > getSizeMax()) {
                                inputFile.setStatus(
                                        InputFile.SIZE_LIMIT_EXCEEDED);
                                FileUploadException exception =
                                        new SizeLimitExceededException(
                                                "the request was rejected because it's size exceeds allowed range");
                                inputFile.getFileInfo()
                                        .setFileName(getFileName());
                                inputFile.getFileInfo().setSize(requestSize);
                                inputFile.getFileInfo().setException(exception);
                                throw exception;
                            }

                            if (requestSize == -1) {
                                inputFile.setStatus(InputFile.UNKNOWN_SIZE);
                                FileUploadException exception =
                                        new UnknownSizeException(
                                                "the request was rejected because it's size is unknown");
                                inputFile.getFileInfo()
                                        .setFileName(getFileName());
                                inputFile.getFileInfo().setException(exception);
                                throw exception;
                            }
                        }
                    }
                } else {
                    // Skip this part.
                    multi.discardBodyData();
                }
                nextPart = multi.readBoundary();
            }
        }
        catch (IOException e) {
            throw new FileUploadException(
                    "Processing of " + MULTIPART_FORM_DATA
                    + " request failed. " + e.getMessage());
        }
        catch (FileUploadException fileExc) {
            throw fileExc;
        }
        finally {
            if (pos != null) {
                pos.stopNotification();
            }
        }
        return items;
    }


    InputFile getInputFile() {
        return inputFile;
    }

    void setInputFile(InputFile inputFile) {
        this.inputFile = inputFile;
    }

    String getFileName() {
        if (fileName == null) {
            return fileName;
        }
        String FILE_SEPARATOR = System.getProperty("file.separator");
        int fileSeparatorIndex = fileName.lastIndexOf(FILE_SEPARATOR);
        if (fileSeparatorIndex < 0) {
            //Here we have to make manuel check for IE, FILE_SEPARATOR not giving proper val with diffrent config
            fileSeparatorIndex = fileName.lastIndexOf("\\");
        }
        if (fileSeparatorIndex > 0) {
            return fileName.substring(fileSeparatorIndex + 1);
        } else {
            return fileName;
        }
    }
}
