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

import com.icesoft.faces.component.CSS_DEFAULT;
import com.icesoft.faces.component.ext.taglib.Util;
import com.icesoft.faces.component.style.OutputStyle;
import com.icesoft.faces.context.effects.JavascriptContext;
import com.icesoft.faces.renderkit.dom_html_basic.DomBasicRenderer;
import org.w3c.dom.Element;

import javax.faces.application.FacesMessage;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.context.FacesContext;
import javax.faces.el.MethodBinding;
import javax.faces.el.ValueBinding;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.FacesEvent;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.EventObject;
import java.util.Iterator;
import java.util.Map;


/**
 * InputFile is a JSF component class representing an ICEfaces inputFile.
 */
public class InputFile extends UICommand implements Serializable{

    public static final String FILE_UPLOAD_COMPONENT_ID =
            "fileUploadComponentId";
    public static final String COMPONENT_TYPE = "com.icesoft.faces.File";
    public static final String RENDERER_TYPE = "com.icesoft.faces.Upload";
    public static final String COMPONENT_FAMILY = "com.icesoft.faces.File";
    public static final int DEFAULT = 0;
    public static final int UPLOADING = 1;
    public static final int SAVED = 2;
    public static final int INVALID = 3;
    public static final int SIZE_LIMIT_EXCEEDED = 4;
    public static final int UNKNOWN_SIZE = 5;

    public static final String FILE_UPLOAD_PREFIX = "fileUpload";
    static final String ICE_UPLOAD_FILE = "uploadHtml";
    static final String SAVE_FLAG = "fileSave";

    //flag to identify if the component already registered
    private boolean register = false;

    private int status = DEFAULT;
    private FileInfo fileInfo = null;
    private File file = null;
    private Element firstTd = null;
    private Element secondTd = null;
    private Object baseClass = null;
    private String methodName = null;
    private Boolean disabled = null;
    private UIComponent parentForm = null;
    private String style = null;
    private String styleClass = null;
    private String label = null;
    FacesContext facesContext = FacesContext.getCurrentInstance();
    private Boolean readonly = null;
    private String enabledOnUserRole = null;
    private String renderedOnUserRole = null;
    private String alt = null;

    /**
     * <p>Return the value of the <code>COMPONENT_TYPE</code> of this
     * component.</p>
     */
    public String getComponentType() {
        return COMPONENT_TYPE;
    }

    /**
     * <p>Return the value of the <code>RENDERER_TYPE</code> of this
     * component.</p>
     */
    public String getRendererType() {
        return RENDERER_TYPE;
    }

    /**
     * <p>Return the value of the <code>COMPONENT_FAMILY</code> of this
     * component.</p>
     */
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

    boolean isRegister() {
        return register;
    }

    public void setRegister(FacesContext facesContext) {
        this.register = true;
        MethodBinding methodBinding = progressListener;
        if (methodBinding != null) {
            String elExpression = methodBinding.getExpressionString();
            String baseName = elExpression
                    .substring(0, elExpression.lastIndexOf('.')) + "}";
            methodName = elExpression.substring(
                    elExpression.lastIndexOf('.') + 1,
                    elExpression.length() - 1);
            ValueBinding vb = FacesContext.getCurrentInstance().getApplication()
                    .createValueBinding(baseName);
            baseClass = vb.getValue(facesContext);
        }
    }

    public void encodeBegin(FacesContext context) throws IOException {
        getCssFile();
        //Register this component to the session
        if (!isRegister()) {
            UIComponent form = DomBasicRenderer.findForm(this);
            if (form != null) {
                setParentForm(form);
            }
            registerWithSession(context);
        }
    }

    public void decode(FacesContext context) {
        if (getStatus() != InputFile.DEFAULT) {
            queueEvent(new ActionEvent(this));
        }
    }

    /**
     * <p/>
     * Return the value of the <code>fileInfo</code> property. </p>
     */
    public FileInfo getFileInfo() {
        if (fileInfo == null) {
            fileInfo = new FileInfo();
        }
        return fileInfo;
    }

    void setFileInfo(FileInfo fileInfo) {
        this.fileInfo = fileInfo;
    }

    /**
     * <p/>
     * Return the value of the <code>file</code> property. </p>
     */
    public File getFile() {
        return file;
    }

