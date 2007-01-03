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

Ice.DnD.StyleReader = Class.create();
Ice.DnD.StyleReader = {

    styles: 'position,top,left,display',

    buildStyle: function(ele) {
        //Ice.DnD.logger.debug("Building Style");
        var result = '';
        Ice.DnD.StyleReader.styles.split(',').each(
                function(style) {
                    result += style + ':' + Ice.DnD.StyleReader.getStyle(ele, style) + ';';
                });
        return result;
    },
    getStyle: function(x, styleProp) {
        if (x.currentStyle)
            var y = x.currentStyle[styleProp];
        else if (window.getComputedStyle)
            var y = document.defaultView.getComputedStyle(x, null).getPropertyValue(styleProp);
        else
            var y = x.style[styleProp];
        return y;
    },

    findCssField:function(ele, f){
        if(!f)
            f = Ice.util.findForm(ele);
        var fe = f.getElementsByTagName('input');
        var cssUpdate = null;
        var i =0;
        // We only want hidden fields.
        for(i=0;i<fe.length;i++){
            if(fe[i].type=='hidden' && fe[i].name == 'icefacesCssUpdates'){
                cssUpdate = fe[i];
                break;
            }
        }
        return cssUpdate;
    },
    upload: function(ele, submit) {

        var cssUpdate = Ice.DnD.StyleReader.findCssField(ele);

        if (cssUpdate) {
            var val = cssUpdate.value;
            var css = Ice.DnD.StyleReader.buildStyle(ele);
            Ice.DnD.logger.debug("Update CSS ID[" + ele.id + "] CSS[" + css + "] form filed name = [" + cssUpdate.name + "]");
            cssUpdate.value = val + ele.id + '{' + css + '}';
            if (submit) {
                var form = Ice.util.findForm(ele);
                iceSubmitPartial(form, ele, null);
            }
        }
    }
}



Ice.modal = Class.create();
Ice.modal = {
    running:false,
    target:null,
    oldListener:null,
    id:null,
    start:function(target) {
        Ice.modal.oldListener = window.document.documentElement.onkeypress;
        window.document.documentElement.onkeypress = function(e){return Ice.modal.keypress(e);}

        var iframe = document.getElementById('iceModalFrame');
        if (!iframe) {
            iframe = document.createElement('iframe');
            iframe.frameborder = "0";
            iframe.id = 'iceModalFrame';
            var context = configuration.connection.context;
            var dest =    context + '/xmlhttp/blank.iface';
            iframe.src = dest;
            iframe.style.zIndex = 25000;
            iframe.style.opacity = 0.5;
            iframe.style.filter = 'alpha(opacity=50)';

            iframe.style.position = 'absolute';
            iframe.style.visibility = 'visible';
            iframe.style.backgroundColor = 'black';

            iframe.style.top = '0';
            iframe.style.left = '0';
            iframe.style.width = document.body.clientWidth + 'px';
            iframe.style.height = document.body.clientHeight + 'px';
            document.body.appendChild(iframe);
            modal = document.getElementById(target);

            modal.style.zIndex = iframe.style.zIndex + 1;
            modal.style.position = 'absolute';
            Ice.modal.target = modal;
            Ice.modal.id = target;
        }
        Ice.modal.running = true;
    },
    stop:function(target) {
        if (Ice.modal.id == target) {
            window.document.documentElement.onkeypress = Ice.modal.oldListener;
            var iframe = document.getElementById('iceModalFrame');
            if (iframe) {                
                iframe.parentNode.removeChild(iframe);
                logger.debug('removed modal iframe for : ' + target);
            }
            Ice.modal.running = false;
        }
    },
    keypress:function(event){
        if(!Ice.modal.running){return true;}
        var cancel = true;
        var src = null;
        var IEEvent = null;
        if(event){
            src = event.target;
        }else{
            IEEvent = window.event;
            src = IEEvent.srcElement;
        }
        if(Ice.modal.containedInId(src, Ice.modal.target.id)){
            cancel = false;
        }

        if(cancel){

            if(event){
                event.stopPropagation();
            }
            if(IEEvent){
                IEEvent.returnValue = false;
                IEEvent.cancelBubble = true;
            }
        }
        return !cancel;
    },
    containedInId:function(node, id){
        if(node.id == id){            
            return true;
        }
        var parent = node.parentNode;
        if(parent){
            return Ice.modal.containedInId(parent, id);
        }
        return false;
    }


};

