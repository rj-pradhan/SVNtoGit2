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

package com.icesoft.faces.facelets;

import com.icesoft.faces.component.UIXhtmlComponent;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletHandler;
import com.sun.facelets.tag.Tag;
import com.sun.facelets.tag.TagAttribute;
import com.sun.facelets.tag.jsf.ComponentSupport;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Mark Collette Date: 9-Nov-2005 Time: 3:28:50 PM
 */

public class UIXhtmlHandler implements OpenFaceletHandler {
    private boolean open;
    private ArrayList childFaceletHandlers;
    private boolean flushed;

    private Tag tag;

    UIXhtmlHandler(Tag tag) {
        this.open = true;
        this.childFaceletHandlers = new ArrayList();
        this.flushed = false;
        this.tag = tag;
    }

    public void apply(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException {
        if( parent != null ) {
            UIComponent current = applyToCurrent( ctx, parent );
            if( current != null ) {
                parent.getChildren().add( current );

                if( childFaceletHandlers != null ) {
                    for(int i = 0; i < childFaceletHandlers.size(); i++) {
                        FaceletHandler child =
                                (FaceletHandler) childFaceletHandlers.get(i);
                        child.apply( ctx, current );
                    }
                }
            }
        }
    }

    protected UIComponent applyToCurrent(FaceletContext ctx, UIComponent parent)
            throws IOException, FacesException {
        UIXhtmlComponent current = new UIXhtmlComponent();
        current.setCreatedByFacelets();
        current.setTag(tag.getQName());
        String id = null;
        TagAttribute[] attribs = tag.getAttributes().getAll();
        for(int i = 0; i < attribs.length; i++) {
            String qName = attribs[i].getQName();
            if( attribs[i].isLiteral() ) {
                String value = attribs[i].getValue();
                if( qName.equals("id") )
                    id = value;
                current.addStandardAttribute(qName, value);
            }
            else {
                // We don't always include the EL jars, so our
                //  code has to use reflection to refer to
                //  javax.el.ValueExpression
                Object value =
                        attribs[i].getValueExpression(ctx, Object.class);
                current.addELValueExpression(qName, value);
            }
        }
        // com.sun.facelets.tag.jsf.ComponentHandler.apply(-)
        //   assigns ids in a similar fashion
        if( id != null ) {
            current.setId( ctx.generateUniqueId(id) );
        }
        else {
            UIViewRoot root =
                    ComponentSupport.getViewRoot( ctx, parent );
            if( root != null ) {
                current.setId( root.createUniqueId() );
            }
        }
        return current;
    }

    public String toString() {
        return "UIXhtmlHandler: " + tag;
    }

    public boolean isTagOpen() {
        return open;
    }

    public void closeTag() {
        open = false;
    }

    public ArrayList getChildFaceletHandlers() {
        return childFaceletHandlers;
    }

    public boolean isFlushed() {
        return flushed;
    }

    public void setFlushed() {
        flushed = true;
    }
}
