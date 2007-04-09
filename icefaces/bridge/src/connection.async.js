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

[ Ice.Community.Connection = new Object, Ice.Connection, Ice.Ajax ].as(function(This, Connection, Ajax) {

    This.AsyncConnection = Object.subclass({
        initialize: function(logger, configuration, defaultQuery) {
            this.logger = logger.child('async-connection');
            this.sendChannel = new Ajax.Client(this.logger.child('ui'));
            this.receiveChannel = new Ajax.Client(this.logger.child('blocking'));
            this.defaultQuery = defaultQuery;
            this.onSendListeners = [];
            this.onReceiveListeners = [];
            this.connectionDownListeners = [];
            this.connectionTroubleListeners = [];

            this.listener = { close: Function.NOOP };
            this.timeoutBomb = { cancel: Function.NOOP };
            this.ping = { pong: Function.NOOP };

            this.getURI = configuration.context + '/block/receive-updates';
            this.sendURI = configuration.context + '/block/send-receive-updates';
            this.receiveURI = configuration.context + '/block/receive-updated-views';

            var timeout = configuration.timeout ? configuration.timeout : 5000;
            this.onSend(function() {
                this.timeoutBomb.cancel();
                this.timeoutBomb = this.connectionDownListeners.broadcaster().delayExecutionFor(timeout);
            }.bind(this));

            this.onReceive(function() {
                this.timeoutBomb.cancel();
            }.bind(this));

            this.badResponseCallback = function() {
                this.connectionDownBroadcaster();
            }.bind(this);

            this.receiveCallback = function(response) {
                try {
                    this.onReceiveListeners.broadcast(response);
                } catch (e) {
                    this.logger.error('receive broadcast failed', e);
                }
            }.bind(this);

            //monitor if the blocking connection needs to be started
            //the blocking connection will be started by the first window that notices
            //that the blocking connection was not started or was closed because the
            //window owning was closed
            this.listenerInitializerProcess = function() {
                try {
                    this.listening = Ice.Cookie.lookup('bconn');
                    this.updatedViews = Ice.Cookie.lookup('updates');
                } catch (e) {
                    //start blocking connection since no other window has started it.
                    this.listening = new Ice.Cookie('bconn', 'started');
                    this.updatedViews = new Ice.Cookie('updates', '');
                    this.connect();
                }
            }.bind(this).repeatExecutionEvery(1000);

            //get the updates for the updated views contained within this window
            this.updatesListenerProcess = function() {
                try {
                    var views = this.updatedViews.loadValue().split(' ');
                    if (views.intersect(viewIdentifiers()).isNotEmpty()) {
                        this.sendChannel.postAsynchronously(this.getURI, this.defaultQuery().asURIEncodedString(), function(request) {
                            request.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded; charset=UTF-8');
                            request.on(Connection.Receive, this.receiveCallback);
                        }.bind(this));
                        this.updatedViews.saveValue(views.complement(viewIdentifiers()).join(' '));
                    }
                } catch (e) {
                    this.logger.warn('failed to listen for updates', e);
                }
            }.bind(this).repeatExecutionEvery(300);

            this.logger.info('asynchronous mode');
        },

        connect: function() {
            this.logger.debug("closing previous connection...");
            this.listener.close();
            this.logger.debug("connect...");
            this.connectionDownBroadcaster = this.connectionDownListeners.broadcaster();
            this.listener = this.receiveChannel.getAsynchronously(this.receiveURI, this.defaultQuery().asURIEncodedString(), function(request) {
                request.on(Connection.BadResponse, this.badResponseCallback);
                request.on(Connection.Receive, this.receiveCallback);
                request.on(Connection.Receive, this.connect.bind(this).delayFor(150));
            }.bind(this));
        },

        send: function(query) {
            var compoundQuery = query.addQuery(this.defaultQuery());
            this.logger.debug('send > ' + compoundQuery.asString());

            this.sendChannel.postAsynchronously(this.sendURI, compoundQuery.asURIEncodedString(), function(request) {
                request.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded; charset=UTF-8');
                request.on(Connection.Receive, this.receiveCallback);
                this.onSendListeners.broadcast(request);
            }.bind(this));
        },

        updateViews: function(views) {
            this.updatedViews.saveValue(views);
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
            this.connectionTroubleListeners.push(callback);
        },

        shutdown: function() {
            //avoid sending XMLHTTP requests that might create new sessions on the server
            try {
                this.send = Function.NOOP;
                this.listener.close();
            } finally {
                [ this.onSendListeners, this.onReceiveListeners, this.connectionDownListeners ].eachWithGuard(function(f) {
                    f.clear();
                });

                [ this.updatesListenerProcess, this.listenerInitializerProcess ].eachWithGuard(function(f) {
                    f.cancel();
                });

                [ this.listening, this.updatedViews ].eachWithGuard(function(f) {
                    f.remove();
                });
            }
        }
    });
});