    /**
     * <p/>
     * Set the value of the <code>file<code> property. </p>
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * <p/>
     * Set the value of the <code>label</code> property. </p>
     */
    public void setLabel(String label) {
        this.label = label;
    }


    private boolean uniqueFolder = true;
    private boolean uniqueFolderSet = false;


    /**
     * <p/>
     * Set the value of the <code>uniqueFolder</code> property. </p>
     */
    public void setUniqueFolder(boolean uniqueFolder) {
        if (uniqueFolder != this.uniqueFolder) {
            this.uniqueFolder = uniqueFolder;
        }
        this.uniqueFolderSet = true;
    }

    /**
     * <p/>
     * Return the value of the <code>uniqueFolder</code> property. </p>
     */
    public boolean isUniqueFolder() {
        if (this.uniqueFolderSet) {
            return (this.uniqueFolder);
        }
        ValueBinding vb = getValueBinding("uniqueFolder");
        if (vb != null) {
            return (Boolean.TRUE.equals(vb.getValue(getFacesContext())));
        }
        return true;
    }


    /**
     * <p/>
     * Return the value of the <code>label</code> property. </p>
     */
    public String getLabel() {
        if (label != null) {
            return label;
        }
        ValueBinding vb = getValueBinding("label");
        return vb != null ? (String) vb.getValue(getFacesContext()) : "Upload";
    }

    private MethodBinding progressListener = null;

    /**
     * <p/>
     * Return the value of the <code>progressListener</code> property. </p>
     */
    public MethodBinding getProgressListener() {
        return this.progressListener;
    }

    /**
     * <p/>
     * Set the value of the <code>progressListener</code> property. </p>
     */
    public void setProgressListener(MethodBinding progressListener) {
        this.progressListener = progressListener;
    }

    public void setValueBinding(String name, ValueBinding vb) {
        if (name != null && name.equals("progressListener")) {
            MethodBinding mb =
                    getFacesContext().getApplication().createMethodBinding(
                            vb.getExpressionString(),
                            new Class[]{EventObject.class});
            setProgressListener(mb);
            return;
        }
        super.setValueBinding(name, vb);
    }

    Element getFirstTd() {
        return firstTd;
    }

    void setFirstTd(Element firstTd) {
        this.firstTd = firstTd;
    }

    Element getSecondTd() {
        return secondTd;
    }

    void setSecondTd(Element secondTd) {
        this.secondTd = secondTd;
    }

