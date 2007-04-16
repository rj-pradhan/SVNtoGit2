
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
// Original license and copyright:
// Copyright (c) 2005 Thomas Fuchs (http://script.aculo.us, http://mir.aculo.us)
//           (c) 2005 Ivan Krstic (http://blogs.law.harvard.edu/ivan)
//           (c) 2005 Jon Tirsen (http://www.tirsen.com)
// Contributors:
//  Richard Livsey
//  Rahul Bhargava
//  Rob Wills
// 
// See scriptaculous.js for full license.

var Autocompleter = {}
Autocompleter.Finder = Class.create();
Autocompleter.Finder = {
      list:new Array(),
      add: function(ele, autocomplete){
        this.list[ele.id] = autocomplete;
      },
      find: function(id){
         return this.list[id];
      }


};
Autocompleter.Base = function() {};
Autocompleter.Base.prototype = {
  baseInitialize: function(element, update, options, rowC, selectedRowC) {
    this.element     = $(element);
    this.update      = $(update);
    this.hasFocus    = false;
    this.changed     = false;
    this.active      = false;
    this.index       = -1;
    this.entryCount  = 0;
    this.rowClass	 =  rowC;
    this.selectedRowClass = selectedRowC;

    if (this.setOptions)
      this.setOptions(options);
    else
      this.options = options || {};

    this.options.paramName    = this.options.paramName || this.element.name;
    this.options.tokens       = this.options.tokens || [];
    this.options.frequency    = this.options.frequency || 0.4;
    this.options.minChars     = this.options.minChars || 1;
    this.options.onShow       = this.options.onShow ||
    function(element, update){
      if(!update.style.position || update.style.position=='absolute') {
        update.style.position = 'absolute';
        Position.clone(element, update, {setHeight: false, offsetTop: element.offsetHeight});
      }
      Effect.Appear(update,{duration:0.15});
    };
    this.options.onHide = this.options.onHide ||
    function(element, update){ new Effect.Fade(update,{duration:0.15}) };

    if (typeof(this.options.tokens) == 'string')
      this.options.tokens = new Array(this.options.tokens);

    this.observer = null;

    this.element.setAttribute('autocomplete','off');

    Element.hide(this.update);

    Event.observe(this.element, "blur", this.onBlur.bindAsEventListener(this));
    Event.observe(this.element, "keypress", this.onKeyPress.bindAsEventListener(this));
    Autocompleter.Finder.add(this.element, this);
      //this.updateNOW('');
  },

  show: function() {
      this.update = $(this.update.id);
    Ice.Autocompleter.logger.debug("Show start");
    if(Element.getStyle(this.update, 'display')=='none'){
        this.options.onShow(this.element, this.update);
    Ice.Autocompleter.logger.debug("Showing");
        }else{
Ice.Autocompleter.logger.debug("Not Showing");        
        }

    if(!this.iefix &&
      (navigator.appVersion.indexOf('MSIE')>0) &&
      (navigator.userAgent.indexOf('Opera')<0) &&
      (Element.getStyle(this.update, 'position')=='absolute')) {
      new Insertion.After(this.update,
       '<iframe id="' + this.update.id + '_iefix" '+
       'style="display:none;position:absolute;filter:progid:DXImageTransform.Microsoft.Alpha(opacity=0);" ' +
       'src="' + configuration.connection.context +'/xmlhttp/blank.iface" frameborder="0" scrolling="no"></iframe>');
      this.iefix = $(this.update.id+'_iefix');
    }
    if(this.iefix) setTimeout(this.fixIEOverlapping.bind(this), 50);
      Ice.Autocompleter.logger.debug("Show done");

  },

  fixIEOverlapping: function() {
    Position.clone(this.update, this.iefix);
    this.iefix.style.zIndex = 1;
    this.update.style.zIndex = 2;
    Element.show(this.iefix);
  },

  hide: function() {


    this.stopIndicator();
    if(Element.getStyle(this.update, 'display')!='none') this.options.onHide(this.element, this.update);
    if(this.iefix) Element.hide(this.iefix);
  },

  startIndicator: function() {
    if(this.options.indicator) Element.show(this.options.indicator);
  },

  stopIndicator: function() {
    if(this.options.indicator) Element.hide(this.options.indicator);
  },

   onKeyPress: function(event) {
    if(!this.active){
         Ice.Autocompleter.logger.debug("Key press ignored. Not active.");
          switch(event.keyCode) {
            case Event.KEY_TAB:
                case Event.KEY_RETURN:
            this.getUpdatedChoices(true,event);
        }
    }
    if(this.active)
      switch(event.keyCode) {
       case Event.KEY_TAB:
       case Event.KEY_RETURN:
          //this.selectEntry();
          //Event.stop(event);

          this.hidden = true; // Hack to fix before beta. Was popup up the list after a selection was made
          this.selectEntry();
                 Ice.Autocompleter.logger.debug("Getting updated choices on enter");
          this.getUpdatedChoices(true,event);
          this.hide();
          Event.stop(event);
       case Event.KEY_ESC:
         this.hide();
         this.active = false;
         Event.stop(event);
         return;
       case Event.KEY_LEFT:
       case Event.KEY_RIGHT:
         return;
       case Event.KEY_UP:
         this.markPrevious();
         this.render();
         //if(navigator.appVersion.indexOf('AppleWebKit')>0)
         Event.stop(event);
         return;
       case Event.KEY_DOWN:
         this.markNext();
         this.render();
         //if(navigator.appVersion.indexOf('AppleWebKit')>0)
         Event.stop(event);
         return;

      }
     else
      if(event.keyCode==Event.KEY_TAB || event.keyCode==Event.KEY_RETURN)
        return;

    this.changed = true;
    this.hasFocus = true;


     this.index = -1;
       //This is to avoid an element being select because the mouse just happens to be over the element when the list pops up
       this.skip_mouse_hover=true;
     if(this.active)
        this.render();
    if(this.observer) clearTimeout(this.observer);
      this.observer =
        setTimeout(this.onObserverEvent.bind(this), this.options.frequency*1000);
  },

  activate: function() {
    this.changed = false;
    this.hasFocus = true;
    //this.getUpdatedChoices();
  },

  onHover: function(event) {
    var element = Event.findElement(event, 'DIV');
    if(this.index != element.autocompleteIndex)
    {
        if(!this.skip_mouse_hover)
            this.index = element.autocompleteIndex;
        //else
            //Ice.Autocompleter.logger.error("Skip mouse hover");

        this.render();
    }{
        //Ice.Autocompleter.logger.error("Index Not equal");
    }
    Event.stop(event);
  },

  onMove: function(event){
      //Ice.Autocompleter.logger.error("Mouse Move");
      if(this.skip_mouse_hover){
          this.skip_mouse_hover=false;
          this.onHover(event);
      }
  },

  onClick: function(event) {
    this.hidden = true; // Hack to fix before beta. Was popup up the list after a selection was made
    var element = Event.findElement(event, 'DIV');
    this.index = element.autocompleteIndex;
    this.selectEntry();
    this.getUpdatedChoices(true, event);
    this.hide();

  },

  onBlur: function(event) {
    // needed to make click events working
    setTimeout(this.hide.bind(this), 250);
    this.hasFocus = false;
    this.active = false;
  },

  render: function() {
    if(this.entryCount > 0) {
      for (var i = 0; i < this.entryCount; i++)
        if(this.index==i) {
            ar = this.rowClass.split(" ");
            for(var ai = 0; ai < ar.length; ai++)
                Element.removeClassName(this.getEntry(i),ar[ai]);
            ar = this.selectedRowClass.split(" ");
            for(var ai = 0; ai < ar.length; ai++)
                Element.addClassName(this.getEntry(i),ar[ai]);
          }
        else {
            ar = this.selectedRowClass.split(" ");
            for(var ai = 0; ai < ar.length; ai++)
                Element.removeClassName(this.getEntry(i),ar[ai]);
            ar = this.rowClass.split(" ");
            for(var ai = 0; ai < ar.length; ai++)
                Element.addClassName(this.getEntry(i),ar[ai]);
        }
      if(this.hasFocus) {

        this.show();
        this.active = true;
      }
    } else {
      this.active = false;
      this.hide();
    }
  },

  markPrevious: function() {
    if(this.index > 0) this.index--
      else this.index = this.entryCount-1;
  },

  markNext: function() {
    if(this.index == -1) {
    	this.index++;
    	return;
    }
    if(this.index < this.entryCount-1) this.index++
      else this.index = 0;
  },

  getEntry: function(index) {
    return this.update.firstChild.childNodes[index];
  },

  getCurrentEntry: function() {
    return this.getEntry(this.index);
  },

  selectEntry: function() {
    this.active = false;

    if(this.index >= 0){
	    this.updateElement(this.getCurrentEntry());
        this.index = -1;
    }
  },

  updateElement: function(selectedElement) {
    if (this.options.updateElement) {
      this.options.updateElement(selectedElement);
      return;
    }
    var value = '';
    if (this.options.select) {
      var nodes = document.getElementsByClassName(this.options.select, selectedElement) || [];
      if(nodes.length>0) value = Element.collectTextNodes(nodes[0], this.options.select);
    } else
      value = Element.collectTextNodesIgnoreClass(selectedElement, 'informal');

    var lastTokenPos = this.findLastToken();
    if (lastTokenPos != -1) {
      var newValue = this.element.value.substr(0, lastTokenPos + 1);
      var whitespace = this.element.value.substr(lastTokenPos + 1).match(/^\s+/);
      if (whitespace)
        newValue += whitespace[0];
      this.element.value = newValue + value;
    } else {
      this.element.value = value;
    }
    this.element.focus();

    if (this.options.afterUpdateElement)
      this.options.afterUpdateElement(this.element, selectedElement);
  },

  updateChoices: function(choices) {
    if(!this.changed && this.hasFocus) {
      this.update.innerHTML = choices;
      Element.cleanWhitespace(this.update);
      Element.cleanWhitespace(this.update.firstChild);

      if(this.update.firstChild && this.update.firstChild.childNodes) {
        this.entryCount =
          this.update.firstChild.childNodes.length;
        for (var i = 0; i < this.entryCount; i++) {
          var entry = this.getEntry(i);
          entry.autocompleteIndex = i;
          this.addObservers(entry);
        }
      } else {
        this.entryCount = 0;
      }

      this.stopIndicator();

      this.index = -1;

      this.render();
    }
  },

  addObservers: function(element) {
    Event.observe(element, "mouseover", this.onHover.bindAsEventListener(this));
    Event.observe(element, "click", this.onClick.bindAsEventListener(this));
    Event.observe(element, "mousemove", this.onMove.bindAsEventListener(this));
  },
    dispose:function() {


        for (var i = 0; i < this.entryCount; i++) {
            var entry = this.getEntry(i);
            entry.autocompleteIndex = i;

            Event.stopObserving(entry, "mouseover", this.onHover);
            Event.stopObserving(entry, "click", this.onClick);
            Event.stopObserving(entry, "mousemove", this.onMove);
        }
         Event.stopObserving(this.element, "mouseover", this.onHover);
            Event.stopObserving(this.element, "click", this.onClick);
            Event.stopObserving(this.element, "mousemove", this.onMove);
        Event.stopObserving(this.element, "blur", this.onBlur);
        Event.stopObserving(this.element, "keypress", this.onKeyPress);

    },

  onObserverEvent: function() {
    this.changed = false;
    if(this.getToken().length>=this.options.minChars) {
      this.startIndicator();
      this.getUpdatedChoices();
    } else {
      this.active = false;
      this.hide();
      this.getUpdatedChoices();
    }
  },

  getToken: function() {
    var tokenPos = this.findLastToken();
    if (tokenPos != -1)
      var ret = this.element.value.substr(tokenPos + 1).replace(/^\s+/,'').replace(/\s+$/,'');
    else
      var ret = this.element.value;

    return /\n/.test(ret) ? '' : ret;
  },

  findLastToken: function() {
    var lastTokenPos = -1;

    for (var i=0; i<this.options.tokens.length; i++) {
      var thisTokenPos = this.element.value.lastIndexOf(this.options.tokens[i]);
      if (thisTokenPos > lastTokenPos)
        lastTokenPos = thisTokenPos;
    }
    return lastTokenPos;
  }
}

