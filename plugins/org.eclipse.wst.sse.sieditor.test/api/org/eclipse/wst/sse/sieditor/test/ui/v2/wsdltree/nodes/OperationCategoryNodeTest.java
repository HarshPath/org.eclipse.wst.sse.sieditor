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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import junit.framework.Assert;

import org.easymock.EasyMock;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatusProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.factory.TreeNodeMapper;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.providers.WSDLLabelProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.FaultNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.OperationCategory;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.OperationCategoryNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ParameterNode;
import org.eclipse.wst.sse.sieditor.test.util.EasymockModelUtils;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.ui.Activator;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;

public class OperationCategoryNodeTest {

    @Test
    public void testGetFaultImage() {
        assertOperationCategoryNodeImage(Activator.NODE_OPER_FAULTS, OperationCategory.FAULT);
    }

    @Test
    public void testGetOutputImage() {
        assertOperationCategoryNodeImage(Activator.NODE_OPER_OUTPUT, OperationCategory.OUTPUT);
    }

    @Test
    public void testGetInputImage() {
        assertOperationCategoryNodeImage(Activator.NODE_OPER_INPUT, OperationCategory.INPUT);
    }

    @Test
    public void testGetFaultName() {
        final OperationCategoryNode node = new OperationCategoryNode(null, OperationCategory.FAULT, null, null);
        assertEquals(Messages.SI_FAULTS_XTND, node.getDisplayName());
    }

    @Test
    public void testGetOutputName() {
        final OperationCategoryNode node = new OperationCategoryNode(null, OperationCategory.OUTPUT, null, null);
        assertEquals(Messages.SI_OUTPUT_XTND, node.getDisplayName());
    }

    @Test
    public void testGetInputName() {
        final OperationCategoryNode node = new OperationCategoryNode(null, OperationCategory.INPUT, null, null);
        assertEquals(Messages.SI_INPUT_XTND, node.getDisplayName());
    }

    @Test
    public void testHasChildrenForInputOperation() {
        final IOperation mockOP = createNiceMock(IOperation.class);
        final ArrayList<IParameter> inputParams = new ArrayList<IParameter>();
        expect(mockOP.getAllInputParameters()).andReturn(inputParams);
        replay(mockOP);

        final OperationCategoryNode node = new OperationCategoryNode(null, OperationCategory.INPUT, mockOP, null);

        assertFalse(node.hasChildren());
        verify(mockOP);
    }

    @Test
    public void testHasChildrenForOutputOperation() {
        final IOperation mockOP = createNiceMock(IOperation.class);
        final ArrayList<IParameter> outputParams = new ArrayList<IParameter>();
        expect(mockOP.getAllOutputParameters()).andReturn(outputParams);
        replay(mockOP);

        final OperationCategoryNode node = new OperationCategoryNode(null, OperationCategory.OUTPUT, mockOP, null);

        assertFalse(node.hasChildren());
        verify(mockOP);
    }

    @Test
    public void testHasChildrenForFaultOperation() {
        final IOperation mockOP = createNiceMock(IOperation.class);
        final ArrayList<IFault> faultParams = new ArrayList<IFault>();
        expect(mockOP.getAllFaults()).andReturn(faultParams);
        replay(mockOP);

        final OperationCategoryNode node = new OperationCategoryNode(null, OperationCategory.FAULT, mockOP, null);

        assertFalse(node.hasChildren());
        verify(mockOP);
    }

    @Test
    public void testChildrenForInputOperationNoParams() {
        final IOperation mockOP = createNiceMock(IOperation.class);
        final ArrayList<IParameter> inputParams = new ArrayList<IParameter>();
        expect(mockOP.getAllInputParameters()).andReturn(inputParams);
        replay(mockOP);

        final OperationCategoryNode node = new OperationCategoryNode(null, OperationCategory.INPUT, mockOP, null);
        node.getChildren();

        verify(mockOP);
    }

