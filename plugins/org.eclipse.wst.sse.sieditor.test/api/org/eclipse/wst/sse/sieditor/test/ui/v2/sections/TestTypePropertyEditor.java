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
package org.eclipse.wst.sse.sieditor.test.ui.v2.sections;

import static org.easymock.EasyMock.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wst.sse.sieditor.ui.v2.AbstractFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.common.BuiltinTypesHelper;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.ITypeDisplayer;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.ITypeDialogStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.TypePropertyEditor;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.typecommitters.ITypeCommitter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;

public class TestTypePropertyEditor {

    private Shell shell;

    private DataTypesFormPageController controller;

    private FormToolkit toolkit;

    private ITypeDisplayer typeDisplayer;

    @Before
    public void setUp() throws Exception {
        controller = createNiceMock(DataTypesFormPageController.class);
        expect(controller.isResourceReadOnly()).andReturn(false);

        final String[] primTypes = BuiltinTypesHelper.getInstance().getCommonlyUsedTypeNames();
        final String[] ret = new String[primTypes.length + 1];
        ret[0] = Messages.TypePropertyEditor_browse_button;
        for (int i = 0, j = 1; i < primTypes.length; i++, j++) {
            ret[j] = primTypes[i];
        }

        expect(controller.getCommonTypesDropDownList()).andStubReturn(ret);
        replay(controller);

        final Display display = Display.getDefault();
        toolkit = new FormToolkit(display);
        typeDisplayer = createMock(ITypeDisplayer.class);
        shell = new Shell(display);
    }

    @Test
    public void testEmptyComboSelection() {

        final TypePropertyEditorTest propertyEditor = new TypePropertyEditorTest(controller, typeDisplayer);
        propertyEditor.createControl(toolkit, shell);

        final Event event = new Event();

        final CCombo typeCombo = propertyEditor.getTypeCombo();

        event.item = typeCombo;
        event.widget = typeCombo;

        // test that an exeption is not thrown when no item is selected from the
        // combo box
        typeCombo.notifyListeners(SWT.Selection, event);

    }

    @Test
    public void testImmediateChange() {
        final TypePropertyEditorTest propertyEditor = new TypePropertyEditorTest(controller, typeDisplayer);
        propertyEditor.createControl(toolkit, shell);

        final Event event = new Event();

        final CCombo typeCombo = propertyEditor.getTypeCombo();

        event.item = typeCombo;
        event.widget = typeCombo;

        final int selectionIndex = 2;// a random number
        typeCombo.select(selectionIndex);
        final String typeName = typeCombo.getItem(selectionIndex);
        final IType type = BuiltinTypesHelper.getInstance().getCommonBuiltinType(typeName);

        typeCombo.notifyListeners(SWT.FocusOut, event);

        Assert.assertEquals(typeName, propertyEditor.getSelectedTypeName());
        Assert.assertEquals(type, propertyEditor.getSelectedType());
        Assert.assertTrue(propertyEditor.commitNameCalled);

        // check that commitName is not called if the selection is the same
        propertyEditor.commitNameCalled = false;
        typeCombo.notifyListeners(SWT.FocusOut, event);
        Assert.assertEquals(typeName, propertyEditor.getSelectedTypeName());
        Assert.assertEquals(type, propertyEditor.getSelectedType());
        Assert.assertFalse(propertyEditor.commitNameCalled);

    }

    class TypePropertyEditorTest extends TypePropertyEditor {

        public boolean commitNameCalled;

        private IType type;

        public TypePropertyEditorTest(final AbstractFormPageController controller, final ITypeDisplayer typeDisplayer) {
            super(controller, typeDisplayer);

        }

        @Override
        protected IType getType() {
            return type;
        }

        @Override
        public CCombo getTypeCombo() {
            return this.typeCombo;
        }

        @Override
        public ITypeDialogStrategy createNewTypeDialogStrategy() {
            return null;
        }

        @Override
        public ITypeCommitter getTypeCommitter() {
            return new ITypeCommitter() {
                @Override
                public void commitName(final IType type, final String name) {
                    commitNameCalled = true;
                    setSelectedType(BuiltinTypesHelper.getInstance().getCommonBuiltinType(getSelectedTypeName()));
                    TypePropertyEditorTest.this.type = getSelectedType();
                }

                @Override
                public void commitType(final IType type) {
                    TypePropertyEditorTest.this.type = getSelectedType();
                }
            };
        }

    }

}