Ajax.Autocompleter = Class.create();
Object.extend(Object.extend(Ajax.Autocompleter.prototype, Autocompleter.Base.prototype), {
  initialize: function(element, update, url, options) {
	  this.baseInitialize(element, update, options);
    this.options.asynchronous  = true;
    this.options.onComplete    = this.onComplete.bind(this);
    this.options.defaultParams = this.options.parameters || null;
    this.url                   = url;
  },

  getUpdatedChoices: function() {
    entry = encodeURIComponent(this.options.paramName) + '=' +
      encodeURIComponent(this.getToken());

    this.options.parameters = this.options.callback ?
      this.options.callback(this.element, entry) : entry;

    if(this.options.defaultParams)
      this.options.parameters += '&' + this.options.defaultParams;

    new Ajax.Request(this.url, this.options);
  },

  onComplete: function(request) {
    this.updateChoices(request.responseText);
  }

});


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
// Original license and copyright:
// Copyright (c) 2005 Thomas Fuchs (http://script.aculo.us, http://mir.aculo.us)
//           (c) 2005 Ivan Krstic (http://blogs.law.harvard.edu/ivan)
//           (c) 2005 Jon Tirsen (http://www.tirsen.com)
// Contributors:
//  Richard Livsey
//  Rahul Bhargava
//  Rob Wills
//
// See scriptaculous.js for full license.

