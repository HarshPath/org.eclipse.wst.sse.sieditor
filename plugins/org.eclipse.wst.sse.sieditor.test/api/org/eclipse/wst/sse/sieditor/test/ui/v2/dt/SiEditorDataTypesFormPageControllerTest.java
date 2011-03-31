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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.SiEditorDataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.common.CompositeCommand;
import org.eclipse.wst.sse.sieditor.core.common.IEnvironment;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

/**
 *
 * 
 */
public class SiEditorDataTypesFormPageControllerTest {

    private static class TestSIDataTypesFormPageController extends SiEditorDataTypesFormPageController {

        private final boolean isDeleteAllowed;
        private Object editMatch;
        private ITreeNode nextTreeNode;
        private ITreeNode nextTreeNodeMatch;
        private Object removeNodeMatch;

        public TestSIDataTypesFormPageController(final IWsdlModelRoot model, final boolean readOnly) {
            super(model, readOnly);
            isDeleteAllowed = true;
        }

        @Override
        protected boolean isEditAllowed(final Object editedObject) {
            assertEquals(editMatch, editedObject);
            return isDeleteAllowed;
        }

        public void setEditedMatch(final Object deleteMatch) {
            this.editMatch = deleteMatch;
        }

        @Override
        protected ITreeNode getNextTreeNode(final ITreeNode selectedTreeNode) {
            assertEquals(nextTreeNodeMatch, selectedTreeNode);
            return nextTreeNode;
        }

        public void setNextTreeNode(final ITreeNode nextTreeNode) {
            this.nextTreeNode = nextTreeNode;
        }

        public void setNextTreeNodeMatch(final ITreeNode nextTreeNodeMatch) {
            this.nextTreeNodeMatch = nextTreeNodeMatch;
        }

        @Override
        public void removeNodeAndItsChildrenFromMap(final ITreeNode treeNode) {
            assertEquals(removeNodeMatch, treeNode);
        }

        public void setRemoveNodeMatch(final Object removeNodeMatch) {
            this.removeNodeMatch = removeNodeMatch;
        }

    }

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
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    // /**
    // * Test method for {@link
    // org.eclipse.wst.sse.sieditor.ui.v2.dt.SiEditorDataTypesFormPageController#handleAddNewNamespaceAction()}.
    // */
    // @Test
    // public final void testHandleAddNewNamespaceAction() {
    // fail("Not yet implemented"); // TODO
    // }
    //
    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.SiEditorDataTypesFormPageController#isAddNamespaceEnabled(ITreeNode)}
     * .
     */
    @Test
    public final void testIsAddNamespaceEnabled_ReadOnly() {
        final IWsdlModelRoot wsdlRootMock = createMock(IWsdlModelRoot.class);
        expect(wsdlRootMock.getModelObject()).andReturn(createMock(IDescription.class));
        expect(wsdlRootMock.getModelObject()).andReturn(null);
        
        final ITreeNode treeNodeMock = createMock(ITreeNode.class);
        expect(treeNodeMock.getCategories()).andReturn(0).anyTimes();

        replay(wsdlRootMock, treeNodeMock);
        final SiEditorDataTypesFormPageController controller = new SiEditorDataTypesFormPageController(wsdlRootMock, true);
        assertFalse(controller.isAddNamespaceEnabled(treeNodeMock));
        assertFalse(controller.isAddNamespaceEnabled(null));
        verify(wsdlRootMock);
    }
    
