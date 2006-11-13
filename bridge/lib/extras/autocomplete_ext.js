
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
        ac = Ice.Autocompleter.rendered[i];
        if(ac!=null){
        try{
            acEle = ac.element;
            if(acEle!=null){
                currentEle = $(ac.element.id);
                // If the current element is not found it could be the result of
                // a panel not currently being rendered. It might be back later.
                if(currentEle != null  && currentEle != acEle){

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
      entry = encodeURIComponent(this.options.paramName) + '=' +
            encodeURIComponent(this.getToken());

          this.options.parameters = this.options.callback ?
            this.options.callback(this.element, entry) : entry;

          if(this.options.defaultParams)
            this.options.parameters += '&' + this.options.defaultParams;

     var form = Ice.util.findForm(this.element);
//     form.focus_hidden_field.value=this.element.id;
     if(isEnterKey)
		iceSubmit(form,this.element,event);
     else	
     	iceSubmitPartial(form, this.element, null);
     	
  },
  
  onComplete: function(request) {
    this.updateChoices(request.responseText);
  },

  updateNOW: function(text){
      if(this.hidden){this.hidden = false;
          //Ice.Autocompleter.logger.debug("Not showing due to hide force");
          return;}
      this.hasFocus = true;
      this.updateChoices(text);
      //Ice.Autocompleter.logger.debug("Update NOW");

      this.show();

      this.render();
      Ice.Autocompleter.check(this.element);
  }

});




