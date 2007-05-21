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

[ Ice.Connection = new Object, Ice.Connection, Ice.Ajax ].as(function(This, Connection, Ajax) {
    This.FormPost = function(request) {
        request.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded; charset=UTF-8');
    };

    This.BadResponse = function(request) {
        return request.isComplete() && !request.isResponseValid();
    }

    This.Receive = function(request) {
        return request.isOkAndComplete();
    }

    This.Ok = function(request) {
        return request.isOkAndComplete();
    }

    This.Unavailable = function(request) {
        return request.isUnavailableAndComplete();
    }

    This.SyncConnection = Object.subclass({
        initialize: function(logger, configuration, defaultQuery) {
            this.logger = logger.child('sync-connection');
            this.channel = new Ajax.Client(this.logger);
            this.defaultQuery = defaultQuery;
            this.onSendListeners = [];
            this.onReceiveListeners = [];
            this.connectionDownListeners = [];
            this.timeoutBomb = { cancel: Function.NOOP };
            this.logger.info('synchronous mode');
            this.sendURI = configuration.context + 'block/send-receive-updates';
            this.disposeViewsURI = configuration.context + 'block/dispose-views';

            var timeout = configuration.timeout ? configuration.timeout : 5000;
            this.onSend(function() {
                this.timeoutBomb.cancel();
                this.timeoutBomb = this.connectionDownListeners.broadcaster().delayExecutionFor(timeout);
            }.bind(this));

            this.onReceive(function() {
                this.timeoutBomb.cancel();
            }.bind(this));

            this.whenDown(function() {
                this.timeoutBomb.cancel();
            }.bind(this));

            this.receiveCallback = function(response) {
                try {
                    this.onReceiveListeners.broadcast(response);
                } catch (e) {
                    this.logger.error('receive broadcast failed', e)
                }
            }.bind(this);

            this.badResponseCallback = this.connectionDownListeners.broadcaster();
        },

        send: function(query) {
            this.logger.debug('send > ' + query.asString());
            var compoundQuery = query.addQuery(this.defaultQuery());
            this.channel.postAsynchronously(this.sendURI, compoundQuery.asURIEncodedString(), function(request) {
                This.FormPost(request);
                request.on(Connection.Receive, this.receiveCallback);
                request.on(Connection.BadResponse, this.badResponseCallback);
                this.onSendListeners.broadcast(request);
            }.bind(this));
        },

        onSend: function(callback) {
            this.onSendListeners.push(callback);
        },

        onReceive: function(callback) {
            this.onReceiveListeners.push(callback);
        },

        whenDown: function(callback) {
            this.connectionDownListeners.push(callback);
        },

        whenTrouble: function(callback) {
            //do nothing
        },

        shutdown: function() {
            this.send = Function.NOOP;
            try {
                this.sendChannel.postAsynchronously(this.disposeViewsURI, this.defaultQuery().asURIEncodedString(), This.FormPost);
            } finally {
                [ this.onSendListeners, this.onReceiveListeners, this.connectionDownListeners ].eachWithGuard(function(f) {
                    f.clear();
                });
            }
        }
    });
});

