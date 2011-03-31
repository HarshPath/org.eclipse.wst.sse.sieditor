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
package org.eclipse.wst.sse.sieditor.test.ui.v2.wsdltree.nodes;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.OperationType;
import org.eclipse.wst.sse.sieditor.test.util.EasymockModelUtils;
import org.eclipse.wst.sse.sieditor.ui.v2.factory.TreeNodeMapper;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.OperationCategory;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.OperationCategoryNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.OperationNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ParameterNode;
import org.junit.Test;

public class OperationNodeTest {

    @Test
    public void testRequestResponseChildrenWithExistingOpNode() {
        final OperationCategoryNode opInCategoryNode = new OperationCategoryNode(null, null, null, null);
        final OperationCategoryNode opOutCategoryNode = new OperationCategoryNode(null, null, null, null);
        final OperationCategoryNode opFaultCategoryNode = new OperationCategoryNode(null, null, null, null);
        final TreeNodeMapper testTreeNodeMapper = new TreeNodeMapper() {
            @Override
            public ITreeNode getCategoryNode(final String category, final Object modelObject) {
                if (OperationCategory.INPUT.toString().equals(category)) {
                    return opInCategoryNode;
                } else if (OperationCategory.OUTPUT.toString().equals(category)) {
                    return opOutCategoryNode;
                } else if (OperationCategory.FAULT.toString().equals(category)) {
                    return opFaultCategoryNode;
                }
                return null;
            }
        };

        final SIFormPageController testPage = new SIFormPageController(null, false, false) {
            @Override
            public TreeNodeMapper getTreeNodeMapper() {
                return testTreeNodeMapper;
            }

        };

        testPage.setShowCategoryNodes(true);

        final IOperation mockOP = createNiceMock(IOperation.class);
        expect(mockOP.getOperationStyle()).andReturn(OperationType.REQUEST_RESPONSE);
        replay(mockOP);

        final OperationNode opNode = new OperationNode(null, mockOP, testPage);
        final Object[] children = opNode.getChildren();
        final List childrenList = Arrays.asList(children);

        assertTrue(childrenList.contains(opInCategoryNode));
        assertTrue(childrenList.contains(opOutCategoryNode));
        assertTrue(childrenList.contains(opFaultCategoryNode));

        verify(mockOP);
    }

    @Test
    public void testRequestResponseChildrenNotExistingOpNode() {
        final TreeNodeMapper testTreeNodeMapper = new TreeNodeMapper() {
            @Override
            public ITreeNode getCategoryNode(final String category, final Object modelObject) {
                return null;
            }
        };

        final SIFormPageController testPage = new SIFormPageController(null, false, false) {
            @Override
            public TreeNodeMapper getTreeNodeMapper() {
                return testTreeNodeMapper;
            }

        };

        testPage.setShowCategoryNodes(true);
        final IOperation mockOP = createNiceMock(IOperation.class);
        expect(mockOP.getOperationStyle()).andReturn(OperationType.REQUEST_RESPONSE);
        replay(mockOP);

        final OperationNode opNode = new OperationNode(null, mockOP, testPage);

        boolean hasInputCategoryNode = false;
        boolean hasOutputCategoryNode = false;
        boolean hasFaultCategoryNode = false;
        for (final Object node : opNode.getChildren()) {
            final OperationCategoryNode categoryNode = (OperationCategoryNode) node;
            if (categoryNode.getOperationCategory().equals(OperationCategory.INPUT)) {
                hasInputCategoryNode = true;
            } else if (categoryNode.getOperationCategory().equals(OperationCategory.FAULT)) {
                hasFaultCategoryNode = true;
            } else if (categoryNode.getOperationCategory().equals(OperationCategory.OUTPUT)) {
                hasOutputCategoryNode = true;
            }
        }

        assertTrue(hasInputCategoryNode);
        assertTrue(hasOutputCategoryNode);
        assertTrue(hasFaultCategoryNode);

        verify(mockOP);
    }

