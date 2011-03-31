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

import org.easymock.EasyMock;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.wst.sse.sieditor.ui.v2.AbstractMasterDetailsBlock.TreeViewerSelectionListener;
import org.junit.Test;


public class TreeViewerSelectionListenerTest {
	
	private IManagedForm managedForm;
	
	private void initFixture(int selectionSize) {
		managedForm = EasyMock.createNiceMock(IManagedForm.class);
		
    	ISelectionProvider selectionProvider = EasyMock.createNiceMock(ISelectionProvider.class);
    	IStructuredSelection selection = EasyMock.createNiceMock(IStructuredSelection.class);
    	
    	if (selectionSize <= 1) {
    		managedForm.fireSelectionChanged(null, selection);
    	} else {
    		managedForm.fireSelectionChanged(null, null);
    	}
    	
    	EasyMock.expect(selection.size()).andStubReturn(selectionSize);
    	
    	EasyMock.replay(selectionProvider, selection, managedForm);
    	
    	TreeViewerSelectionListener listener = new TreeViewerSelectionListener(null, managedForm);
    	listener.selectionChanged(new SelectionChangedEvent(selectionProvider, selection));
	}
	
	@Test
	public void testEmptySelection() {
		initFixture(0);
		EasyMock.verify(managedForm);
		
	}

	@Test
	public void testSingleSelection() {
		initFixture(1);
		EasyMock.verify(managedForm);
	}
	
	@Test
	public void testMultipleSelection() {
		initFixture(2);
		EasyMock.verify(managedForm);
	}
	
}
