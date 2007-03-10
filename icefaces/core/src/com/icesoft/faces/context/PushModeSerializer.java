package com.icesoft.faces.context;

import com.icesoft.faces.util.DOMUtils;
import com.icesoft.faces.webapp.command.CommandQueue;
import com.icesoft.faces.webapp.command.UpdateElements;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.IOException;
import java.util.ArrayList;

public class PushModeSerializer implements DOMSerializer {
    private Document oldDocument;
    private CommandQueue commandQueue;

    public PushModeSerializer(Document currentDocument, CommandQueue commandQueue) {
        this.oldDocument = currentDocument;
        this.commandQueue = commandQueue;
    }

    public void serialize(Document document) throws IOException {
        Node[] changed = DOMUtils.domDiff(oldDocument, document);
        for (int i = 0; i < changed.length; i++) {
            Element changeRoot =
                    DOMUtils.ascendToNodeWithID(changed[i]);
            for (int j = 0; j < i; j++) {
                if (changed[j] == null)
                    continue;
                // If new is already in, then discard new
                if (changeRoot == changed[j]) {
                    changeRoot = null;
                    break;
                }
                // If new is parent of old, then replace old with new
                else if (isAncestor(changeRoot, changed[j])) {
                    changed[j] = null;
                }
                // If new is a child of old, then discard new
                else if (isAncestor(changed[j], changeRoot)) {
                    changeRoot = null;
                    break;
                }
            }
            changed[i] = changeRoot;
        }

        ArrayList elementList = new ArrayList(changed.length);
        for (int i = 0; i < changed.length; i++) {
            Element element = (Element) changed[i];
            if (null != element) {
                elementList.add(element);
            }
        }
        Element[] elements = (Element[]) elementList.toArray(new Element[elementList.size()]);
        commandQueue.put(new UpdateElements(elements));

        oldDocument = document;
    }

    private boolean isAncestor(Node test, Node child) {
        if (test == null || child == null)
            return false;
        if (!test.hasChildNodes()) {
            return false;
        }
        Node parent = child;
        while ((parent = parent.getParentNode()) != null) {
            if (test.equals(parent)) {
                return true;
            }
        }
        return false;
    }
}
