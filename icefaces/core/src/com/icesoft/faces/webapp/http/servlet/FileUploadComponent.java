package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.context.BridgeFacesContext;

import javax.faces.el.ValueBinding;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

public interface FileUploadComponent {
    
    void uploadFile(String fileName, String defaultFolder, String contentType, InputStream fileContent) throws IOException;

    ValueBinding trackProgress();

    void renderIFrame(Writer writer, BridgeFacesContext context) throws IOException;
}