Ice.Autocompleter = Class.create();
Ice.Autocompleter.rendered = new Array();

Ice.Autocompleter.init = function(){
        Ice.Autocompleter.logger = logger.child('autocomplete');
 };

Ice.Autocompleter.check = function(ele){
    var i = 0;
    for(i = 0; i < Ice.Autocompleter.rendered.length;i++){
        var ac = Ice.Autocompleter.rendered[i];
        if(ac!=null){
        try{
            var acEle = ac.element;
            if(acEle!=null){
                var currentEle = $(ac.element.id);
                // If the current element is not found it could be the result of
                // a panel not currently being rendered. It might be back later.
                if(currentEle != null  && currentEle != acEle){
                    Ice.Autocompleter.logger.debug("Rebuild Autocomplete");
                    Ice.Autocompleter.rendered[i] = null;
                    new Ice.Autocompleter(currentEle, ac.update.id, ac.options, ac.rowClass, ac.selectedRowClass);

                    ac.dispose();
                    ac.element = null;
                }else{
                    try{
                    if(ele!=null){
                        if(ac.element.id != ele.id){
                            ac.hide();
                        }
                    }
                    }catch(eee){Ice.Autocompleter.logger.error("Error hiding old Autocomplete [" + eee.message + "]", eee);}
                }
            }
            }catch(ee){Ice.Autocompleter.logger.error("Error initializing Autocompleter [" + ee.message + "]", ee);}
        }
        }
    }

