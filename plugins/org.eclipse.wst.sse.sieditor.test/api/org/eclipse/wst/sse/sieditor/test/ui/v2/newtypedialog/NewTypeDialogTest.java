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
package org.eclipse.wst.sse.sieditor.test.ui.v2.newtypedialog;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.ITypeDialogStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.NewTypeDialog;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;

/**
 *
 * 
 */
public class NewTypeDialogTest {

    private static final String INVALID_NAME = "inv^ 45 ali&*<d n a!@#me";
    private static final String WHITE_SPACE_PROBLEM_NAME = " whiteSpaceProblemName  ";
    private static final String ERROR_MSG = "msg";
    private static final String DEFAULT_NAME = "defaultName";
    private static final String TITLE_VALUE = "TitleValue";
    private ITypeDialogStrategy strategyMock;
    private Shell parentShell;
    private NewTypeDialogExposer dialogExposed;

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        strategyMock = createMock(ITypeDialogStrategy.class);
        parentShell = new Shell();

        dialogExposed = new NewTypeDialogExposer(parentShell, strategyMock);

    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    private static class NewTypeDialogExposer extends NewTypeDialog {

        public int createTypeCounter = 0;
        public int createNameCompositeCounter = 0;
        public int createRadioCompositeCounter;
        public int radioSelectedCounter;
        public int validateAndMarkCounter;
        private boolean blockOnOpen;

        public NewTypeDialogExposer(final Shell parentShell, final ITypeDialogStrategy strategy) {
            super(parentShell, strategy);
        }

        public int getShellStyleExposed() {
            return getShellStyle();
        }

        @Override
        public String getSelectedComboType() {
            return super.getSelectedComboType();
        }

        @Override
        public void buttonPressed(final int buttonId) {
            super.buttonPressed(buttonId);
        }

        @Override
        protected void setDefaultName() {
            super.setDefaultName();
        }

        public ITypeDialogStrategy getStrategy() {
            return strategy;
        }

        public ControlDecoration getDecoration() {
            return decoration;
        }

        boolean getCleanFlag() {
            return cleanFlag;
        }

        boolean getExternalModifyFlag() {
            return externalNameModifyFlag;
        }

        // make the dialog non modal :)
        @Override
        public void setBlockOnOpen(final boolean shouldBlock) {
            if (blockOnOpen) {
                super.setBlockOnOpen(shouldBlock);
            } else {
                super.setBlockOnOpen(false);
            }
        }

        @Override
        protected void createNameComposite(final Composite parent) {
            createNameCompositeCounter++;
            super.createNameComposite(parent);
        }

        @Override
        protected void createRadioComposite(final Composite parent) {
            createRadioCompositeCounter++;
            super.createRadioComposite(parent);
        }

        @Override
        protected void radioSelected() {
            radioSelectedCounter++;
            super.radioSelected();
        }

        @Override
        protected void validateAndMark() {
            validateAndMarkCounter++;
            super.validateAndMark();
        }
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.NewTypeDialog#buttonPressed(int)}
     * .
     */
    @Test
    public final void testButtonPressed() {
        setUpCreateAndOpenMock();
        dialogExposed.createAndOpen();
        dialogExposed.buttonPressed(Dialog.OK);
        assertEquals(NewTypeDialog.RADIO_SELECTION_ELEMENT, dialogExposed.getNewTypeType());
        assertEquals(DEFAULT_NAME, dialogExposed.getNewTypeName());
        dialogExposed.buttonPressed(Dialog.CANCEL);
        assertNull(dialogExposed.getNewTypeType());
        assertNull(dialogExposed.getNewTypeType());
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.NewTypeDialog#NewTypeDialog(org.eclipse.swt.widgets.Shell, org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.ITypeDialogStrategy)}
     * .
     */
    @Test
    public final void testNewTypeDialog() {
        assertEquals(strategyMock, dialogExposed.getStrategy());
        assertEquals(SWT.RESIZE, dialogExposed.getShellStyleExposed() & SWT.RESIZE);
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.NewTypeDialog#createAndOpen()}
     * .
     */
    @Test
    public final void testCreateAndOpen() {
        setUpCreateAndOpenMock();
        dialogExposed.createAndOpen();
        assertEquals(TITLE_VALUE, dialogExposed.getShell().getText());
        dialogExposed.buttonPressed(Dialog.OK);
        assertEquals(NewTypeDialog.RADIO_SELECTION_ELEMENT, dialogExposed.getNewTypeType());
        assertEquals(DEFAULT_NAME, dialogExposed.getNewTypeName());
        dialogExposed.buttonPressed(Dialog.CANCEL);
        assertNull(dialogExposed.getNewTypeType());
        assertNull(dialogExposed.getNewTypeType());
    }

    private void setUpCreateAndOpenMock() {
        expect(strategyMock.isElementEnabled()).andReturn(Boolean.valueOf(true)).atLeastOnce();
        expect(strategyMock.isStructureTypeEnabled()).andReturn(Boolean.valueOf(true)).atLeastOnce();
        expect(strategyMock.isSimpleTypeEnabled()).andReturn(Boolean.valueOf(true)).atLeastOnce();
        expect(strategyMock.getDialogTitle()).andReturn(TITLE_VALUE);
        expect(strategyMock.getDefaultName(NewTypeDialog.RADIO_SELECTION_ELEMENT)).andReturn(DEFAULT_NAME);
        expect(strategyMock.isDuplicateName(DEFAULT_NAME, NewTypeDialog.RADIO_SELECTION_ELEMENT)).andReturn(false);
        replay(strategyMock);
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.NewTypeDialog#createDialogArea(org.eclipse.swt.widgets.Composite)}
     * .
     */
    @Test
    public final void testCreateDialogAreaComposite() {
        setUpCreateAndOpenMock();
        dialogExposed.createRadioCompositeCounter = 0;
        dialogExposed.createNameCompositeCounter = 0;
        dialogExposed.createAndOpen();
        final Composite dialogArea = (Composite) dialogExposed.getShell().getChildren()[0];
        final Layout layout = dialogArea.getLayout();
        assertTrue(layout instanceof GridLayout);

        assertEquals(1, dialogExposed.createRadioCompositeCounter);
        assertEquals(1, dialogExposed.createNameCompositeCounter);
        dialogExposed.buttonPressed(Dialog.CANCEL);
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.NewTypeDialog#createRadioComposite(org.eclipse.swt.widgets.Composite)}
     * .
     */
    @Test
    public final void testCreateRadioComposite() {
        setUpCreateAndOpenMock();
        dialogExposed.createAndOpen();
        final Composite dialogArea = (Composite) dialogExposed.getShell().getChildren()[0];
        final Composite radioComposite = (Composite) dialogArea.getChildren()[0];
        final Control[] children = radioComposite.getChildren();
        assertTrue(children[0] instanceof Group);
        final Group group = (Group) children[0];
        final Control[] groupChildren = group.getChildren();
        for (final Control cButton : groupChildren) {
            assertTrue(cButton instanceof Button);
            final Button button = (Button) cButton;
            assertTrue(button.isEnabled());
        }
        final Button button1 = (Button) groupChildren[0];
        assertEquals(SWT.RADIO, button1.getStyle() & SWT.RADIO);
        assertEquals(Messages.NewTypeDialog_element_radio_button, button1.getText());
        assertTrue(button1.getSelection());

        final Button button2 = (Button) groupChildren[1];
        assertEquals(SWT.RADIO, button2.getStyle() & SWT.RADIO);
        assertEquals(Messages.NewTypeDialog_structure_type_radio_button, button2.getText());
        assertFalse(button2.getSelection());

        final Button button3 = (Button) groupChildren[2];
        assertEquals(SWT.RADIO, button3.getStyle() & SWT.RADIO);
        assertEquals(Messages.NewTypeDialog_simple_type_radio_button, button3.getText());
        assertFalse(button3.getSelection());

        verify(strategyMock);
        reset(strategyMock);
        expect(strategyMock.getDefaultName(NewTypeDialog.RADIO_SELECTION_ELEMENT)).andReturn(DEFAULT_NAME).atLeastOnce();
        expect(strategyMock.isDuplicateName(DEFAULT_NAME, NewTypeDialog.RADIO_SELECTION_ELEMENT)).andReturn(false).atLeastOnce();
        expect(strategyMock.getDefaultName(NewTypeDialog.RADIO_SELECTION_STRUCTURE_TYPE)).andReturn(DEFAULT_NAME).atLeastOnce();
        expect(strategyMock.isDuplicateName(DEFAULT_NAME, NewTypeDialog.RADIO_SELECTION_STRUCTURE_TYPE)).andReturn(false)
                .atLeastOnce();
        expect(strategyMock.getDefaultName(NewTypeDialog.RADIO_SELECTION_SIMPLE_TYPE)).andReturn(DEFAULT_NAME).atLeastOnce();
        expect(strategyMock.isDuplicateName(DEFAULT_NAME, NewTypeDialog.RADIO_SELECTION_SIMPLE_TYPE)).andReturn(false)
                .atLeastOnce();
        replay(strategyMock);
        button1.notifyListeners(SWT.Selection, null);
        button1.setSelection(true);
        assertEquals(NewTypeDialog.RADIO_SELECTION_ELEMENT, dialogExposed.getSelectedComboType());
        assertEquals(1, dialogExposed.radioSelectedCounter);

        button2.notifyListeners(SWT.Selection, null);
        button1.setSelection(false);
        button2.setSelection(true);
        assertEquals(NewTypeDialog.RADIO_SELECTION_STRUCTURE_TYPE, dialogExposed.getSelectedComboType());
        assertEquals(2, dialogExposed.radioSelectedCounter);

        button3.notifyListeners(SWT.Selection, null);
        button2.setSelection(false);
        button3.setSelection(true);
        assertEquals(NewTypeDialog.RADIO_SELECTION_SIMPLE_TYPE, dialogExposed.getSelectedComboType());
        assertEquals(3, dialogExposed.radioSelectedCounter);

        button1.notifyListeners(SWT.Selection, null);
        button3.setSelection(false);
        button1.setSelection(true);
        assertEquals(NewTypeDialog.RADIO_SELECTION_ELEMENT, dialogExposed.getSelectedComboType());
        assertEquals(4, dialogExposed.radioSelectedCounter);

        dialogExposed.buttonPressed(Dialog.CANCEL);
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.NewTypeDialog#createNameComposite(org.eclipse.swt.widgets.Composite)}
     * .
     */
    @Test
    public final void testCreateNameComposite() {
        setUpCreateAndOpenMock();
        dialogExposed.createAndOpen();
        final Composite dialogArea = (Composite) dialogExposed.getShell().getChildren()[0];
        final Composite dialogContent = (Composite) dialogArea.getChildren()[0];
        final Control control = dialogContent.getChildren()[1];
        assertTrue(control instanceof Composite);
        final Composite nameComposite = (Composite) control;
        final Control[] children = nameComposite.getChildren();
        assertEquals(2, children.length);
        assertTrue(children[0] instanceof Label);
        assertEquals(Messages.NewTypeDialog_label_name, ((Label) children[0]).getText());
        assertTrue(children[1] instanceof Text);
        assertEquals(DEFAULT_NAME, ((Text) children[1]).getText());
        final ControlDecoration decoration = dialogExposed.getDecoration();
        assertEquals(decoration.getControl(), children[1]);
        assertEquals(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR).getImage(),
                decoration.getImage());

        dialogExposed.buttonPressed(Dialog.CANCEL);
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.NewTypeDialog#setDefaultName()}
     * .
     */
    @Test
    public final void testSetDefaultName() {
        setUpCreateAndOpenMock();
        dialogExposed.createAndOpen();
        final Composite dialogArea = (Composite) dialogExposed.getShell().getChildren()[0];
        final Composite dialogContent = (Composite) dialogArea.getChildren()[0];
        final Control control = dialogContent.getChildren()[1];
        assertTrue(control instanceof Composite);
        final Composite nameComposite = (Composite) control;
        final Control[] children = nameComposite.getChildren();
        assertEquals(DEFAULT_NAME, ((Text) children[1]).getText());
        verify(strategyMock);
        reset(strategyMock);
        final String newName = DEFAULT_NAME + "1";
        expect(strategyMock.getDefaultName(NewTypeDialog.RADIO_SELECTION_ELEMENT)).andReturn(newName); //$NON-NLS-1$
        expect(strategyMock.isDuplicateName(newName, NewTypeDialog.RADIO_SELECTION_ELEMENT)).andReturn(Boolean.valueOf(false)); //$NON-NLS-1$
        replay(strategyMock);
        dialogExposed.setDefaultName();
        assertEquals(newName, ((Text) children[1]).getText()); //$NON-NLS-1$

        dialogExposed.buttonPressed(Dialog.CANCEL);
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.NewTypeDialog#nameModified()}
     * .
     */
    @Test
    public final void testNameModified() {
        setUpCreateAndOpenMock();
        dialogExposed.createAndOpen();

        verify(strategyMock);
        reset(strategyMock);
        expect(strategyMock.isDuplicateName(DEFAULT_NAME, NewTypeDialog.RADIO_SELECTION_ELEMENT)).andReturn(
                Boolean.valueOf(false)); //$NON-NLS-1$
        replay(strategyMock);

        dialogExposed.validateAndMarkCounter = 0;

        assertTrue(dialogExposed.getCleanFlag());
        assertTrue(dialogExposed.getExternalModifyFlag());

        dialogExposed.nameModified();

        assertFalse(dialogExposed.getCleanFlag());
        assertTrue(dialogExposed.getExternalModifyFlag());
        assertEquals(1, dialogExposed.validateAndMarkCounter);

        dialogExposed.buttonPressed(Dialog.CANCEL);
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.NewTypeDialog#radioSelected()}
     * .
     */
    @Test
    public final void testRadioSelected() {
        setUpCreateAndOpenMock();
        dialogExposed.createAndOpen();

        verify(strategyMock);
        reset(strategyMock);
        expect(strategyMock.getDefaultName(NewTypeDialog.RADIO_SELECTION_ELEMENT)).andReturn(DEFAULT_NAME); //$NON-NLS-1$
        expect(strategyMock.isDuplicateName(DEFAULT_NAME, NewTypeDialog.RADIO_SELECTION_ELEMENT)).andReturn(
                Boolean.valueOf(false)).anyTimes(); //$NON-NLS-1$
        replay(strategyMock);

        dialogExposed.validateAndMarkCounter = 0;

        assertTrue(dialogExposed.getCleanFlag());
        assertTrue(dialogExposed.getExternalModifyFlag());

        dialogExposed.radioSelected();

        assertTrue(dialogExposed.getCleanFlag());
        assertTrue(dialogExposed.getExternalModifyFlag());
        assertEquals(1, dialogExposed.validateAndMarkCounter);

        verify(strategyMock);
        reset(strategyMock);
        expect(strategyMock.isDuplicateName(DEFAULT_NAME, NewTypeDialog.RADIO_SELECTION_ELEMENT)).andReturn(
                Boolean.valueOf(false)).anyTimes(); //$NON-NLS-1$
        replay(strategyMock);

        dialogExposed.validateAndMarkCounter = 0;

        dialogExposed.nameModified();

        assertFalse(dialogExposed.getCleanFlag());

        dialogExposed.radioSelected();

        assertEquals(2, dialogExposed.validateAndMarkCounter);

        dialogExposed.buttonPressed(Dialog.CANCEL);
    }

    boolean modalDialogClosed = false;

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.NewTypeDialog#validateAndMark()}
     * .
     */
    @Test
    public final void testValidateAndMark() {
        setUpCreateAndOpenMock();
        reset(strategyMock);
        expect(strategyMock.isElementEnabled()).andReturn(Boolean.valueOf(true)).atLeastOnce();
        expect(strategyMock.isStructureTypeEnabled()).andReturn(Boolean.valueOf(true)).atLeastOnce();
        expect(strategyMock.isSimpleTypeEnabled()).andReturn(Boolean.valueOf(true)).atLeastOnce();
        expect(strategyMock.getDialogTitle()).andReturn(TITLE_VALUE);
        expect(strategyMock.getDefaultName(NewTypeDialog.RADIO_SELECTION_ELEMENT)).andReturn(DEFAULT_NAME);
        expect(strategyMock.isDuplicateName(DEFAULT_NAME, NewTypeDialog.RADIO_SELECTION_ELEMENT)).andReturn(true);
        expect(strategyMock.getDuplicateNameErrorMessage(NewTypeDialog.RADIO_SELECTION_ELEMENT)).andReturn(ERROR_MSG);
        replay(strategyMock);
        dialogExposed.createAndOpen();
        assertEquals(Messages.NewTypeDialog_msg_error_header_invalid_name + Messages.NewTypeDialog_error_message_new_line
                + ERROR_MSG, dialogExposed.getDecoration().getDescriptionText());

        dialogExposed.buttonPressed(Dialog.CANCEL);
        verify(strategyMock);

        reset(strategyMock);
        expect(strategyMock.isElementEnabled()).andReturn(Boolean.valueOf(true)).atLeastOnce();
        expect(strategyMock.isStructureTypeEnabled()).andReturn(Boolean.valueOf(true)).atLeastOnce();
        expect(strategyMock.isSimpleTypeEnabled()).andReturn(Boolean.valueOf(true)).atLeastOnce();
        expect(strategyMock.getDialogTitle()).andReturn(TITLE_VALUE);
        expect(strategyMock.getDefaultName(NewTypeDialog.RADIO_SELECTION_ELEMENT)).andReturn(WHITE_SPACE_PROBLEM_NAME);
        expect(strategyMock.isDuplicateName(WHITE_SPACE_PROBLEM_NAME, NewTypeDialog.RADIO_SELECTION_ELEMENT)).andReturn(false);
        replay(strategyMock);
        dialogExposed.createAndOpen();
        assertEquals(Messages.NewTypeDialog_msg_error_header_invalid_name + Messages.NewTypeDialog_error_message_new_line
                + Messages.NewTypeDialog_msg_error_invalid_name_whitespace, dialogExposed.getDecoration().getDescriptionText());

        dialogExposed.buttonPressed(Dialog.CANCEL);
        verify(strategyMock);

        reset(strategyMock);
        expect(strategyMock.isElementEnabled()).andReturn(Boolean.valueOf(true)).atLeastOnce();
        expect(strategyMock.isStructureTypeEnabled()).andReturn(Boolean.valueOf(true)).atLeastOnce();
        expect(strategyMock.isSimpleTypeEnabled()).andReturn(Boolean.valueOf(true)).atLeastOnce();
        expect(strategyMock.getDialogTitle()).andReturn(TITLE_VALUE);
        expect(strategyMock.getDefaultName(NewTypeDialog.RADIO_SELECTION_ELEMENT)).andReturn(INVALID_NAME);
        expect(strategyMock.isDuplicateName(INVALID_NAME, NewTypeDialog.RADIO_SELECTION_ELEMENT)).andReturn(true);
        expect(strategyMock.getDuplicateNameErrorMessage(NewTypeDialog.RADIO_SELECTION_ELEMENT)).andReturn(ERROR_MSG);
        replay(strategyMock);
        dialogExposed.createAndOpen();

        assertEquals(Messages.NewTypeDialog_msg_error_header_invalid_name + Messages.NewTypeDialog_error_message_new_line
                + Messages.NewTypeDialog_msg_error_invalid_name + Messages.NewTypeDialog_error_message_new_line + ERROR_MSG,
                dialogExposed.getDecoration().getDescriptionText());

        dialogExposed.buttonPressed(Dialog.CANCEL);
        verify(strategyMock);
    }

    //Buggy test, to be refactorred or deleted
//    /**
//     * Test method for
//     * {@link org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.NewTypeDialog#createAndOpen()}
//     * .
//     */
//    @Test
//    public final void testEnsureModal() {
//        dialogExposed.blockOnOpen = true;
//        Thread closeThread = new Thread(new Runnable() {
//            public void run() {
//                // wait for the opening thread to start the dialog
//                long targetTimeMillis = System.currentTimeMillis() + 500;
//                while (targetTimeMillis - System.currentTimeMillis() > 0) {
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                    }
//                }
//                // assert that opening thread has not changed the value
//                // of the field
//                assertFalse(modalDialogClosed);
//                // close the window
//                Display.getDefault().asyncExec(new Runnable() {
//                    public void run() {
//
//                        dialogExposed.buttonPressed(Dialog.CANCEL);
//                    }
//                });
//                // wait for the main thead to notify
//                synchronized (dialogExposed) {
//                    while (true) {
//                        try {
//                            dialogExposed.wait();
//                            break;
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//                // assert that after the cancle button press,
//                // the opening thread has proceeded by setting the flag
//                // and tofiying this one.
//                assertTrue(modalDialogClosed);
//            }
//        });
//        closeThread.start();
//        // after starting the thread open the shell and enter the
//        // runEventLoop();
//        setUpCreateAndOpenMock();
//        dialogExposed.createAndOpen();
//        // raise the flag - to show that the thead has left the loop
//        modalDialogClosed = true;
//        // notify the waiting close thread
//        synchronized (dialogExposed) {
//            dialogExposed.notifyAll();
//        }
//
//        while (closeThread.isAlive()) {
//            try {
//                synchronized (dialogExposed) {
//                    dialogExposed.notifyAll();
//                }
//                closeThread.join();
//            } catch (InterruptedException e1) {
//            }
//        }
//    }
}
