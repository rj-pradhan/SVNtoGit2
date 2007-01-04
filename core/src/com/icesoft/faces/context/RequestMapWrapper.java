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

package com.icesoft.faces.context;

import java.io.Serializable;

import javax.faces.context.ExternalContext;
import javax.servlet.http.HttpServletRequest;
import javax.portlet.PortletRequest;
import java.util.Enumeration;
import java.util.HashMap;

import com.icesoft.faces.util.ArrayEnumeration;

/* RequestMapWrapper is a wrapper for the implied Map of the current
   request (which varies depending on initial request or application
   initiated update).
   This should be implemented as a pure wrapper for the request, however
   backward compatibility with ICEfaces single execution of JSPs requires
   that the map contain values from previous requests.
*/
public class RequestMapWrapper
        extends AbstractAttributeMap implements Serializable {
    private transient ExternalContext externalContext;
    private transient HashMap hashMap = new HashMap();

    public RequestMapWrapper(ExternalContext externalContext) {
        setExternalContext(externalContext);
    }

    public void setExternalContext(ExternalContext externalContext)  {
        HttpServletRequest servletRequest = null;
        PortletRequest portletRequest = null;

        this.externalContext = externalContext;
        Object request = externalContext.getRequest();
        //HttpServletRequest can take priority as if the request
        //is both,  it doesn't matter how we get/set the attributes
        if (request instanceof HttpServletRequest)  {
            servletRequest = (HttpServletRequest) request;
        } else if (request instanceof PortletRequest) {
            portletRequest = (PortletRequest) request;
        }

        if (null != servletRequest) {
            Enumeration names = servletRequest.getAttributeNames();
            while (names.hasMoreElements()) {
                String name = (String) names.nextElement();
                hashMap.put(name, servletRequest.getAttribute(name));
            }
        } else if (null != portletRequest) {
            Enumeration names = portletRequest.getAttributeNames();
            while (names.hasMoreElements()) {
                String name = (String) names.nextElement();
                hashMap.put(name, portletRequest.getAttribute(name));
            }
        }

    }

    /*
      * @see com.icesoft.faces.context.AbstractAttributeMap#getAttribute(java.lang.String)
      */
    protected Object getAttribute(String key) {
    
        return hashMap.get(key);

/*
        Object request = externalContext.getRequest();
        Object value = null;
        if (request instanceof HttpServletRequest) {
            value = ((HttpServletRequest) request).getAttribute(key);
        } else if (request instanceof PortletRequest) {
            value = ((PortletRequest) request).getAttribute(key);
        }
        return value;
*/

    }

    /*
      * @see com.icesoft.faces.context.AbstractAttributeMap#setAttribute(java.lang.String, java.lang.Object)
      */
    protected void setAttribute(String key, Object value) {
    
        hashMap.put(key, value);
        
        Object request = externalContext.getRequest();
        if (request instanceof HttpServletRequest) {
            ((HttpServletRequest) request).setAttribute(key, value);
         } else if (request instanceof PortletRequest) {
            ((PortletRequest) request).setAttribute(key, value);
        }

    }

    /*
      * @see com.icesoft.faces.context.AbstractAttributeMap#removeAttribute(java.lang.String)
      */
    protected void removeAttribute(String key) {
    
        hashMap.remove(key);
        
        Object request = externalContext.getRequest();
        if (request instanceof HttpServletRequest) {
            ((HttpServletRequest) request).removeAttribute(key);
        } else if (request instanceof PortletRequest) {
            ((PortletRequest) request).removeAttribute(key);
        }

    }

    /*
      * @see com.icesoft.faces.context.AbstractAttributeMap#getAttributeNames()
      */
    protected Enumeration getAttributeNames() {
    
            return new ArrayEnumeration(hashMap.keySet().toArray());

/*
        Object request = externalContext.getRequest();
        Enumeration names = null;
        if (request instanceof HttpServletRequest) {
            ((HttpServletRequest) request).getAttributeNames();
        } else if (request instanceof PortletRequest) {
            names = ((PortletRequest) request).getAttributeNames();
        }
        return names;
*/

    }

}