Ice.Autocompleter.destroyAll= function(){
    var i = 0;
    for(i = 0; i < Ice.Autocompleter.rendered.length;i++){
        ac = Ice.Autocompleter.rendered[i];
        if(ac!=null){
        try{
             ac.dispose();
             ac.element = null;
            }catch(ee){alert("Error destroying Autocompleter [" + ee.message + "]", ee);}
        }
        } Ice.Autocompleter.rendered = new Array();
    }


Ice.Autocompleter.isInitialized = function(ele){
    ele = $(ele);
    var found = false;
    $A(Ice.Autocompleter.rendered).each(function(ac){
            if(ac){
                if(ac.element.id == ele.id){
                    found=true;
                }
            }
        });
    return found;
}


Object.extend(Object.extend(Ice.Autocompleter.prototype, Autocompleter.Base.prototype), {
  initialize: function(element, update, options, rowClass, selectedRowClass) {
      //alert("Nothing Done");
    if(Ice.Autocompleter.isInitialized(element))return;
      if(options)
        options.minChars  = 0;
      else
        options = {minChars:0};
      this.baseInitialize(element, update, options, rowClass, selectedRowClass);

     this.options.onComplete    = this.onComplete.bind(this);
    this.options.defaultParams = this.options.parameters || null;
    Ice.Autocompleter.rendered.push(this);
  },

  getUpdatedChoices: function(isEnterKey, event) {
      if(!event){
          event = new Object();
      }
      entry = encodeURIComponent(this.options.paramName) + '=' +
            encodeURIComponent(this.getToken());

          this.options.parameters = this.options.callback ?
            this.options.callback(this.element, entry) : entry;

          if(this.options.defaultParams)
            this.options.parameters += '&' + this.options.defaultParams;

     var form = Ice.util.findForm(this.element);
//     form.focus_hidden_field.value=this.element.id;
     if(isEnterKey){
        Ice.Autocompleter.logger.debug("Sending submit");
        iceSubmit(form,this.element,event);
     }
     else{
         Ice.Autocompleter.logger.debug("Sending partial submit");
         iceSubmitPartial(form, this.element, event);
     }

  },

  onComplete: function(request) {
    this.updateChoices(request.responseText);
  },

  updateNOW: function(text){
       Ice.Autocompleter.logger.debug("Start Update NOW");
      if(this.hidden){this.hidden = false;
          Ice.Autocompleter.logger.debug("Not showing due to hide force");
          return;}
      this.hasFocus = true;
      this.updateChoices(text);


      this.show();

      this.render();
      Ice.Autocompleter.check(this.element);
  }

});