[ Ice.Document ].as(function(This) {

    This.Update = function(updateElement) {
        try {
            var address = updateElement.getAttribute('address');
            var html = updateElement.firstChild.data.replace(/<\!\#cdata\#/g, '<![CDATA[').replace(/\#\#>/g, ']]>');

            address.asExtendedElement().replaceHtml(html);
            logger.debug('applied update : ' + html);
        } catch (e) {
            logger.error('failed to insert element: ' + html, e);
        }
    }
});