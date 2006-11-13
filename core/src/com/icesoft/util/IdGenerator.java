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

package com.icesoft.util;

import com.icesoft.util.encoding.Base64;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The <code>IdGenerator</code> is responsible for generating a unique ID based
 * on a counter, the current time in milliseconds, an arbitrary string, the IP
 * address of the localhost, and a random number. </p>
 */
public class IdGenerator {
    private static Log log = LogFactory.getLog(IdGenerator.class);

    private static long counter;
    private static String ipAddress;

    static {
        try {
            ipAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException exception) {
            if (log.isFatalEnabled()) {
                log.fatal(
                        "Failed to get IP address for localhost!", exception);
            }
        }
    }

    private static MessageDigest md5;

    static {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException exception) {
            if (log.isFatalEnabled()) {
                log.fatal("The MD5 algorithm is not available!", exception);
            }
        }
    }

    /**
     * Creates a unique ID based on the specified <code>string</code>. </p>
     *
     * @param string the arbitrary string on which the unique ID to be created
     *               is based.
     * @return a unique ID.
     */
    public static synchronized String create(String string) {
        if (string == null || string.trim().length() == 0) {
            return null;
        }
        StringBuffer _input = new StringBuffer();
        _input.append(++counter);
        _input.append(System.currentTimeMillis());
        _input.append(string);
        _input.append(ipAddress);
        _input.append(Math.random());
        return
                new String(
                        Base64.encodeForURL(
                                md5.digest(_input.toString().getBytes())));
    }
}
