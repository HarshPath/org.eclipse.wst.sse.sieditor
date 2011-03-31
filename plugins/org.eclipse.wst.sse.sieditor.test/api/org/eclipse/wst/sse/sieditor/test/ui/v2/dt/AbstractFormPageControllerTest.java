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

import static org.easymock.EasyMock.*;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.wst.sse.sieditor.ui.v2.AbstractFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.common.BuiltinTypesHelper;
import org.eclipse.wst.sse.sieditor.ui.v2.eventing.ISIEvent;
import org.eclipse.wst.sse.sieditor.ui.v2.eventing.ISIEventListener;
import org.eclipse.wst.sse.sieditor.ui.v2.factory.TreeNodeMapper;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.utils.UIUtils;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.OperationCategory;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;

/**
 *
 * 
 */
public class AbstractFormPageControllerTest {
    private TestAbstractFormPageController controller;

    public static class TestAbstractFormPageController extends AbstractFormPageController {

        // TreeNodeMapper testNodeMapper;

        // if this boolean is set the class will return it's value, if not - it
        // will return the super.isEditAllowed();
        Boolean isEditAllowed = null;

        public TestAbstractFormPageController(final IWsdlModelRoot model, final boolean readOnly) {
            super(model, readOnly);
        }

        @Override
        public void fireTreeNodeSelectionEvent(final ITreeNode treeNode) {
            // TODO Auto-generated method stub
            super.fireTreeNodeSelectionEvent(treeNode);
        }

        @Override
        public void fireTreeNodeSelectionEvent(final IModelObject modelObject) {
            // TODO Auto-generated method stub
            super.fireTreeNodeSelectionEvent(modelObject);
        }

        // /**
        // * Method used to check if a testTreeNodeMapper is set to the
        // controller (mapper for test purposes)
        // * @return the testTreeNodeMapper
        // */
        // public TreeNodeMapper getTestNodeMapper() {
        // return testNodeMapper;
        // }
        // /**
        // * Sets a node mapper used by the controller for test purposes . It is
        // returned from the getTreeNodeMapper() method if different than null,
        // otherwise - the original is returned
        // * @param testNodeMapper the node mapper used in tests
        // */
        // public void setTestNodeMapper(TreeNodeMapper testNodeMapper) {
        // this.testNodeMapper = testNodeMapper;
        // }
        // //Returns the original if the testNodeMapper is not set. Returns the
        // testNodeMapper otherwise.
        // @Override
        // public TreeNodeMapper getTreeNodeMapper() {
        // if(testNodeMapper == null){
        // return super.getTreeNodeMapper();
        // }
        // return testNodeMapper;
        // }

        @Override
        protected ITreeNode getNextTreeNode(final ITreeNode selectedTreeNode) {
            return null; // sub classes' implementations are to be tested
        }

        /*
         * @Override //TODO mock PreEditServie to test isDelete and
         * isEditAllowed protected PreEditService getPreEditService() { return
         * null; PreEditService preEditService =
         * createMock(PreEditService.class);
         * expect(preEditService.startEdit(model)).andReturn(true);
         * expect(preEditService.startEdit(model)).andReturn(false);
         * expect(preEditService.startEdit(model)).andReturn(true);
         * expect(preEditService.startEdit(model)).andReturn(false);
         * replay(preEditService); return preEditService; }
         */

        public void setIsEditAllowed(final Boolean isEditAllowed) {
            this.isEditAllowed = isEditAllowed;
        }

        @Override
        public boolean isEditAllowed(final Object editedObject) {
            if (isEditAllowed == null) {
                return super.isEditAllowed(editedObject);
            }
            return isEditAllowed.booleanValue();
        }

        public IModelRoot getModel() {
            return model;
        }

        @Override
        protected IModelObject getModelObject() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void editItemNameTriggered(final ITreeNode treeNode, final String newName) {
            // TODO Auto-generated method stub
        }

        @Override
        protected String getEditorID() {
            // TODO Auto-generated method stub
            return null;
        }

    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        initController(false);
    }

