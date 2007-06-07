package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.application.D2DViewHandler;
import com.icesoft.faces.context.BridgeFacesContext;
import com.icesoft.faces.webapp.http.common.Configuration;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class UploadServlet implements PseudoServlet {
    private static final Log Log = LogFactory.getLog(UploadServlet.class);
    private Map views;
    private long maxSize;
    private String uploadDirectory;
    private boolean uploadDirectoryAbsolute;
    private ServletContext servletContext;

    public UploadServlet(Map views, Configuration configuration, ServletContext servletContext) {
        this.views = views;
        this.maxSize = configuration.getAttributeAsLong("uploadMaxFileSize", 3 * 1024 * 1024);//3Mb
        // ngriffin@liferay.com: Partial fix for http://jira.icefaces.org/browse/ICE-1600
        this.uploadDirectory = configuration.getAttribute("uploadDirectory", "");
        this.uploadDirectoryAbsolute = configuration.getAttributeAsBoolean("uploadDirectoryAbsolute", false);
        this.servletContext = servletContext;
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
                view.updateOnRequest(request, response);
                BridgeFacesContext context = view.getFacesContext();
                //FileUploadComponent component = (FileUploadComponent) context.getViewRoot().findComponent(componentID);
                FileUploadComponent component = (FileUploadComponent) D2DViewHandler.findComponent(componentID, context.getViewRoot());
                progressCalculator.setListenerAndContext(component, context);
                try {
                    context.setCurrentInstance();
                    component.upload(
                            item,
                            uploadDirectory,
                            uploadDirectoryAbsolute,
                            maxSize,
                            context,
                            servletContext,
                            request.getRequestedSessionId());
                } catch (IOException e) {
                    try {
                        progressCalculator.reset();
                    } catch (Throwable tr) {
                        //ignore
                    }
                } catch (Throwable t) {
                    try {
                        progressCalculator.reset();
                    } catch (Throwable tr) {
                        //ignore
                    }
                    Log.warn("File upload failed", t);
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
        private final int GRANULARITY = 10;
        private FileUploadComponent listener;
        private BridgeFacesContext context;
        private int lastGranularlyNotifiablePercent = -1;

        public void progress(long read, long total) {
            if (total > 0) {
                int percentage = (int) ((read * 100L) / total);
                int percentageAboveGranularity = percentage % GRANULARITY;
                int granularNotifiablePercentage = percentage - percentageAboveGranularity;
                boolean shouldNotify = granularNotifiablePercentage > lastGranularlyNotifiablePercent;
                lastGranularlyNotifiablePercent = granularNotifiablePercentage;
                if (shouldNotify)
                    potentiallyNotify();
            }
        }

        public void setListenerAndContext(
                FileUploadComponent listener, BridgeFacesContext context) {
            this.listener = listener;
            this.context = context;
            potentiallyNotify();
        }

        public void reset() {
            BridgeFacesContext ctx = context;
            FileUploadComponent component = listener;
            context = null;
            listener = null;
            if (ctx != null && component != null) {
                ctx.setCurrentInstance();
                component.setProgress(0);
            }
        }

        protected void potentiallyNotify() {
            if (listener != null && lastGranularlyNotifiablePercent >= 0) {
                context.setCurrentInstance();
                listener.setProgress(lastGranularlyNotifiablePercent);
            }
        }
    }
}
