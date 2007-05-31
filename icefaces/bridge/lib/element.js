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

[ Ice.ElementModel = new Object ].as(function(This) {

    This.TemporaryContainer = function() {
        var container = document.body.appendChild(document.createElement('div'));
        container.style.visibility = 'hidden';
        This.TemporaryContainer = function() {
            return container;
        };

        return container;
    };

    This.DisconnectAllListenersAndPeers = function(e) {
        var elements = e.getElementsByTagName('*');
        for (var i = 0; i < elements.length; i++) {
            var element = elements[i];
            var peer = element.peer;
            if (peer) {
                //disconnect listeners
                peer.eachListenerName(function(listenerName) {
                    element[listenerName.toLowerCase()] = null;
                });
                //disconnect peers
                element.peer = null;
                peer.element = null;
            }
        }
    };

    This.Element = Object.subclass({
        MouseListenerNames: [ 'onClick', 'onDblClick', 'onMouseDown', 'onMouseMove', 'onMouseOut', 'onMouseOver', 'onMouseUp' ],

        KeyListenerNames: [ 'onKeyDown', 'onKeyPress', 'onKeyUp', 'onHelp' ],

        initialize: function(element) {
            this.element = element;
        },

        id: function() {
            return this.element.id;
        },

        captureAndRedirectSubmits: function() {
            $enumerate(this.element.getElementsByTagName('form')).collect($element).each(function(form) {
                form.captureOnSubmit();
                form.redirectSubmit();
            });
            if ('form' == this.element.tagName.toLowerCase()) {
                this.captureOnSubmit();
                this.redirectSubmit();
            }
        },

        replaceHtml: function(html) {
            this.withTemporaryContainer(function(container) {
                container.innerHTML = html;
                var newElement = container.firstChild;
                this.disconnectAllListenersAndPeers();
                this.replaceHostElementWith(newElement);
            });
            this.captureAndRedirectSubmits();
        },

        withAllChildElements: function(iterator) {
            var elements = this.element.getElementsByTagName('*');
            for (var i = 0; i < elements.length; i++) {
                var peer = elements[i].peer;
                if (peer) iterator(peer);
            }
        },

        disconnectAllListenersAndPeers: /MSIE/.test(navigator.userAgent) ?
            function() {
                //cleanup in a different (pseudo) thread
                This.DisconnectAllListenersAndPeers.delayFor(100)(this.element);
            } :
            function() {
                This.DisconnectAllListenersAndPeers(this.element);
            },

        serializeOn: function(query) {
        },

        sendOn: function(connection) {
            Query.create(function(query) {
                this.serializeOn(query);
            }.bind(this)).sendOn(connection);
        },

        send: function() {
            this.sendOn(connection);
        },

        withTemporaryContainer: function(execute) {
            try {
                execute.apply(this, [ This.TemporaryContainer() ]);
            } finally {
                This.TemporaryContainer().innerHTML = '';
            }
        },

        defaultReplaceHostElementWith: function(newElement) {
            this.displayOff();
            this.element.parentNode.replaceChild(newElement, this.element);
            this.element = newElement;
            this.element.peer = this;
        },

        replaceHostElementWith: function(newElement) {
            this.defaultReplaceHostElementWith(newElement);
        },

        //hide deleted elements -- Firefox 1.0.x renders tables after they are removed from the document.
        displayOff: /Safari/.test(navigator.userAgent) ? Function.NOOP : function() {
            this.element.style.display = 'none';
        },

        eachListenerName: function(iterator) {
            this.MouseListenerNames.each(iterator);
            this.KeyListenerNames.each(iterator);
        }
    });

    This.Element.adaptToElement = function(e) {
        if (e.peer) return e.peer;
        //no polymophism here...'switch' is the way then.
        switch (e.tagName.toLowerCase()) {
            case 'textarea':
            case 'input': e.peer = new This.InputElement(e); break;
            case 'th':
            case 'td':
            case 'tr': e.peer = new This.TableCellElement(e); break;
            case 'button': e.peer = new This.ButtonElement(e); break;
            case 'select': e.peer = new This.SelectElement(e); break;
            case 'form': e.peer = new This.FormElement(e); break;
            case 'head': e.peer = new This.HeadElement(e); break;
            case 'body': e.peer = new This.BodyElement(e); break;
            case 'script': e.peer = new This.ScriptElement(e); break;
            case 'title': e.peer = new This.TitleElement(e); break;
            case 'a': e.peer = new This.AnchorElement(e); break;
            case 'fieldset': e.peer = new This.FieldSetElement(e); break;
            case 'object': e.peer = new This.ObjectElement(e); break;
            case 'iframe': e.peer = new This.IFrameElement(e); break;
            default : e.peer = new This.Element(e); break;
        }

        return e.peer;
    };

    This.InputElement = This.Element.subclass({
        InputListenerNames: [ 'onBlur', 'onFocus', 'onChange' ],

        initialize: function(element) {
            this.element = element;
            var type = element.type.toLowerCase();
            this.isSubmitElement = type == 'submit' || type == 'image' || type == 'button';
        },

        isSubmit: function() {
            return this.isSubmitElement;
        },

        form: function() {
            return This.Element.adaptToElement(this.element.form);
        },

        focus: function() {
            var onFocusListener = this.element.onfocus;
            this.element.onfocus = Function.NOOP;
            this.element.focus();
            this.element.onfocus = onFocusListener;
        },

        replaceHostElementWith: function(newElement) {
            this.eachAttributeName(function(attributeName) {
                var value = newElement[attributeName];
                this.element[attributeName] = value ? value : null; 
            }.bind(this));

            //'style' attribute special case
            var newStyle = newElement.getAttribute('style');
            var oldStyle = this.element.getAttribute('style');
            if (newElement != oldStyle) {
                this.element.setAttribute('style', newStyle);
            }

            //overwrite listeners and bind them to the existing element
            this.eachListenerName(function(listenerName) {
                var name = listenerName.toLowerCase();
                this.element[name] = newElement[name] ? newElement[name].bind(this.element) : null;
                newElement[name] = null;
            }.bind(this));
        },

        eachAttributeName: function(iterator) {
            //core and i18n attributes (except 'id' and 'style' attributes)
            ['className', 'title', 'lang', 'dir'].each(iterator);
            //input element attributes
            ['type', 'name', 'value', 'checked', 'disabled', 'readonly',
             'size', 'maxLength', 'src', 'alt', 'useMap', 'isMap', 'tabIndex',
             'accessKey', 'accept'].each(iterator);
        },

        serializeOn: function(query) {
            switch (this.element.type.toLowerCase()) {
                case 'image':
                case 'textarea':
                case 'submit':
                case 'hidden':
                case 'password':
                case 'text': query.add(this.element.name, this.element.value); break;
                case 'checkbox':
                case 'radio': if (this.element.checked) query.add(this.element.name, this.element.value || 'on'); break;
            }
        },

        eachListenerName: function(iterator) {
            this.MouseListenerNames.each(iterator);
            this.KeyListenerNames.each(iterator);
            this.InputListenerNames.each(iterator);
        }
    });

    This.SelectElement = This.InputElement.subclass({
        isSubmit: function() {
            return false;
        },

        replaceHostElementWith: function(newElement) {
            this.defaultReplaceHostElementWith(newElement);
        },

        serializeOn: function(query) {
            $enumerate(this.element.options).select(function(option) {
                return option.selected;
            }).each(function(selectedOption) {
                var value = selectedOption.value || (selectedOption.value == '' ? '' : selectedOption.text);
                query.add(this.element.name, value);
            }.bind(this));
        }
    });

    This.ButtonElement = This.InputElement.subclass({

        initialize: function(element) {
            this.element = element;
            this.isSubmitElement = element.type.toLowerCase() == 'submit';
        },

        isSubmit: function() {
            return this.isSubmitElement;
        },

        replaceHostElementWith: function(newElement) {
            this.defaultReplaceHostElementWith(newElement);
        },

        serializeOn: function(query) {
            query.add(this.element.name, this.element.value);
        }
    });

    This.FormElement = This.Element.subclass({
        FormListenerNames: [ 'onReset', 'onSubmit', 'submit' ],

        formElements: /Safari/.test(navigator.userAgent) ? function() {
            //todo: find a more performant way to discard old form elements in Safari
            var filteredElements = [];
            $enumerate(this.element.elements).reverse().each(function(element) {
                //Safari keeps old form elements around so they need to be discarded
                if (!filteredElements.detect(function(filteredElement) {
                    return element.id && filteredElement.element.id && filteredElement.element.id == element.id;
                })) filteredElements.push(This.Element.adaptToElement(element));
            });

            return filteredElements;
        } : function() {
            return $enumerate(this.element.elements).collect($element);
        },

        //captures normal form submit events and sends them through a XMLHttpRequest
        captureOnSubmit: function() {
            var previousOnSubmit = this.element.onsubmit;
            this.element.onsubmit = function(event) {
                if (previousOnSubmit) previousOnSubmit();
                $event(event).cancelDefaultAction();
                iceSubmit(this.element, null, event);
            };
        },

        //redirect normal form submits through a XMLHttpRequest
        redirectSubmit: function() {
            var previousSubmit = this.element.submit;
            this.element.submit = function() {
                if (previousSubmit) previousSubmit();
                iceSubmit(this.element, null, null);
            };
        },

        serializeOn: function(query) {
            this.formElements().each(function(formElement) {
                if (!formElement.isSubmit()) formElement.serializeOn(query);
            });
        },

        eachListenerName: function(iterator) {
            this.MouseListenerNames.each(iterator);
            this.KeyListenerNames.each(iterator);
            this.FormListenerNames.each(iterator);
        }
    });

    This.HeadElement = This.Element.subclass({
        replaceHtml: function(html) {
            this.element.innerHTML = html.substring(html.indexOf('>') + 1, html.lastIndexOf('<'));
        }
    });

    This.BodyElement = This.Element.subclass({
        replaceHtml: function(html) {
            this.disconnectAllListenersAndPeers();
            this.element.innerHTML = html.substring(html.indexOf('>') + 1, html.lastIndexOf('<'));
            this.captureAndRedirectSubmits();
        }
    });

    This.ScriptElement = This.Element.subclass({
        replaceHtml: function(html) {
            //if script element is updated its code will be evaluate in IE (thus evaluating it twice)
            //evaluate code in the 'window' context
            var scriptCode = html.substring(html.indexOf('>') + 1, html.lastIndexOf('<'));
            if (scriptCode != '' && scriptCode != ';') {
                var evalFunc = function() {
                    eval(scriptCode);
                };
                evalFunc.apply(window);
            }
        }
    });

    This.TitleElement = This.Element.subclass({
        replaceHtml: function(html) {
            this.element.ownerDocument.title = html.substring(html.indexOf('>') + 1, html.lastIndexOf('<'));
        }
    });

    This.FieldSetElement = This.Element.subclass({
        isSubmit: function(html) {
            return false;
        }
    });

    This.ObjectElement = This.Element.subclass({
        isSubmit: function(html) {
            return false;
        }
    });

    This.AnchorElement = This.Element.subclass({

        focus: function() {
            var onFocusListener = this.element.onfocus;
            this.element.onfocus = Function.NOOP;
            this.element.focus();
            this.element.onfocus = onFocusListener;
        },

        serializeOn: function(query) {
            query.add(this.element.name, this.element.name);
        },

        form: function() {
            var parent = this.element.parentNode;
            while (parent) {
                if (parent.tagName && parent.tagName.toLowerCase() == 'form') return This.Element.adaptToElement(parent);
                parent = parent.parentNode;
            }

            throw 'Cannot find enclosing form.';
        }
    });

    This.TableCellElement = This.Element.subclass({
        replaceHtml: function(html) {
            this.withTemporaryContainer(function(container) {
                //very bizarre hack to account for innerHTML not allowing top level TR and TD
                container.innerHTML = '<TABLE>' + html + '</TABLE>';
                var newElement = container.firstChild;
                while ((null != newElement) && (this.element.id != newElement.id)) {
                    newElement = newElement.firstChild;
                }
                this.disconnectAllListenersAndPeers();
                this.replaceHostElementWith(newElement);
            });
            this.captureAndRedirectSubmits();
        }
    });

    This.IFrameElement = This.Element.subclass({
        replaceHostElementWith: function(newElement) {
            this.eachAttributeName(function(attributeName) {
                var value = newElement.getAttribute(attributeName);
                if (value == null) {
                    this.element.removeAttribute(attributeName);
                } else {
                    this.element.setAttribute(attributeName, value);
                }
            }.bind(this));

            //special case for the 'src' attribute (Safari bug?)
            var oldLocation = this.element.contentWindow.location.href;
            var newLocation = newElement.contentWindow.location.href;
            if (oldLocation != newLocation) {
                this.element.contentWindow.location = newLocation;
            }

            //overwrite listeners and bind them to the existing element
            this.eachListenerName(function(listenerName) {
                var name = listenerName.toLowerCase();
                this.element[name] = newElement[name] ? newElement[name].bind(this.element) : null;
                newElement[name] = null;
            }.bind(this));
        },

        eachAttributeName: function(iterator) {
            ['title', 'lang', 'dir', 'class', 'style', 'align', 'frameborder',
             'width', 'height', 'hspace', 'ismap', 'longdesc', 'marginwidth',
             'marginheight', 'name', 'scrolling'].each(iterator);
        }
    });

    //public call
    window.$element = This.Element.adaptToElement;
});
