package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.context.BridgeFacesContext;
import org.apache.commons.fileupload.FileItemStream;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.Writer;

public interface FileUploadComponent {
    
    void upload(FileItemStream fileItemStream, String uploadDirectory, boolean uploadDirectoryAbsolute, long maxSize, BridgeFacesContext bfg, ServletContext servletContext, String sessionId) throws IOException;

    void setProgress(int percentage);

    void renderIFrame(Writer writer, BridgeFacesContext context) throws IOException;
}
