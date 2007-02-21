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

package com.icesoft.faces.component.ext;


import javax.faces.component.UIComponent;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesListener;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class KeyEvent extends ActionEvent {
    private String clientSideEventModel;

    private final String TYPE = "type";
    private final String KEYCODE = "keyCode";
    private final String CTRLKEY = "ctrlKey";
    private final String SHIFTKEY = "shiftKey";
    private final String ALTKEY = "altKey";
    private final String COMPONENTID = "componentId";

    public static final int ESC = 27;
    public static final int TAB = 9;
    public static final int CAPSLOCK = 20;
    public static final int SHIFT = 16;
    public static final int CTRL = 17;
    public static final int START_LEFT = 91;
    public static final int START_RIGHT = 92;
    public static final int CONTEXT_MENU = 93;
    public static final int ALT = 18;
    public static final int SPACE = 32;
    public static final int CARRIAGE_RETURN = 13;
    public static final int LINE_FEED = 10;
    public static final int BACK_SLASH = 220;
    public static final int BACK_SPACE = 8;

    public static final int INSERT = 45;
    public static final int DEL = 46;
    public static final int HOME = 36;
    public static final int END = 35;
    public static final int PAGE_UP = 33;
    public static final int PAGE_DOWN = 34;

    public static final int PRINT_SCREEN = 44;
    public static final int SCR_LK = 145;
    public static final int PAUSE = 19;


    public static final int LEFT_ARROW_KEY = 37;
    public static final int UP_ARROW_KEY = 38;
    public static final int RIGHT_ARROW_KEY = 39;
    public static final int DOWN_ARROW_KEY = 40;


    public static final int F1 = 112;
    public static final int F2 = 113;
    public static final int F3 = 114;
    public static final int F4 = 115;
    public static final int F5 = 116;
    public static final int F6 = 117;
    public static final int F7 = 118;
    public static final int F8 = 119;
    public static final int F9 = 120;
    public static final int F10 = 121;
    public static final int F11 = 122;
    public static final int F12 = 123;


    private String type;
    private int keyCode;
    private boolean ctrlKey;
    private boolean shiftKey;
    private boolean altKey;
    private String componentId;


    public KeyEvent(UIComponent uiComponent, String clientSideEventModel) {
        super(uiComponent);
        this.clientSideEventModel = clientSideEventModel;
        populateEvent();
    }

    public boolean isAltKey() {
        return altKey;
    }

    public String getComponentId() {
        return componentId;
    }

    public boolean isCtrlKey() {
        return ctrlKey;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public boolean isShiftKey() {
        return shiftKey;
    }

    public String getType() {
        return type;
    }

    private void setType(Object type) {
        if (type != null) {
            ;
        }
        this.type = type.toString();
    }

    private void setKeyCode(Object keyCode) {
        try {
            if (keyCode != null) {
                this.keyCode = Integer.parseInt(keyCode.toString());
            }
        } catch (Exception e) {
            //invalid keycode
        }
    }

    private void setComponentId(Object componentId) {
        if (componentId != null) {
            this.componentId = componentId.toString();
        }
    }

    private void setCtrlKey(Object ctrlKey) {
        try {
            this.ctrlKey = new Boolean(ctrlKey.toString()).booleanValue();
        } catch (Exception e) {
            this.ctrlKey = false;
        }
    }

    private void setShiftKey(Object shiftKey) {
        try {
            this.shiftKey = new Boolean(shiftKey.toString()).booleanValue();
        } catch (Exception e) {
            this.shiftKey = false;
        }
    }

    private void setAltKey(Object altKey) {
        try {
            this.altKey = new Boolean(altKey.toString()).booleanValue();
        } catch (Exception e) {
            this.altKey = false;
        }
    }

    /* (non-Javadoc)
     * @see javax.faces.event.FacesEvent#isAppropriateListener(javax.faces.event.FacesListener)
     */
    public boolean isAppropriateListener(FacesListener arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see javax.faces.event.FacesEvent#processListener(javax.faces.event.FacesListener)
     */
    public void processListener(FacesListener arg0) {
        // TODO Auto-generated method stub

    }

    private void populateEvent() {
        Map eventMap = new HashMap();
        StringTokenizer event = new StringTokenizer(clientSideEventModel, ";");
        while (event.hasMoreTokens()) {
            String eventInfo = event.nextToken();
            int colon = eventInfo.indexOf(":");
            eventMap.put(eventInfo.substring(0, colon),
                         eventInfo.substring(colon + 1));
        }
        setType(eventMap.get(TYPE));
        setKeyCode(eventMap.get(KEYCODE));
        setCtrlKey(eventMap.get(CTRLKEY));
        setShiftKey(eventMap.get(SHIFTKEY));
        setAltKey(eventMap.get(ALTKEY));
        setComponentId(eventMap.get(COMPONENTID));
    }
}
