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

Ice.DnD = Class.create();
var IceLoaded = false;
Ice.DnD.logger = Class.create();
Ice.DnD.logger = {
    debug:function(){},
    warn:function(){},
    error:function(){}
};
Ice.DnD = {
    log: function(s) {
        //  dumblog(s);
        Ice.DnD.logger.debug(s);
    },
    DRAG_START:1,
    DRAG_CANCEL:2,
    DROPPED:3,
    HOVER_START:4,
    HOVER_END:5,
    init:function() {

        Ice.DnD.logger = window.logger.child('dragDrop');
       
        Ice.StateMon.logger = window.logger.child('stateMon');
        Ice.StateMon.destroyAll();
        IceLoaded = true;
    },
    elementReplaced:function(ele) {
        var currentEle = $(ele.id);
        if (currentEle != null && currentEle != ele) {

            return true;
        } else {

            return false;
        }
    },
    check:function () {
         Ice.StateMon.checkAll();
     },

    alreadyDrag:function(ele) {
        ele = $(ele)
        var found = false;
        
        $A(Draggables.drags).each(function(drag) {
            if (drag.element && drag.element.id == ele.id) {
                    found = true;
            }
        });
        return found;
    },

    sortableDraggable:function(ele){
        ele = $(ele)
        var found = false;

        $A(Draggables.drags).each(function(drag) {
            if (drag.element && drag.element.id == ele.id) {
                if(drag.options.sort){
                    found = true;
                }
            }
        });
        return found;
    },

    alreadyDrop:function(ele) {
        ele = $(ele)
        var found = false;
        $(Droppables.drops).each(function(drop) {
            if (drop && drop.element.id == ele.id) {

                    found = true;
            }
        });
        return found;
    },

    alreadySort:function(ele) {

        var opts = Sortable.options(ele);
        if(opts)return true;
        return false;
     }
};

Ice.Accordion = {
    fire:function(eleId) {
        var ele = document.getElementById(eleId);
        try{
        if(Element.visible(ele)){
            Effect.SlideUp(ele,{uploadCSS:true,submit:true,duration:0.5});
        }else{
            Effect.SlideDown(ele,{uploadCSS:true,submit:true,duration:0.5});
        }
            }catch(eee){

            console.log("Error in accordion [" + eee + "]");
        }

    }
}