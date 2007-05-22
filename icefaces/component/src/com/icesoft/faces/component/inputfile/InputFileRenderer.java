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

import com.icesoft.faces.context.BridgeFacesContext;
import com.icesoft.faces.utils.MessageUtils;
import org.apache.commons.fileupload.FileUploadBase;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.Renderer;
import java.io.IOException;
import java.io.StringWriter;

public class InputFileRenderer extends Renderer {

    public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
        InputFile c = (InputFile) component;
        BridgeFacesContext facesContext = (BridgeFacesContext) context;
        ResponseWriter writer = context.getResponseWriter();
        StringWriter iframeContentWriter = new StringWriter();
        c.renderIFrame(iframeContentWriter, facesContext);
        String pseudoURL = "javascript: document.write('" + iframeContentWriter.toString().replaceAll("\"", "%22") + "'); document.close();";

        writer.startElement("iframe", c);
        writer.writeAttribute("src", pseudoURL, null);
        writer.writeAttribute("class", c.getStyleClass(), null);
        writer.writeAttribute("style", "overflow: hidden;", null);
        writer.writeAttribute("width", c.getWidth() + "px", null);
        writer.writeAttribute("height", c.getHeight() + "px", null);
        writer.writeAttribute("title", "Input File Frame", null);
        writer.writeAttribute("frameborder", "0", null);
        writer.writeAttribute("marginwidth", "0", null);
        writer.writeAttribute("marginheight", "0", null);
        writer.writeAttribute("scrolling", "no", null);        
        writer.endElement("iframe");

        Throwable uploadException = c.getUploadException();
        if (uploadException != null) {
            try {
                throw uploadException;
            } catch (FileUploadBase.FileSizeLimitExceededException e) {
                context.addMessage(null, MessageUtils.getMessage(context, InputFile.SIZE_LIMIT_EXCEEDED_MESSAGE_ID));
            } catch (FileUploadBase.UnknownSizeException e) {
                context.addMessage(null, MessageUtils.getMessage(context, InputFile.UNKNOWN_SIZE_MESSAGE_ID));
            } catch (FileUploadBase.InvalidContentTypeException e) {
                context.addMessage(null, MessageUtils.getMessage(context, InputFile.INVALID_FILE_MESSAGE_ID));
            } catch (Throwable t) {
                //ignore
            }
        }
    }
}