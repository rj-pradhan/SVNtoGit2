package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.common.Server;
import com.icesoft.faces.webapp.http.servlet.ServletView;

import java.util.Map;

public class DisposeViews implements Server {
    private Map views;

    public DisposeViews(Map views) {
        this.views = views;
    }

    public void service(Request request) throws Exception {
        String[] viewIdentifiers = request.getParameterAsStrings("viewNumber");
        for (int i = 0; i < viewIdentifiers.length; i++) {
            String viewIdentifier = viewIdentifiers[i];
            //todo: remove dependency on com.icesoft.faces.webapp.http.servlet package
            ServletView view = (ServletView) views.remove(viewIdentifier);
            view.dispose();
        }
    }

    public void shutdown() {
    }
}