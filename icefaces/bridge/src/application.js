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

[ Ice.Community ].as(function(This) {

    This.Application = Object.subclass({
        initialize: function() {
            var logger = window.logger = this.logger = new Ice.Log.Logger([ 'window' ]);
            this.logHandler = window.console && window.console.firebug ? new Ice.Log.FirebugLogHandler(logger) : new Ice.Log.WindowLogHandler(logger, window);
            var documentSynchronizer = new Ice.Document.Synchronizer(logger);
            window.statusManager = new Ice.Status.StatusManager();

            window.identifier = Math.round(Math.random() * 10000).toString();
            window.connection = this.connection = configuration.synchronous ? new Ice.Connection.SyncConnection(logger, configuration.connection, defaultParameters) : new This.Connection.AsyncConnection(logger, configuration.connection, defaultParameters);

            window.onKeyPress(function(e) {
                if (e.isEscKey()) e.cancelDefaultAction();
            });

            this.connection.onSend(function() {
                Ice.Focus.userInterupt = false;
            });

            this.connection.onReceive(function(request) {
                Ice.Command.deserializeAndExecute(request.contentAsDOM().documentElement);
                Ice.RedirectFormSubmits();
            }.bind(this));

            this.connection.onReceive(function() {
                documentSynchronizer.synchronize();
            });

            this.connection.whenDown(function() {
                logger.warn('connection to server was lost');
                statusManager.connectionLost.on();
                this.dispose();
            }.bind(this));

            this.connection.onSend(function(request) {
                statusManager.busy.on();
            });

            this.connection.onReceive(function(request) {
                statusManager.busy.off();
            });

            this.logger.info('page loaded!');
        },

        dispose: function() {
            this.connection.shutdown();
            this.logger.info('page unloaded!');
            this.logHandler.disable();
            this.dispose = Function.NOOP;
        }
    });

    window.onLoad(function() {
        try {
            this.application = new This.Application;
        } catch(ignore) {
            if (console)
                console.error(ignore);
        }
    });

    window.onUnload(function() {
        try {
            this.application.dispose();
        } catch(ignore) {
            if (console)
                console.error(ignore);
        }
    });
});