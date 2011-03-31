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
package org.eclipse.wst.sse.sieditor.test.ui.v2.nodes.impl;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.swt.graphics.Image;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.impl.AbstractTreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.labels.ITreeNodeLabelsProviderFactory;
import org.eclipse.wst.sse.sieditor.test.util.EasymockModelUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;

public class AbstractTreeNodeTest {
    
    private IElement modelObject;
    
    private ITreeNode parent;

    @Before
    public void setUp() throws Exception {
        modelObject = EasymockModelUtils.createIElementMockFromSameModel();
        parent = createNiceMock(ITreeNode.class);
    }

    @After
    public void tearDown() throws Exception {
        modelObject = null;
        parent = null;
    }
    
    @Test
    public void testTreeNodeCategoryReferanceUpdatedOnIsReadonlyCall() {
    	replay(parent, modelObject);
    	final TestAbstractTreeNode node = new TestAbstractTreeNode(modelObject, parent);
    	
    	assertFalse(node.isReadOnly());
    	assertEquals(0, node.getCategories() & ITreeNode.CATEGORY_REFERENCE);
    	
    	// modelObject category have to be changed after construction of the 'node'
    	reset(modelObject);
    	EasymockModelUtils.recordReferingIElementMockFromSameModel(modelObject);
    	replay(modelObject);
    	
    	assertTrue(node.isReadOnly());
    	assertEquals(ITreeNode.CATEGORY_REFERENCE, node.getCategories() & ITreeNode.CATEGORY_REFERENCE);
    }
    
    @Test
    public void testParentCategoryReferanceIsTransferedToChildrenOnIsReadonlyCall() {
    	replay(parent, modelObject);
    	final TestAbstractTreeNode node = new TestAbstractTreeNode(modelObject, parent);
    	
    	assertFalse(node.isReadOnly());
    	assertEquals(0, node.getCategories() & ITreeNode.CATEGORY_REFERENCE);
    	
    	// parent category have to be changed after construction of the 'node'
    	reset(parent);
    	expect(parent.getCategories()).andReturn(ITreeNode.CATEGORY_REFERENCE).anyTimes();
    	replay(parent);
    	
    	assertTrue(node.isReadOnly());
    	assertEquals(ITreeNode.CATEGORY_REFERENCE, node.getCategories() & ITreeNode.CATEGORY_REFERENCE);
    }

    @Test
    public void testGetModelObject() {
        final TestAbstractTreeNode node = new TestAbstractTreeNode(modelObject, parent);
        assertTrue(node.getModelObject().equals(modelObject));
    }

    @Test
    public void testGetParent() {
        final TestAbstractTreeNode node = new TestAbstractTreeNode(modelObject, parent);
        assertTrue(node.getParent().equals(parent));
    }

    @Test
    public void testHasChildren() {
        replay(modelObject, parent);
        
        TestAbstractTreeNode node = new TestAbstractTreeNode(modelObject, parent) {
            @Override
            public Object[] getChildren() {
                return UIConstants.EMPTY_ARRAY;
            }
        };
        assertFalse(node.hasChildren());
        
        
        node = new TestAbstractTreeNode(modelObject, parent) {
            @Override
            public Object[] getChildren() {
                return new Object[] {new Object()};
            }
        };
        assertTrue(node.hasChildren());
    }
    
    class TestAbstractTreeNode extends AbstractTreeNode {

        public TestAbstractTreeNode(final IModelObject modelObject, final ITreeNode parent) {
            super(modelObject, parent, null);
        }

        @Override
        public Object[] getChildren() {
            return null;
        }

        @Override
        public String getDisplayName() {
            return null;
        }

        @Override
        public Image getImage() {
            return null;
        }

        @Override
        protected ITreeNodeLabelsProviderFactory getLabelsProviderFactory() {
            return null;
        }
        
    }

}
