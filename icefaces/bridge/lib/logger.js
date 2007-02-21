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

[ Ice.Log = new Object ].as(function(This) {
    This.Priority = Object.subclass({
        debug: function(logger, message, exception) {
            logger.log(This.Priority.DEBUG, message, exception);
        },

        info: function(logger, message, exception) {
            logger.log(This.Priority.INFO, message, exception);
        },

        warn: function(logger, message, exception) {
            logger.log(This.Priority.WARN, message, exception);
        },

        error: function(logger, message, exception) {
            logger.log(This.Priority.ERROR, message, exception);
        },

        asString: function() {
            return this.name;
        }
    });

    This.Debug = This.Priority.subclass({
        initialize: function() {
            this.name = 'debug';
            this.color = '#333';
        }
    });

    This.Info = This.Debug.subclass({
        initialize: function() {
            this.name = 'info';
            this.color = 'green';
        },

        debug: Function.NOOP
    });

    This.Warn = This.Info.subclass({
        initialize: function() {
            this.name = 'warn';
            this.color = 'orange';
        },

        info: Function.NOOP
    });

    This.Error = This.Warn.subclass({
        initialize: function() {
            this.name = 'error';
            this.color = 'red';
        },

        warn: Function.NOOP
    });

    This.Priority.DEBUG = new This.Debug;
    This.Priority.INFO = new This.Info;
    This.Priority.WARN = new This.Warn;
    This.Priority.ERROR = new This.Error;
    This.Priority.Levels = [ This.Priority.DEBUG, This.Priority.INFO, This.Priority.WARN, This.Priority.ERROR ];

    This.Event = Object.subclass({
        initialize: function(category, priority, message, exception) {
            this.timestamp = new Date();
            this.category = category;
            this.priority = priority;
            this.message = message;
            this.exception = exception;
        },

        asString: function() {
            return this.timestamp.toTimestamp() + ' ' +
                   this.priority.asString() + ' \t[' +
                   this.category.join('.') + '] : ' +
                   this.message +
                   (this.exception ? ('\n' + this.exception) : '');
        },

        asNodeIn: function(element) {
            var elementDocument = element.ownerDocument;
            this.asString().split('\n').each(function(line) {
                if (line.containsWords()) {
                    var eventNode = elementDocument.createElement('div');
                    eventNode.style.padding = '3px';
                    eventNode.style.color = this.priority.color;
                    element.appendChild(eventNode).appendChild(elementDocument.createTextNode(line));
                }
            }.bind(this));
        }
    });

    //alias for IE
    This.Event.prototype.toString = This.Event.prototype.asString;

    This.Logger = Object.subclass({
        initialize: function(category, handler, priority) {
            this.handler = handler || { handle: Function.NOOP };
            this.category = category;
            this.children = [];
            this.priority = priority || This.Priority.ERROR;
        },

        log: function(priority, message, exception) {
            this.handler.handle(new This.Event(this.category, priority, message, exception));
        },

        debug: function(message, exception) {
            this.priority.debug(this, message, exception);
        },

        info: function(message, exception) {
            this.priority.info(this, message, exception);
        },

        warn: function(message, exception) {
            this.priority.warn(this,  message, exception);
        },

        error: function(message, exception) {
            this.priority.error(this, message, exception);
        },

        child: function(category) {
            var childCategory = this.category.copy();
            childCategory.push(category);
            var child = new This.Logger(childCategory, this.handler);
            this.children.push(child);
            return child;
        },

        threshold: function(thresholdPriority) {
            this.priority = thresholdPriority;
            this.children.each(function(logger) {
                logger.threshold(thresholdPriority);
            })
        },

        handleWith: function(handler) {
            this.handler = handler;
        }
    });

    This.WindowLogHandler = Object.subclass({
        initialize: function(logger, parentWindow, lines, thresholdPriority) {
            this.lineOptions = [ 25, 50, 100, 200, 400 ];
            this.logger = logger;
            this.logger.handleWith(this);
            this.parentWindow = parentWindow;
            this.lines = lines || this.lineOptions[3];
            this.thresholdPriority = thresholdPriority || This.Priority.DEBUG;
            this.categoryMatcher = /.*/;
            this.closeOnExit = true;
            this.noopHandle = Function.NOOP;
            this.opHandle = function(event) {
                if (this.categoryMatcher.test(event.category.join('.'))) {
                    event.asNodeIn(this.log);
                    this.log.scrollTop = this.log.scrollHeight;
                }
                this.clearPreviousEvents();
            };
            this.handle = this.noopHandle;

            this.parentWindow.onKeyPress(function(e) {
                var key = e.keyCode();
                if ((key == 20 || key == 84) && e.isCtrlPressed() && e.isShiftPressed()) {
                    this.enable();
                }
            }.bind(this));
        },

        clearPreviousEvents: function() {
            var nodes = $A(this.log.childNodes);
            nodes.copyFrom(0, nodes.length - this.lines).each(function(node) {
                this.log.removeChild(node)
            }.bind(this));
        },

        clearAllEvents: function() {
            $A(this.log.childNodes).each(function(node) {
                this.log.removeChild(node)
            }.bind(this));
        },

        enable: function() {
            try {
                this.window = this.parentWindow.open('', 'log', 'scrollbars=1,width=800,height=680');
                var windowDocument = this.window.document;
                //todo: create container for the controlls
                this.log = this.window.document.getElementById('log-window');
                if (this.log) return;

                windowDocument.body.appendChild(windowDocument.createTextNode(' Close on exit '));
                var closeOnExitCheckbox = windowDocument.createElement('input');
                closeOnExitCheckbox.style.margin = '2px';
                closeOnExitCheckbox.setAttribute('type', 'checkbox');
                closeOnExitCheckbox.defaultChecked = true;
                closeOnExitCheckbox.checked = true;
                closeOnExitCheckbox.onclick = function() {
                    this.closeOnExit = closeOnExitCheckbox.checked;
                }.bind(this);
                windowDocument.body.appendChild(closeOnExitCheckbox);

                windowDocument.body.appendChild(windowDocument.createTextNode(' Lines '));
                var lineCountDropDown = windowDocument.createElement('select');
                lineCountDropDown.style.margin = '2px';
                this.lineOptions.each(function(count, index) {
                    var option = lineCountDropDown.appendChild(windowDocument.createElement('option'));
                    if (this.lines == count) lineCountDropDown.selectedIndex = index;
                    option.appendChild(windowDocument.createTextNode(count.toString()));
                }.bind(this));

                lineCountDropDown.onchange = function(event) {
                    this.lines = this.lineOptions[lineCountDropDown.selectedIndex];
                    this.clearPreviousEvents();
                }.bind(this);
                windowDocument.body.appendChild(lineCountDropDown);

                windowDocument.body.appendChild(windowDocument.createTextNode(' Category '));
                var categoryInputText = windowDocument.createElement('input');
                categoryInputText.style.margin = '2px';
                categoryInputText.setAttribute('type', 'text');
                categoryInputText.setAttribute('value', this.categoryMatcher.source);
                categoryInputText.onchange = function() {
                    this.categoryMatcher = categoryInputText.value.asRegexp();
                }.bind(this);
                windowDocument.body.appendChild(categoryInputText);


                windowDocument.body.appendChild(windowDocument.createTextNode(' Level '));
                var levelDropDown = windowDocument.createElement('select');
                levelDropDown.style.margin = '2px';
                This.Priority.Levels.each(function(priority, index) {
                    var option = levelDropDown.appendChild(windowDocument.createElement('option'));
                    if (this.thresholdPriority == priority) levelDropDown.selectedIndex = index;
                    option.appendChild(windowDocument.createTextNode(priority.asString()));
                }.bind(this));

                this.logger.threshold(this.thresholdPriority);
                levelDropDown.onchange = function(event) {
                    this.thresholdPriority = This.Priority.Levels[levelDropDown.selectedIndex];
                    this.logger.threshold(this.thresholdPriority);
                }.bind(this);
                windowDocument.body.appendChild(levelDropDown);

                var startStopButton = windowDocument.createElement('input');
                startStopButton.style.margin = '2px';
                startStopButton.setAttribute('type', 'button');
                startStopButton.setAttribute('value', 'Stop');
                startStopButton.onclick = function() {
                    startStopButton.setAttribute('value', this.toggle() ? 'Stop' : 'Start');
                }.bind(this);
                windowDocument.body.appendChild(startStopButton);

                var clearButton = windowDocument.createElement('input');
                clearButton.style.margin = '2px';
                clearButton.setAttribute('type', 'button');
                clearButton.setAttribute('value', 'Clear');
                clearButton.onclick = function() {
                    this.clearAllEvents();
                }.bind(this);
                windowDocument.body.appendChild(clearButton);

                this.log = windowDocument.body.appendChild(windowDocument.createElement('pre'));
                this.log.id = 'log-window';
                this.log.style.width = '100%';
                this.log.style.minHeight = '0';
                this.log.style.maxHeight = '550px';
                this.log.style.borderWidth = '1px';
                this.log.style.borderStyle = 'solid';
                this.log.style.borderColor = '#999';
                this.log.style.backgroundColor = '#ddd';
                this.log.style.overflow = 'scroll';

                this.window.onunload = function() {
                    this.disable();
                }.bind(this);

                this.handle = this.opHandle;
            } catch (e) {
                this.disable();
            }
        },

        disable: function() {
            this.logger.threshold(This.Priority.ERROR);
            this.handle = this.noopHandle;
            if (this.closeOnExit && this.window) this.window.close();
        },

        toggle: function() {
            if (this.handle == this.noopHandle) {
                this.handle = this.opHandle;
                return true;
            } else {
                this.handle = this.noopHandle;
                return false;
            }
        }
    });
});



