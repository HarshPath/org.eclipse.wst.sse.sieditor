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
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.wst.sse.sieditor.ui.v2.common.DocumentationSection;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.detailspages.OperationDetailsPage;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.OperationNode;
import org.eclipse.wst.sse.sieditor.test.util.EasymockModelUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.OperationType;

public class TestOperationDetailsPage {

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
		final IOperation operation = EasymockModelUtils.createIOperationTypeMockFromSameModel();
		expect(operation.getName()).andReturn(FAULT_NAME).atLeastOnce();
		expect(operation.getDocumentation()).andReturn(FAULT_DOC).atLeastOnce();
		expect(operation.getOperationStyle()).andReturn(OperationType.REQUEST_RESPONSE).atLeastOnce();
		replay(operation);
		
		final OperationNode faultNode = new OperationNode(null, operation , null);
		
		final IStructuredSelection selection = createMock(IStructuredSelection.class);
		expect(selection.size()).andReturn(1);
		expect(selection.getFirstElement()).andReturn(faultNode);
		replay(selection);
		
		final OperationDetailsPageExposer page = new OperationDetailsPageExposer(controller);
		page.initialize(managedForm);
		page.createContents(shell);
		page.selectionChanged(null, selection);
		
		assertEquals(FAULT_NAME, page.getNameTextControl().getText());
		assertEquals(FAULT_DOC, page.getDocumentationSection().getDocumentationText());
		assertEquals(OperationType.REQUEST_RESPONSE.toString(), page.getTypeCombo().getText());
		assertEquals(OperationType.REQUEST_RESPONSE, page.getInitialOpStyle());
		
