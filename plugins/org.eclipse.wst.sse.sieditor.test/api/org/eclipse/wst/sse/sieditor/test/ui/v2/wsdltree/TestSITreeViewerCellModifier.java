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
package org.eclipse.wst.sse.sieditor.test.ui.v2.wsdltree;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.SITreeViewerCellModifier;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;

public class TestSITreeViewerCellModifier {

	@Test
	public void testCanEditTreeNode() {
	        final IModelObject modelObjectMock = createMock(IModelObject.class);
		final ITreeNode node = createNiceMock(ITreeNode.class);
                expect(node.getModelObject()).andReturn(modelObjectMock).anyTimes();
                expect(node.isReadOnly()).andReturn(false).anyTimes();
		replay(node);
		
		final SIFormPageController mockController = createNiceMock(SIFormPageController.class);
		replay(mockController);
		
		final SITreeViewerCellModifier modifier = new SITreeViewerCellModifier(mockController);
		modifier.setSelectedElement(node);
		assertTrue(modifier.canModify(node, ""));
	}
	
	@Test
	public void testModifyTreeNode() {
		final ITreeNode node = createNiceMock(ITreeNode.class);
		replay(node);
		
		final Display display = Display.getDefault();
        final Composite composite = new Composite(new Shell(display), SWT.NONE);
		final Tree tree = new Tree(composite, SWT.SINGLE);
		final TreeItem item = new TreeItem(tree, SWT.NONE);
		item.setData(node);
		
		final String newName = "newName";
		final SIFormPageController mockController = createNiceMock(SIFormPageController.class);
		mockController.editItemNameTriggered(node, newName);
		replay(mockController);
		
		final SITreeViewerCellModifier modifier = new SITreeViewerCellModifier(mockController);
		modifier.setSelectedElement(node);
		modifier.modify(item, "", newName);
		
		verify(mockController);
	}
	
	@Test
	public void testGetValueDelegateToNodeName() {
		final ITreeNode node = createNiceMock(ITreeNode.class);
		expect(node.getDisplayName()).andReturn("nodeName");
		replay(node);
		
		final SITreeViewerCellModifier modifier = new SITreeViewerCellModifier(null);
		modifier.setSelectedElement(node);
		modifier.getValue(node, "");
		
		verify(node);
	}
}
