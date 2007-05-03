package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.context.BridgeFacesContext;
import org.apache.commons.fileupload.FileItemStream;

import java.io.IOException;
import java.io.Writer;

public interface FileUploadComponent {
    
    void upload(FileItemStream fileItemStream, String defaultFolder, long maxSize, BridgeFacesContext bfg) throws IOException;

    void setProgress(int percentage);

    void renderIFrame(Writer writer, BridgeFacesContext context) throws IOException;
}
