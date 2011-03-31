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
package org.eclipse.wst.sse.sieditor.test.ui.v2.dt.propertyEditor;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.ITypeDisplayer;
import org.eclipse.wst.sse.sieditor.ui.v2.newtypedialog.ITypeDialogStrategy;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.TypePropertyEditor;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.typecommitters.ITypeCommitter;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ParameterNode;
import org.eclipse.wst.sse.sieditor.test.util.EasymockModelUtils;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.UnresolvedType;

public class TypePropertyEditorTest {

    private static boolean getSelectedTypeCalledCheck = false;

    private static class TypePropertyEditorExposer extends TypePropertyEditor {

        private IType type;

        public void setType(final IType type) {
            this.type = type;
        }

        public TypePropertyEditorExposer(final SIFormPageController controller, final ITypeDisplayer typeDisplayer) {
            super(controller, typeDisplayer);
        }

        @Override
        protected IType getType() {
            return type;
        }

        @Override
        public IType getSelectedType() {
            getSelectedTypeCalledCheck = true;
            return super.getSelectedType();
        }

        @Override
        public ITypeDialogStrategy createNewTypeDialogStrategy() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ITypeCommitter getTypeCommitter() {
            return new ITypeCommitter() {
                @Override
                public void commitType(final IType type) {
                }

                @Override
                public void commitName(final IType type, final String name) {
                }
            };
        }

        @Override
        public boolean canNavigateToType() {
            return super.canNavigateToType();
        }

    }

    private static class TestTypePropertyEditor extends TypePropertyEditorExposer {
        public TestTypePropertyEditor(final SIFormPageController controller, final ITypeDisplayer typeDisplayer) {
            super(controller, typeDisplayer);
        }

        @Override
        public boolean canNavigateToType() {
            return true;
        }
    }

    // @Test
    // public final void testNavigateParameterEditor() {
    // SIFormPageController controllerMock =
    // createMock(SIFormPageController.class);
    // expect(controllerMock.isResourceReadOnly()).andReturn(Boolean.valueOf(false));
    // expect(controllerMock.getCommonTypesDropDownList()).andReturn(new
    // String[]{"a","b","c"});
    // expect(controllerMock.isResourceReadOnly()).andReturn(Boolean.valueOf(false)).times(2);
    // replay(controllerMock);
    // ITypeDisplayer typeDisplayer = createMock(ITypeDisplayer.class);
    //        
    // TestTypePropertyEditor parameterTypeEditor = new
    // TestTypePropertyEditor(controllerMock, typeDisplayer);
    // Shell shell = new Shell(Display.getCurrent());
    // parameterTypeEditor.createControl(new FormToolkit(Display.getCurrent()),
    // shell, new Composite(shell, SWT.NONE));
    // // parameterTypeEditor.setInput(new StructureTypeNode(public class
    // org.easymock.createMock(IModelObject.class), , nodeMapper));
    // //
    // assertEquals(Boolean.valueOf(false),parameterTypeEditor.getTypeLink().isEnabled());
    // verify(controllerMock);
    // fail("Not yet implemented"); // TODO
    // }
    //    
    @Test
    public final void testSetEnabled() {
        boolean isResourceReadOnly = false;
        final boolean isReadOnlyNode = false;
        final Shell shell = new Shell(Display.getCurrent());
        final Composite containerComposite = new Composite(shell, SWT.NONE);

        SIFormPageController controller = setUpMockAndEnv(isResourceReadOnly, isReadOnlyNode, containerComposite);

        assertTrue(((CCombo) containerComposite.getChildren()[1]).getEnabled());

        verify(controller);
    }

    @Test
    public final void testSetNotEnabled() {
        boolean isResourceReadOnly = true;
        final boolean isReadOnlyNode = false;
        final Shell shell = new Shell(Display.getCurrent());
        final Composite containerComposite = new Composite(shell, SWT.NONE);

        SIFormPageController controller = setUpMockAndEnv(isResourceReadOnly, isReadOnlyNode, containerComposite);

        assertFalse(((CCombo) containerComposite.getChildren()[1]).getEnabled());
        verify(controller);
    }

    @Test
    public final void testSetNotEnabledEditorReadOnly() {
        boolean isResourceReadOnly = false;
        final boolean isReadOnlyNode = true;
        final Shell shell = new Shell(Display.getCurrent());
        final Composite containerComposite = new Composite(shell, SWT.NONE);

        SIFormPageController controller = setUpMockAndEnv(isResourceReadOnly, isReadOnlyNode, containerComposite);

        assertFalse(((CCombo) containerComposite.getChildren()[1]).getEnabled());

        verify(controller);
    }

    private SIFormPageController setUpMockAndEnv(final boolean isResourceReadOnly, final boolean isReadOnlyNode,
            final Composite containerComposite) {
        SIFormPageController controller = createNiceMock(SIFormPageController.class);
        expect(controller.isResourceReadOnly()).andReturn(Boolean.valueOf(isResourceReadOnly)).anyTimes();
        expect(controller.getCommonTypesDropDownList()).andReturn(new String[] { "a", "b", "c" });
        replay((controller));
        final ITypeDisplayer typeDisplayer = createMock(ITypeDisplayer.class);
        final TestTypePropertyEditor editor = new TestTypePropertyEditor(controller, typeDisplayer);
        editor.initialize(createMock(IManagedForm.class));

        final SIFormPageController nodeMapperContainer = new SIFormPageController(null, false, true);
        nodeMapperContainer.setShowCategoryNodes(true);
        final IParameter parameter = EasymockModelUtils.createIParameterTypeMockFromSameModel();
        replay(parameter);

        final ParameterNode parameterNode = new ParameterNode(null, parameter, nodeMapperContainer) {

            @Override
            public boolean isReadOnly() {
                return isReadOnlyNode;
            }

        };
        editor.setInput(parameterNode);

        editor.createControl(new FormToolkit(Display.getCurrent()), containerComposite);

        editor.update();
        return controller;
    }


    @Test
    public void testCanNavigateToTypeWhenUnresolved() {
        TypePropertyEditorExposer typePropertyEditor = new TypePropertyEditorExposer(null,null);
        typePropertyEditor.setSelectedType(UnresolvedType.instance());
        getSelectedTypeCalledCheck = false;
        assertFalse(typePropertyEditor.canNavigateToType());
        assertTrue(getSelectedTypeCalledCheck);
    }

}
