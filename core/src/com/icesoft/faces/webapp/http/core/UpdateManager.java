package com.icesoft.faces.webapp.http.core;

import com.icesoft.faces.webapp.xmlhttp.ResponseState;
import edu.emory.mathcs.backport.java.util.concurrent.Semaphore;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Enumeration;

public class UpdateManager {
    private Semaphore semaphore = new Semaphore(1);
    private HttpSession session;

    public UpdateManager(HttpSession session) {
        this.session = session;
        this.session.setAttribute(UpdateManager.class.toString(), semaphore);
        try {
            this.semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    String[] getUpdatedViews() {
        while (true) {
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                //ignore interrupts
            }
            ResponseState[] states = getAll();
            ArrayList views = new ArrayList();
            for (int i = 0; i < states.length; i++) {
                ResponseState state = states[i];
                if (state.hasUpdates()) {
                    views.add(state.getViewNumber());
                }
            }
            if (!views.isEmpty()) {
                return (String[]) views.toArray(new String[views.size()]);
            }
        }
    }

    public void serialize(String[] views, Writer writer) throws IOException {
        writer.write("<updates>");
        for (int i = 0; i < views.length; i++) {
            ResponseState state = (ResponseState) session.getAttribute(views[i] + '/' + ResponseState.STATE);
            state.serialize(writer);
        }
        writer.write("</updates>");
    }

    ResponseState[] getAll() {
        //This method is called when the semaphore is released.
        //getAttributeNames() will throw IllegalStateException if the the session is expired
        //todo: find a better mechanism to asynchronously send a 'session expired' command to the bridge 
        Enumeration e = session.getAttributeNames();
        ArrayList states = new ArrayList();
        while (e.hasMoreElements()) {
            String name = (String) e.nextElement();
            if (name.endsWith(ResponseState.STATE)) {
                states.add(session.getAttribute(name));
            }
        }

        return (ResponseState[]) states.toArray(new ResponseState[states.size()]);
    }

    public void shutdown() {
        semaphore.release();
    }

}
