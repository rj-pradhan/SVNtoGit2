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
    Ice.Parameter.Query.create(function(query) {
        'partial'.associateWith(true).serializeOn(query);

        $event(evt, component).serializeOn(query);
        if (form && form.id) $element(form).serializeOn(query);
        if (component && component.id) $element(component).serializeOn(query);
    }).send();
    resetHiddenFieldsFor(form);
}

function iceSubmit(aForm, aComponent, anEvent) {
    aForm = (aForm ? aForm : aComponent.form);
    var event = $event(anEvent, aComponent);
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
            Ice.Parameter.Query.create(function(query) {
                'partial'.associateWith(false).serializeOn(query);
                event.serializeOn(query);
                if (submit) submit.serializeOn(query);
                if (form) form.serializeOn(query);
            }).send();
        }
    } else {
        var component = aComponent && aComponent.id ? $element(aComponent) : null;

        Ice.Parameter.Query.create(function(query) {
            'partial'.associateWith(false).serializeOn(query);
            event.serializeOn(query);
            if (component) component.serializeOn(query);
            if (form) form.serializeOn(query);
        }).send();
    }

    resetHiddenFieldsFor(aForm);
}

//todo: determine if the cleanup of hidden fields should be at framework or component level
function resetHiddenFieldsFor(aForm) {
    $enumerate(aForm.elements).each(function(formElement) {
        if (formElement.type == 'hidden' && formElement.name != 'viewNumber') formElement.value = '';
    });
}