    @Test
    public void testChildrenForInputOperationWithExistingParams() {
        final IParameter inputParameter = EasymockModelUtils.createIParameterTypeMockFromSameModel();
        replay(inputParameter);

        final ArrayList<IParameter> inputParams = new ArrayList<IParameter>();
        inputParams.add(inputParameter);

        final IOperation mockOP = createNiceMock(IOperation.class);
        expect(mockOP.getAllInputParameters()).andReturn(inputParams);
        replay(mockOP);

        final SIFormPageController testPage = new SIFormPageController(null, false, false);

        final OperationCategoryNode operationCategoryNode = new OperationCategoryNode(null, OperationCategory.INPUT, mockOP,
                testPage);
        final ParameterNode inParameterNode = new ParameterNode(operationCategoryNode, inputParameter, null);

        testPage.getTreeNodeMapper().addToNodeMap(inputParameter, inParameterNode);

        final Object[] children = operationCategoryNode.getChildren();

        assertEquals(1, children.length);
        assertSame(inParameterNode, children[0]);
        verify(mockOP);
    }

    @Test
    public void testChildrenForInputOperationNoExistingParams() {
        final IParameter inputParameter = EasymockModelUtils.createIParameterTypeMockFromSameModel();
        replay(inputParameter);

        final ArrayList<IParameter> inputParams = new ArrayList<IParameter>();
        inputParams.add(inputParameter);

        final IOperation mockOP = createNiceMock(IOperation.class);
        expect(mockOP.getAllInputParameters()).andReturn(inputParams);
        replay(mockOP);

        final boolean[] addToNodeMapCalled = { false };
        final TreeNodeMapper testTreeNodeMapper = new TreeNodeMapper() {
            @Override
            public void addToNodeMap(final IModelObject modelObject, final ITreeNode treeNode) {
                addToNodeMapCalled[0] = true;
            }

            @Override
            public ITreeNode getTreeNode(final IModelObject modelObject) {
                return null;
            }
        };

        final SIFormPageController testPage = new SIFormPageController(null, false, false) {
            @Override
            public TreeNodeMapper getTreeNodeMapper() {
                return testTreeNodeMapper;
            }

        };

        final OperationCategoryNode node = new OperationCategoryNode(null, OperationCategory.INPUT, mockOP, testPage);
        final Object[] children = node.getChildren();

        assertEquals(1, children.length);
        assertNotNull(children[0]);
        assertTrue(addToNodeMapCalled[0]);
        verify(mockOP);
    }

    @Test
    public void testChildrenForOutputOperationNoParams() {
        final IOperation mockOP = createNiceMock(IOperation.class);
        final ArrayList<IParameter> outputParams = new ArrayList<IParameter>();
        expect(mockOP.getAllOutputParameters()).andReturn(outputParams);
        replay(mockOP);

        final OperationCategoryNode node = new OperationCategoryNode(null, OperationCategory.OUTPUT, mockOP, null);
        node.getChildren();

        verify(mockOP);
    }

    @Test
    public void testChildrenForFaultOperationExistingFaultNode() {
        final IFault fault = createNiceMock(IFault.class);
        final ArrayList<IFault> faults = new ArrayList<IFault>();
        faults.add(fault);

        final IOperation mockOP = createNiceMock(IOperation.class);
        expect(mockOP.getAllFaults()).andReturn(faults);
        replay(mockOP);

        final FaultNode faultNode = new FaultNode(null, null, null);
        final TreeNodeMapper testTreeNodeMapper = new TreeNodeMapper() {
            @Override
            public ITreeNode getTreeNode(final IModelObject modelObject) {
                return faultNode;
            }
        };

        final SIFormPageController testPage = new SIFormPageController(null, false, false) {
            @Override
            public TreeNodeMapper getTreeNodeMapper() {
                return testTreeNodeMapper;
            }

        };
        final OperationCategoryNode node = new OperationCategoryNode(null, OperationCategory.FAULT, mockOP, testPage);
        final Object[] children = node.getChildren();

        assertEquals(1, children.length);
        assertSame(faultNode, children[0]);
        verify(mockOP);
    }