    @Test
    public void testAnyChildrenWithExistingOpNode() {
        final OperationCategoryNode opInCategoryNode = new OperationCategoryNode(null, null, null, null);
        final OperationCategoryNode opFaultCategoryNode = new OperationCategoryNode(null, OperationCategory.FAULT, null, null);
        final TreeNodeMapper testTreeNodeMapper = new TreeNodeMapper() {
            @Override
            public ITreeNode getCategoryNode(final String category, final Object modelObject) {
                if (OperationCategory.INPUT.toString().equals(category)) {
                    return opInCategoryNode;
                }
                if (OperationCategory.FAULT.toString().equals(category)) {
                    return opFaultCategoryNode;
                }
                return null;
            }
        };

        final SIFormPageController testPage = new SIFormPageController(null, false, false) {
            @Override
            public TreeNodeMapper getTreeNodeMapper() {
                return testTreeNodeMapper;
            }
        };

        testPage.setShowCategoryNodes(true);

        final IOperation mockOP = createNiceMock(IOperation.class);
        expect(mockOP.getOperationStyle()).andReturn(OperationType.ASYNCHRONOUS);
        replay(mockOP);

        final OperationNode opNode = new OperationNode(null, mockOP, testPage);
        final Object[] children = opNode.getChildren();
        final List childrenList = Arrays.asList(children);

        assertTrue(childrenList.contains(opInCategoryNode));
        assertFalse(childrenList.contains(opFaultCategoryNode));
        verify(mockOP);
    }

    @Test
    public void testAsyncChildrenWithExistingOpNodeWithFaults() {
        final OperationCategoryNode opInCategoryNode = new OperationCategoryNode(null, OperationCategory.INPUT, null, null);
        final OperationCategoryNode opFaultCategoryNode = new OperationCategoryNode(null, OperationCategory.FAULT, null, null);
        final TreeNodeMapper testTreeNodeMapper = new TreeNodeMapper() {
            @Override
            public ITreeNode getCategoryNode(final String category, final Object modelObject) {
                if (OperationCategory.INPUT.toString().equals(category)) {
                    return opInCategoryNode;
                }
                if (OperationCategory.FAULT.toString().equals(category)) {
                    return opFaultCategoryNode;
                }
                return null;
            }
        };

        final SIFormPageController testController = new SIFormPageController(null, false, true) {
            @Override
            public TreeNodeMapper getTreeNodeMapper() {
                return testTreeNodeMapper;
            }

        };

        testController.setShowCategoryNodes(true);

        final IOperation mockOP = createNiceMock(IOperation.class);
        expect(mockOP.getOperationStyle()).andReturn(OperationType.ASYNCHRONOUS);
        replay(mockOP);

        final OperationNode opNode = new OperationNode(null, mockOP, testController);
        final Object[] children = opNode.getChildren();
        final List childrenList = Arrays.asList(children);

        assertTrue(childrenList.contains(opInCategoryNode));
        assertTrue(childrenList.contains(opFaultCategoryNode));

        verify(mockOP);
    }
    
    /**
     * Asserts that the category nodes won't be included in the children.
     */
    @Test
    public void testGetChildrenNoCategories() {
        
        final IOperation operation = EasymockModelUtils.createIOperationTypeMockFromSameModel();        
        expect(operation.getOperationStyle()).andReturn(OperationType.ASYNCHRONOUS).once();
        final Collection<IParameter> parameters = new ArrayList<IParameter>();
        final IParameter parameter = EasymockModelUtils.createIParameterTypeMockFromSameModel();
        parameters.add(parameter);
        
        expect(operation.getAllInputParameters()).andReturn(parameters).once();
        
        final SIFormPageController controller = createMock(SIFormPageController.class);        
        final TreeNodeMapper mapper = createMock(TreeNodeMapper.class);

        expect(controller.isAsynchronousOperationFaultsEnabled()).andReturn(Boolean.valueOf(false));
        expect(controller.getTreeNodeMapper()).andReturn(mapper).anyTimes();
        expect(controller.isShowCategoryNodes()).andReturn(false).once();

        replay(controller, parameter);
        
        final OperationCategoryNode categoryNode = new OperationCategoryNode(null,OperationCategory.INPUT,operation,controller);
        final ParameterNode paramNode = new ParameterNode(null, parameter, null);
        final List<ITreeNode> paramNodesList = new ArrayList<ITreeNode>();
        paramNodesList.add(paramNode);
        
        expect(mapper.getCategoryNode(OperationCategory.INPUT.toString(), operation)).andReturn(categoryNode).once();
        expect(mapper.getTreeNode(parameter, categoryNode)).andReturn(paramNodesList).anyTimes();
        
        replay(operation, mapper);
        
        final OperationNode operationNode = new OperationNode(null, operation, controller);        
        
        
        final Object[] children = operationNode.getChildren();
        Assert.assertEquals(1, children.length);
        Assert.assertSame(paramNode, children[0]);

        verify(operation, parameter, controller, mapper);        
    }
}

