package com.icesoft.faces.webapp.command;

import com.icesoft.faces.util.DOMUtils;
import org.w3c.dom.Element;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class UpdateElements implements Command {
    private final static Pattern START_CDATA = Pattern.compile("<\\!\\[CDATA\\[");
    private final static Pattern END_CDATA = Pattern.compile("\\]\\]>");
    private Element[] updates;

    public UpdateElements(Element[] updates) {
        this.updates = updates;
    }

    public Command coalesceWith(UpdateElements updateElementsCommand) {
        Set coallescedUpdates = new HashSet();
        Element[] previousUpdates = updateElementsCommand.updates;

        for (int i = 0; i < previousUpdates.length; i++) {
            Element previousUpdate = previousUpdates[i];
            Element selectedUpdate = previousUpdate;
            for (int j = 0; j < updates.length; j++) {
                Element update = updates[j];
                if (update.getAttribute("id").equals(previousUpdate.getAttribute("id"))) {
                    selectedUpdate = update;
                    break;
                }
            }
            coallescedUpdates.add(selectedUpdate);
        }
        coallescedUpdates.addAll(Arrays.asList(updates));

        return new UpdateElements((Element[]) coallescedUpdates.toArray(new Element[coallescedUpdates.size()]));
    }

    public Command coalesceWith(Command command) {
        return command.coalesceWith(this);
    }

    public Command coalesceWith(Macro macro) {
        return new Macro(macro, this);
    }

    public Command coalesceWith(Redirect redirect) {
        return new Macro(redirect, this);
    }

    public Command coalesceWith(SessionExpired sessionExpired) {
        return sessionExpired;
    }

    public Command coalesceWith(SetCookie setCookie) {
        return new Macro(setCookie, this);
    }

    public Command coalesceWith(NOOP noop) {
        return this;
    }

    public void serializeTo(Writer writer) throws IOException {
        writer.write("<updates>");
        for (int i = 0; i < updates.length; i++) {
            Element update = updates[i];
            if (update == null) continue;
            writer.write("<update address=\"");
            writer.write(update.getAttribute("id"));
            writer.write("\"><![CDATA[");
            String content = DOMUtils.nodeToString(update);
            content = START_CDATA.matcher(content).replaceAll("<!#cdata#");
            content = END_CDATA.matcher(content).replaceAll("##>");
            writer.write(content);
            writer.write("]]></update>");
        }
        writer.write("</updates>");
    }

    public String toString() {
        try {
            StringWriter writer = new StringWriter();
            serializeTo(writer);
            return writer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
