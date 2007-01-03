package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.http.Server;
import com.icesoft.faces.webapp.http.Request;
import com.icesoft.faces.webapp.http.Configuration;

import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.FactoryFinder;

public class ServePage implements Server {
    private final static LifecycleFactory LIFECYCLE_FACTORY = (LifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
    private Lifecycle lifecycle = LIFECYCLE_FACTORY.getLifecycle(LIFECYCLE_FACTORY.DEFAULT_LIFECYCLE);
    private boolean concurrentDOMViews;

    public ServePage(Configuration configuration) {
        concurrentDOMViews = configuration.getAttributeAsBoolean("concurrentDOMViews", false);
    }

    public void service(Request request) throws Exception {
    }
}
