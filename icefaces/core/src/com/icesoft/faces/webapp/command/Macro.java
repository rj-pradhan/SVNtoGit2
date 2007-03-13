package com.icesoft.faces.webapp.command;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Macro implements Command {
    private Collection commands = new ArrayList();

    public Macro(Command commandA, Command commandB) {
        commands.add(commandA);
        commands.add(commandB);
    }

    public Command coalesceWith(Command command) {
        return command.coalesceWith(this);
    }

    public Command coalesceWith(UpdateElements updateElements) {
        commands.add(updateElements);
        return this;
    }

    public Command coalesceWith(Redirect redirect) {
        commands.add(redirect);
        return this;
    }

    public Command coalesceWith(SessionExpired sessionExpired) {
        return sessionExpired;
    }

    public Command coalesceWith(Macro macro) {
        commands.addAll(macro.commands);
        return this;
    }

    public void addCommand(Command command) {
        commands.add(command);
    }

    public Command coalesceWith(SetCookie setCookie) {
        commands.add(setCookie);
        return this;
    }

    public Command coalesceWith(NOOP noop) {
        return this;
    }

    public void serializeTo(Writer writer) throws IOException {
        Iterator i = commands.iterator();
        writer.write("<macro>");
        while (i.hasNext()) {
            Command command = (Command) i.next();
            command.serializeTo(writer);
        }
        writer.write("</macro>");
    }
}
