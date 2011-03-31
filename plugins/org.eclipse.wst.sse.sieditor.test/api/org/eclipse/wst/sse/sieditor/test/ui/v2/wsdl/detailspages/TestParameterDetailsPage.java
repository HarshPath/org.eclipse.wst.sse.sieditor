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
package org.eclipse.wst.sse.sieditor.test.ui.v2.wsdl.detailspages;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.easymock.EasyMock;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.wst.sse.sieditor.ui.v2.common.DocumentationSection;
import org.eclipse.wst.sse.sieditor.ui.v2.common.IProblemDecorator;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.ITypeDisplayer;
import org.eclipse.wst.sse.sieditor.ui.v2.factory.TreeNodeMapper;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.propertyeditor.TypePropertyEditor;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.detailspages.ParameterDetailsPage;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ParameterNode;
import org.eclipse.wst.sse.sieditor.test.util.EasymockModelUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

public class TestParameterDetailsPage {

    private Shell shell;

    private IManagedForm managedForm;

    private ITypeDisplayer typeDisplayer;

    @Before
    public void setUp() throws Exception {
        final Display display = Display.getDefault();
        managedForm = createNiceMock(IManagedForm.class);
        typeDisplayer = createNiceMock(ITypeDisplayer.class);
        replay(typeDisplayer);
        shell = new Shell(display);
    }

    @After
    public void tearDown() throws Exception {
        managedForm = null;
        shell = null;
    }

    @Test
    public void testSelectionChanged() {
        managedForm.dirtyStateChanged();
        replay(managedForm);

        final SIFormPageController controller = createNiceMock(SIFormPageController.class);
        expect(controller.getCommonTypesDropDownList()).andReturn(new String[] { "boolean" });
        expect(controller.isShowCategoryNodes()).andReturn(true);
        replay(controller);

        final IType type = createNiceMock(IType.class);
        replay(type);

        final String NAME = "faultName";
        final String DOC = "faultDocumentation";
        final IParameter param = EasymockModelUtils.createIParameterTypeMockFromSameModel();
        expect(param.getName()).andReturn(NAME).atLeastOnce();
        expect(param.getDocumentation()).andReturn(DOC).atLeastOnce();
        expect(param.getType()).andReturn(type).atLeastOnce();
        ITreeNode parent = EasyMock.createNiceMock(ITreeNode.class);
        replay(param, parent);

        final ParameterNode paramNode = new ParameterNode(null, param, controller);

        final IStructuredSelection selection = createMock(IStructuredSelection.class);
        expect(selection.size()).andReturn(1);
        expect(selection.getFirstElement()).andReturn(paramNode);
        replay(selection);

        final IProblemDecorator decorator = createMock(IProblemDecorator.class);
        decorator.setModelObject(param);
        decorator.updateDecorations();
        replay(decorator);
        
        final ParameterDetailsPageExposer page = new ParameterDetailsPageExposer(controller, typeDisplayer);
        page.initialize(managedForm);
        page.createContents(shell);
        page.setProblemDecorator(decorator);
        page.selectionChanged(null, selection);

        assertEquals(NAME, page.getNameTextControl().getText());
        assertEquals(DOC, page.getDocumentationSection().getDocumentationText());
        assertFalse(page.isDirty());
        assertFalse(page.getTypePropertyEditor().isStale());
        
        verify(param);
        verify(managedForm);
        verify(decorator);
    }

    @Test
    public void testDocumentationTextFocusLost() {
        final String DOC = "faultDocumentation";
        final String NEW_DOCTEXT = "newfaultDocumentation";
        final IParameter param = EasymockModelUtils.createIParameterTypeMockFromSameModel();
        expect(param.getDocumentation()).andReturn(DOC).atLeastOnce();
        replay(param);

        final ParameterNode paramNode = new ParameterNode(null, param, null);

        final SIFormPageController controller = createNiceMock(SIFormPageController.class);
        expect(controller.getCommonTypesDropDownList()).andReturn(new String[] { "boolean" });
        controller.editDocumentation(paramNode, NEW_DOCTEXT);
        replay(controller);

        final ParameterDetailsPageExposer page = new ParameterDetailsPageExposer(controller, typeDisplayer);
        page.initialize(managedForm);
        page.createContents(shell);
        page.setInput(param);
        page.documentationTextModified();
        page.getDocumentationSection().update(NEW_DOCTEXT);
        page.setTreeNode(paramNode);

        page.documentationTextFocusLost();

        verify(param);
        verify(controller);
    }

    @Test
    public void testIsStale() {
        final String NAME = "faultName";
        final String DOC = "DocText";

        final IType type = createNiceMock(IType.class);
        replay(type);

        final IParameter param = EasymockModelUtils.createIParameterTypeMockFromSameModel();
        expect(param.getDocumentation()).andReturn(DOC).atLeastOnce();
        expect(param.getName()).andReturn(NAME).atLeastOnce();
        expect(param.getType()).andReturn(type).atLeastOnce();
        replay(param);

        final SIFormPageController controller = createNiceMock(SIFormPageController.class);
        expect(controller.getCommonTypesDropDownList()).andReturn(new String[] { "boolean" });
        expect(controller.isShowCategoryNodes()).andReturn(true);
        expect(controller.getTreeNodeMapper()).andReturn(new TreeNodeMapper()).anyTimes();
        replay(controller);

        final ParameterNode paramNode = new ParameterNode(null, param, controller);

        final ParameterDetailsPageExposer page = new ParameterDetailsPageExposer(controller, typeDisplayer);
        page.initialize(managedForm);
        page.createContents(shell);
        page.setTreeNode(paramNode);
        page.setInput(param);
        page.getNameTextControl().setText(NAME);
        page.getDocumentationSection().update(DOC);
        page.getTypePropertyEditor().update();

        assertFalse(page.isStale());

        verify(param);
    }

