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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.wst.sse.sieditor.ui.v2.common.DocumentationSection;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.detailspages.ServiceInterfaceDetailsPage;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ServiceInterfaceNode;
import org.eclipse.wst.sse.sieditor.test.util.EasymockModelUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;

public class TestServiceInterfaceDetailsPage {

    private Shell shell;

    private IManagedForm managedForm;

    @Before
    public void setUp() throws Exception {
        final Display display = Display.getDefault();
        managedForm = createNiceMock(IManagedForm.class);
        shell = new Shell(display);
    }

    @After
    public void tearDown() throws Exception {
        managedForm = null;
        shell = null;
    }

    @Test
    public void testSelectionChanged() {

        final SIFormPageController controller = createNiceMock(SIFormPageController.class);
        replay(controller);

        final String FAULT_NAME = "faultName";
        final String FAULT_DOC = "faultDocumentation";
        final IServiceInterface fault = EasymockModelUtils.createIServiceInterfaceMockFromSameModel();
        expect(fault.getName()).andReturn(FAULT_NAME).atLeastOnce();
        expect(fault.getDocumentation()).andReturn(FAULT_DOC).atLeastOnce();
        replay(fault);

        final ServiceInterfaceNode faultNode = new ServiceInterfaceNode(null, fault, controller);

        final IStructuredSelection selection = createMock(IStructuredSelection.class);
        expect(selection.size()).andReturn(1);
        expect(selection.getFirstElement()).andReturn(faultNode);
        replay(selection);

        final ServiceInterfaceDetailsPageExposer page = new ServiceInterfaceDetailsPageExposer(controller);
        page.initialize(managedForm);
        page.createContents(shell);
        page.selectionChanged(null, selection);

        assertEquals(FAULT_NAME, page.getNameTextControl().getText());
        assertEquals(FAULT_DOC, page.getDocumentationSection().getDocumentationText());
        assertFalse(page.isDirty());
        verify(fault);
    }

    @Test
    public void testDocumentationTextFocusLost() {
        final String FAULT_DOC = "faultDocumentation";
        final String NEW_DOCTEXT = "NewDocText";

        final IServiceInterface fault = EasymockModelUtils.createIServiceInterfaceMockFromSameModel();
        expect(fault.getDocumentation()).andReturn(FAULT_DOC).atLeastOnce();
        replay(fault);

        final ServiceInterfaceNode serviceInterfaceNode = new ServiceInterfaceNode(null, fault, null);

        final SIFormPageController controller = createNiceMock(SIFormPageController.class);
        controller.editDocumentation(serviceInterfaceNode, NEW_DOCTEXT);
        replay(controller);

        final ServiceInterfaceDetailsPageExposer page = new ServiceInterfaceDetailsPageExposer(controller);
        page.initialize(managedForm);
        page.createContents(shell);
        page.setInput(fault);
        page.documentationTextModified();
        page.getDocumentationSection().update(NEW_DOCTEXT);
        page.setTreeNode(serviceInterfaceNode);

        page.documentationTextFocusLost();

        verify(fault);
        verify(controller);
    }

    @Test
    public void testIsStale() {
        final String FAULT_NAME = "faultName";
        final String FAULT_DOC = "DocText";

        final IServiceInterface fault = EasymockModelUtils.createIServiceInterfaceMockFromSameModel();
        expect(fault.getDocumentation()).andReturn(FAULT_DOC).atLeastOnce();
        expect(fault.getName()).andReturn(FAULT_NAME).atLeastOnce();
        replay(fault);

        final ServiceInterfaceNode serviceInterfaceNode = new ServiceInterfaceNode(null, fault, null);

        final SIFormPageController controller = createNiceMock(SIFormPageController.class);
        replay(controller);

        final ServiceInterfaceDetailsPageExposer page = new ServiceInterfaceDetailsPageExposer(controller);
        page.initialize(managedForm);
        page.createContents(shell);
        page.setTreeNode(serviceInterfaceNode);
        page.setInput(fault);
        page.getNameTextControl().setText(FAULT_NAME);
        page.getDocumentationSection().update(FAULT_DOC);

        assertFalse(page.isStale());

        verify(fault);
    }

