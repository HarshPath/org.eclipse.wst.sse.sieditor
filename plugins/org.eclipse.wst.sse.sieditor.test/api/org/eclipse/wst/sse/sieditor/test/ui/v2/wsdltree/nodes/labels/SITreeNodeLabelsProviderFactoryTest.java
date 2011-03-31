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
package org.eclipse.wst.sse.sieditor.test.ui.v2.wsdltree.nodes.labels;

import static org.junit.Assert.assertTrue;

import org.easymock.EasyMock;
import org.eclipse.wst.sse.sieditor.ui.v2.factory.TreeNodeMapper;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.labels.provider.DefaultNodeLabelsProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.labels.provider.NullNodeLabelsProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.FaultNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ImportedServiceNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ImportedServicesNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.OperationCategoryNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ParameterNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ServiceInterfaceNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.labels.SITreeNodeLabelsProviderFactory;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.labels.provider.FaultNodeLabelsProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.labels.provider.ImportedServiceNodeLabelsProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.labels.provider.ImportedServicesNodeLabelsProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.labels.provider.OperationCategoryNodeLabelsProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.labels.provider.ParameterNodeLabelsProvider;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;

public class SITreeNodeLabelsProviderFactoryTest {

    private final SITreeNodeLabelsProviderFactory factory = SITreeNodeLabelsProviderFactory.instance();

    private IServiceInterface iServiceInterface;
    private IParameter iParameter;
    private IFault iFault;
    private IDescription iDescription;

    private OperationCategoryNode operationCategoryNode;
    private ImportedServicesNode importedServicesNode;
    private ServiceInterfaceNode serviceInterfaceNode;
    private ParameterNode parameterNode;
    private FaultNode faultNode;
    private FaultNode faultNodeWithNullModelObject;
    private ImportedServiceNode importedServiceNode;

    @Before
    public void setUp() {
        iServiceInterface = EasyMock.createNiceMock(IServiceInterface.class);
        iParameter = EasyMock.createNiceMock(IParameter.class);
        iFault = EasyMock.createNiceMock(IFault.class);
        iDescription = EasyMock.createNiceMock(IDescription.class);
        EasyMock.expect(iDescription.getComponent()).andReturn(null);
        EasyMock.replay(iDescription);

        operationCategoryNode = new OperationCategoryNode(null, null, null, null);
        final SIFormPageController siFormPageController = createSIFormPageControllerMock();
        importedServicesNode = new ImportedServicesNode(iDescription, siFormPageController);
        serviceInterfaceNode = new ServiceInterfaceNode(null, iServiceInterface, null);
        parameterNode = new ParameterNode(null, iParameter, null);
        faultNode = new FaultNode(null, iFault, null);
        importedServiceNode = new ImportedServiceNode(iDescription, null, siFormPageController);
        faultNodeWithNullModelObject = new FaultNode(null, null, null);
    }

    @Test
    public void getLabelsProviderForTreeNode() {
        assertTrue(factory.getLabelsProvider(operationCategoryNode) instanceof OperationCategoryNodeLabelsProvider);
        assertTrue(factory.getLabelsProvider(importedServicesNode) instanceof ImportedServicesNodeLabelsProvider);
        assertTrue(factory.getLabelsProvider(serviceInterfaceNode) instanceof DefaultNodeLabelsProvider);
        assertTrue(factory.getLabelsProvider(faultNode) instanceof FaultNodeLabelsProvider);
        assertTrue(factory.getLabelsProvider(parameterNode) instanceof ParameterNodeLabelsProvider);
        assertTrue(factory.getLabelsProvider(importedServiceNode) instanceof ImportedServiceNodeLabelsProvider);
        assertTrue(factory.getLabelsProvider(faultNodeWithNullModelObject) instanceof NullNodeLabelsProvider);
    }

    @Test
    public void getLabelsProviderForModelObject() {
        assertTrue(factory.getLabelsProvider(iServiceInterface) instanceof DefaultNodeLabelsProvider);
        assertTrue(factory.getLabelsProvider(iFault) instanceof FaultNodeLabelsProvider);
        assertTrue(factory.getLabelsProvider(iParameter) instanceof ParameterNodeLabelsProvider);
        assertTrue(factory.getLabelsProvider(iDescription) instanceof ImportedServiceNodeLabelsProvider);
        assertTrue(factory.getLabelsProvider((IModelObject) null) instanceof NullNodeLabelsProvider);
    }

    // =========================================================
    // mock-ups
    // =========================================================

    private SIFormPageController createSIFormPageControllerMock() {
        final SIFormPageController siFormPageController = new SIFormPageController(null, false, false) {
            @Override
            public TreeNodeMapper getTreeNodeMapper() {
                return null;
            }
        };
        return siFormPageController;
    }
    
}
