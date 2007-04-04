[ Ice.Script = new Object, Ice.Ajax.Client ].as(function(This, Client) {

    //todo: should this code be part of Element.replaceHtml method?    
    This.Loader = Object.subclass({
        initialize: function(logger) {
            this.logger = logger.child('script-loader');
            this.referencedScripts = [];//list of urls
            this.client = new Client(this.logger);
            //the scripts present on the page are already evaluated
            $enumerate(document.documentElement.getElementsByTagName('script')).each(function(script) {
                if (script.src) this.referencedScripts.push(script.src);
            }.bind(this));
        },

        searchAndEvaluateScripts: function(element) {
            $enumerate(element.getElementsByTagName('script')).each(this.evaluateScript.bind(this));
        },

        evaluateScript: function(script) {
            var uri = script.src;
            if (uri) {
                if (!this.referencedScripts.include(script.src)) {
                    this.logger.debug('loading : ' + uri);
                    this.client.getAsynchronously(uri, '', function(request) {
                        request.on(Ice.Connection.Ok, function() {
                            this.referencedScripts.push(uri);
                            this.logger.debug('evaluating script at : ' + uri);
                            try {
                                eval(request.content());
                            } catch (e) {
                                this.logger.warn('Failed to evaluate script located at: ' + uri, e);
                            }
                        }.bind(this));
                    }.bind(this));
                }
            } else {
                var code = script.text;
                this.logger.debug('evaluating script : ' + code);
                try{
                    eval(code);
                } catch (e) {
                    this.logger.warn('Failed to evaluate script: \n' + code, e);
                }
            }
        }
    });
});