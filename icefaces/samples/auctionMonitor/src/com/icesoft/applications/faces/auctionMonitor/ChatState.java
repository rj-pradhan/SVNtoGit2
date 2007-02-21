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

package com.icesoft.applications.faces.auctionMonitor;

import com.icesoft.applications.faces.auctionMonitor.beans.UserBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Iterator;
import java.util.Vector;

/**
 * Class used to contain application wide state information Holds a list of all
 * users currently in the chat Can also update everyone in this list by calling
 * reRender on each UserBean This is done in a seperate thread so processing can
 * continue normally
 */
public class ChatState {
    private static Log log = LogFactory.getLog(ChatState.class);
    private static ChatState singleton = null;
    private Vector userList = new Vector(0);
    private boolean stamp =
            true; // timestamps enabled on chat message log, default = true

    /**
     * Default constructor with no parameters, is private to fufill the
     * singleton
     */
    private ChatState() {
    }

    public int getNumParticipants() {
        return (userList.size());
    }

    public boolean getTimeStampEnabled() {
        return (stamp);
    }

    public boolean toggleTimeStamp() {
        stamp = !stamp;
        return (stamp);
    }

    /**
     * Method to return a singleton instance of this class
     *
     * @return this ChatState object
     */
    public static synchronized ChatState getInstance() {
        if (singleton == null) {
            singleton = new ChatState();
        }
        return (singleton);
    }

    /**
     * Method to add the passed UserBean to the current user list
     *
     * @param child UserBean to add
     */
    public void addUserChild(UserBean child) {
        userList.add(child);
    }

    /**
     * Method to remove the passed UserBean from the current user list
     *
     * @param child UserBean to remove
     * @return boolean true if the removal succeeded
     */
    public boolean removeUserChild(UserBean child) {
        int index = 0;
        Iterator users = userList.iterator();
        UserBean current;
        while (users.hasNext()) {
            current = (UserBean) users.next();

            // Ensure the current object is not null
            // Otherwise casting it will cause an exception
            if (current != null) {
                // Check if the current object equals the child to remove
                if (current.equals(child)) {
                    userList.remove(index);
                    return (true);
                }
            } else {
                userList.remove(index);
            }

            index++;
        }

        return (false);
    }

    /**
     * Method to loop through all children UserBeans and force each one to
     * update Each UserBean is extracted, cast, and then has reRender() called
     * This is done inside a seperate thread because otherwise there is a
     * possibility of deadlock with the routine clock render calls in auction
     * monitor Doing this may add more overhead, but much more stability
     *
     * @return boolean true if the update thread was executed
     */
    public boolean updateAll() {
        Thread updater = new Thread(new Runnable() {
            public void run() {
                try {
                    Iterator users = userList.iterator();
                    UserBean current;
                    while (users.hasNext()) {
                        current = (UserBean) users.next();

                        if (current != null) {
                            Thread.currentThread().setContextClassLoader(
                                    current.getClass().getClassLoader());
                            current.reRender();
                        } else {
                            users.remove();
                        }
                    }
                } catch (Exception e) {
                    if (log.isErrorEnabled()) {
                        log.error(
                                "Updating all users from the ChatState failed because of " +
                                e);
                    }
                }
            }
        });
        updater.start();

        return (true);
    }
}
