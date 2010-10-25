/*******************************************************************************
 * Copyright (c) 2010 SAP AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Emil Simeonov - initial API and implementation.
 *    Dimitar Donchev - initial API and implementation.
 *    Dimitar Tenev - initial API and implementation.
 *    Nevena Manova - initial API and implementation.
 *    Georgi Konstantinov - initial API and implementation.
 *    Richard Birenheide - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.ui.preferences;

import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import org.eclipse.wst.sse.sieditor.ui.Activator;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;

public class ServiceInterfaceEditorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public static final String EDIT_REFERENCED_POPUP_NOT_SHOW_AGAIN = "edit_referenced_popup_not_show_again"; //$NON-NLS-1$
 
    public ServiceInterfaceEditorPreferencePage() {
        IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();

        setPreferenceStore(preferenceStore);
        setDescription(Messages.ServiceInterfaceEditorPreferencePage_1);
        
    }
    
    @Override
    protected void createFieldEditors() {

        addField(new BooleanFieldEditor(EDIT_REFERENCED_POPUP_NOT_SHOW_AGAIN, 
                Messages.ServiceInterfaceEditorPreferencePage_2,
                getFieldEditorParent()) {

            private Button checkButton;

            @Override
            protected Button getChangeControl(Composite parent) {
                return checkButton = super.getChangeControl(parent);
            }

            @Override
            protected void doLoad() {
                if (checkButton != null) {
                    String value = getPreferenceStore().getString(getPreferenceName());
                    checkButton.setSelection(UIConstants.EMPTY_STRING.equals(value));
                }
            }

            @Override
            protected void doLoadDefault() {
                if (checkButton != null) {
                    String value = getPreferenceStore().getDefaultString(getPreferenceName());
                    checkButton.setSelection(UIConstants.EMPTY_STRING.equals(value));
                }
            }

            @Override
            protected void doStore() {
                getPreferenceStore().setValue(getPreferenceName(),
                        checkButton.getSelection() ? UIConstants.EMPTY_STRING : MessageDialogWithToggle.NEVER);
            }

        });

    }

    public void init(IWorkbench workbench) {
        // TODO Auto-generated method stub

    }

}
