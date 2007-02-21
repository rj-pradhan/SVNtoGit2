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

[ Ice.Community.Connection = new Object, Ice.Connection ].as(function(This, Connection) {

    This.AsyncConnection = Object.subclass({
        initialize: function(ajax, logger, configuration, defaultQuery) {
            this.ajax = ajax;
            this.logger = logger.child('async-connection');
            this.defaultQuery = defaultQuery;
            this.onSendListeners = [];
            this.onReceiveListeners = [];
            this.onRedirectListeners = [];
            this.connectionDownListeners = [];
            this.sessionExpiredListeners = [];
            this.listener = { close: Function.NOOP };
            this.timeoutBomb = { cancel: Function.NOOP };
            this.sendURI = configuration.context + '/block/send-updates';
            this.receiveURI = configuration.context + '/block/receive-updates';

            var timeout = configuration.timeout ? configuration.timeout : 5000;
            this.onSend(function() {
                this.timeoutBomb.cancel();
                this.timeoutBomb = this.connectionDownListeners.broadcaster().delayExecutionFor(timeout);
            }.bind(this));

            this.onReceive(function() {
                this.timeoutBomb.cancel();
            }.bind(this));

            this.connect();
            this.logger.info('asynchronous mode');
        },

        connect: function() {
            this.logger.debug("closing previous connection...");
            this.listener.close();
            this.logger.debug("connect...");
            this.connectionDownBroadcaster = this.connectionDownListeners.broadcaster();
            this.listener = this.ajax.getAsynchronously(this.receiveURI, this.defaultQuery().asURIEncodedString(), function(request) {
                request.on(Connection.BadResponse, function() {
                    this.connectionDownBroadcaster();
                }.bind(this));
                request.on(Connection.Redirect, function() {
                    this.connectionDownBroadcaster = Function.NOOP;
                    this.onRedirectListeners.broadcast(request.getResponseHeader('X-REDIRECT'));
                }.bind(this));
                request.on(Connection.SessionExpired, this.sessionExpiredListeners.broadcaster());
                request.on(Connection.Receive, function() {
                    try {
                        this.onReceiveListeners.broadcast(request);
                    } catch (e) {
                        this.logger.error('receive broadcast failed', e);
                    } finally {
                        this.connect();
                    }
                }.bind(this));
            }.bind(this));
        },

        send: function(query) {

            var compoundQuery = query.addQuery(this.defaultQuery());
            this.logger.debug('send > ' + compoundQuery.asString());

            this.ajax.postAsynchronously(this.sendURI, compoundQuery.asURIEncodedString(), function(request) {
                request.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded; charset=UTF-8');
                request.on(Connection.SessionExpired, this.sessionExpiredListeners.broadcaster());
                request.on(Connection.Redirect, function() {
                    this.connectionDownBroadcaster = Function.NOOP;
                    this.onRedirectListeners.broadcast(request.getResponseHeader('X-REDIRECT'));
                }.bind(this));
                Ice.Focus.userInterupt = false;
                this.onSendListeners.broadcast(request);
            }.bind(this));
        },

        onSend: function(callback) {
            this.onSendListeners.push(callback);
            Ice.Focus.userInterupt = false;
        },

        onReceive: function(callback) {
            this.onReceiveListeners.push(callback);
        },

        onRedirect: function(callback) {
            this.onRedirectListeners.push(callback);
        },

        whenDown: function(callback) {
            this.connectionDownListeners.push(callback);
        },

        whenExpired: function(callback) {
            this.sessionExpiredListeners.push(callback);
        },

        shutdown: function() {
            this.listener.close();
            this.onSendListeners.clear();
            this.onReceiveListeners.clear();
            this.onRedirectListeners.clear();
            this.connectionDownListeners.clear();
            this.sessionExpiredListeners.clear();
        }
    });
});

