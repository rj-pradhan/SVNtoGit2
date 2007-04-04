[ Ice.Command = new Object ].as(function(This) {

    This.Updates = function(element) {
        $enumerate(element.getElementsByTagName('update')).each(function(updateElement) {
            try {
                var address = updateElement.getAttribute('address');
                var html = updateElement.firstChild.data.replace(/<\!\#cdata\#/g, '<![CDATA[').replace(/\#\#>/g, ']]>');
                address.asExtendedElement().replaceHtml(html);
                logger.debug('applied update : ' + html);
                window.scriptLoader.searchAndEvaluateScripts(address.asElement());
            } catch (e) {
                logger.error('failed to insert element: ' + html, e);
            }
        });
    };

    This.Redirect = function(element) {
        var url = element.getAttribute("url");
        logger.info('Redirecting to ' + url);
        var redirectViewNumber = url.contains('?') ? '&rvn=' : '?rvn=';
        window.location.href = url + redirectViewNumber + viewIdentifiers().first();
    };

    This.SessionExpired = function() {
        logger.warn('Session has expired');
        statusManager.sessionExpired.on();
        application.dispose();
    };

    This.ServerError = function(message) {
        logger.error('Server side error');
        logger.error(message.firstChild.data);
        statusManager.serverError.on();
        application.dispose();
    };

    This.Macro = function(message) {
        $enumerate(message.childNodes).each(function(subMessage) {
            This.deserializeAndExecute(subMessage);
        });
    };

    This.deserializeAndExecute = function(message) {
        switch (message.tagName) {
            case 'noop': /*do nothing*/; break;
            case 'updates': This.Updates(message); break;
            case 'redirect': This.Redirect(message); break;
            case 'server-error': This.ServerError(message); break;
            case 'session-expired': This.SessionExpired(message); break;
            case 'macro': This.Macro(message); break;
            default: throw 'Unknown message received: ' + message.tagName;
        }
    };
});