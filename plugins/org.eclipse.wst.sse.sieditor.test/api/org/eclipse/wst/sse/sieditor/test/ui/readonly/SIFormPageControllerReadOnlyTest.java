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
package org.eclipse.wst.sse.sieditor.test.ui.readonly;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.FaultNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.OperationCategory;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.OperationNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ParameterNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ServiceInterfaceNode;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.utils.StatusUtils;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.OperationType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

/**
 *
 * 
 */
public class SIFormPageControllerReadOnlyTest extends SIEditorBaseTest {

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        super.tearDown();
    }

    private static class TestSIFormPageController extends SIFormPageController {

        public TestSIFormPageController(IWsdlModelRoot model, boolean readOnly) {
            super(model, readOnly,false);
        }

        public Boolean isEditAllowed;

        @Override
        protected boolean isEditAllowed(Object editedObject) {
            return isEditAllowed != null ? isEditAllowed.booleanValue() : super.isEditAllowed(editedObject);
        }

        public Boolean isDeleteAllowed;

        @Override
        protected boolean isDeleteAllowed(Object editedObject) {
            return isDeleteAllowed != null ? isDeleteAllowed.booleanValue() : super.isDeleteAllowed(editedObject);
        }

    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController#canEdit(org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode)}
     * .
     */
    @Test
    public final void testCanEdit() {
        TestSIFormPageController sifpc = new TestSIFormPageController(createMock(IWsdlModelRoot.class), true);
        sifpc.isEditAllowed = Boolean.valueOf(true);
        assertFalse(sifpc.canEdit(null));
        ITreeNode treeNodeMock = createMock(ITreeNode.class);
        expect(treeNodeMock.getModelObject()).andReturn(null);
        expect(treeNodeMock.isReadOnly()).andReturn(false).anyTimes();
        IModelObject modelObjectMock = createMock(IModelObject.class);
        expect(treeNodeMock.getModelObject()).andReturn(modelObjectMock).atLeastOnce();
        replay(treeNodeMock);

        assertFalse(sifpc.canEdit(treeNodeMock));

        sifpc.isEditAllowed = Boolean.valueOf(false);
        assertFalse(sifpc.canEdit(treeNodeMock));

        sifpc.isEditAllowed = Boolean.valueOf(true);
        assertTrue(sifpc.canEdit(treeNodeMock));

        verify(treeNodeMock);
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController#addNewServiceInterface()}
     * .
     * 
     * @throws ExecutionException
     * @throws CoreException
     * @throws IOException
     */
    @Test
    public final void testAddNewServiceInterface() throws ExecutionException, IOException, CoreException {
        // just any wsdl
        IWsdlModelRoot wsdlRoot = getWSDLModelRoot(org.eclipse.wst.sse.sieditor.test.model.Constants.DATA_PUBLIC_SELF_KESHAV
                + "Documentation.wsdl", "Documentation.wsdl"); //$NON-NLS-1$ //$NON-NLS-2$
        TestSIFormPageController sifpc = new TestSIFormPageController(wsdlRoot, false);
        sifpc.isEditAllowed = Boolean.valueOf(true);

        StatusUtils.isUnderJunitExecution = true;
        long statusCalls = StatusUtils.getShowStatusDialog_calls();

        int siCount = wsdlRoot.getDescription().getAllInterfaces().size();

        sifpc.addNewServiceInterface();

        assertEquals(0, StatusUtils.getShowStatusDialog_calls() - statusCalls);
        assertEquals(siCount + 1, wsdlRoot.getDescription().getAllInterfaces().size());

        // with a readOnly controller
        sifpc.isEditAllowed = Boolean.valueOf(false);

        statusCalls = StatusUtils.getShowStatusDialog_calls();
        siCount = wsdlRoot.getDescription().getAllInterfaces().size();

        sifpc.addNewServiceInterface();

        assertEquals(1, StatusUtils.getShowStatusDialog_calls() - statusCalls);
        assertEquals(siCount, wsdlRoot.getDescription().getAllInterfaces().size());
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController#addNewOperation(org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode)}
     * .
     * 
     * @throws CoreException
     * @throws IOException
     */
    @Test
    public final void testAddNewOperation() throws IOException, CoreException {
        // just any wsdl
        IWsdlModelRoot wsdlRoot = getWSDLModelRoot(org.eclipse.wst.sse.sieditor.test.model.Constants.DATA_PUBLIC_SELF_KESHAV
                + "Documentation.wsdl", "Documentation.wsdl"); //$NON-NLS-1$
        TestSIFormPageController sifpc = new TestSIFormPageController(wsdlRoot, false);
        sifpc.isEditAllowed = Boolean.valueOf(true);

        StatusUtils.isUnderJunitExecution = true;
        long statusCalls = StatusUtils.getShowStatusDialog_calls();

        IServiceInterface serviceInterface = wsdlRoot.getDescription().getAllInterfaces().iterator().next();
        ServiceInterfaceNode siNode = new ServiceInterfaceNode(null, serviceInterface, sifpc);
        sifpc.getTreeNodeMapper().addToNodeMap(serviceInterface, siNode);

        int operationsCount = serviceInterface.getAllOperations().size();

        sifpc.addNewOperation(siNode);

        assertEquals(0, StatusUtils.getShowStatusDialog_calls() - statusCalls);
        assertEquals(operationsCount + 1, serviceInterface.getAllOperations().size());

        // with a readOnly controller
        sifpc.isEditAllowed = Boolean.valueOf(false);

        statusCalls = StatusUtils.getShowStatusDialog_calls();
        operationsCount = serviceInterface.getAllOperations().size();

        sifpc.addNewOperation(siNode);

        assertEquals(1, StatusUtils.getShowStatusDialog_calls() - statusCalls);
        assertEquals(operationsCount, serviceInterface.getAllOperations().size());
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController#addNewFault(org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode)}
     * .
     * 
     * @throws CoreException
     * @throws IOException
     */
    @Test
    public final void testAddNewFault() throws IOException, CoreException {
        // just any wsdl
        IWsdlModelRoot wsdlRoot = getWSDLModelRoot(org.eclipse.wst.sse.sieditor.test.model.Constants.DATA_PUBLIC_SELF_KESHAV
                + "Documentation.wsdl", "Documentation.wsdl"); //$NON-NLS-1$
        TestSIFormPageController sifpc = new TestSIFormPageController(wsdlRoot, false);
        sifpc.isEditAllowed = Boolean.valueOf(true);

        StatusUtils.isUnderJunitExecution = true;
        long statusCalls = StatusUtils.getShowStatusDialog_calls();

        IServiceInterface serviceInterface = wsdlRoot.getDescription().getAllInterfaces().iterator().next();
        ServiceInterfaceNode siNode = new ServiceInterfaceNode(null, serviceInterface, sifpc);
        sifpc.getTreeNodeMapper().addToNodeMap(serviceInterface, siNode);

        OperationNode operationNode = (OperationNode) siNode.getChildren()[0];

        int faultsCount = operationNode.getModelObject().getAllFaults().size();

        sifpc.addNewFault(operationNode);

        assertEquals(0, StatusUtils.getShowStatusDialog_calls() - statusCalls);
        assertEquals(faultsCount + 1, operationNode.getModelObject().getAllFaults().size());

        // with a readOnly controller
        sifpc.isEditAllowed = Boolean.valueOf(false);

        statusCalls = StatusUtils.getShowStatusDialog_calls();
        faultsCount = operationNode.getModelObject().getAllFaults().size();

        sifpc.addNewFault(operationNode);

        assertEquals(1, StatusUtils.getShowStatusDialog_calls() - statusCalls);
        assertEquals(faultsCount, operationNode.getModelObject().getAllFaults().size());
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController#addNewParameter(org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode, org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.OperationCategory)}
     * .
     * 
     * @throws CoreException
     * @throws IOException
     */
    @Test
    public final void testAddNewParameter() throws IOException, CoreException {
        // just any wsdl
        IWsdlModelRoot wsdlRoot = getWSDLModelRoot(org.eclipse.wst.sse.sieditor.test.model.Constants.DATA_PUBLIC_SELF_KESHAV
                + "Documentation.wsdl", "Documentation.wsdl"); //$NON-NLS-1$
        TestSIFormPageController sifpc = new TestSIFormPageController(wsdlRoot, false);
        sifpc.isEditAllowed = Boolean.valueOf(true);

        StatusUtils.isUnderJunitExecution = true;
        long statusCalls = StatusUtils.getShowStatusDialog_calls();

        IServiceInterface serviceInterface = wsdlRoot.getDescription().getAllInterfaces().iterator().next();
        ServiceInterfaceNode siNode = new ServiceInterfaceNode(null, serviceInterface, sifpc);
        sifpc.getTreeNodeMapper().addToNodeMap(serviceInterface, siNode);

        OperationNode operationNode = (OperationNode) siNode.getChildren()[0];

        int paramCount = operationNode.getModelObject().getAllInputParameters().size();

        sifpc.addNewParameter(operationNode, OperationCategory.INPUT);

        assertEquals(0, StatusUtils.getShowStatusDialog_calls() - statusCalls);
        assertEquals(paramCount + 1, operationNode.getModelObject().getAllInputParameters().size());

        // with a readOnly controller
        sifpc.isEditAllowed = Boolean.valueOf(false);

        statusCalls = StatusUtils.getShowStatusDialog_calls();
        paramCount = operationNode.getModelObject().getAllInputParameters().size();

        sifpc.addNewParameter(operationNode, OperationCategory.INPUT);

        assertEquals(1, StatusUtils.getShowStatusDialog_calls() - statusCalls);
        assertEquals(paramCount, operationNode.getModelObject().getAllInputParameters().size());
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController#deleteItemTriggered(org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode)}
     * .
     * 
     * @throws CoreException
     * @throws IOException
     */
    @Test
    public final void testDeleteItemTriggered() throws IOException, CoreException {
        IWsdlModelRoot wsdlRoot = getWSDLModelRoot(org.eclipse.wst.sse.sieditor.test.model.Constants.DATA_PUBLIC_SELF_KESHAV
                + "Documentation.wsdl", "Documentation.wsdl"); //$NON-NLS-1$
        TestSIFormPageController sifpc = new TestSIFormPageController(wsdlRoot, false);
        sifpc.isEditAllowed = Boolean.valueOf(true);

        StatusUtils.isUnderJunitExecution = true;
        long statusCalls = StatusUtils.getShowStatusDialog_calls();

        IServiceInterface serviceInterface = wsdlRoot.getDescription().getAllInterfaces().iterator().next();
        ServiceInterfaceNode serviceInterfaceNode = new ServiceInterfaceNode(null, serviceInterface, sifpc);
        sifpc.getTreeNodeMapper().addToNodeMap(serviceInterface, serviceInterfaceNode);

        OperationNode operationNode = (OperationNode) serviceInterfaceNode.getChildren()[0];

        ParameterNode inputParameterNode = (ParameterNode) operationNode.getChildren()[0];

        FaultNode faultNode = (FaultNode) operationNode.getChildren()[2];

        sifpc.isEditAllowed = Boolean.valueOf(false);
        ArrayList<ITreeNode> nodes = new ArrayList<ITreeNode>();
        nodes.add(faultNode);
        nodes.add(inputParameterNode);
        nodes.add(operationNode);
        nodes.add(serviceInterfaceNode);
        sifpc.deleteItemsTriggered(nodes);

        assertEquals(serviceInterface, wsdlRoot.getDescription().getAllInterfaces().iterator().next());
        assertEquals(operationNode, serviceInterfaceNode.getChildren()[0]);
        assertEquals(inputParameterNode, operationNode.getChildren()[0]);
        assertEquals(faultNode, operationNode.getChildren()[2]);

        sifpc.isEditAllowed = Boolean.valueOf(true);

        sifpc.deleteItemsTriggered(nodes);

        assertEquals(0, wsdlRoot.getDescription().getAllInterfaces().size());

    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController#editDescriptionNamespaceTriggered(java.lang.String)}
     * .
     * 
     * @throws CoreException
     * @throws IOException
     */
    @Test
    public final void testEditDescriptionNamespaceTriggered() throws IOException, CoreException {
        IWsdlModelRoot wsdlRoot = getWSDLModelRoot(org.eclipse.wst.sse.sieditor.test.model.Constants.DATA_PUBLIC_SELF_KESHAV
                + "Documentation.wsdl", "Documentation.wsdl"); //$NON-NLS-1$ //$NON-NLS-2$
        TestSIFormPageController sifpc = new TestSIFormPageController(wsdlRoot, false);
        sifpc.isEditAllowed = Boolean.valueOf(false);

        StatusUtils.isUnderJunitExecution = true;
        long statusCalls = StatusUtils.getShowStatusDialog_calls();

        String namespace = wsdlRoot.getDescription().getNamespace();
        String newNamespace = "http://someThing.com"; //$NON-NLS-1$
        assertNotSame(newNamespace, namespace);

        sifpc.editDescriptionNamespaceTriggered(newNamespace);

        assertEquals(namespace, wsdlRoot.getDescription().getNamespace());
        assertEquals(1, StatusUtils.getShowStatusDialog_calls() - statusCalls);

        sifpc.isEditAllowed = Boolean.valueOf(true);

        sifpc.editDescriptionNamespaceTriggered(newNamespace);

        assertEquals(newNamespace, wsdlRoot.getDescription().getNamespace());
        assertEquals(1, StatusUtils.getShowStatusDialog_calls() - statusCalls);
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController#editParameterTypeTriggered(ITreeNode, IType)}
     * .
     * 
     * @throws CoreException
     * @throws IOException
     */
    @Test
    public final void testEditParameterTypeTriggered() throws IOException, CoreException {
        IWsdlModelRoot wsdlRoot = getWSDLModelRoot(org.eclipse.wst.sse.sieditor.test.model.Constants.DATA_PUBLIC_SELF_KESHAV
                + "Documentation.wsdl", "Documentation.wsdl"); //$NON-NLS-1$ //$NON-NLS-2$
        TestSIFormPageController sifpc = new TestSIFormPageController(wsdlRoot, false);
        sifpc.isEditAllowed = Boolean.valueOf(false);

        StatusUtils.isUnderJunitExecution = true;
        long statusCalls = StatusUtils.getShowStatusDialog_calls();

        IType newType = wsdlRoot.getDescription().getContainedSchemas().get(0).getAllContainedTypes().iterator().next();

        IServiceInterface service = wsdlRoot.getDescription().getAllInterfaces().iterator().next();
        ServiceInterfaceNode serviceInterfaceNode = new ServiceInterfaceNode(null, service, sifpc);
        ParameterNode parameterNode = (ParameterNode) ((ITreeNode) serviceInterfaceNode.getChildren()[0]).getChildren()[0];
        IParameter parameterToChange = (IParameter) parameterNode.getModelObject();

        IType oldType = parameterToChange.getType();

        sifpc.editParameterTypeTriggered(parameterNode, newType);

        assertEquals(oldType, parameterToChange.getType());
        assertEquals(1, StatusUtils.getShowStatusDialog_calls() - statusCalls);

        sifpc.isEditAllowed = Boolean.valueOf(true);
        statusCalls = StatusUtils.getShowStatusDialog_calls();

        sifpc.editParameterTypeTriggered(parameterNode, newType);

        assertEquals(newType, parameterToChange.getType());
        assertEquals(0, StatusUtils.getShowStatusDialog_calls() - statusCalls);
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController#editItemNameTriggered(org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode, java.lang.String)}
     * .
     * 
     * @throws CoreException
     * @throws IOException
     */
    @Test
    public final void testEditItemNameTriggered() throws IOException, CoreException {
        IWsdlModelRoot wsdlRoot = getWSDLModelRoot(org.eclipse.wst.sse.sieditor.test.model.Constants.DATA_PUBLIC_SELF_KESHAV
                + "Documentation.wsdl", "Documentation.wsdl"); //$NON-NLS-1$ //$NON-NLS-2$
        TestSIFormPageController sifpc = new TestSIFormPageController(wsdlRoot, false);
        sifpc.isEditAllowed = Boolean.valueOf(false);

        StatusUtils.isUnderJunitExecution = true;
        long statusCalls = StatusUtils.getShowStatusDialog_calls();

        IServiceInterface serviceInterface = wsdlRoot.getDescription().getAllInterfaces().iterator().next();
        ServiceInterfaceNode siNode = new ServiceInterfaceNode(null, serviceInterface, sifpc);

        String oldName = siNode.getDisplayName();
        String newName = "NewServiceInterfaceName"; //$NON-NLS-1$

        sifpc.editItemNameTriggered(siNode, newName);

        assertEquals(oldName, siNode.getDisplayName());
        assertEquals(1, StatusUtils.getShowStatusDialog_calls() - statusCalls);

        sifpc.isEditAllowed = Boolean.valueOf(true);
        statusCalls = StatusUtils.getShowStatusDialog_calls();

        sifpc.editItemNameTriggered(siNode, newName);

        assertEquals(newName, siNode.getDisplayName());
        assertEquals(0, StatusUtils.getShowStatusDialog_calls() - statusCalls);
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController#editOperationTypeTriggered(org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode, org.eclipse.wst.sse.sieditor.model.wsdl.api.OperationType)}
     * .
     * 
     * @throws CoreException
     * @throws IOException
     */
    @Test
    public final void testEditOperationTypeTriggered() throws IOException, CoreException {
        IWsdlModelRoot wsdlRoot = getWSDLModelRoot(org.eclipse.wst.sse.sieditor.test.model.Constants.DATA_PUBLIC_SELF_KESHAV
                + "Documentation.wsdl", "Documentation.wsdl"); //$NON-NLS-1$ //$NON-NLS-2$
        TestSIFormPageController sifpc = new TestSIFormPageController(wsdlRoot, false);
        sifpc.isEditAllowed = Boolean.valueOf(false);

        StatusUtils.isUnderJunitExecution = true;
        long statusCalls = StatusUtils.getShowStatusDialog_calls();

        IServiceInterface serviceInterface = wsdlRoot.getDescription().getAllInterfaces().iterator().next();
        ServiceInterfaceNode siNode = new ServiceInterfaceNode(null, serviceInterface, sifpc);
        OperationNode operationNodeToEdit = (OperationNode) siNode.getChildren()[0];

        OperationType oldType = operationNodeToEdit.getModelObject().getOperationStyle();
        OperationType newType = OperationType.ASYNCHRONOUS;
        assertNotSame(newType, oldType);

        sifpc.editOperationTypeTriggered(operationNodeToEdit, newType);

        assertEquals(oldType, operationNodeToEdit.getModelObject().getOperationStyle());
        assertEquals(1, StatusUtils.getShowStatusDialog_calls() - statusCalls);

        sifpc.isEditAllowed = Boolean.valueOf(true);
        statusCalls = StatusUtils.getShowStatusDialog_calls();

        sifpc.editOperationTypeTriggered(operationNodeToEdit, newType);

        assertEquals(newType, operationNodeToEdit.getModelObject().getOperationStyle());
        assertEquals(0, StatusUtils.getShowStatusDialog_calls() - statusCalls);
    }
}
