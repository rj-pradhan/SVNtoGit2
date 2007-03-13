package com.icesoft.faces.webapp.command;

import java.io.IOException;
import java.io.Writer;

public class SessionExpired implements Command {

    public Command coalesceWith(Command command) {
        return command.coalesceWith(this);
    }

    public Command coalesceWith(Redirect redirect) {
        return this;
    }

    public Command coalesceWith(Macro macro) {
        return this;
    }

    public Command coalesceWith(UpdateElements updateElements) {
        return this;
    }

    public Command coalesceWith(SessionExpired sessionExpired) {
        return this;
    }

    public Command coalesceWith(SetCookie setCookie) {
        return this;
    }

    public Command coalesceWith(NOOP noop) {
        return this;
    }

    public void serializeTo(Writer writer) throws IOException {
        writer.write("<session-expired/>");
    }
}