    @Test
    public void testNameControlFocusListener() {
        final String FAULT_NAME = "faultName";
        final String NEW_FAULT_NAME = "new" + FAULT_NAME;

        final IServiceInterface fault = EasymockModelUtils.createIServiceInterfaceMockFromSameModel();
        expect(fault.getName()).andReturn(FAULT_NAME).atLeastOnce();
        replay(fault);

        final ServiceInterfaceNode serviceInterfaceNode = new ServiceInterfaceNode(null, fault, null);

        final SIFormPageController controller = createNiceMock(SIFormPageController.class);
        replay(controller);

        final ServiceInterfaceDetailsPageExposer page = new ServiceInterfaceDetailsPageExposer(controller);
        page.initialize(managedForm);
        page.createContents(shell);
        page.setTreeNode(serviceInterfaceNode);
        page.setInput(fault);

        assertFalse(page.isDirty());
        page.getNameTextControl().setText(NEW_FAULT_NAME);
        assertTrue(page.isDirty());

        page.getNameTextControl().notifyListeners(SWT.FocusOut, new Event());

        assertFalse(page.isDirty());

        verify(fault);
        verify(controller);
    }

    @Test
    public void testModifyTextListener() {
        final String FAULT_NAME = "faultName";
        final String NEW_FAULT_NAME = "new" + FAULT_NAME;

        final IServiceInterface fault = EasymockModelUtils.createIServiceInterfaceMockFromSameModel();
        expect(fault.getName()).andReturn(FAULT_NAME).atLeastOnce();
        replay(fault);

        final ServiceInterfaceNode serviceInterfaceNode = new ServiceInterfaceNode(null, fault, null);

        final SIFormPageController controller = createNiceMock(SIFormPageController.class);
        replay(controller);

        final ServiceInterfaceDetailsPageExposer page = new ServiceInterfaceDetailsPageExposer(controller);
        page.initialize(managedForm);
        page.createContents(shell);
        page.setTreeNode(serviceInterfaceNode);
        page.setInput(fault);

        assertFalse(page.isDirty());

        page.getNameTextControl().setText(NEW_FAULT_NAME);

        assertTrue(page.isDirty());

        verify(fault);
        verify(controller);
    }

    @Test
    public final void testReadOnlySet() {
        final SIFormPageController controller = createNiceMock(SIFormPageController.class);
        expect(controller.isResourceReadOnly()).andReturn(Boolean.valueOf(true)).atLeastOnce();
        replay(controller);

        final String SI_NAME = "siName";
        final String SI_DOC = "siDocumentation";
        final IServiceInterface serviceInterface = EasymockModelUtils.createIServiceInterfaceMockFromSameModel();
        expect(serviceInterface.getName()).andReturn(SI_NAME).atLeastOnce();
        expect(serviceInterface.getDocumentation()).andReturn(SI_DOC).atLeastOnce();
        replay(serviceInterface);

        final ServiceInterfaceNode siNode = new ServiceInterfaceNode(null, serviceInterface, controller);

        final IStructuredSelection selection = new StructuredSelection(siNode);

        final ServiceInterfaceDetailsPageExposer page = new ServiceInterfaceDetailsPageExposer(controller);
        page.createContents(shell);
        page.selectionChanged(null, selection);
        assertFalse(page.getNameTextControl().getEditable());

        verify(controller);
        reset(controller);
        expect(controller.isResourceReadOnly()).andReturn(Boolean.valueOf(false)).atLeastOnce();
        replay(controller);
        
        page.selectionChanged(null, selection);
        assertTrue(page.getNameTextControl().getEditable());
        
        verify(controller);
        verify(serviceInterface);
    }

    private class ServiceInterfaceDetailsPageExposer extends ServiceInterfaceDetailsPage {

        public ServiceInterfaceDetailsPageExposer(final SIFormPageController controller) {
            super(controller);
        }

        public Text getNameTextControl() {
            return nameTextControl;
        }

        public DocumentationSection getDocumentationSection() {
            return documentationSection;
        }

        public void setTreeNode(final ServiceInterfaceNode newTreeNode) {
            treeNode = newTreeNode;
        }

        public void setInput(final IServiceInterface newFault) {
            input = newFault;
        }

    }
}
