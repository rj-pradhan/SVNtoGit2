[ Ice.Command = new Object ].as(function(This) {

    This.Updates = function(element) {
        $enumerate(element.getElementsByTagName('update')).each(function(updateElement) {
            try {
                var address = updateElement.getAttribute('address');
                var html = updateElement.firstChild.data.replace(/<\!\#cdata\#/g, '<![CDATA[').replace(/\#\#>/g, ']]>');
                address.asExtendedElement().replaceHtml(html);
                logger.debug('applied update : ' + html);
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
        logger.warn('session has expired');
        statusManager.sessionExpired.on();
        application.dispose();
    };
});