    @Test
    public void testChildrenForFaultOperationNotExistingFaultNode() {
        final IFault fault = createNiceMock(IFault.class);
        final ArrayList<IFault> faults = new ArrayList<IFault>();
        faults.add(fault);

        final IOperation mockOP = createNiceMock(IOperation.class);
        expect(mockOP.getAllFaults()).andReturn(faults);
        replay(mockOP);

        final TreeNodeMapper testTreeNodeMapper = new TreeNodeMapper() {
            @Override
            public ITreeNode getTreeNode(final IModelObject modelObject) {
                return null;
            }
        };

        final SIFormPageController testPage = new SIFormPageController(null, false, false) {
            @Override
            public TreeNodeMapper getTreeNodeMapper() {
                return testTreeNodeMapper;
            }

        };
        final OperationCategoryNode node = new OperationCategoryNode(null, OperationCategory.FAULT, mockOP, testPage);
        final Object[] children = node.getChildren();

        assertEquals(1, children.length);
        assertNotNull(children[0]);
        verify(mockOP);
    }

    private void assertOperationCategoryNodeImage(final String nodeOperation, final OperationCategory opCategory) {
        final Display display = Display.getDefault();
        final Image image = new Image(display, 10, 10);

        final ITreeNode parentNode = createMock(ITreeNode.class);
        expect(parentNode.isReadOnly()).andReturn(false).anyTimes();
        expect(parentNode.getCategories()).andReturn(ITreeNode.CATEGORY_MAIN).anyTimes();
        replay(parentNode);

        final OperationCategoryNode node = new OperationCategoryNode(parentNode, opCategory, null, null) {
            protected ImageRegistry getImageRegistry() {
                final ImageRegistry registry = new ImageRegistry(display);
                registry.put(nodeOperation, image);
                return registry;
            }
        };

        assertEquals(image, node.getImage());
    }

    /**
     * Make sure that if the categories are hidden from the tree, then the
     * correct tooltip text is shown
     */
    @Test
    public void testTooltip() {

        final SIFormPageController controller = new SIFormPageController(null, false, false);
        controller.setShowCategoryNodes(false);

        final OperationCategoryNode node = new OperationCategoryNode(null, OperationCategory.INPUT, null, controller);

        final ITreeNode someNode = createMock(ITreeNode.class);
        expect(someNode.getDisplayName()).andReturn("displayName").once();

        replay(someNode);

        final String tooltipText = node.getTooltipTextFor(someNode);
        Assert.assertEquals(org.eclipse.wst.sse.sieditor.ui.i18n.Messages.OperationCategoryNode_input_param_tooltip
                + "displayName", tooltipText);
        verify(someNode);
    }

    /**
     * Ensures that when the category nodes (Input/Output/Faults) are shown,
     * they will be decorated if their children are invalid
     */
    @Test
    public void testCategoryNodeDecoration() {

        final SIFormPageController controller = new SIFormPageController(null, false, false);
        controller.setShowCategoryNodes(true);

        final IOperation operationMock = createMock(IOperation.class);
        final IParameter parameterMock = EasymockModelUtils.createIParameterTypeMockFromSameModel();
        final ArrayList<IParameter> list = new ArrayList<IParameter>();
        list.add(parameterMock);
        expect(operationMock.getAllInputParameters()).andReturn(list).anyTimes();

        final ITreeNode parentNode = createNiceMock(ITreeNode.class);

        final IValidationStatusProvider validationService = createNiceMock(IValidationStatusProvider.class);

        expect(validationService.getStatusMarker((IModelObject) EasyMock.anyObject())).andReturn(IStatus.ERROR);
        replay(parentNode, validationService);

        final OperationCategoryNode operationCategoryNode = new OperationCategoryNode(parentNode, OperationCategory.INPUT,
                operationMock, controller);

        final Image expectedImage = Activator.getDefault().getImage(operationCategoryNode.getImage(), IStatus.ERROR);

        final WSDLLabelProvider labelProvider = new WSDLLabelProvider() {
            @Override
            protected IValidationStatusProvider getValidationStatusProvider(final Object modelObject) {

                return validationService;
            };

        };

        replay(operationMock, parameterMock);

        final Image actualImage = labelProvider.getImage(operationCategoryNode);

        Assert.assertSame(expectedImage, actualImage);
    }
}
