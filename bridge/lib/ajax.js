/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * "The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is ICEfaces 1.5 open source software code, released
 * November 5, 2006. The Initial Developer of the Original Code is ICEsoft
 * Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 * 2004-2006 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"
 * License), in which case the provisions of the LGPL License are
 * applicable instead of those above. If you wish to allow use of your
 * version of this file only under the terms of the LGPL License and not to
 * allow others to use your version of this file under the MPL, indicate
 * your decision by deleting the provisions above and replace them with
 * the notice and other provisions required by the LGPL License. If you do
 * not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the LGPL License."
 *
 */

[ Ice.Ajax = new Object ].as(function(This) {
    This.Client = Object.subclass({
        initialize: function(logger) {
            this.logger = logger;
            this.cookies = new Object;
            document.cookie.split('; ').each(function(cookieDetails) {
                var cookie = cookieDetails.split('=');
                this.cookies[cookie.first()] = cookie.last();
            }.bind(this));
            //determine which request factory should be used
            try {
                if (window.createRequest) {
                    this.createRequest = function() {
                        var request = new This.RequestProxy(window.createRequest(), this.logger);
                        //override 'post' method since ICEBrowser cannot send 'POST' requests properly.
                        request.post = function(asynchronous, path, query, requestConfigurator) {
                            this.get(asynchronous, path, query, requestConfigurator);
                        };
                        return request;
                    }.bind(this);
                } else if (window.XMLHttpRequest) {
                    this.createRequest = function() {
                        return new This.RequestProxy(new XMLHttpRequest(), this.logger);
                    }.bind(this);
                } else if (window.ActiveXObject) {
                    this.createRequest = function() {
                        return new This.RequestProxy(new ActiveXObject('Microsoft.XMLHTTP'), this.logger);
                    }.bind(this);
                }
            } catch (e) {
                this.logger.error('failed to create factory request', e);
            }
        },

        getAsynchronously: function(path, query, requestConfigurator) {
            return this.createRequest().getAsynchronously(path, query, requestConfigurator);
        },

        getSynchronously: function(path, query, requestConfigurator) {
            return this.createRequest().getSynchronously(path, query, requestConfigurator);
        },

        postAsynchronously: function(path, query, requestConfigurator) {
            return this.createRequest().postAsynchronously(path, query, requestConfigurator);
        },

        postSynchronously: function(path, query, requestConfigurator) {
            return this.createRequest().postSynchronously(path, query, requestConfigurator);
        }
    });


    This.ResponseCallback = Object.subclass({
        initialize: function(testFunction, handlerFunction) {
            this.testFunction = testFunction;
            this.handlerFunction = handlerFunction;
        },

        execute: function(request) {
            if (this.testFunction(request)) this.handlerFunction(request);
        }
    });

    This.RequestProxy = Object.subclass({
        initialize: function(request, logger) {
            this.identifier =  + Math.random().toString().substr(2, 7);
            this.request = request;
            this.logger = logger;
            this.callbacks = [];
            this.responseCallback = function() {
                if (this.isComplete()) {
                    this.logger.debug('[' + this.identifier + '] : receive [' + this.request.status + '] ' + this.request.statusText);
                }
                this.callbacks.each(function(callback) {
                    try {
                        callback.execute(this);
                    } catch (e) {
                        this.logger.error('failed to respond', e);
                    }
                }.bind(this));
            }.bind(this);
        },

        on: function(test, handler) {
            this.callbacks.push(new This.ResponseCallback(test, handler));
        },

        isComplete: function() {
            return this.request.readyState == 4;
        },

        isResponseValid: function() {
            try {
                return this.request.status >= 0;
            } catch (e) {
                return false;
            }
        },

        isOk: function() {
            try {
                return this.request.status == 200;
            } catch (e) {
                return false;
            }
        },

        isUnavailable: function() {
            try {
                return this.request.status == 503;
            } catch (e) {
                return false;
            }
        },

        isOkAndComplete: function() {
            return this.isComplete() && this.isOk();
        },

        isUnavailableAndComplete: function() {
            return this.isComplete() && this.isUnavailable();
        },

        getAsynchronously: function(path, query, requestConfigurator) {
            //the 'rand' parameter is used to force IE into creating new request object, thus avoiding potential infinite loops.
            this.request.open('GET', path + '?' + query + '&rand=' + Math.random(), true);
            if (requestConfigurator) requestConfigurator(this);
            this.request.onreadystatechange = this.responseCallback;
            this.logger.debug('[' + this.identifier + '] : send asynchronous GET');
            this.request.send('');
            return this;
        },

        postAsynchronously:  function(path, query, requestConfigurator) {
            this.request.open('POST', path, true);
            if (requestConfigurator) requestConfigurator(this);
            this.request.onreadystatechange = this.responseCallback;
            Ice.Focus.userInterupt = false;
            //the 'rand' parameter is used to force Firefox to commit the response to the server.
            this.logger.debug('[' + this.identifier + '] : send asynchronous POST');
            this.request.send(query + '&rand=' + Math.random() + '\n\n');
            return this;
        },

        getSynchronously: function(path, query, requestConfigurator) {
            //the 'rand' parameter is used to force IE into creating new request object, thus avoiding potential infinite loops.
            this.request.open('GET', path + '?' + query + '&rand=' + Math.random(), false);
            if (requestConfigurator) requestConfigurator(this);
            this.logger.debug('[' + this.identifier + '] : send synchronous GET');
            this.request.send('');
            this.responseCallback();
            return this;
        },

        postSynchronously:  function(path, query, requestConfigurator) {
            this.request.open('POST', path, false);
            if (requestConfigurator) requestConfigurator(this);
            //the 'rand' parameter is used to force Firefox to commit the response to the server.
            this.logger.debug('[' + this.identifier + '] : send synchronous POST');
            Ice.Focus.userInterupt = false;
            this.request.send(query + '&rand=' + Math.random() + '\n\n');
            this.responseCallback();
            return this;
        },

        setRequestHeader: function(name, value) {
            this.request.setRequestHeader(name, value);
        },

        getResponseHeader: function(name) {
            try {
                return this.request.getResponseHeader(name);
            } catch (e) {
                return null;
            }
        },

        containsResponseHeader: function(name) {
            try {
                var header = this.request.getResponseHeader(name);
                return header && header != '';
            } catch (e) {
                return false;
            }
        },

        content: function() {
            try {
                return this.request.responseText;
            } catch (e) {
                return '';
            }
        },

        contentAsDOM: function() {
            return this.request.responseXML;
        },

        close: function() {
            //replace the callback to avoid infinit loop since the callback is
            //executed also when the connection is aborted.
            this.request.onreadystatechange = Function.NOOP;
            this.request.abort();
            //avoid potential memory leaks since 'this.request' is a native object 
            this.request = null;
            this.callbacks = null;
            this.logger.debug('[' + this.identifier + '] : connection closed');
        }
    });
});