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

var DropRegions = {

    init:false,
    SCALE:10,
    map:[],
    register: function(drop) {
        var element = drop.element;
        var topLeft = Position.cumulativeOffset(element);
        var bottomRight = [topLeft[0] + element.offsetWidth, topLeft[1] + element.offsetHeight];

        var tlX = Math.round(topLeft[0] / this.SCALE);
        var tlY = Math.round(topLeft[1] / this.SCALE);

        var brX = Math.round(bottomRight[0] / this.SCALE) + 1;
        var brY = Math.round(bottomRight[1] / this.SCALE) + 1;

        var x = 0;
        var y = 0;

        for (x = tlX; x < brX; x++) {
            for (y = tlY; y < brY; y++) {
                if (this.map[x] == null)
                    this.map[x] = [];
                if (this.map[x][y] == null)
                    this.map[x][y] = [];
                this.map[x][y].push(drop);
            }
        }

    },

    drops: function(point) {
        var x = Math.round(point[0] / DropRegions.SCALE);
        var y = Math.round(point[1] / DropRegions.SCALE);

        if (this.map[x] == null)
            return [];
        if (this.map[x][y] == null)
            return [];
        return this.map[x][y];
    }
}

Ice.DndEvent = Class.create();
Ice.DndEvent.lastEvent = null;
Ice.DndEvent.prototype = {
    drag:null,
    drop:null,
    eventType:null,
    dragFire:null,
    dropFire:null,
    initialize: function() {
    },
    submit: function() {
        var ele = this.drag.element;
        if (this.drag.options.sort == true)return;
        thisEv = ele.id + '-' + this.eventType;
        if (thisEv == Ice.DndEvent.lastEvent) {
            //Ice.DnD.logger.debug("Duplicate Event [" + this.eventType + "] Ignored");
            return;
            //Don't send the same event twice
        }

        try {
            Ice.DndEvent.lastEvent = thisEv;
            ignoreDrag = this.ignoreEvent(this.drag.options.mask);
            if (ignoreDrag)Ice.DnD.logger.debug("Drag Type [" + this.eventType + "] Ignored. Mask [" + this.drag.options.mask + "]");
            ignoreDrop = true;

            if (this.drop)ignoreDrop = this.ignoreEvent(this.drop.mask);
            if (this.drop)Ice.DnD.logger.debug("Drop Mask [" + this.drop.mask + "] Ignored [" + ignoreDrop + "]");
            if (ignoreDrag && ignoreDrop)return;
            if (this.drop && ignoreDrop)Ice.DnD.logger.debug("Drop Type [" + this.eventType + "] Ignored. Mask [" + this.drop.mask + "]");

            var ignoreCss = false;
            if (this.drag.options.revert == true)ignoreCss = true;
            if (this.drag.options.dragGhost == true)ignoreCss = true;

            if (this.eventType == 4 || this.eventType == 5 || this.eventType == 1)ignoreCss = true; // Don't send style updates on hovering

            Ice.DnD.logger.debug("DnD Event [" + this.eventType + "] ignoreCss[" + ignoreCss + "] value [" + Ice.DnD.StyleReader.buildStyle(ele) + "]");
            if (!ignoreDrag) {
                Ice.DnD.logger.debug("Drag CSS");
                this.populateDrag(ele, ignoreCss);
                // Different browsers need different values (Safri will take second, IE will take first for example)
                if (this.drag.dragGhost == true)this.populateDrag(this.drag._original, ignoreCss);
            }
            if (!ignoreDrop) {
                Ice.DnD.logger.debug("Drop CSS");
                this.populateDrop(this.drop.element, ignoreCss);
            }
            if (!ignoreDrag || !ignoreDrop) {
                Ice.DnD.logger.debug("DnD Event [" + this.eventType + "] Sent");
                var form = Ice.util.findForm(ele);
                var formId = form.id;
                var nothingEvent = new Object();
                var cssUpdate = Ice.DnD.StyleReader.findCssField(ele, form);
                fe = ele.getElementsByTagName('input');
                Ice.DnD.logger.debug("Sending Drag Form. Form is [" + form.id + "] Type [" + fe[0].value + "]");
                var ii = 0;
                for (ii = 0; ii < fe.length; ii++) {
                    Ice.DnD.logger.debug("Drag Form Field[" + fe[ii].name + "] Value [" + fe[ii].value + "]");
                }
                Ice.DnD.logger.debug("Submitting  drag form ID[" + form.id + "] CssUpdate [" + cssUpdate.value + "]!");
                try{
                    iceSubmitPartial(form, ele, nothingEvent);
                }catch(formExcept){
                    Ice.DnD.logger.error("error submitting dnd event", formExcept);

                }
                Ice.DnD.logger.debug("drag form ID[" + form.id + "] submitted");
                // Drop targets might be in a separate form. If this is the case then
                // submit both forms
                if (!ignoreDrop) {
                    form = Ice.util.findForm(this.drop.element);

                    if (form.id != formId) {
                        Ice.DnD.logger.debug("Diff [" + form.id + "]!=[" + formId + "] Submitting");
                        iceSubmitPartial(form, this.drop.element, nothingEvent);
                    }
                }
            }
        } catch(exc) {
            Ice.DnD.logger.error("Could not find form in drag drop", exc);
        }
        return;
    },
    populateDrag:function(ele, ignoreCss) {
        var fe = ele.getElementsByTagName('input');
        var ne = new Array();
        var i =0;
        // We only want hidden fields.
        for(i=0;i<fe.length;i++){
            
            if(fe[i].type=='hidden'){
                ne.push(fe[i]);
            }
        }
        fe = ne;
        fe[0].value = this.eventType;
        if (this.drop)fe[1].value = this.drop.element.id;
        if (!ignoreCss)
            Ice.DnD.StyleReader.upload(ele);
        return true;
    },
    populateDrop:function(ele, ignoreCss) {
        var fe = ele.getElementsByTagName('input');
        var ne = new Array();
        var i =0;
        // We only want hidden fields.
        for(i=0;i<fe.length;i++){

            if(fe[i].type=='hidden'){
                ne.push(fe[i]);
            }
        }
        fe = ne;
        fe[0].value = this.eventType;
        fe[1].value = this.drag.element.id;

        if (!ignoreCss)
            Ice.DnD.StyleReader.upload(ele);

        return true;
    },

    ignoreEvent:function(mask) {
        if (!mask)return false;//No mask, no ignore
        var result = false;

        if (mask) {
            if (mask.indexOf(this.eventType) != -1) {
                result = true;
            }
        }
        return result;
    }
};

Ice.SortEvent = Class.create();
Ice.SortEvent.prototype = {
    initialize: function() {

    },
    start:function(){
        Ice.DnD.logger.debug('Starting Sort Event');
    },
    end:function(){
        Ice.DnD.logger.debug('Ending Sort Event');
    }
};
