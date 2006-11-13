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

package com.icesoft.faces.component.inputfile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This is an auxiliary class for inputFile component, not intended to used
 * externally.
 */
public class ProgressOutputStream extends OutputStream implements Runnable {
    OutputStream originalStream = null;
    InputFile inputFile = null;
    boolean saved = false;
    double bytesWritten = -1;
    double requestSize = -1;
    private HttpSession session = null;

    public ProgressOutputStream(OutputStream out, InputFile inputFile,
                                int requestSize, HttpSession session) {
        originalStream = out;
        this.inputFile = inputFile;
        this.requestSize = requestSize / 1024;
        this.session = session;
        new Thread(this, "fileupload").start();
    }

    public void write(int b) throws IOException {
        originalStream.write(b);
    }

    public void write(byte b[], int off, int len) throws IOException {

        originalStream.write(b, off, len);
        bytesWritten += len;
    }


    boolean isSaved() {
        return saved;
    }

    void setSaved(boolean saved) {
        this.saved = saved;
        if (saved) {
            inputFile.getFileInfo().setPercent(100);
            inputFile.fireEvent();
        }
    }

    public void run() {
        while (!isSaved()) {
            session.getId();
            int percent =
                    (int) Math.abs(((bytesWritten / 1024) / requestSize) * 100);
            inputFile.getFileInfo().setPercent(percent);
            inputFile.setStatus(InputFile.UPLOADING);
            inputFile.fireEvent();

            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
