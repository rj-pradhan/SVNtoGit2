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

[ Ice.Status = new Object ].as(function(This) {

    This.ElementIndicator = Object.subclass({
        initialize: function(elementID, indicators) {
            this.elementID = elementID;
            this.indicators = indicators;
            this.indicators.push(this);
            this.off();
        },

        on: function() {
            this.indicators.each(function(indicator) {
                if (indicator != this) indicator.off();
            }.bind(this));
            this.elementID.asElement().style.visibility = 'visible';
        },

        off: function() {
            this.elementID.asElement().style.visibility = 'hidden';
        }
    });

    This.ToggleIndicator = Object.subclass({
        initialize: function(onElement, offElement) {
            this.onElement = onElement;
            this.offElement = offElement;
            this.off();
        },

        on: function() {
            this.onElement.on();
            this.offElement.off();
        },

        off: function() {
            this.onElement.off();
            this.offElement.on();
        }
    });

    This.PointerIndicator = Object.subclass({
        initialize: function(element) {
            this.element = element;
            this.previousCursor = this.element.style.cursor;
        },

        on: /Safari/.test(navigator.userAgent) ? Function.NOOP : function() {
            this.element.style.cursor = 'wait';
        },

        off: /Safari/.test(navigator.userAgent) ? Function.NOOP : function() {
            this.element.style.cursor = this.previousCursor;
        }
    });

    This.OverlayIndicator = Object.subclass({
        initialize: function(message, description, panel) {
            this.message = message;
            this.description = description;
            this.panel = panel;
        },

        on: function() {
            this.panel.on();
            messageContainer = document.createElement('div');
            messageContainer.style.position = 'absolute';
            messageContainer.style.textAlign = 'center';
            messageContainer.style.zIndex = '10001';
            messageContainer.style.color = 'black';
            messageContainer.style.backgroundColor = 'white';
            messageContainer.style.paddingLeft = '0';
            messageContainer.style.paddingRight = '0';
            messageContainer.style.paddingTop = '15px';
            messageContainer.style.paddingBottom = '15px';
            messageContainer.style.borderBottomColor = 'gray';
            messageContainer.style.borderRightColor = 'gray';
            messageContainer.style.borderTopColor = 'silver';
            messageContainer.style.borderLeftColor = 'silver';
            messageContainer.style.borderWidth = '2px';
            messageContainer.style.borderStyle = 'solid';
            messageContainer.style.width = '270px';
            document.body.appendChild(messageContainer);

            var messageElement = document.createElement('div');
            messageElement.appendChild(document.createTextNode(this.message));
            messageElement.style.marginLeft = '30px';
            messageElement.style.textAlign = 'left';
            messageElement.style.fontSize = '14px';
            messageElement.style.fontSize = '14px';
            messageElement.style.fontWeight = 'bold';
            messageContainer.appendChild(messageElement);

            var descriptionElement = document.createElement('div');
            descriptionElement.appendChild(document.createTextNode(this.description));
            descriptionElement.style.fontSize = '11px';
            descriptionElement.style.marginTop = '7px';
            descriptionElement.style.marginBottom = '7px';
            descriptionElement.style.fontWeight = 'normal';
            messageElement.appendChild(descriptionElement);

            var buttonElement = document.createElement('input');
            buttonElement.type = 'button';
            buttonElement.value = 'Reload';
            buttonElement.style.fontSize = '11px';
            buttonElement.style.fontWeight = 'normal';
            buttonElement.onclick = function() {
                window.location.reload();
            };
            messageContainer.appendChild(buttonElement);
            var resize = function() {
                messageContainer.style.left = ((window.width() - messageContainer.clientWidth) / 2) + 'px';
                messageContainer.style.top = ((window.height() - messageContainer.clientHeight) / 2) + 'px';
            }.bind(this);
            resize();
            window.onResize(resize);
            messageContainer = null;
            messageElement = null;
            descriptionElement = null;
            buttonElement = null;
        }
    });

    This.StatusManager = Object.subclass({
        initialize: function() {
            if ('connection-status'.asElement()) {
                this.indicators = [];
                var connectionWorking = new This.ElementIndicator('connection-working', this.indicators);
                var connectionIdle = new This.ElementIndicator('connection-idle', this.indicators);
                this.busy = new This.ToggleIndicator(connectionWorking, connectionIdle);
                this.connectionLost = new This.ElementIndicator('connection-lost', this.indicators);
                this.sessionExpired = this.connectionLost;
            } else {
                this.busy = new This.PointerIndicator(document.body);
                var description = 'To reconnect click the Reload button on the browser or click the button below';
                this.sessionExpired = new This.OverlayIndicator('User Session Expired', description, this);
                this.connectionLost = new This.OverlayIndicator('Network Connection Interrupted', description, this);
            }
        },

        on: function() {
            document.body.style.zIndex = '0';
            window.frames[0].document.body.style.backgroundColor = 'white';

            var panelContainer = document.getElementById('history-frame');
            panelContainer.style.position = 'absolute';
            panelContainer.style.visibility = 'visible';
            panelContainer.style.backgroundColor = 'white';
            panelContainer.style.zIndex = '10000';
            panelContainer.style.top = '0';
            panelContainer.style.left = '0';

            var resize = function() {
                panelContainer.style.width = window.width() + 'px';
                panelContainer.style.height = window.height() + 'px';
            };
            resize();
            window.onResize(resize);
        }
    });
});
