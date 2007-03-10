package com.icesoft.faces.webapp.command;

import java.io.IOException;
import java.io.Writer;

public class NOOP implements Command {

    public Command coalesceWith(Command command) {
        return command.coalesceWith(this);
    }

    public Command coalesceWith(Macro macro) {
        return macro;
    }

    public Command coalesceWith(UpdateElements updateElements) {
        return updateElements;
    }

    public Command coalesceWith(Redirect redirect) {
        return redirect;
    }

    public Command coalesceWith(SetCookie setCookie) {
        return setCookie;
    }


    public Command coalesceWith(NOOP noop) {
        return noop;
    }

    public void serializeTo(Writer writer) throws IOException {
        //do nothing!
    }
}
