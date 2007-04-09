package com.icesoft.faces.webapp.command;

import java.io.IOException;
import java.io.Writer;

public class Pong implements Command {

    public Command coalesceWith(Command command) {
        return command.coalesceWith(this);
    }

    public Command coalesceWith(Macro macro) {
        macro.addCommand(this);
        return macro;
    }

    public Command coalesceWith(UpdateElements updateElements) {
        return new Macro(updateElements, this);
    }

    public Command coalesceWith(Redirect redirect) {
        return redirect;
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

    public Command coalesceWith(Pong pong) {
        return pong;
    }

    public void serializeTo(Writer writer) throws IOException {
        writer.write("<pong/>");
    }
}
