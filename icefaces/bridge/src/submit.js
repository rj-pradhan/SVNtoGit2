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

function iceSubmitPartial(form, component, evt) {
    form = (form ? form : component.form);
    populateEvent(form, component, evt);
    if(connection != null){
        Ice.Parameter.Query.create(function(query) {
            'focus'.associateWith(currentFocus).serializeOn(query);
            'partial'.associateWith('true').serializeOn(query);

            if (form && form.id) $element(form).serializeOn(query);
            if (component && component.id) $element(component).serializeOn(query);
        }).sendOn(connection);
    }
    resetHiddenFieldsFor(form);
}

function iceSubmit(aForm, aComponent, anEvent) {
    aForm = (aForm ? aForm : aComponent.form);
    populateEvent(aForm, aComponent, anEvent);

    var event = $event(anEvent);
    var form = $element(aForm);
    //all key events are discarded except when 'enter' is pressed...not good!
    if (event.isKeyEvent()) {
        if (event.isEnterKey()) {
            //find a default submit element to submit the form
            var submit = form ? form.formElements().detect(function(element) {
                return element.id() == element.form().id() + ':default';
            }) : null;

            //cancel the default action to block 'onclick' event on the submit element
            event.cancelDefaultAction();
            // In some applications you can click a link before connection is initialized
            // TODO: Fix this!
            if(connection != null){
                Ice.Parameter.Query.create(function(query) {
                    'focus'.associateWith(currentFocus).serializeOn(query);
                    if (submit) submit.serializeOn(query);
                    if (form) form.serializeOn(query);
                }).sendOn(connection);
            }
        }
    } else {
        var component = aComponent && aComponent.id ? $element(aComponent) : null;

        Ice.Parameter.Query.create(function(query) {
            'focus'.associateWith(currentFocus).serializeOn(query);
            if (component) component.serializeOn(query);
            if (form) form.serializeOn(query);
        }).sendOn(connection);
    }

    resetHiddenFieldsFor(aForm);
}

//todo: determine if the cleanup of hidden fields should be at framework or component level
function resetHiddenFieldsFor(aForm) {
    $enumerate(aForm.elements).each(function(formElement) {
        if (formElement.type == 'hidden' && formElement.name != 'icefacesID' && formElement.name != 'viewNumber') formElement.value = '';
    });
}

//todo: replace with event serialization
function populateEvent(form, component, event) {
    if(event){
        try {
            //builds the javascript event as raw string, would sent to the server as hidden field. Available only if ice:form element is used.
            if (form && form.elements[form.id + ":_ideventModel"] && form.elements[form.id + ":_ideventModel"] != 'undefined')
                form.elements[form.id + ":_ideventModel"].value = "type:" + event.type + ";keyCode:" + event.keyCode + ";ctrlKey:" + event.ctrlKey + ";shiftKey:" + event.shiftKey + ";altKey:" + event.altKey + ";componentId:" + component.id + ";";
        } catch (ee) {
            logger.error('populateEvent failed ', ee);
        }
    }
}