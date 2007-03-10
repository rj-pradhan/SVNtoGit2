package com.icesoft.faces.webapp.command;

import java.io.IOException;
import java.io.Writer;
import java.net.URI;

public class Redirect implements Command {
    private URI uri;

    public Redirect(URI uri) {
        this.uri = uri;
    }

    public Redirect(String uri) {
        this.uri = URI.create(uri);
    }

    public Command coalesceWith(Command command) {
        return command.coalesceWith(this);
    }

    public Command coalesceWith(Macro macro) {
        return this;
    }

    public Command coalesceWith(UpdateElements updateElements) {
        return this;
    }

    public Command coalesceWith(Redirect redirect) {
        return this;
    }

    public Command coalesceWith(SetCookie setCookie) {
        return new Macro(setCookie, this);
    }

    public Command coalesceWith(NOOP noop) {
        return this;
    }

    public void serializeTo(Writer writer) throws IOException {
        writer.write("<redirect url=\"" + uri + "\"/>");
    }
}
