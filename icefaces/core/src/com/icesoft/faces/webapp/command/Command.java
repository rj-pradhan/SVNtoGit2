package com.icesoft.faces.webapp.command;

import java.io.IOException;
import java.io.Writer;

public interface Command {

    Command coalesceWith(Command command);

    Command coalesceWith(Macro macro);

    Command coalesceWith(UpdateElements updateElements);

    Command coalesceWith(Redirect redirect);

    Command coalesceWith(SessionExpired sessionExpired);

    Command coalesceWith(SetCookie setCookie);

    Command coalesceWith(NOOP noop);

    void serializeTo(Writer writer) throws IOException;
}