    @Test
    public final void testIsAddNamespaceEnabled_ReadWrite() {
        final IWsdlModelRoot wsdlRootMock = createMock(IWsdlModelRoot.class);
        expect(wsdlRootMock.getModelObject()).andReturn(createMock(IDescription.class));
        expect(wsdlRootMock.getModelObject()).andReturn(null);
        final ITreeNode treeNodeMock = createMock(ITreeNode.class);
        expect(treeNodeMock.getCategories()).andReturn(0).anyTimes();

        replay(wsdlRootMock, treeNodeMock);
        final SiEditorDataTypesFormPageController controller = new SiEditorDataTypesFormPageController(wsdlRootMock, false);
        assertTrue(controller.isAddNamespaceEnabled(null));
        assertFalse(controller.isAddNamespaceEnabled(treeNodeMock));
        verify(wsdlRootMock);
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController#handleRemoveAction(org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode)}
     * .
     */
    @Test
    public final void testHandleRemoveAction() {
        final ITreeNode treeNodeMock = createMock(ITreeNode.class);
        final IWsdlModelRoot modelRootMock = createMock(IWsdlModelRoot.class);

        final IEnvironment envMock = createMock(IEnvironment.class);
        final IModelObject xsdComponentMock = createMock(ISchema.class);

        prepairHandleRemoveCommandMocks(treeNodeMock, modelRootMock, envMock, xsdComponentMock, CompositeCommand.class,
                Status.OK_STATUS);

        expect(modelRootMock.getDescription()).andReturn(null);
        expect(xsdComponentMock.getParent()).andReturn(null).anyTimes();
        replay(treeNodeMock, modelRootMock, envMock, xsdComponentMock);

        final TestSIDataTypesFormPageController controller = new TestSIDataTypesFormPageController(modelRootMock, false);

        controller.setEditedMatch(xsdComponentMock);
        controller.setNextTreeNodeMatch(treeNodeMock);
        controller.setNextTreeNode(createMock(ITreeNode.class));
        controller.setRemoveNodeMatch(treeNodeMock);
        controller.handleRemoveAction(Arrays.asList(treeNodeMock));

        verify(treeNodeMock, modelRootMock, envMock);
    }

    private void prepairHandleRemoveCommandMocks(final ITreeNode treeNodeMock, final IModelRoot modelRootMock, final IEnvironment envMock,
            final IModelObject xsdComponentMock, final Class<? extends AbstractNotificationOperation> commandToBeExecuted,
            final IStatus returnStatus) {
        expect(treeNodeMock.getModelObject()).andReturn(xsdComponentMock);
        expect(modelRootMock.getEnv()).andReturn(envMock).times(3);
        expect(envMock.getEditingDomain()).andReturn(null).times(2);

        try {
            expect(envMock.execute(isA(commandToBeExecuted))).andReturn(returnStatus);
        } catch (final ExecutionException e) {
            fail();
        }
    }

    // /**
    // * Test method for {@link
    // org.eclipse.wst.sse.sieditor.ui.v2.dt.SiEditorDataTypesFormPageController#createRemoveCommand(org.eclipse.wst.sse.sieditor.model.api.IModelObject)}.
    // */
    // @Test
    // public final void testCreateRemoveCommand() {
    // TestSIDataTypesFormPageController controller = new
    // TestSIDataTypesFormPageController(null,false);
    // controller.setNewModel(createMock(IXSDModelRoot.class), false);
    // //result from the DTFormPageController on this argument should be null
    // assertEquals(null,controller.createRemoveCommand(createMock(IModelObject.class)));
    // //if the model object is a ISchema - the model root must be a
    // WSDLModelRoot
    // //otherwise an exception should be thrown
    // Boolean caughtFlag = Boolean.valueOf(false);
    // try{
    // controller.createRemoveCommand(createMock(ISchema.class));
    // }catch(IllegalArgumentException e)
    // {
    // assertNotNull(e.getMessage());
    // caughtFlag = Boolean.valueOf(true);
    // }
    //        Assert.assertTrue("an exception which should have been thrown was not cought", caughtFlag); //$NON-NLS-1$
    //        
    // IWsdlModelRoot modelRootMock = createMock(IWsdlModelRoot.class);
    // IEnvironment envMock = createMock(IEnvironment.class);
    // expect(modelRootMock.getDescription()).andReturn(null);
    // expect(modelRootMock.getEnv()).andReturn(envMock);
    // expect(envMock.getEditingDomain()).andReturn(createMock(InternalTransactionalEditingDomain.class));
    // replay(modelRootMock,envMock);
    //        
    // controller = new TestSIDataTypesFormPageController(modelRootMock, false);
    // AbstractNotificationOperation createdRemoveCommand =
    // controller.createRemoveCommand(createMock(ISchema.class));
    // assertNotNull(createdRemoveCommand);
    // verify(modelRootMock,envMock);
    // }
}
