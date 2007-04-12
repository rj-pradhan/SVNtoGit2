package com.icesoft.faces.webapp.command;

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class SetCookie implements Command {
    private final static DateFormat CookieDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss z");
    static {
        CookieDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }
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

    public Command coalesceWith(SessionExpired sessionExpired) {
        return sessionExpired;
    }

    public Command coalesceWith(SetCookie setCookie) {
        return new Macro(setCookie, this);
    }

    public Command coalesceWith(Pong pong) {
        return new Macro(pong, this);
    }

    public Command coalesceWith(NOOP noop) {
        return this;
    }

    public void serializeTo(Writer writer) throws IOException {
        writer.write("<set-cookie>");
        writer.write(cookie.getName());
        writer.write("=");
        writer.write(cookie.getValue());
        writer.write("; ");
        int maxAge = cookie.getMaxAge();
        if (maxAge >= 0) {
            Date expiryDate = new Date(System.currentTimeMillis() + maxAge * 1000);
            writer.write(CookieDateFormat.format(expiryDate));
            writer.write("; ");
        }
        String path = cookie.getPath();
        if (path != null) {
            writer.write("path=");
            writer.write(path);
            writer.write("; ");
        }
        String domain = cookie.getDomain();
        if (domain != null) {
            writer.write("domain=");
            writer.write(domain);
            writer.write("; ");
        }
        if (cookie.getSecure()) {
            writer.write("secure;");
        }
        writer.write("</set-cookie>");
    }
}
