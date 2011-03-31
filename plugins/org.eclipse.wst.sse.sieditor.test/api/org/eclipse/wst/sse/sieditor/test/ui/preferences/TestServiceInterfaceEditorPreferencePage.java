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
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.test.ui.preferences;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;
import org.eclipse.wst.sse.sieditor.ui.preferences.ServiceInterfaceEditorPreferencePage;

public class TestServiceInterfaceEditorPreferencePage {
    
    private Shell shell = null;
    
    @Before
    public void setUp() {
        final Display display = Display.getDefault();
        shell = new Shell(display);
    }
    
    @Test
    public void testCheckButtonForOpenNewEditorWarningIsInPlace() {
        ServiceInterfaceEditorPreferencePage prefPage = new ServiceInterfaceEditorPreferencePage();
        prefPage.createControl(shell);
        
        Control[] children = ((Composite)shell.getChildren()[0]).getChildren();
        
        int i=0;
        assertEquals("Expected preference page description at this place.", Label.class, children[i].getClass());
        i++;
        assertEquals("Expected composite with checkbox buttotn.", Composite.class, children[i].getClass());
        assertEquals("Expected Button class which have to be the check button", 
                Button.class, ((Composite)((Composite)children[i]).getChildren()[0]).getChildren()[0].getClass());
        Button checkButton = (Button)((Composite)((Composite)children[i]).getChildren()[0]).getChildren()[0];
        assertEquals("Expected button for: 'Show warning on opening referenced file'", Messages.ServiceInterfaceEditorPreferencePage_2, checkButton.getText());
        
    }
    
    @Test
    public void testCheckButtonForOpenNewEditorWarningIsInSyncWithPersistanceStore() {
        ServiceInterfaceEditorPreferencePage prefPage = new ServiceInterfaceEditorPreferencePage();
        prefPage.createControl(shell);
        
        Control[] children = ((Composite)shell.getChildren()[0]).getChildren();
        Button checkButton = (Button)((Composite)((Composite)children[1]).getChildren()[0]).getChildren()[0];
    
        IPreferenceStore preferenceStore = prefPage.getPreferenceStore();
        String showPopup = preferenceStore.getString(ServiceInterfaceEditorPreferencePage.EDIT_REFERENCED_POPUP_NOT_SHOW_AGAIN);
        assertEquals("", showPopup);
        
        checkButton.setSelection(false);
        prefPage.performOk();
        
        showPopup = preferenceStore.getString(ServiceInterfaceEditorPreferencePage.EDIT_REFERENCED_POPUP_NOT_SHOW_AGAIN);
        assertEquals(MessageDialogWithToggle.NEVER, showPopup);
        
        checkButton.setSelection(true);
        prefPage.performOk();
                
        showPopup = preferenceStore.getString(ServiceInterfaceEditorPreferencePage.EDIT_REFERENCED_POPUP_NOT_SHOW_AGAIN);
        assertEquals(UIConstants.EMPTY_STRING, showPopup);

    }
    
    @Test
    public void testCheckButtonForOpenNewEditorWarningDefaultValueIsShow() {
        ServiceInterfaceEditorPreferencePageExpose prefPage = new ServiceInterfaceEditorPreferencePageExpose();
        prefPage.createControl(shell);
        
        Control[] children = ((Composite)shell.getChildren()[0]).getChildren();
        Button checkButton = (Button)((Composite)((Composite)children[1]).getChildren()[0]).getChildren()[0];
    
        checkButton.setSelection(false);

        prefPage.performDefaults();
                
        assertTrue("Default value of the button is to show pop-up dialog", checkButton.getSelection());

    }
    
    private class ServiceInterfaceEditorPreferencePageExpose extends ServiceInterfaceEditorPreferencePage {
        @Override
        public void performDefaults() {
            super.performDefaults();
        }
    }
    
}
