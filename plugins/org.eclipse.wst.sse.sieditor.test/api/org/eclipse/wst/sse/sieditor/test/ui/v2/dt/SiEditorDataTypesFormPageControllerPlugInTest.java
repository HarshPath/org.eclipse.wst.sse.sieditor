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
package org.eclipse.wst.sse.sieditor.test.ui.v2.dt;

import java.util.Arrays;
import java.util.List;

import org.eclipse.wst.sse.sieditor.ui.v2.dt.SiEditorDataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.NamespaceNode;
import org.eclipse.wst.sse.sieditor.ui.v2.factory.TreeNodeMapper;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

/**
 *
 *
 */
public class SiEditorDataTypesFormPageControllerPlugInTest extends SIEditorBaseTest {

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


    private IWsdlModelRoot wsdlModelRoot;
    private TestSiEditorDataTypesFormPageController controller;

    /**
     * @throws java.lang.Exception
     */
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        wsdlModelRoot = getWSDLModelRoot("pub/self/mix2/PurchaseOrderConfirmation.wsdl", "PurchaseOrderConfirmation.wsdl");
        controller = new TestSiEditorDataTypesFormPageController(wsdlModelRoot, false);
    }

    /**
     * @throws java.lang.Exception
     */
    @Override
    @After
    public void tearDown() throws Exception {
    }

    private static class TestSiEditorDataTypesFormPageController extends SiEditorDataTypesFormPageController{
        
        TestSiEditorDataTypesFormPageController(IWsdlModelRoot modelRoot, boolean readOnly){
            super(modelRoot,readOnly);
        }
        @Override
        public String getNewNamespaceName() {
            return super.getNewNamespaceName();
        }
        
    }
    
    /**
     * Test method for {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.SiEditorDataTypesFormPageController#handleAddNewNamespaceAction()}.
     */
    @Test
    public final void testHandleAddNewNamespaceAction() {
        //chechk if schema exists
        List<ISchema> containedSchemas = wsdlModelRoot.getDescription().getContainedSchemas();
        assertEquals(2,containedSchemas.size());
        String newNamespaceName = controller.getNewNamespaceName();
        assertEquals(0,wsdlModelRoot.getDescription().getSchema(newNamespaceName).length);
        //add the new schema
        controller.handleAddNewNamespaceAction();
        ISchema[] schemas = wsdlModelRoot.getDescription().getSchema(newNamespaceName);
        assertEquals(1, schemas.length);
        assertNotNull(schemas[0]);
        
        TreeNodeMapper treeNodeMapper = controller.getTreeNodeMapper();
        containedSchemas = wsdlModelRoot.getDescription().getContainedSchemas();        
        for (ISchema iSchema : containedSchemas) {
            treeNodeMapper.addToNodeMap(iSchema, new NamespaceNode(iSchema, treeNodeMapper));
        }
      
        //remove it
        ITreeNode schemaTreeNode = treeNodeMapper.getTreeNode(schemas[0]);
        controller.handleRemoveAction(Arrays.asList(schemaTreeNode));
        assertEquals(0,wsdlModelRoot.getDescription().getSchema(newNamespaceName).length);
        //remove all other
        containedSchemas = wsdlModelRoot.getDescription().getContainedSchemas();        
        for (ISchema iSchema : containedSchemas) {
            controller.handleRemoveAction(Arrays.asList(treeNodeMapper.getTreeNode(iSchema)));
        }
        
        newNamespaceName = controller.getNewNamespaceName();
        assertEquals(0,wsdlModelRoot.getDescription().getSchema(newNamespaceName).length);
        containedSchemas = wsdlModelRoot.getDescription().getContainedSchemas();
        assertEquals(0, containedSchemas.size());
        controller.handleAddNewNamespaceAction();
        schemas = wsdlModelRoot.getDescription().getSchema(newNamespaceName);
        assertEquals(1, schemas.length);
        assertNotNull(schemas[0]);
    }
}
