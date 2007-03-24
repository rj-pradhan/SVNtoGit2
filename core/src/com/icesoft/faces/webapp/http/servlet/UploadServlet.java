package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.context.BridgeFacesContext;
import com.icesoft.faces.webapp.http.common.Configuration;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

import javax.faces.el.ValueBinding;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class UploadServlet implements PseudoServlet {
    private Map views;
    private long maxSize;
    private String defaultFolder;

    public UploadServlet(Map views, Configuration configuration, ServletContext servletContext) {
        this.views = views;
        this.maxSize = configuration.getAttributeAsLong("uploadMaxFileSize", 3 * 1024 * 1024);//3Mb
        this.defaultFolder = servletContext.getRealPath(configuration.getAttribute("uploadDirectory", "."));
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ServletFileUpload uploader = new ServletFileUpload();
        final ProgressCalculator progressCalculator = new ProgressCalculator();
        uploader.setFileSizeMax(maxSize);
        uploader.setProgressListener(new ProgressListener() {
            public void update(long read, long total, int chunkIndex) {
                progressCalculator.progress(read, total);
            }
        });

        FileItemIterator iter = uploader.getItemIterator(request);
        String viewIdentifier = null;
        String componentID = null;
        while (iter.hasNext()) {
            FileItemStream item = iter.next();
            if (item.isFormField()) {
                String name = item.getFieldName();
                if ("componentID".equals(name)) {
                    componentID = Streams.asString(item.openStream());
                } else if ("viewNumber".equals(name)) {
                    viewIdentifier = Streams.asString(item.openStream());
                }
            } else {
                ServletView view = (ServletView) views.get(viewIdentifier);
                view.setAsCurrentDuring(request, response);
                BridgeFacesContext context = progressCalculator.context = view.getFacesContext();
                FileUploadComponent component = (FileUploadComponent) context.getViewRoot().findComponent(componentID);
                progressCalculator.listener = component.trackProgress();
                try {
                    component.uploadFile(item.getName(), defaultFolder, item.getContentType(), item.openStream());
                } catch (IOException e) {
                    try {
                        progressCalculator.reset();
                    } catch (Throwable tr) {
                        //ignore
                    }
                    //ignore
                } catch (Throwable t) {
                    //todo: log it!
                    t.printStackTrace();
                } finally {
                    response.setContentType("text/html");
                    response.setHeader("Connection", "close");
                    component.renderIFrame(response.getWriter(), context);
                }
            }
        }
    }

    public void shutdown() {
    }

    private static class ProgressCalculator {
        private int GRANULARITY = 10;
        private ValueBinding listener;
        private BridgeFacesContext context;
        private int stepCount = 0;

        public void progress(long read, long total) {
            if (listener != null) {
                long step = total / GRANULARITY;
                if ((stepCount + 1) * step < read) {
                    int percentage = ++stepCount * 100 / GRANULARITY;
                    listener.setValue(context, new Integer(percentage));
                }
            }
        }

        public void reset() {
            ValueBinding binding = listener;
            listener = null;
            binding.setValue(context, new Integer(0));
        }
    }
}
