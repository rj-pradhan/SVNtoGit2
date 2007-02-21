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

package com.icesoft.faces.context.effects;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import java.util.Map;

/**
 * Encode an effect call to an javascript event
 */
public class LocalEffectEncoder {

    private static final String[] EVENTS = {
            "click", "dblclick", "mousedown",
            "mouseup", "mousemove", "mouseover", "mouseout",
            "change", "reset", "submit",
            "keypress", "keydown", "keyup"
    };

    private static String[] ATTRIBUTES = new String[EVENTS.length];
    private static final String ATTRIBUTE_PREFIX = "on";
    private static String[] EFFECTS = new String[EVENTS.length];
    private static final String EFFECT_SUFFIX = "effect";

    static {
        for (int index = 0; index < EVENTS.length; index++) {
            ATTRIBUTES[index] = ATTRIBUTE_PREFIX + EVENTS[index];
            EFFECTS[index] = ATTRIBUTES[index] + EFFECT_SUFFIX;
        }
    }

    private static final Log log = LogFactory.getLog(LocalEffectEncoder.class);


    public static void encodeLocalEffects(UIComponent comp, Element rootNode,
                                          FacesContext facesContext) {
        Map atts = comp.getAttributes();
        try {
            for (int i = 0; i < EVENTS.length; i++) {
                Effect fx = (Effect) atts.get(EFFECTS[i]);
                if (fx == null) {
                    // in some cases the value binding can be null on the initial render
                    // but contain an effect later. This makes a place holder for that effect
                    // once it arrives.
                    if (comp.getValueBinding(EFFECTS[i]) != null) {
                        fx = new BlankEffect();
                    }
                }

                if (fx != null) {
                    String value = JavascriptContext.applyEffect(fx,
                                                                 comp.getClientId(
                                                                         facesContext),
                                                                 facesContext);

                    String original = (String) atts.get(ATTRIBUTES[i]);
                    if (original == null) {
                        original = "";
                    }

                    rootNode.setAttribute(ATTRIBUTES[i], value + original);
                }
            }
        } catch (Exception e) {

            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
