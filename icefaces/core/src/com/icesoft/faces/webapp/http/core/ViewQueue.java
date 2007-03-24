package com.icesoft.faces.webapp.http.core;

import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;

public class ViewQueue extends LinkedBlockingQueue {
    private Runnable listener;

    public void onPut(Runnable listener) {
        this.listener = listener;
    }

    public void put(Object object) throws InterruptedException {
        if (!contains(object)) {
            super.put(object);
        }
        listener.run();
    }
}
