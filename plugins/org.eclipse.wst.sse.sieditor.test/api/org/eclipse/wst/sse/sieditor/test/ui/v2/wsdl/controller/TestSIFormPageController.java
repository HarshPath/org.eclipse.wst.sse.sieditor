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
package org.eclipse.wst.sse.sieditor.test.ui.v2.wsdl.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import junit.framework.Assert;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.sse.sieditor.ui.v2.eventing.ISIEvent;
import org.eclipse.wst.sse.sieditor.ui.v2.eventing.ISIEventListener;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.utils.UIUtils;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.FaultNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.OperationCategory;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.OperationCategoryNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ParameterNode;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.OperationType;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.OperationFault;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.OperationParameter;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

public class TestSIFormPageController extends SIEditorBaseTest {
    private IWsdlModelRoot wsdlModelRoot = null;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        wsdlModelRoot = getWSDLModelRoot("pub/self/mix2/PurchaseOrderConfirmation.wsdl", "PurchaseOrderConfirmation.wsdl");
    }

    @Test
    public void testEditOperationType() {
        final SIFormPageController controller = new SIFormPageController(wsdlModelRoot, false, false);
        final Object[] allServiceInterfaceNodes = controller.getAllServiceInterfaceNodes(wsdlModelRoot.getDescription());
        final ITreeNode sieNode = (ITreeNode) allServiceInterfaceNodes[0];
        final Object[] children = sieNode.getChildren();
        final ITreeNode operationNode = (ITreeNode) children[0];
        final IOperation operation = (IOperation) operationNode.getModelObject();

        assertEquals(OperationType.REQUEST_RESPONSE, operation.getOperationStyle());
        assertTrue(controller.canEdit(operationNode));

        controller.editOperationTypeTriggered(operationNode, OperationType.ASYNCHRONOUS);

        assertEquals(OperationType.ASYNCHRONOUS, operation.getOperationStyle());
    }

    @Test
    public void testEditDocumentation() {
        final SIFormPageController controller = new SIFormPageController(wsdlModelRoot, false, false);
        controller.setShowCategoryNodes(true);

        final Object[] allServiceInterfaceNodes = controller.getAllServiceInterfaceNodes(wsdlModelRoot.getDescription());
        final ITreeNode sieNode = (ITreeNode) allServiceInterfaceNodes[0];
        final Object[] children = sieNode.getChildren();
        final ITreeNode operationNode = (ITreeNode) children[0];
        final ITreeNode inputParam = getFirstTreeNodeByCategory(operationNode, OperationCategory.INPUT);
        final ParameterNode inputParamNode = (ParameterNode) inputParam.getChildren()[0];
        final OperationParameter operationInputParam = (OperationParameter) inputParamNode.getModelObject();

        final String newDocText = "This is new documentation!";
        assertTrue(controller.canEdit(inputParamNode));
        controller.editDocumentation(inputParamNode, newDocText);

        assertEquals(newDocText, operationInputParam.getDocumentation());
    }

    @Test
    public void testEditParameterName() {
        final SIFormPageController controller = new SIFormPageController(wsdlModelRoot, false, false);
        controller.setShowCategoryNodes(true);

        final Object[] allServiceInterfaceNodes = controller.getAllServiceInterfaceNodes(wsdlModelRoot.getDescription());
        final ITreeNode sieNode = (ITreeNode) allServiceInterfaceNodes[0];
        final Object[] children = sieNode.getChildren();
        final ITreeNode operationNode = (ITreeNode) children[0];
        final ITreeNode inputParam = getFirstTreeNodeByCategory(operationNode, OperationCategory.INPUT);
        final ParameterNode inputParamNode = (ParameterNode) inputParam.getChildren()[0];
        final OperationParameter operationInputParam = (OperationParameter) inputParamNode.getModelObject();

        assertTrue(controller.canEdit(inputParamNode));
        assertTrue(controller.isRenameItemEnabled(inputParamNode));
        controller.editItemNameTriggered(inputParamNode, "newName");

        assertEquals("newName", operationInputParam.getName());
    }

    @Test
    public void testEditParameterNameWithDuplicatedOne() throws IOException, CoreException {
        wsdlModelRoot = getWSDLModelRoot("pub/self/mix/SalesOrderApp.wsdl", "SalesOrderApp.wsdl");
        final SIFormPageController controller = new SIFormPageController(wsdlModelRoot, false, false);
        controller.setShowCategoryNodes(true);
        final Object[] allServiceInterfaceNodes = controller.getAllServiceInterfaceNodes(wsdlModelRoot.getDescription());
        final ITreeNode sieNode = (ITreeNode) allServiceInterfaceNodes[0];
        final Object[] children = sieNode.getChildren();
        final ITreeNode operationNode = (ITreeNode) children[0];
        final ITreeNode inputParam = getFirstTreeNodeByCategory(operationNode, OperationCategory.INPUT);
        final ParameterNode inputParamNode1 = (ParameterNode) inputParam.getChildren()[0];
        final OperationParameter operationInputParam1 = (OperationParameter) inputParamNode1.getModelObject();
        final ParameterNode inputParamNode2 = (ParameterNode) inputParam.getChildren()[1];
        final OperationParameter operationInputParam2 = (OperationParameter) inputParamNode2.getModelObject();

        assertTrue(controller.canEdit(inputParamNode1));
        assertTrue(controller.isRenameItemEnabled(inputParamNode1));

        final boolean[] duplicateNameException = { false };
        controller.addEventListener(new ISIEventListener() {
            public void notifyEvent(final ISIEvent event) {
                duplicateNameException[0] = true;
            }
        });

        controller.editItemNameTriggered(inputParamNode1, operationInputParam2.getName());

        assertEquals(operationInputParam2.getName(), operationInputParam1.getName());
        assertEquals("no error messages events are fired", false, duplicateNameException[0]); //$NON-NLS-1$
    }

    @Test
    public void testEditParameterType() {
        testEditParameter(OperationCategory.INPUT);
    }

    @Test
    public void testEditFaultType() {
        testEditParameter(OperationCategory.FAULT);
    }

    public void testEditParameter(final OperationCategory category) {
        final SIFormPageController controller = new SIFormPageController(wsdlModelRoot, false, false);
        controller.setShowCategoryNodes(true);
        final Object[] allServiceInterfaceNodes = controller.getAllServiceInterfaceNodes(wsdlModelRoot.getDescription());
        final ITreeNode sieNode = (ITreeNode) allServiceInterfaceNodes[0];
        final Object[] children = sieNode.getChildren();
        final ITreeNode operationNode = (ITreeNode) children[0];
        final ITreeNode param = getFirstTreeNodeByCategory(operationNode, category);
        final ITreeNode paramNode = (ITreeNode) param.getChildren()[0];
        final IModelObject operationParam = paramNode.getModelObject();

        final IType booleanType = UIUtils.instance().getCommonTypeByName("boolean");

        assertTrue(controller.canEdit(paramNode));
        controller.editParameterTypeTriggered(paramNode, booleanType);

        // assertTrue("SimpleType is expected to be wrapped in StructureType!",
        // operationInputParam.getType() instanceof StructureType);
        //
        // StructureType wraperStructure = (StructureType)
        // operationInputParam.getType();
        // IElement element =
        // wraperStructure.getAllElements().iterator().next();

        if (operationParam instanceof OperationFault) {
            assertEquals(booleanType, ((OperationFault) operationParam).getParameters().iterator().next().getType());
        } else if (operationParam instanceof OperationParameter) {
            assertEquals(booleanType, ((OperationParameter) operationParam).getType());
        } else {
            Assert.fail("Unexpecte parameter type");
        }

    }

    @Test
    public void testEditParameterTypeAsString() {
        final SIFormPageController controller = new SIFormPageController(wsdlModelRoot, false, false);
        controller.setShowCategoryNodes(true);
        final Object[] allServiceInterfaceNodes = controller.getAllServiceInterfaceNodes(wsdlModelRoot.getDescription());
        final ITreeNode sieNode = (ITreeNode) allServiceInterfaceNodes[0];
        final Object[] children = sieNode.getChildren();
        final ITreeNode operationNode = (ITreeNode) children[0];
        final ITreeNode inputParam = getFirstTreeNodeByCategory(operationNode, OperationCategory.INPUT);
        final ParameterNode inputParamNode = (ParameterNode) inputParam.getChildren()[0];
        final OperationParameter operationInputParam = (OperationParameter) inputParamNode.getModelObject();

        assertTrue(controller.canEdit(inputParamNode));
        controller.editParameterTypeTriggered(inputParamNode, "boolean");

        // assertTrue("SimpleType is expected to be wrapped in StructureType!",
        // operationInputParam.getType() instanceof StructureType);
        //
        // StructureType wraperStructure = (StructureType)
        // operationInputParam.getType();
        // IElement element =
        // wraperStructure.getAllElements().iterator().next();

        assertEquals("boolean", operationInputParam.getType().getName());
    }

    @Test
    public void testEditWsdlDescriptionNamespace() {
        final SIFormPageController controller = new SIFormPageController(wsdlModelRoot, false, false);
        final IDescription description = wsdlModelRoot.getDescription();

        final String namespace = description.getNamespace();

        final String newNamespace = namespace + "/my";
        controller.editDescriptionNamespaceTriggered(newNamespace);

        assertEquals(newNamespace, description.getNamespace());
    }

