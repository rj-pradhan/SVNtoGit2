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
        /* the following replaces ampersand entities incorrectly decoded
           by Safari 2.0.4.  It appears to be fixed in nightly Safari builds 
        */
        url = url.replace(/&#38;/g,"&");
        logger.info('Redirecting to ' + url);
        var redirectViewNumber = url.contains('?') ? '&rvn=' : '?rvn=';
        window.location.href = url + redirectViewNumber + viewIdentifiers().first();
    };

    This.SessionExpired = function() {
        logger.warn('Session has expired');
        statusManager.sessionExpired.on();
        application.dispose();
    };

    This.SetCookie = function(message) {
        document.cookie = message.firstChild.data;
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

    var commands = [];
    This.deserializeAndExecute = function(message) {
        var messageName = message.tagName;
        for (name in commands) {
            if (name == messageName) {
                commands[messageName](message); return;
            }
        }
        
        throw 'Unknown message received: ' + messageName;
    };

    This.ParsingError = function(message) {
        logger.error('Parsing error');
        var errorNode = message.firstChild;
        logger.error(errorNode.data);
        var sourceNode = errorNode.firstChild;
        logger.error(sourceNode.data);
    };

    This.register = function(messageName, command) {
        commands[messageName] = command;
    };

    This.register('noop', Function.NOOP);
    This.register('updates', This.Updates);
    This.register('set-cookie', This.SetCookie);
    This.register('redirect', This.Redirect);
    This.register('server-error', This.ServerError);
    This.register('session-expired', This.SessionExpired);
    This.register('macro', This.Macro);
    This.register('parsererror', This.ParsingError);
});
