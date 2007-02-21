
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

Ice.Menu = Class.create();
Ice.Menu = {
    currentMenu:null,
    openMenus:new Array(0),
    printOpenMenus:function() {
    	var openMenuString = '';
        for (var i = 0; i < Ice.Menu.openMenus.length; i++) {
            openMenuString = openMenuString + Ice.Menu.openMenus[i].id + ' , ';
        }
        return openMenuString;
    },
    printHoverMenuAndOpenMenus: function(hoverMenu) {
    	alert('hoverMenu=[' + hoverMenu.id + ']\n'
    		+'openMenus=[' + Ice.Menu.printOpenMenus() + ']');
    },
    printArray: function(arrayToPrint) {
    	var buffer = '';
		for (var i = 0; i < arrayToPrint.length; i++) {
			buffer = buffer + arrayToPrint[i] + ', ';
		}    	
		return buffer;
    },
    printArrayOfIds: function(arrayToPrint) {
    	var buffer = '';
		for (var i = 0; i < arrayToPrint.length; i++) {
			buffer = buffer + arrayToPrint[i].id + ', ';
		}    	
		return buffer;
    },
    hideAll: function(){
        for (var i = 0; i < Ice.Menu.openMenus.length; i++ ) {
            Ice.Menu.openMenus[i].style.display='none';
        }
        Ice.Menu.openMenus = new Array();
    },
    getPosition: function(element,positionProperty) {
	    var position = 0;
	    while (element != null) {
		    position += element["offset" + positionProperty];
		    element = element.offsetParent;
	    }
	    return position;
    },
    show: function(supermenu,submenu,submenuDiv) {
    	supermenu=$(supermenu);
    	submenu=$(submenu);
    	submenuDiv=$(submenuDiv);
	    if (submenu) {
            Ice.Menu.showMenuWithId(submenu);
            if (submenuDiv) {
            	var styleLeft = (Ice.Menu.getPosition(supermenu,"Left") + supermenu.offsetWidth) + "px";
       		    submenu.style.left = styleLeft;
       		    var styleTop = Ice.Menu.getPosition(submenuDiv,"Top")  + "px";
                submenu.style.top = styleTop;
            } else {
            	var styleLeft = Ice.Menu.getPosition(supermenu,"Left")  + "px";
    		    submenu.style.left = styleLeft;
    		    var styleTop = (Ice.Menu.getPosition(supermenu,"Top") + supermenu.offsetHeight) + "px";
                submenu.style.top = styleTop;
            }
	    }
	    if ((Ice.Menu.currentMenu) && (submenu != Ice.Menu.currentMenu) && (supermenu != Ice.Menu.currentMenu))  {
	    	Ice.Menu.hideMenuWithId(Ice.Menu.currentMenu.id);
        }
        Ice.Menu.currentMenu = submenu;
    },
    hideOrphanedMenusNotRelatedTo: function(hoverMenu) {
    	// form an array of allowable names
    	var relatedMenus = new Array();
    	var idSegments = hoverMenu.id.split(':');
		var nextRelatedMenu = '';
		for (var i=0; i<idSegments.length; i++) {
			nextRelatedMenu = nextRelatedMenu + idSegments[i];
			var concatArray = new Array(nextRelatedMenu + '_sub');
			relatedMenus = relatedMenus.concat(concatArray);
			nextRelatedMenu = nextRelatedMenu + ':';
		}
		// iterate over open menus and set display='none' for any menu
		// that is not in the array of menus related to the current menu
		var arrayLength = Ice.Menu.openMenus.length;
		var menusToHide = new Array();
		for (var j = 0; j < arrayLength; j ++) {
			var nextOpenMenu = $(Ice.Menu.openMenus[j]);
			var found = 'false';
			for (var k = 0; k < relatedMenus.length; k++) {
				if (nextOpenMenu.id == relatedMenus[k]) {
					found = 'true';
				}
			}
			if (found != 'true') {
				menusToHide[menusToHide.length] = nextOpenMenu.id;
			}
		}
		// iterate over the menus to hide
		Ice.Menu.hideMenusWithIdsInArray(menusToHide);
    },
    hideSubmenu: function(hoverMenu) {
    	var cur = Ice.Menu.currentMenu;
    	var hoverParentId = hoverMenu.id.substring(0,hoverMenu.id.lastIndexOf(':'));
		var curParentId = cur.id.substring(0,cur.id.lastIndexOf(':'));
		if (hoverParentId == curParentId) {
			Ice.Menu.hideMenuWithId(Ice.Menu.currentMenu);
		}
    },
    hideMenusWithIdsInArray: function(menuIdArray) {
    	if (menuIdArray) {
    		for (var i = 0; i < menuIdArray.length; i ++) {
    			Ice.Menu.hideMenuWithId(menuIdArray[i]);
    		}
    	}
    },
    hideMenuWithId: function(menu) {
    	if (menu) {
	    	menu = $(menu);
	    	menu.style.display='none';
	    	Ice.Menu.removeFromOpenMenus(menu);
    	}
    	return;
    },
    removeFromOpenMenus: function(menu) {
    	var tempArray = new Array();
    	for (var i = 0; i < Ice.Menu.openMenus.length; i ++) {
    		if (Ice.Menu.openMenus[i].id != menu.id) {
    			tempArray = tempArray.concat(new Array(Ice.Menu.openMenus[i]));
    		}
    	}
    	Ice.Menu.openMenus = tempArray;
    },
    showMenuWithId: function(menu) {
    	if (menu) {
    		menu = $(menu);
    		menu.style.display='';
			Ice.Menu.addToOpenMenus(menu);
    	}
    },
    addToOpenMenus: function(menu) {
		if (menu) {
			menu = $(menu);
			var found = 'false';
	    	for (var i = 0; i < Ice.Menu.openMenus.length; i ++) {
	    		if (Ice.Menu.openMenus[i].id == menu.id) {
	    			found = 'true';
	    			break;
	    		}
	    	}
	    	if (found != 'true') {
	            var openMenu = new Array(menu);
	            Ice.Menu.openMenus = Ice.Menu.openMenus.concat(openMenu);
	    	}
	    	
		}
    }
}
document.documentElement.onclick = Ice.Menu.hideAll;