//    @Test
//    public void testAddNewServiceInterface() throws ExecutionException {
//        final SIFormPageController controller = new SIFormPageController(wsdlModelRoot, false, false);
//        controller.setShowCategoryNodes(true);
//        final IDescription description = wsdlModelRoot.getDescription();
//        final Object[] allServiceInterfaceNodes = controller.getAllServiceInterfaceNodes(description);
//        final ITreeNode sieNode = (ITreeNode) allServiceInterfaceNodes[0];
//
//        assertTrue(controller.isAddNewServiceInterfaceEnabled(sieNode));
//
//        controller.addNewServiceInterface();
//        final Object[] newAllServiceInterfaceNodes = controller.getAllServiceInterfaceNodes(description);
//
//        assertEquals(allServiceInterfaceNodes.length + 1, newAllServiceInterfaceNodes.length);
//
//        // get the newly added SI
//        final Set<?> newInterfaces = new HashSet<Object>(Arrays.asList(newAllServiceInterfaceNodes));
//        newInterfaces.removeAll(Arrays.asList(allServiceInterfaceNodes));
//
//        assertEquals(1, newInterfaces.size());
//        final Object addedInterface = newInterfaces.iterator().next();
//        assertTrue(addedInterface instanceof ServiceInterfaceNode);
//
//        final ServiceInterfaceNode node = (ServiceInterfaceNode) addedInterface;
//        final IServiceInterface serviceInterface = (IServiceInterface) node.getModelObject();
//
//        // assert that the newly added SI has one operation
//        assertEquals(1, serviceInterface.getAllOperations().size());
//
//        final IEnvironment env = wsdlModelRoot.getEnv();
//
//        env.getOperationHistory().undo(env.getUndoContext(), new NullProgressMonitor(), null);
//        assertEquals(allServiceInterfaceNodes.length, controller.getAllServiceInterfaceNodes(description).length);
//
//        env.getOperationHistory().redo(env.getUndoContext(), new NullProgressMonitor(), null);
//        assertEquals(allServiceInterfaceNodes.length + 1, controller.getAllServiceInterfaceNodes(description).length);
//    }

    @Test
    public void testAddNewOperationToServiceInterface() {
        final SIFormPageController controller = new SIFormPageController(wsdlModelRoot, false, false);

        final Object[] allServiceInterfaceNodes = controller.getAllServiceInterfaceNodes(wsdlModelRoot.getDescription());
        final ITreeNode sieNode = (ITreeNode) allServiceInterfaceNodes[0];
        final IServiceInterface sie = (IServiceInterface) sieNode.getModelObject();
        final Collection<IOperation> allOperations = sie.getAllOperations();

        assertTrue(controller.isAddNewOperationEnabled(sieNode));

        controller.addNewOperation(sieNode);

        final Collection<IOperation> allNewOperations = sie.getAllOperations();

        assertEquals(allOperations.size() + 1, allNewOperations.size());
    }

    @Test
    public void testIsDeleteItemsEnabled() {
        final SIFormPageController controller = new SIFormPageController(wsdlModelRoot, false, false);
        Assert.assertFalse(controller.isDeleteItemsEnabled(null));
        Assert.assertFalse(controller.isDeleteItemsEnabled(new Object[0]));
        Assert.assertFalse(controller.isDeleteItemsEnabled(new Object[] { new Object() }));
    }

    @Test
    public void testAddNewFaultToServiceOperation() {
        final SIFormPageController controller = new SIFormPageController(wsdlModelRoot, false, false);

        final Object[] allServiceInterfaceNodes = controller.getAllServiceInterfaceNodes(wsdlModelRoot.getDescription());
        final ITreeNode sieNode = (ITreeNode) allServiceInterfaceNodes[0];
        final Object[] children = sieNode.getChildren();
        final ITreeNode operationNode = (ITreeNode) children[0];
        final IOperation operation = (IOperation) operationNode.getModelObject();

        final Collection<IFault> allFaults = operation.getAllFaults();

        assertTrue(controller.isAddNewFaultEnabled(operationNode));

        controller.addNewFault(operationNode);

        final Collection<IFault> allNewFaults = operation.getAllFaults();

        assertEquals(allFaults.size() + 1, allNewFaults.size());
    }

    @Test
    public void testAddNewInputParameterToServiceOperation() {
        final SIFormPageController controller = new SIFormPageController(wsdlModelRoot, false, false);
        controller.setShowCategoryNodes(true);
        final Object[] allServiceInterfaceNodes = controller.getAllServiceInterfaceNodes(wsdlModelRoot.getDescription());
        final ITreeNode sieNode = (ITreeNode) allServiceInterfaceNodes[0];
        final Object[] children = sieNode.getChildren();
        final ITreeNode operationNode = (ITreeNode) children[0];
        final ITreeNode inputParamCategory = getFirstTreeNodeByCategory(operationNode, OperationCategory.INPUT);
        final ITreeNode inputParam = (ITreeNode) inputParamCategory.getChildren()[0];

        final IOperation operation = (IOperation) operationNode.getModelObject();
        final Collection<IParameter> allInputParameters = operation.getAllInputParameters();

        assertTrue(controller.isAddNewInParameterEnabled((inputParam)));

        controller.addNewParameter(inputParam, OperationCategory.INPUT);

        final Collection<IParameter> allNewInputParameters = operation.getAllInputParameters();

        assertEquals(allInputParameters.size() + 1, allNewInputParameters.size());
    }

    @Test
    public void testAddNewOutputParameterToServiceOperation() {
        final SIFormPageController controller = new SIFormPageController(wsdlModelRoot, false, false);
        controller.setShowCategoryNodes(true);
        final Object[] allServiceInterfaceNodes = controller.getAllServiceInterfaceNodes(wsdlModelRoot.getDescription());
        final ITreeNode sieNode = (ITreeNode) allServiceInterfaceNodes[0];
        final Object[] children = sieNode.getChildren();
        final ITreeNode operationNode = (ITreeNode) children[0];
        final ITreeNode outputParamCategory = getFirstTreeNodeByCategory(operationNode, OperationCategory.OUTPUT);
        final ITreeNode outputParam = (ITreeNode) outputParamCategory.getChildren()[0];
        final IOperation operation = (IOperation) operationNode.getModelObject();
        final Collection<IParameter> allOutputParameters = operation.getAllOutputParameters();

        assertTrue(controller.isAddNewOutParameterEnabled((outputParam)));

        controller.addNewParameter(outputParam, OperationCategory.OUTPUT);

        final Collection<IParameter> allNewOutputParameters = operation.getAllOutputParameters();

        assertEquals(allOutputParameters.size() + 1, allNewOutputParameters.size());
    }

    @Test
    public void testDeleteServiceInterfaceNode() {
        final SIFormPageController controller = new SIFormPageController(wsdlModelRoot, false, false);

        final Object[] allServiceInterfaceNodes = controller.getAllServiceInterfaceNodes(wsdlModelRoot.getDescription());
        final ITreeNode sieNode = (ITreeNode) allServiceInterfaceNodes[0];

        assertTrue(controller.isDeleteItemEnabled(sieNode));
        controller.deleteItemsTriggered(Arrays.asList(sieNode));

        final Object[] allServiceNewInterfaceNodes = controller.getAllServiceInterfaceNodes(wsdlModelRoot.getDescription());

        assertEquals(0, allServiceNewInterfaceNodes.length);
    }

    @Test
    public void testDeleteOperationNode() {
        final SIFormPageController controller = new SIFormPageController(wsdlModelRoot, false, false);

        final Object[] allServiceInterfaceNodes = controller.getAllServiceInterfaceNodes(wsdlModelRoot.getDescription());
        final ITreeNode sieNode = (ITreeNode) allServiceInterfaceNodes[0];
        final Object[] children = sieNode.getChildren();
        final ITreeNode operationNode = (ITreeNode) children[0];

        assertTrue(controller.isDeleteItemEnabled(operationNode));
        controller.deleteItemsTriggered(Arrays.asList(operationNode));

        final Object[] allOperationNodes = sieNode.getChildren();

        assertEquals(0, allOperationNodes.length);
    }

    @Test
    public void testDeleteFaultNode() {
        final SIFormPageController controller = new SIFormPageController(wsdlModelRoot, false, false);
        controller.setShowCategoryNodes(true);
        final Object[] allServiceInterfaceNodes = controller.getAllServiceInterfaceNodes(wsdlModelRoot.getDescription());
        final ITreeNode sieNode = (ITreeNode) allServiceInterfaceNodes[0];
        final Object[] children = sieNode.getChildren();
        final ITreeNode operationNode = (ITreeNode) children[0];
        final ITreeNode faultParam = getFirstTreeNodeByCategory(operationNode, OperationCategory.FAULT);

        final IOperation operation = (IOperation) operationNode.getModelObject();
        final Collection<IFault> allFaults = operation.getAllFaults();

        final FaultNode faultNode = (FaultNode) faultParam.getChildren()[0];
        assertTrue(controller.isDeleteItemEnabled(faultNode));
        controller.deleteItemsTriggered(Arrays.asList((ITreeNode) faultNode));

        final Collection<IFault> allNewFaults = operation.getAllFaults();

        assertEquals(allFaults.size() - 1, allNewFaults.size());
    }

    @Test
    public void testDeleteParameterNode() {
        final SIFormPageController controller = new SIFormPageController(wsdlModelRoot, false, false);
        controller.setShowCategoryNodes(true);
        final Object[] allServiceInterfaceNodes = controller.getAllServiceInterfaceNodes(wsdlModelRoot.getDescription());
        final ITreeNode sieNode = (ITreeNode) allServiceInterfaceNodes[0];
        final Object[] children = sieNode.getChildren();
        final ITreeNode operationNode = (ITreeNode) children[0];
        final ITreeNode inputParam = getFirstTreeNodeByCategory(operationNode, OperationCategory.INPUT);

        final IOperation operation = (IOperation) operationNode.getModelObject();
        final Collection<IParameter> allInputParameters = operation.getAllInputParameters();

        final ParameterNode paramNode = (ParameterNode) inputParam.getChildren()[0];
        assertTrue(controller.isDeleteItemEnabled(paramNode));
        controller.deleteItemsTriggered(Arrays.asList((ITreeNode) paramNode));

        final Collection<IParameter> allNewInputParameters = operation.getAllInputParameters();

        assertEquals(allInputParameters.size() - 1, allNewInputParameters.size());
    }

    private ITreeNode getFirstTreeNodeByCategory(final ITreeNode operationNode, final OperationCategory desiredCatefory) {
        ITreeNode param = null;
        for (final Object node : operationNode.getChildren()) {
            final OperationCategory operationCategory = ((OperationCategoryNode) node).getOperationCategory();
            if (operationCategory.equals(desiredCatefory)) {
                param = (ITreeNode) node;
                break;
            }
        }
        return param;
    }

    /**
     * Ensure that when show categories is toggled, the tree would be refreshed
     */
    @Test
    public void testToggleShowCategories() {

        final boolean[] notified = new boolean[1];
        final SIFormPageController controller = new SIFormPageController(null, false, false);
        controller.addEventListener(new ISIEventListener() {

            @Override
            public void notifyEvent(final ISIEvent event) {
                if (event.getEventId() == ISIEvent.ID_REFRESH_TREE) {
                    notified[0] = true;
                }
            }
        });

        controller.setShowCategoryNodes(false);
        Assert.assertEquals(true, notified[0]);
    }
}
