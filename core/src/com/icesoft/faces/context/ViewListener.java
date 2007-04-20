package com.icesoft.faces.context;

public interface ViewListener {

    /**
     * New view has been created
     */
    void viewCreated();

    /**
     * View has been disposed either by window closing
     * or timeout.
     */
    void viewDisposed();

}
