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
package org.eclipse.wst.sse.sieditor.test.ui.v2.wsdl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IDetailsPageProvider;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wst.sse.sieditor.ui.v2.AbstractMasterDetailsBlock;
import org.junit.After;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IModelChangeEvent;

public class AbstractMasterDetailsBlockTest {

    private KeyAdapter keyAdapter;
    private boolean removePressed = false;

    @After
    public void tearDown() {
        keyAdapter = null;
        removePressed = false;
    }

    @Test
    public void deletePressed_Enabled() {
        new AbstractMasterDetailsBlock() {
            @Override
            protected void removePressed() {
                removePressed = true;
            }

            @Override
            public KeyAdapter createKeyListener() {
                keyAdapter = super.createKeyListener();
                return keyAdapter;
            }

            @Override
            protected boolean shouldProcessRemoveAction(final KeyEvent e) {
                return true;
            }

            @Override
            protected void createButtons(final FormToolkit toolkit, final Composite buttonsComposite) {
            }

            @Override
            protected IDetailsPageProvider createDetailsPageProvider() {
                return null;
            }

            @Override
            protected Button getRemoveButton() {
                return null;
            }

            @Override
            public void componentChanged(final IModelChangeEvent event) {
            }

            @Override
            protected void updateButtonsState(IStructuredSelection structSelection) {
                // TODO Auto-generated method stub
                
            }
        }.createKeyListener();
        keyAdapter.keyPressed(null);
        assertTrue(removePressed);
    }

    @Test
    public void deletePressed_Disabled() {
        new AbstractMasterDetailsBlock() {
            @Override
            protected void removePressed() {
                removePressed = true;
            }

            @Override
            public KeyAdapter createKeyListener() {
                keyAdapter = super.createKeyListener();
                return keyAdapter;
            }

            @Override
            protected boolean shouldProcessRemoveAction(final KeyEvent e) {
                return false;
            }

            @Override
            protected void createButtons(final FormToolkit toolkit, final Composite buttonsComposite) {
            }

            @Override
            protected IDetailsPageProvider createDetailsPageProvider() {
                return null;
            }

            @Override
            protected Button getRemoveButton() {
                return null;
            }

            @Override
            public void componentChanged(final IModelChangeEvent event) {
            }

            @Override
            protected void updateButtonsState(IStructuredSelection structSelection) {
                // TODO Auto-generated method stub
                
            }
        }.createKeyListener();
        keyAdapter.keyPressed(null);
        assertFalse(removePressed);
    }

}
