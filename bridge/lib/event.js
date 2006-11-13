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

[ Ice.EventModel = new Object, Ice.ElementModel.Element, Ice.Parameter.Query, Ice.Geometry ].as(function(This, Element, Query, Geometry) {
    This.IE = new Object;
    This.Netscape = new Object;

    This.Event = Object.subclass({
        initialize: function(event) {
            this.event = event;
        },

        on: function(modifier) {
            modifier.apply(this);
        },

        cancel: function() {
            this.cancelBubbling();
            this.cancelDefaultAction();
        },

        isKeyEvent: function() {
            return false;
        },

        isMouseEvent: function() {
            return false;
        },

        serializeEventOn: function(query) {
            query.add('ice.event.target', this.target().id());
            query.add('ice.event.type', 'on' + this.event.type);
        },

        serializeOn: function(query) {
            this.serializeEventOn(query);
        },

        sendOn: function(connection) {
            Query.create(function(query) {
                this.serializeOn(query);
                //todo: when ICEFaces will not require enclosing forms sendOn should serialize just the element.  
                try {
                    //try to serialize the entire form.
                    this.target().form().serializeOn(query);
                } catch (e) {
                    //no enclosing form. serialize just the element.
                    this.target().serializeOn(query);
                }
                "partial".associateWith('true').serializeOn(query);
                "focus".associateWith(this.target().id()).serializeOn(query);
            }.bind(this)).sendOn(connection);
        },

        sendFullOn: function(connection) {
            Query.create(function(query) {
                this.serializeOn(query);
                try {
                    //try to serialize the entire form.
                    this.target().form().serializeOn(query);
                } catch (e) {
                    //no enclosing form. serialize just the element.
                    this.target().serializeOn(query);
                }
                "partial".associateWith('false').serializeOn(query);
                "focus".associateWith(this.target().id()).serializeOn(query);
            }.bind(this)).sendOn(connection);
        },

        sendWithCondition: function(condition) {
            if (condition(this)) this.send();
        },

        send: function() {
            this.cancel();
            this.sendOn(connection);
        },

        sendFull: function() {
            this.cancel();
            this.sendFullOn(connection);
        }
    });

    This.IE.Event = This.Event.subclass({
        target: function() {
            return Element.adaptToElement(this.event.srcElement);
        },

        cancelBubbling: function() {
            this.event.cancelBubble = true;
        },

        cancelDefaultAction: function() {
            this.event.returnValue = false;
        }
    });

    This.Netscape.Event = This.Event.subclass({
        target: function() {
            return Element.adaptToElement(this.event.target);
        },

        cancelBubbling: function() {
            this.event.stopPropagation();
        },

        cancelDefaultAction: function() {
            this.event.preventDefault();
        }
    });

    var KeyAndMouseEventMethods = {
        isAltPressed: function() {
            return this.event.altKey;
        },

        isCtrlPressed: function() {
            return this.event.ctrlKey;
        },

        isShiftPressed: function() {
            return this.event.shiftKey;
        },

        isMetaPressed: function() {
            return this.event.metaKey;
        },

        serializeKeyAndMouseEventOn: function(query) {
            query.add('ice.event.alt', this.isAltPressed());
            query.add('ice.event.ctrl', this.isCtrlPressed());
            query.add('ice.event.shift', this.isShiftPressed());
            query.add('ice.event.meta', this.isMetaPressed());
        }
    };

    var MouseEventMethods = {
        isMouseEvent: function() {
            return true;
        },

        serializeOn: function(query) {
            this.serializeEventOn(query);
            this.serializeKeyAndMouseEventOn(query);
            this.pointer().serializeOn(query);
            query.add('ice.event.left', this.isLeftButton());
            query.add('ice.event.right', this.isRightButton());
        }
    };

    This.IE.MouseEvent = This.IE.Event.subclass({
        pointer: function() {
            return new Geometry.Point(this.event.clientX + (document.documentElement.scrollLeft || document.body.scrollLeft), this.event.clientY + (document.documentElement.scrollTop || document.body.scrollTop));
        },

        isLeftButton: function() {
            return this.event.button == 1;
        },

        isRightButton: function() {
            return this.event.button == 2;
        }
    });
    This.IE.MouseEvent.methods(KeyAndMouseEventMethods);
    This.IE.MouseEvent.methods(MouseEventMethods);

    This.Netscape.MouseEvent = This.Netscape.Event.subclass({
        pointer: function() {
            return new Geometry.Point(this.event.pageX, this.event.pageY);
        },

        isLeftButton: function() {
            return this.event.which == 1;
        },

        isRightButton: function() {
            return this.event.which == 3;
        }
    });
    This.Netscape.MouseEvent.methods(KeyAndMouseEventMethods);
    This.Netscape.MouseEvent.methods(MouseEventMethods);

    var KeyEventMethods = {
        keyCharacter: function() {
            return String.fromCharCode(this.keyCode());
        },

        isEnterKey: function() {
            return this.keyCode() == 13;
        },

        isEscKey: function() {
            return this.keyCode() == 27;
        },

        isBackspaceKey: function() {
            return this.keyCode() == 8;
        },

        isDeleteKey: function() {
            return this.keyCode() == 46 || /*safari*/this.keyCode() == 63272;
        },

        isSpaceKey: function() {
            return this.keyCode() == 32;
        },

        isTabKey: function() {
            return this.keyCode() == 9 || /*safari*/(this.isShiftPressed() && this.keyCode() == 25);
        },

        isHomeKey: function() {
            return this.keyCode() == 36 || /*safari*/this.keyCode() == 63273;
        },

        isEndKey: function() {
            return this.keyCode() == 35 || /*safari*/this.keyCode() == 63275;
        },

        isPageUpKey: function() {
            return this.keyCode() == 33 || /*safari*/this.keyCode() == 63276;
        },

        isPageDownKey: function() {
            return this.keyCode() == 34 || /*safari*/this.keyCode() == 63277;
        },

        isArrowUpKey: function() {
            return this.keyCode() == 38 || /*safari*/this.keyCode() == 63232;
        },

        isArrowDownKey: function() {
            return this.keyCode() == 40 || /*safari*/this.keyCode() == 63233;
        },

        isArrowLeftKey: function() {
            return this.keyCode() == 37 || /*safari*/this.keyCode() == 63234;
        },

        isArrowRightKey: function() {
            return this.keyCode() == 39 || /*safari*/this.keyCode() == 63235;
        },

        isKeyEvent: function() {
            return true;
        },

        serializeOn: function(query) {
            this.serializeEventOn(query);
            this.serializeKeyAndMouseEventOn(query);
            query.add('ice.event.keycode', this.keyCode());
        }
    };

    This.IE.KeyEvent = This.IE.Event.subclass({
        keyCode: function() {
            return this.event.keyCode;
        }
    });
    This.IE.KeyEvent.methods(KeyAndMouseEventMethods);
    This.IE.KeyEvent.methods(KeyEventMethods);

    This.Netscape.KeyEvent = This.Netscape.Event.subclass({
        keyCode: function() {
            return this.event.which == 0 ? this.event.keyCode : this.event.which;
        }
    });
    This.Netscape.KeyEvent.methods(KeyAndMouseEventMethods);
    This.Netscape.KeyEvent.methods(KeyEventMethods);

    This.Event.adaptToPlainEvent = function(e) {
        return window.event ? new This.IE.Event(event) : new This.Netscape.Event(e);
    };

    This.Event.adaptToMouseEvent = function(e) {
        return window.event ? new This.IE.MouseEvent(event) : new This.Netscape.MouseEvent(e);
    };

    This.Event.adaptToKeyEvent = function(e) {
        return window.event ? new This.IE.KeyEvent(event) : new This.Netscape.KeyEvent(e);
    };

    This.Event.adaptToEvent = function(e) {
        var eventType = (window.event || e).type;
        var detector = function (name) {
            return name.toLowerCase() == 'on' + eventType;
        };
        if (Element.prototype.KeyListenerNames.detect(detector)) {
            return This.Event.adaptToKeyEvent(e);
        } else if (Element.prototype.MouseListenerNames.detect(detector)) {
            return This.Event.adaptToMouseEvent(e);
        } else {
            return This.Event.adaptToPlainEvent(e);
        }
    };

    //public call
    window.$event = This.Event.adaptToEvent;
});