		assertFalse(page.isDirty());
		verify(operation);
	}
	
	@Test
	public void testDocumentationTextFocusLost() {
		managedForm.dirtyStateChanged();
		replay(managedForm);
		
		final String DOC = "faultDocumentation";
		final String NEW_DOC = "newfaultDocumentation";
		final IOperation operation = EasymockModelUtils.createIOperationTypeMockFromSameModel();
		expect(operation.getDocumentation()).andReturn(DOC).atLeastOnce();
		replay(operation);
		
		final OperationNode operationNode = new OperationNode(null, operation , null);
		
		final SIFormPageController controller = createNiceMock(SIFormPageController.class);
		controller.editDocumentation(operationNode, NEW_DOC);
		replay(controller);
		
		final OperationDetailsPageExposer page = new OperationDetailsPageExposer(controller);
		page.initialize(managedForm);
		page.createContents(shell);
		page.setTreeNode(operationNode);
		page.setInput(operation);
		page.getDocumentationSection().update(NEW_DOC);
		//on the first keystroak on a thext field - the editor should be marked dirty
		assertEquals(true,page.isDirty());
		page.documentationTextModified();
		page.documentationTextFocusLost();
		
		assertFalse(page.isDirty());
		verify(operation);
		verify(managedForm);
		verify(controller);
	}
	
	@Test
	public void testisStale() {
	
		final String DOC = "faultDocumentation";
		final String NEW_DOC = DOC+"new";
		final String NAME = "name";
		final String NEW_NAME = NAME+"new";
		final IOperation operation = EasymockModelUtils.createIOperationTypeMockFromSameModel();
		expect(operation.getDocumentation()).andReturn(DOC).atLeastOnce();
		expect(operation.getName()).andReturn(NAME).atLeastOnce();
		expect(operation.getOperationStyle()).andReturn(OperationType.REQUEST_RESPONSE).atLeastOnce();
		replay(operation);
		
		OperationNode operationNode = new OperationNode(null, operation , null);
		
		final SIFormPageController controller = createNiceMock(SIFormPageController.class);
		replay(controller);
		
		final OperationDetailsPageExposer page = new OperationDetailsPageExposer(controller);
		page.initialize(managedForm);
		page.createContents(shell);
		page.setTreeNode(operationNode);
		page.setInput(operation);
		page.getNameTextControl().setText(NAME);
		page.getDocumentationSection().update(DOC);
		page.getTypeCombo().select(0);
		
		assertFalse(page.isStale());
	              
                verify(operation);

                //stale because of the OpType
                final IOperation typeFailInput = EasymockModelUtils.createIOperationTypeMockFromSameModel();
                expect(typeFailInput.getOperationStyle()).andReturn(OperationType.ASYNCHRONOUS);
                replay(typeFailInput);
                page.setInput(typeFailInput);
                assertEquals(true, page.isStale());
                verify(typeFailInput);

                //stale because of the Doc
                final IOperation docFailInput = EasymockModelUtils.createIOperationTypeMockFromSameModel();
                expect(docFailInput.getOperationStyle()).andReturn(OperationType.REQUEST_RESPONSE);
                expect(docFailInput.getDocumentation()).andReturn(NEW_DOC).times(1);
                replay(docFailInput);
                page.setInput(docFailInput);
                assertEquals(true, page.isStale());
                verify(docFailInput);
                
                //stale because of the Name             
                final IOperation nameFailInput = EasymockModelUtils.createIOperationTypeMockFromSameModel();
                expect(nameFailInput.getName()).andReturn(NEW_NAME);
                replay(nameFailInput);
                operationNode = new OperationNode(null, nameFailInput, null);
                page.setTreeNode(operationNode);
                assertEquals(true, page.isStale());
                verify(nameFailInput);
	}
	
	@Test 
	public void testTypeComboSelectionListener() {
		final IOperation operation = EasymockModelUtils.createIOperationTypeMockFromSameModel();
		expect(operation.getOperationStyle()).andReturn(OperationType.REQUEST_RESPONSE).atLeastOnce();
		replay(operation);
		
		final OperationNode operationNode = new OperationNode(null, operation , null);
		
		final SIFormPageController controller = createNiceMock(SIFormPageController.class);
		replay(controller);
		
		final OperationDetailsPageExposer page = new OperationDetailsPageExposer(controller);
		page.initialize(managedForm);
		page.createContents(shell);
		page.setTreeNode(operationNode);
		page.setInput(operation);

		assertFalse(page.isDirty());
		// 1 = OperationType.ASYNCHRONOUS
		page.getTypeCombo().select(1);
		page.getTypeCombo().notifyListeners(SWT.Selection, new Event());
		
		assertTrue(page.isDirty());
		
		verify(operation);
		verify(controller);
	}
	
	@Test 
	public void testTypeComboFocusListener() {
		final IOperation operation = EasymockModelUtils.createIOperationTypeMockFromSameModel();
		expect(operation.getOperationStyle()).andReturn(OperationType.REQUEST_RESPONSE).atLeastOnce();
		replay(operation);
		
		final OperationNode operationNode = new OperationNode(null, operation , null);
		
		final SIFormPageController controller = createNiceMock(SIFormPageController.class);
		controller.editOperationTypeTriggered(operationNode, OperationType.ASYNCHRONOUS);
		replay(controller);
		
		final OperationDetailsPageExposer page = new OperationDetailsPageExposer(controller);
		page.initialize(managedForm);
		page.createContents(shell);
		page.setTreeNode(operationNode);
		page.setInput(operation);

		assertFalse(page.isDirty());
		// 1 = OperationType.ASYNCHRONOUS
		page.getTypeCombo().select(1);
		page.getTypeCombo().notifyListeners(SWT.Selection, new Event());
		
		assertTrue(page.isDirty());
		
		page.getTypeCombo().notifyListeners(SWT.FocusOut, new Event());
		
		verify(operation);
		verify(controller);
	}
	
	@Test 
	public void testNameControlFocusListener() {
		final String NAME = "faultName";
		final String NEW_NAME = "new" + NAME;
		
		final IOperation operation = EasymockModelUtils.createIOperationTypeMockFromSameModel();
		expect(operation.getName()).andReturn(NAME).atLeastOnce();
		replay(operation);
		
		final OperationNode operationNode = new OperationNode(null, operation , null);
		
		final SIFormPageController controller = createNiceMock(SIFormPageController.class);
		replay(controller);
		
		final OperationDetailsPageExposer page = new OperationDetailsPageExposer(controller);
		page.initialize(managedForm);
		page.createContents(shell);
		page.setTreeNode(operationNode);
		page.setInput(operation);

		assertFalse(page.isDirty());
		page.getNameTextControl().setText(NEW_NAME);
		assertTrue(page.isDirty());
		
		page.getNameTextControl().notifyListeners(SWT.FocusOut, new Event());
		
		assertFalse(page.isDirty());
		
		verify(operation);
		verify(controller);
	}
	
	@Test 
	public void testModifyTextListener() {
		final String NAME = "faultName";
		final String NEW_NAME = "new" + NAME;
		
		final IOperation operation = EasymockModelUtils.createIOperationTypeMockFromSameModel();
		expect(operation.getName()).andReturn(NAME).atLeastOnce();
		replay(operation);
		
		final OperationNode faultNode = new OperationNode(null, operation , null);
		
		final SIFormPageController controller = createNiceMock(SIFormPageController.class);
		replay(controller);
		
		final OperationDetailsPageExposer page = new OperationDetailsPageExposer(controller);
		page.initialize(managedForm);
		page.createContents(shell);
		page.setTreeNode(faultNode);
		page.setInput(operation);

		assertFalse(page.isDirty());
		
		page.getNameTextControl().setText(NEW_NAME);
		
		assertTrue(page.isDirty());
		
		verify(operation);
		verify(controller);
	}
	
	@Test
        public void testReadOnlySet() {
	        final IOperation operation = EasymockModelUtils.createIOperationTypeMockFromSameModel();
                
	        final SIFormPageController controller = createNiceMock(SIFormPageController.class);
                expect(controller.isResourceReadOnly()).andReturn(Boolean.valueOf(false)).atLeastOnce();
                replay(controller);
                
                final String OPERATION_NAME = "operationName";
                final String OPERATION_DOC = "operationDocumentation";
                expect(operation.getName()).andReturn(OPERATION_NAME).atLeastOnce();
                expect(operation.getDocumentation()).andReturn(OPERATION_DOC).atLeastOnce();
                expect(operation.getOperationStyle()).andReturn(OperationType.REQUEST_RESPONSE).atLeastOnce();
                replay(operation);
                
                final OperationNode operationNode = new OperationNode(null, operation , null);
                
                final IStructuredSelection selection = new StructuredSelection(operationNode);
                
                final OperationDetailsPageExposer page = new OperationDetailsPageExposer(controller);
                page.initialize(managedForm);
                page.createContents(shell);
                page.selectionChanged(null, selection);
                
                assertTrue(page.getNameTextControl().getEditable());
                assertTrue(page.getTypeCombo().getEnabled());
                
                verify(controller);
                reset(controller);
                expect(controller.isResourceReadOnly()).andReturn(Boolean.valueOf(true)).atLeastOnce();
                expect(controller.isPartOfEdittedDocument(operation)).andReturn(Boolean.valueOf(true)).anyTimes();
                replay(controller);
                
                page.selectionChanged(null, selection);
                assertFalse(page.getNameTextControl().getEditable());
                assertFalse(page.getTypeCombo().getEnabled());
                
                verify(operation,controller);
                
                verify(controller);
                reset(controller);
                expect(controller.isResourceReadOnly()).andReturn(Boolean.valueOf(true)).anyTimes();
                expect(controller.isPartOfEdittedDocument(operation)).andReturn(Boolean.valueOf(true)).anyTimes();
                replay(controller);
                
                page.selectionChanged(null, selection);
                assertFalse(page.getNameTextControl().getEditable());
                assertFalse(page.getTypeCombo().getEnabled());
                
                verify(operation,controller);
        }
	
	private class OperationDetailsPageExposer extends OperationDetailsPage {

		public OperationDetailsPageExposer(final SIFormPageController controller) {
			super(controller);
		}
		
		public Text getNameTextControl() {
			return nameTextControl;
		}
		
		public DocumentationSection getDocumentationSection() {
			return documentationSection;
		} 
		
		public CCombo getTypeCombo() {
			return typeCombo;
		} 
		
		public OperationType getInitialOpStyle() {
			return initialOpStyle;
		}		
		
		public void setTreeNode(final OperationNode newTreeNode) {
			treeNode = newTreeNode;
		}
		
		public void setInput(final IOperation newOperation) {
			input = newOperation;
		}
		
		
		
	}
}
