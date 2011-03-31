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
package org.eclipse.wst.sse.sieditor.test.ui.v2.factory;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesContentProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IDataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.factory.TreeNodeMapper;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.StructureType;

public class TestTreeNodeMapper extends SIEditorBaseTest {
	
	private TreeNodeMapper treeNodeMapper;
	
	IXSDModelRoot xsdModelRoot;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		
		treeNodeMapper = new TreeNodeMapper();
		xsdModelRoot = getXSDModelRoot("validation/circularElementRef.xsd", "circularElementRef.xsd"); 
		
		IDataTypesFormPageController controller = createMock(IDataTypesFormPageController.class);
		expect(controller.getTreeNodeMapper()).andReturn(treeNodeMapper).anyTimes();
		replay(controller);
		
		DataTypesContentProvider contentProvider = new DataTypesContentProvider(controller);
		recursiveCreateAllTreeNodes(contentProvider.getElements(xsdModelRoot));
	}

	@Test
	public void testDoNotReturnFirstReferenceNode() throws IOException, CoreException {
        ISchema schema = xsdModelRoot.getSchema();
		StructureType element2 = (StructureType)schema .getType(true, "Element2");
		IElement anonymousElement = element2.getElements("AnonymousElement").iterator().next();
		
		List<ITreeNode> treeNodesForAnonymousElement = treeNodeMapper.getTreeNode(anonymousElement, ITreeNode.CATEGORY_MAIN);
		assertTrue("It is expected that the model has one main \"AnonymousElement\", and one reference to \"AnonymousElement\".", 
				treeNodesForAnonymousElement.size() > 1);
		
		ITreeNode anonymousElementTreeNode = treeNodeMapper.getTreeNode(anonymousElement);
		assertTrue("It is expected the real, but not referance \"AnonymousElement\" to be returned.", 
				(anonymousElementTreeNode.getCategories() & ITreeNode.CATEGORY_REFERENCE) == 0);
	}

	private void recursiveCreateAllTreeNodes(Object[] elements) {
		for(Object element : elements) {
			if(element instanceof ITreeNode) {
				recursiveCreateAllTreeNodes(((ITreeNode)element).getChildren());
			}
		}
	}
}