    @Test
    public void testModifyTextListener() {
        final String NAME = "faultName";
        final String NEW_NAME = "new" + NAME;

        final IType type = createNiceMock(IType.class);
        replay(type);

        final IParameter param = EasymockModelUtils.createIParameterTypeMockFromSameModel();
        expect(param.getName()).andReturn(NAME).atLeastOnce();
        replay(param);

        final ParameterNode paramNode = new ParameterNode(null, param, null);

        final SIFormPageController controller = createNiceMock(SIFormPageController.class);
        expect(controller.getCommonTypesDropDownList()).andReturn(new String[] { "boolean" });
        replay(controller);

        final ParameterDetailsPageExposer page = new ParameterDetailsPageExposer(controller, typeDisplayer);
        page.initialize(managedForm);
        page.createContents(shell);
        page.setTreeNode(paramNode);
        page.setInput(param);

        assertFalse(page.isDirty());

        page.getNameTextControl().setText(NAME);

        assertFalse(page.isDirty());

        page.getNameTextControl().setText(NEW_NAME);

        assertTrue(page.isDirty());

        verify(param);
        verify(controller);
    }

    @Test
    public void testTextFocusListener() {
        final String NAME = "faultName";
        final String NEW_NAME = "new" + NAME;

        final IType type = createNiceMock(IType.class);
        replay(type);

        final IParameter param = EasymockModelUtils.createIParameterTypeMockFromSameModel();
        expect(param.getName()).andReturn(NAME).atLeastOnce();
        replay(param);

        final ParameterNode paramNode = new ParameterNode(null, param, null);

        final SIFormPageController controller = createNiceMock(SIFormPageController.class);
        expect(controller.getCommonTypesDropDownList()).andReturn(new String[] { "boolean" });
        controller.editItemNameTriggered(paramNode, NEW_NAME);
        replay(controller);

        final ParameterDetailsPageExposer page = new ParameterDetailsPageExposer(controller, typeDisplayer);
        page.initialize(managedForm);
        page.createContents(shell);
        page.setTreeNode(paramNode);
        page.setInput(param);

        assertFalse(page.isDirty());

        page.getNameTextControl().setText(NEW_NAME);

        assertTrue(page.isDirty());

        page.getNameTextControl().notifyListeners(SWT.FocusOut, new Event());

        assertFalse(page.isDirty());

        verify(param);
        verify(controller);
    }

    @Test
    public final void testReadOnlySet() {
        managedForm.dirtyStateChanged();
        replay(managedForm);

        final IParameter param = EasymockModelUtils.createIParameterTypeMockFromSameModel();
        
        final SIFormPageController controller = createNiceMock(SIFormPageController.class);
        expect(controller.getCommonTypesDropDownList()).andReturn(new String[] { "boolean" });
        expect(controller.isResourceReadOnly()).andReturn(Boolean.valueOf(false)).atLeastOnce();
        replay(controller);

        final IType type = createNiceMock(IType.class);
        replay(type);

        final String NAME = "faultName";
        final String DOC = "faultDocumentation";
        
        expect(param.getName()).andReturn(NAME).atLeastOnce();
        expect(param.getDocumentation()).andReturn(DOC).atLeastOnce();
        expect(param.getType()).andReturn(type).atLeastOnce();
        ITreeNode parent = createNiceMock(ITreeNode.class);
        expect(parent.getParent()).andReturn(parent);
        replay(param, parent);

        final boolean isReadOnlyParamNode[] = {false};
        final ParameterNode paramNode = new ParameterNode(null, param, controller) {

            @Override
            public boolean isReadOnly() {
                return isReadOnlyParamNode[0];
            }
            
        };

        final IStructuredSelection selection = new StructuredSelection(paramNode);

        final ParameterDetailsPageExposer page = new ParameterDetailsPageExposer(controller, typeDisplayer);
        page.initialize(managedForm);
        page.createContents(shell);
        page.selectionChanged(null, selection);

        assertTrue(page.getNameTextControl().getEditable());

        verify(controller);
        reset(controller);
        expect(controller.isResourceReadOnly()).andReturn(Boolean.valueOf(true)).atLeastOnce();
        replay(controller);

        page.selectionChanged(null, selection);
        assertFalse(page.getNameTextControl().getEditable());

        verify(controller, param);
        
        verify(controller);
        reset(controller);
        expect(controller.isResourceReadOnly()).andReturn(Boolean.valueOf(false)).anyTimes();

        replay(controller);

        isReadOnlyParamNode[0] = true;
        page.selectionChanged(null, selection);
        assertFalse(page.getNameTextControl().getEditable());

        verify(controller, param);
    }

    private class ParameterDetailsPageExposer extends ParameterDetailsPage {

        public ParameterDetailsPageExposer(final SIFormPageController controller, final ITypeDisplayer typeDisplayer) {
            super(controller, typeDisplayer);
        }

        public Text getNameTextControl() {
            return nameTextControl;
        }

        public DocumentationSection getDocumentationSection() {
            return documentationSection;
        }

        public void setTreeNode(final ParameterNode newTreeNode) {
            treeNode = newTreeNode;
            typeEditor.setInput(treeNode);
        }

        public void setInput(final IParameter newFault) {
            input = newFault;
        }

        public TypePropertyEditor getTypePropertyEditor() {
            return typeEditor;
        }
    }
}
