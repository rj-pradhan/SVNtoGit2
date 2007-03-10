package com.icesoft.faces.webapp.command;

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.Writer;

public class SetCookie implements Command {
    private Cookie cookie;

    public SetCookie(Cookie cookie) {
        this.cookie = cookie;
    }

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
        return new Macro(redirect, this);
    }

    public Command coalesceWith(SetCookie setCookie) {
        return new Macro(setCookie, this);
    }

    public Command coalesceWith(NOOP noop) {
        return this;
    }

    public void serializeTo(Writer writer) throws IOException {
        writer.write("<set-cookie>" + cookie.toString() + "</set-cookie>");
    }
}