    boolean fireEvent() {
        if (baseClass != null && methodName != null) {
            Class array[] = new Class[1];
            array[0] = EventObject.class;

            Object methodArray[] = new Object[1];
            methodArray[0] = new EventObject(this);
            try {
                Method m = baseClass.getClass().getMethod(methodName, array);
                m.invoke(baseClass, methodArray);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    /**
     * <p>Return the value of the <code>status</code> property. The valid values
     * are: <ul> <li> DEFAULT    (this is component's normal state, no
     * operation) <li> UPLOADING  (available in progressListener only, when the
     * file is uploading) </ul> The following states would only be accessible
     * through the actionListener only, as a result of the file upload action.
     * An action can be taken based on one of the the following state.
     * <p/>
     * The bottom three states also let you allow to get exception object from
     * the FileInfo object. <ul> <li> SAVED <li> INVALID <li>
     * SIZE_LIMIT_EXCEEDED <li> UNKNOWN_SIZE </ul> </p>
     */
    public int getStatus() {
        return status;
    }

    void setStatus(int status) {
        this.status = status;
        if (status != InputFile.DEFAULT) {
            Map requestParameterMap =
                    facesContext.getExternalContext().getRequestParameterMap();
            String formId = parentForm.getClientId(facesContext);
            requestParameterMap.put(formId, formId);
            parentForm.getAttributes().put("fileUploaded", "fileUploaded");
        }

        if (status == InputFile.UPLOADING) {
            if (getFileInfo().getPercent() > 0 &&
                getFileInfo().getPercent() < 90) {
                try {
                    if (!isHeartbeatStopped()) {
                        String call =
                                "var h = window.connection.heartbeat; if(h != null)h.stop();";
                        JavascriptContext.addJavascriptCall(facesContext, call);
                        ((HttpSession) facesContext.getExternalContext()
                                .getSession(true)).setMaxInactiveInterval(600);
                        heartbeatStopped = true;
                    }
                } catch (Exception e) {
                    // opensource edition
                }
            }
        }
    }

    /**
     * <p>Return the value of the <code>disabled</code> property.</p>
     */
    public boolean isDisabled() {
        if (!Util.isEnabledOnUserRole(this)) {
            return true;
        } else {
            if (disabled != null) {
                return disabled.booleanValue();
            }
            ValueBinding vb = getValueBinding("disabled");
            Boolean boolVal = vb != null ?
                              (Boolean) vb.getValue(getFacesContext()) : null;
            return boolVal != null ? boolVal.booleanValue() : false;

        }

    }

    /**
     * <p>Set the value of the <code>disabled</code> property.</p>
     */
    public void setDisabled(boolean disabled) {
        this.disabled = Boolean.valueOf(disabled);
    }

    /**
     * <p>Return the value of the <code>readonly</code> property.</p>
     */
    public boolean isReadonly() {
        if (readonly != null) {
            return readonly.booleanValue();
        }
        ValueBinding vb = getValueBinding("readonly");
        Boolean boolVal =
                vb != null ? (Boolean) vb.getValue(getFacesContext()) : null;
        return boolVal != null ? boolVal.booleanValue() : false;
    }

    /**
     * <p>Set the value of the <code>readonly</code> property.</p>
     */
    public void setReadonly(boolean readonly) {
        this.readonly = Boolean.valueOf(readonly);
    }

    /**
     * <p>Set the value of the <code>enabledOnUserRole</code> property.</p>
     */
    public void setEnabledOnUserRole(String enabledOnUserRole) {
        this.enabledOnUserRole = enabledOnUserRole;
    }

    /**
     * <p>Return the value of the <code>enabledOnUserRole</code> property.</p>
     */
    public String getEnabledOnUserRole() {
        if (enabledOnUserRole != null) {
            return enabledOnUserRole;
        }
        ValueBinding vb = getValueBinding("enabledOnUserRole");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Set the value of the <code>renderedOnUserRole</code> property.</p>
     */
    public void setRenderedOnUserRole(String renderedOnUserRole) {
        this.renderedOnUserRole = renderedOnUserRole;
    }

    /**
     * <p>Return the value of the <code>renderedOnUserRole</code> property.</p>
     */
    public String getRenderedOnUserRole() {
        if (renderedOnUserRole != null) {
            return renderedOnUserRole;
        }
        ValueBinding vb = getValueBinding("renderedOnUserRole");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    /**
     * <p>Return the value of the <code>rendered</code> property.</p>
     */
    public boolean isRendered() {
        if (!Util.isRenderedOnUserRole(this)) {
            return false;
        }
        return super.isRendered();
    }

    UIComponent getParentForm() {
        return parentForm;
    }

    void setParentForm(UIComponent parentForm) {
        this.parentForm = parentForm;
    }

    void clearFormState() {
        if (parentForm != null && parentForm.getAttributes().containsKey("fileUpload")) {
            parentForm.getAttributes().remove("fileUpload");
        }
    }

    public void processUpdates(FacesContext context) {
        super.processUpdates(context);
        clearFormState();

        if (getStatus() == InputFile.SAVED) {
            ValueBinding vb = getValueBinding("file");
            if (vb != null) {
                vb.setValue(facesContext, getFile());
            }
        }

        if (getStatus() != InputFile.DEFAULT &&
            getStatus() != InputFile.SAVED) {
            FileInfo fo = getFileInfo();
            String msg = "Exception processing updates";
            if(fo == null){
                msg = "File info is null";
            }else{
                Exception ex = fo.getException();
                if(ex == null){
                    msg = "Null exception";
                }else{
                    msg = ex.getMessage();
                }
            }
            FacesMessage message =
                    new FacesMessage(msg);
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            context.addMessage(getClientId(context), message);
        }


    }

    public void broadcast(FacesEvent event) throws AbortProcessingException {
        // Perform standard superclass processing
        super.broadcast(event);

        // action and actionListener has been invoked now reset the state to default
        setStatus(DEFAULT);
        if (isHeartbeatStopped()) {
            String call =
                    "var h = window.connection.heartbeat; if(h != null) {h.start();}";
            JavascriptContext.addJavascriptCall(facesContext, call);
            heartbeatStopped = false;
        }
    }

    /**
     * <p>Set the value of the <code>style</code> property.</p>
     */
    public void setStyle(String style) {
        this.style = style;
    }

    /**
     * <p>Return the value of the <code>style</code> property.</p>
     */
    public String getStyle() {
        if (style != null) {
            return style;
        }
        ValueBinding vb = getValueBinding("style");
        return vb != null ? (String) vb.getValue(getFacesContext()) :
               "border:none; width:400px; height:50px; padding-top:0px;";
    }

    /**
     * <p>Set the value of the <code>styleClass</code> property.</p>
     */
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    /**
     * <p>Return the value of the <code>styleClass</code> property.</p>
     */
    public String getStyleClass() {
        return Util.getDisaledOREnabledClass(this, isDisabled(), styleClass,
                                             "styleClass",
                                             CSS_DEFAULT.ICE_FILE_UPLOAD_BASE_CLASS);
    }

    String getInputTextClass() {
        String result = CSS_DEFAULT.ICE_FILE_UPLOAD_DEFAULT_INPUT_TEXT_CLASS;
        if (isDisabled()) {
            result += "-dis";
        }
        return Util.appendNewStyleClass(CSS_DEFAULT.ICE_FILE_UPLOAD_BASE_CLASS,
                                        styleClass, result);
    }

    private String buttonClass = null;

    public void setButtonClass(String buttonClass) {
        this.buttonClass = buttonClass;
    }

    public String getButtonClass() {
        String result = CSS_DEFAULT.ICE_FILE_UPLOAD_DEFAULT_BUTTON_CLASS;
        if (buttonClass != null) {
            result = buttonClass;
        }
        if (isDisabled()) {
            result += "-dis";
        }

        // Append the style if it is still the default
        if (result.equals(CSS_DEFAULT.ICE_FILE_UPLOAD_DEFAULT_BUTTON_CLASS)) {
            return Util.appendNewStyleClass(
                    CSS_DEFAULT.ICE_FILE_UPLOAD_BASE_CLASS, styleClass, result);
        }

        // Otherwise return the user specified override class
        return result;
    }

    //backwards compatability

    /**
     * <p>Return the value of the <code>fileName</code> property.</p>
     *
     * @deprecated use getFileInfo().getFileName() instead.
     */
    public String getFilename() {
        return this.getFileInfo().getFileName();
    }

    /**
     * <p>Set the value of the <code>fileName</code> property.</p>
     *
     * @deprecated use getFileInfo().setFileName() instead.
     */
    public void setFilename(String filename) {
        this.getFileInfo().setFileName(filename);
    }

    /**
     * <p>Return the value of the <code>size</code> property.</p>
     *
     * @deprecated use getFileInfo().getSize() instead.
     */
    public long getFilesize() {
        return this.getFileInfo().getSize();
    }

    /**
     * <p>Set the value of the <code>size</code> property.</p>
     *
     * @deprecated use getFileInfo().setSize() instead.
     */
    public void setFilesize(long filesize) {
        this.getFileInfo().setSize(filesize);
    }

    /**
     * <p>Return the value of the <code>mimeType</code> property.</p>
     *
     * @deprecated use getFileInfo().getContentType() instead.
     */
    public String getMimeType() {
        return this.getFileInfo().getContentType();
    }

    /**
     * <p>Set the value of the <code>mimeType</code> property.</p>
     *
     * @deprecated use getFileInfo().setContentType() instead.
     */
    public void setMimeType(String mimeType) {
        this.getFileInfo().setContentType(mimeType);
    }

    /**
     * <p>Gets the state of the instance as a <code>Serializable</code>
     * Object.</p>
     */
    public Object saveState(FacesContext context) {
        Object values[] = new Object[26];
        values[0] = super.saveState(context);
        values[1] = fileInfo;
        values[2] = firstTd;
        values[3] = secondTd;
        values[4] = baseClass;
        values[5] = methodName;
        values[6] = disabled;
        values[7] = parentForm;
        values[8] = style;
        values[9] = styleClass;
        values[10] = label;
        values[11] = facesContext;
        values[12] = readonly;
        values[13] = enabledOnUserRole;
        values[14] = renderedOnUserRole;
        return ((Object) (values));
    }

    /**
     * <p>Perform any processing required to restore the state from the entries
     * in the state Object.</p>
     */
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        fileInfo = (FileInfo) values[1];
        firstTd = (Element) values[2];
        secondTd = (Element) values[3];
        baseClass = (Object) values[4];
        methodName = (String) values[5];
        disabled = (Boolean) values[6];
        parentForm = (UIForm) values[7];
        style = (String) values[8];
        styleClass = (String) values[9];
        label = (String) values[10];
        facesContext = (FacesContext) values[11];
        readonly = (Boolean) values[12];
        enabledOnUserRole = (String) values[13];
        renderedOnUserRole = (String) values[14];
    }

    private boolean heartbeatStopped = false;

    private boolean isHeartbeatStopped() {
        return heartbeatStopped;
    }

    /**
     * <p>Set the value of the <code>alt</code> property.</p>
     */
    public void setAlt(String alt) {
        this.alt = alt;
    }

    /**
     * <p>Return the value of the <code>alt</code> property.</p>
     */
    public String getAlt() {
        if (alt != null) {
            return alt;
        }
        ValueBinding vb = getValueBinding("alt");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    // maxlength
    private String maxlength = null;

    /**
     * <p>Set the value of the <code>maxlength</code> property.</p>
     */
    public void setMaxlength(String maxlength) {
        this.maxlength = maxlength;
    }

    /**
     * <p>Return the value of the <code>maxlength</code> property.</p>
     */
    public String getMaxlength() {
        if (maxlength != null) {
            return maxlength;
        }
        ValueBinding vb = getValueBinding("maxlength");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    // size
    private String size = null;

    /**
     * <p>Set the value of the <code>size</code> property.</p>
     */
    public void setSize(String size) {
        this.size = size;
    }

    /**
     * <p>Return the value of the <code>size</code> property.</p>
     */
    public String getSize() {
        if (size != null) {
            return size;
        }
        ValueBinding vb = getValueBinding("size");
        return vb != null ? (String) vb.getValue(getFacesContext()) : null;
    }

    private String registrationId;

    String getRegistrationId() {
        if (registrationId == null) {
            registrationId = FILE_UPLOAD_PREFIX + getClientId(facesContext) +
                             this.hashCode();
        }
        return registrationId;
    }

    String getQueryString(FacesContext facesContext) {
        String inputTextClass = getInputTextClass();
        String buttonClass = getButtonClass();
        String label = getLabel();
        boolean uniqueFolder = isUniqueFolder();
        String queryString =
                "?" + FILE_UPLOAD_COMPONENT_ID + "=" + getRegistrationId() +
                "&disabled=" + isDisabled() +
                "&inputTextClass=" + inputTextClass +
                "&buttonClass=" + buttonClass +
                "&label=" + label +
                "&uniqueFolder=" + uniqueFolder +
                "&cssFile=" + getCssFile();
        return queryString;
    }

    void registerWithSession(FacesContext facesContext) {
        if (!facesContext.getExternalContext().getSessionMap()
                .containsKey(getRegistrationId())) {
            facesContext.getExternalContext().getSessionMap()
                    .put(getRegistrationId(), this);
            setRegister(facesContext);
        }
    }

    public long getSizeMax() {
        return FileUploadServlet.uploadMaxFileSize;
    }

    private String cssFile = null;

    public String getCssFile() {
        if (cssFile == null) {
            cssFile = "./xmlhttp/css/xp/xp.css";
            UIComponent root = (UIComponent) getFacesContext().getViewRoot();
            setCssFile(root);
        }
        return cssFile;
    }

    private void setCssFile(UIComponent component) {
        if (component instanceof OutputStyle) {
            cssFile += (cssFile != null) ? ", " : "";
            cssFile += component.getAttributes().get("href").toString();
        }
        Iterator children = component.getChildren().iterator();
        while (children.hasNext()) {
            setCssFile((UIComponent) children.next());
        }
    }

}
