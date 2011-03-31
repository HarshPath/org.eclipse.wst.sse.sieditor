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
package org.eclipse.wst.sse.sieditor.test.ui.v2.providers;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.providers.WSDLContentProvider;
import org.junit.Test;


public class TestWSDLContentProvider {

	@Test
	public void testGetChildrenDelegatesToParentElement() {
		ITreeNode parentElement = createMock(ITreeNode.class);
		expect(parentElement.getChildren()).andReturn(null);
		replay(parentElement);
		
		WSDLContentProvider provider = new WSDLContentProvider(null);
		provider.getChildren(parentElement);
		
		verify(parentElement);
	}
	
	@Test
	public void testGetParentDelegatesToParentElement() {
		ITreeNode parentElement = createMock(ITreeNode.class);
		expect(parentElement.getParent()).andReturn(null);
		replay(parentElement);
		
		WSDLContentProvider provider = new WSDLContentProvider(null);
		provider.getParent(parentElement);
		
		verify(parentElement);
	}
	
	@Test
	public void testHasChildrenDelegatesToParentElement() {
		ITreeNode parentElement = createMock(ITreeNode.class);
		expect(parentElement.hasChildren()).andReturn(false);
		replay(parentElement);
		
		WSDLContentProvider provider = new WSDLContentProvider(null);
		provider.hasChildren(parentElement);
		
		verify(parentElement);
	}
}