    /**
     * Method creating a controller with a mocked {@link IWsdlModelRoot} object
     * for model
     * 
     * @param readOnly
     */
    private void initController(final boolean readOnly) {
        final IWsdlModelRoot mockedModelRoot = createMock(IWsdlModelRoot.class);
        controller = new TestAbstractFormPageController(mockedModelRoot, readOnly);
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.controller.AbstractFormPageController#isResourceReadOnly()}
     * .
     */
    @Test
    public final void testIsResourceReadOnly() {
        assertFalse(controller.isResourceReadOnly());
        initController(true);
        assertTrue(controller.isResourceReadOnly());
    }

    private int invocationCount = 0;

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.controller.AbstractFormPageController#setNewModel(org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot, boolean)}
     * .
     */
    @Test
    public void testSetNewModel() {
        // lets create a controller, fill the nodeMapper, then set a new
        // model(mock)not read only, see if the listeners
        // are noticed and the nodeMapper is cleared. Set a readOnly model and
        // check if isReadOnly works fine for it.
        IModelObject categoryObject = createNiceMock(IModelObject.class);
        replay(categoryObject);

        TreeNodeMapper treeNodeMapper = controller.getTreeNodeMapper();
        treeNodeMapper.addToNodeMap(categoryObject, createMock(ITreeNode.class));
        treeNodeMapper.addToCategoryNodeMap(OperationCategory.INPUT.name(), categoryObject, createMock(ITreeNode.class));
        controller.addEventListener(new ISIEventListener() {
            @Override
            public void notifyEvent(final ISIEvent event) {
                assertTrue(ISIEvent.ID_REFRESH_INPUT == event.getEventId());
                assertNull(event.getEventParams());
                invocationCount++;
            }
        });
        controller.setNewModel(createMock(IWsdlModelRoot.class), false);
        assertTrue(invocationCount == 1);
        assertFalse(controller.isResourceReadOnly());
        assertEquals(treeNodeMapper, controller.getTreeNodeMapper());
        assertNull(treeNodeMapper.getTreeNode(categoryObject));
        assertNull(treeNodeMapper.getCategoryNode(OperationCategory.INPUT.name(), categoryObject));
        controller.setNewModel(createMock(IWsdlModelRoot.class), true);
        assertTrue(controller.isResourceReadOnly());
        assertTrue(invocationCount == 2);
    }

    private static class AbstractControllerTestSelectionListener implements ISIEventListener {
        private ITreeNode treeNode;
        private int notifyCounter;

        /**
         * Listener used testing the fireTreeNodeSelectionEvent() method of the
         * controller
         * 
         * @param treeNode
         *            the tree node which is expected to be selected
         */
        public AbstractControllerTestSelectionListener(final ITreeNode treeNode) {
            this.treeNode = treeNode;
            notifyCounter = 0;
        }

        @Override
        public void notifyEvent(final ISIEvent event) {
            notifyCounter++;
            assertEquals(ISIEvent.ID_SELECT_TREENODE, event.getEventId());
            assertEquals(event.getEventParams().length, 1);
            assertEquals(treeNode, event.getEventParams()[0]);
        }

        /**
         * 
         * @return an integer - containing listener invocation count
         */
        public int getNotifyCounter() {
            return notifyCounter;
        }

        /**
         * Sets the tree node which the listener expects to be notified of
         * 
         * @param treeNode
         *            the tree node which is expected to be selected
         */
        public void setTreeNode(final ITreeNode treeNode) {
            this.treeNode = treeNode;
        }
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.controller.AbstractFormPageController#fireTreeNodeSelectionEvent(org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ITreeNode)}
     * .
     */
    @Test
    public final void testFireTreeNodeSelectionEventITreeNode() {
        // tests if a selection event is properly fired to the listeners
        // (probably views)
        // Tests also with null
        final ITreeNode treeNodeMock = createMock(ITreeNode.class);
        final AbstractControllerTestSelectionListener selectionListener = new AbstractControllerTestSelectionListener(
                treeNodeMock);
        final AbstractControllerTestSelectionListener selectionListener2 = new AbstractControllerTestSelectionListener(
                treeNodeMock);
        controller.addEventListener(selectionListener);
        controller.addEventListener(selectionListener2);
        controller.fireTreeNodeSelectionEvent(treeNodeMock);
        assertTrue(1 == selectionListener.getNotifyCounter());
        assertTrue(1 == selectionListener2.getNotifyCounter());
        controller.removeEventListener(selectionListener2);
        final ITreeNode nullTreeNode = null;
        selectionListener.setTreeNode(null);
        controller.fireTreeNodeSelectionEvent(nullTreeNode);
        assertTrue(2 == selectionListener.getNotifyCounter());
        assertTrue(1 == selectionListener2.getNotifyCounter());
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.controller.AbstractFormPageController#fireTreeNodeSelectionEvent(org.eclipse.wst.sse.sieditor.model.api.IModelObject)}
     * .
     */
    @Test
    public final void testFireTreeNodeSelectionEventIModelObject() {
        // tests if a selection event is properly fired to the listeners
        // (probably views)
        // Tests also with null
        final ITreeNode treeNodeMock = createMock(ITreeNode.class);
        final AbstractControllerTestSelectionListener selectionListener = new AbstractControllerTestSelectionListener(
                treeNodeMock);
        final IModelObject modelObjectMock = createMock(IModelObject.class);
        controller.getTreeNodeMapper().addToNodeMap(modelObjectMock, treeNodeMock);
        controller.addEventListener(selectionListener);
        controller.fireTreeNodeSelectionEvent(modelObjectMock);
        assertTrue(1 == selectionListener.getNotifyCounter());
        final ITreeNode nullTreeNode = null;
        selectionListener.setTreeNode(null);
        controller.fireTreeNodeSelectionEvent(nullTreeNode);
        assertTrue(2 == selectionListener.getNotifyCounter());
    }

    private class TestErrorEventListener implements ISIEventListener {
        private int invCounter;
        private String errorMsg;

        @Override
        public void notifyEvent(final ISIEvent event) {
            invCounter++;
            assertEquals(ISIEvent.ID_ERROR_MSG, event.getEventId());
            assertEquals(event.getEventParams().length, 1);
            assertEquals(errorMsg, event.getEventParams()[0]);
        }

        public int getInvCounter() {
            return invCounter;
        }

        public void setErrorMsg(final String errorMsg) {
            this.errorMsg = errorMsg;
        }
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.controller.AbstractFormPageController#fireShowErrorMsgEvent(java.lang.String)}
     * .
     */
    @Test
    public final void testFireShowErrorMsgEvent() {
        final String ERROR_MSG = "test.error.message"; //$NON-NLS-1$
        final String ERROR_MSG_2 = "test.error.message.2"; //$NON-NLS-1$
        final TestErrorEventListener errorListener = new TestErrorEventListener();
        errorListener.setErrorMsg(ERROR_MSG);
        controller.addEventListener(errorListener);
        controller.fireShowErrorMsgEvent(ERROR_MSG);
        assertTrue(1 == errorListener.getInvCounter());
        errorListener.setErrorMsg(ERROR_MSG_2);
        controller.fireShowErrorMsgEvent(ERROR_MSG_2);
        assertTrue(2 == errorListener.getInvCounter());
        errorListener.setErrorMsg(null);
        controller.fireShowErrorMsgEvent(null);
        assertTrue(3 == errorListener.getInvCounter());
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.controller.AbstractFormPageController#canEdit(org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.ITreeNode)}
     * .
     */
    @Test
    public final void testCanEdit() {
        assertFalse(controller.canEdit(null));
        final ITreeNode mockedTreeNode = createMock(ITreeNode.class);
        expect(mockedTreeNode.getModelObject()).andReturn(null);
        expect(mockedTreeNode.getModelObject()).andReturn(createMock(IModelObject.class)).times(4);
        replay(mockedTreeNode);
        assertFalse(controller.canEdit(mockedTreeNode));
        controller.setIsEditAllowed(Boolean.valueOf(true));
        assertTrue(controller.canEdit(mockedTreeNode));
        controller.setIsEditAllowed(Boolean.valueOf(false));
        assertFalse(controller.canEdit(mockedTreeNode));
        verify(mockedTreeNode);
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.controller.AbstractFormPageController#getCommonTypesDropDownList()}
     * .
     */
    @Test
    public final void testGetCommonTypesDropDownList() {
        final String[] commonTypesDropDownList = controller.getCommonTypesDropDownList();
        assertNotNull(commonTypesDropDownList);
        assertEquals(Messages.TypePropertyEditor_browse_button, commonTypesDropDownList[0]);
        final String[] commonlyUsedTypeNames = BuiltinTypesHelper.getInstance().getCommonlyUsedTypeNames();
        for (int i = 0; i < commonlyUsedTypeNames.length; i++) {
            assertEquals(commonlyUsedTypeNames[i], commonTypesDropDownList[i + 1]);
        }
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.controller.AbstractFormPageController#getCommonTypeByName(java.lang.String)}
     * .
     */
    @Test
    public final void testGetCommonTypeByName() {
        String name = null;
        assertNull(UIUtils.instance().getCommonTypeByName(name));
        name = UIConstants.EMPTY_STRING;
        assertNull(UIUtils.instance().getCommonTypeByName(name));
        final String[] typeNames = BuiltinTypesHelper.getInstance().getCommonlyUsedTypeNames();
        for (final String typeName : typeNames) {
            assertEquals(BuiltinTypesHelper.getInstance().getCommonBuiltinType(typeName), UIUtils.instance().getCommonTypeByName(
                    typeName));
        }
    }

}
