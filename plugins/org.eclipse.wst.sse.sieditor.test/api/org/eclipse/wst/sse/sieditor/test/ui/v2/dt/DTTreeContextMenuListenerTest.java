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
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.easymock.EasyMock;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.ContextMenuConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DTTreeContextMenuListener;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.ISiEditorDataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IDataTypesTreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.INamespaceNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IStructureTypeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.xsd.XSDSchema;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;

/**
 *
 * 
 */
public class DTTreeContextMenuListenerTest {

    private TreeViewerMock treeViewer;
    private SIDTControllerMock controllerMock;

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
        final Display display = Display.getDefault();
        final Composite composite = new Composite(new Shell(display), SWT.NONE);

        treeViewer = new TreeViewerMock(composite);
        controllerMock = new SIDTControllerMock(false);

    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    static class DTControllerMock extends DataTypesFormPageController {

        public DTControllerMock(final boolean readonly) {
            super(null, readonly);
        }

        protected IModelObject modelObjectMatch;
        protected Boolean isEditDeleteAllowed;
        protected ITreeNode treeNodeMatch;
        protected int addElementCounter;
        protected int addSimpleTypeCounter;
        protected int addStructureTypeCounter;
        protected int addAttributeCounter;
        protected int removeCounter;

        protected IModelObject modelObject;

        public void setModelObjectForCompare(final IModelObject modelObjectMatch) {
            this.modelObjectMatch = modelObjectMatch;
        }

        @Override
        public boolean isPartOfEdittedDocument(final IModelObject modelObject) {
            if (modelObjectMatch != null) {
                assertEquals(modelObjectMatch, modelObject);
            } else {
                assertNull(modelObject);
            }
            return true;
        }

        @Override
        protected boolean isEditAllowed(final Object editedObject) {
            if (isEditDeleteAllowed != null) {
                return isEditDeleteAllowed.booleanValue();
            }
            if (modelObjectMatch != null) {
                assertEquals(modelObjectMatch, editedObject);
            } else {
                assertNull(editedObject);
            }
            return true;
        }

        @Override
        protected boolean isDeleteAllowed(final Object editedObject) {
            if (isEditDeleteAllowed != null) {
                return isEditDeleteAllowed.booleanValue();
            }
            if (modelObjectMatch != null) {
                assertEquals(modelObjectMatch, editedObject);
            } else {
                assertNull(editedObject);
            }
            return true;
        }

        public void setEditDeleteAllowed(final Boolean isEditDeleteAllowed) {
            this.isEditDeleteAllowed = isEditDeleteAllowed;
        }

        public void setTreeNodeMatch(final ITreeNode treeNodeMatch) {
            this.treeNodeMatch = treeNodeMatch;
        }

        @Override
        public void handleAddElementAction(final ITreeNode selectedElement) {
            assertEquals(treeNodeMatch, selectedElement);
            addElementCounter++;
        }

        public int getAddElementCounter() {
            return addElementCounter;
        }

        public void setAddElementCounter(final int addElementCounter) {
            this.addElementCounter = addElementCounter;
        }

        @Override
        public void handleAddSimpleTypeAction(final ITreeNode selecetedNode) {
            assertEquals(treeNodeMatch, selecetedNode);
            addSimpleTypeCounter++;
        }

        public int getAddSimpleTypeCounter() {
            return addSimpleTypeCounter;
        }

        public void setAddSimpleTypeCounter(final int addSimpleTypeCounter) {
            this.addSimpleTypeCounter = addSimpleTypeCounter;
        }

        @Override
        public void handleAddStructureTypeAction(final ITreeNode selectedElement) {
            assertEquals(treeNodeMatch, selectedElement);
            addStructureTypeCounter++;
        }

        public int getAddStructureTypeCounter() {
            return addStructureTypeCounter;
        }

        public void setAddStructureTypeCounter(final int addStructureTypeCounter) {
            this.addStructureTypeCounter = addStructureTypeCounter;
        }

        @Override
        public void handleAddAttributeAction(final ITreeNode selectedElement) {
            assertEquals(treeNodeMatch, selectedElement);
            addAttributeCounter++;
        }

        public int getAddAttributeCounter() {
            return addAttributeCounter;
        }

        public void setAddAttributeCounter(final int addAttributeCounter) {
            this.addAttributeCounter = addAttributeCounter;
        }

        @Override
        public void handleRemoveAction(final List<ITreeNode> removedTreeNodes) {
            assertFalse(removedTreeNodes.isEmpty());
            assertEquals(treeNodeMatch, removedTreeNodes.get(0));
            removeCounter++;
        }

        public int getRemoveCounter() {
            return removeCounter;
        }

        public void setRemoveCounter(final int removeCounter) {
            this.removeCounter = removeCounter;
        }

        @Override
        protected IModelObject getModelObject() {

            return modelObject;
        }

        public void setModelObject(final IModelObject modelObject) {
            this.modelObject = modelObject;
        }
    }

    static class SIDTControllerMock extends DTControllerMock implements ISiEditorDataTypesFormPageController {
        private int addNamespaceCounter;

        SIDTControllerMock(final boolean readOnly) {
            super(readOnly);
        }

        @Override
        public boolean isAddNamespaceEnabled(final ITreeNode selectedNode) {
            if (isEditDeleteAllowed != null) {
                return isEditDeleteAllowed.booleanValue();
            }
            return true;
        }

        @Override
        public void handleAddNewNamespaceAction() {
            addNamespaceCounter++;
        }

        public int getAddNamespaceCounter() {
            return addNamespaceCounter;
        }

        public void setAddNamespaceCounter(final int addNamespaceCounter) {
            this.addNamespaceCounter = addNamespaceCounter;
        }

        @Override
        public ISchema addNewNamespace(final String newName) {
            // TODO Auto-generated method stub
            return null;
        }

    }

    class TreeViewerMock extends TreeViewer {

        ISelection selection;
        private Object elementMatch;
        private int editElementCounter;

        public TreeViewerMock(final Composite parent) {
            super(parent);
        }

        @Override
        public ISelection getSelection() {
            if (selection == null) {
                return new StructuredSelection();
            }
            return selection;
        }

        @Override
        public void setSelection(final ISelection selection) {
            this.selection = selection;
        }

        @Override
        public void editElement(final Object element, final int column) {
            assertEquals(elementMatch, element);
            assertEquals(0, column);
            editElementCounter++;
        }

        public void setElementMatch(final Object elementMatch) {
            this.elementMatch = elementMatch;
        }

        public int getEditElementCounter() {
            return editElementCounter;
        }

        public void setEditElementCounter(final int editElementCounter) {
            this.editElementCounter = editElementCounter;
        }
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DTTreeContextMenuListener#menuAboutToShow(org.eclipse.jface.action.IMenuManager)}
     * .
     */
    @SuppressWarnings("boxing")
    @Test
    public final void testMenuAboutToShow() {

        controllerMock = new SIDTControllerMock(true);
        final ITreeNode nodeMock = createNiceMock(IDataTypesTreeNode.class);
        final IModelObject objectMock = createNiceMock(IModelObject.class);
        expect(nodeMock.getModelObject()).andReturn(objectMock);
        replay(nodeMock);
        controllerMock.setModelObjectForCompare(objectMock);
        treeViewer.setSelection(new StructuredSelection(nodeMock));

        final DTTreeContextMenuListener dtTreeContextMenuListener = new DTTreeContextMenuListener(controllerMock, treeViewer);

        final IMenuManager menu = new MenuManager();
        dtTreeContextMenuListener.menuAboutToShow(menu);
        final IContributionItem[] items = menu.getItems();

        int i = 0;
        assertEquals(17, items.length);
        assertTrue(items[i] instanceof GroupMarker);
        assertEquals(ContextMenuConstants.GROUP_ADD_ITEMS, items[i].getId());
        i++;
        assertTrue(items[i] instanceof ActionContributionItem);
        assertEquals(ContextMenuConstants.ADD_NAMESPACE_ACTION_ID, items[i].getId());
        i++;
        assertTrue(items[i] instanceof ActionContributionItem);
        assertEquals(ContextMenuConstants.ADD_GLOBAL_ELEMENT_ACTION_ID, items[i].getId());
        i++;
        assertTrue(items[i] instanceof ActionContributionItem);
        assertEquals(ContextMenuConstants.ADD_SIMPLE_TYPE_ACTION_ID, items[i].getId());
        i++;
        assertTrue(items[i] instanceof ActionContributionItem);
        assertEquals(ContextMenuConstants.ADD_STRUCTURE_TYPE_ACTION_ID, items[i].getId());
        i++;
        assertTrue(items[i] instanceof Separator);
        assertEquals(ContextMenuConstants.GROUP_CONTEXT_ITEMS, items[i].getId());
        i++;
        assertTrue(items[i] instanceof ActionContributionItem);
        assertEquals(ContextMenuConstants.ADD_ELEMENT_ACTION_ID, items[i].getId());
        i++;
        assertTrue(items[i] instanceof ActionContributionItem);
        assertEquals(ContextMenuConstants.ADD_ATTRIBUTE_ACTION_ID, items[i].getId());
        i++;

        assertTrue(items[i] instanceof Separator);
        assertEquals(ContextMenuConstants.GROUP_REFACTOR, items[i].getId());
        i++;
        assertTrue(items[i] instanceof IMenuManager);
        assertRefactorMenuState((IMenuManager) items[i]);
        i++;

        assertTrue(items[i] instanceof Separator);
        assertEquals(org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.ContextMenuConstants.GROUP_OPEN_IN_NEW_EDITOR, items[i]
                .getId());
        i++;
        assertTrue(items[i] instanceof ActionContributionItem);
        assertEquals(org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.ContextMenuConstants.OPEN_IN_NEW_EDITOR_ACTION_ID, items[i]
                .getId());
        i++;
        assertTrue(items[i] instanceof Separator);
        assertEquals(ContextMenuConstants.GROUP_COPY_PASTE, items[i].getId());
        i++;
        assertTrue(items[i] instanceof ActionContributionItem);
        assertEquals(ContextMenuConstants.COPY_TYPE_ACTION_ID, items[i].getId());
        i++;
        assertTrue(items[i] instanceof ActionContributionItem);
        assertEquals(ContextMenuConstants.PASTE_TYPE_ACTION_ID, items[i].getId());
        i++;
        assertTrue(items[i] instanceof ActionContributionItem);
        assertEquals(ContextMenuConstants.REMOVE_ACTION_ID, items[i].getId());
        i++;
        assertTrue(items[i] instanceof Separator);
        assertEquals(ContextMenuConstants.GROUP_EDIT, items[i].getId());
    }

    @SuppressWarnings("boxing")
    @Test
    public final void testMenuAboutToShowStandalone() {
        final DTControllerMock dtControllerMock = new DTControllerMock(true);
        final ITreeNode nodeMock = createNiceMock(IDataTypesTreeNode.class);
        final IModelObject objectMock = createISchemaMockFromSameModel();
        expect(nodeMock.getModelObject()).andReturn(objectMock).anyTimes();
        replay(nodeMock);
        dtControllerMock.setModelObjectForCompare(objectMock);
        treeViewer.setSelection(new StructuredSelection(nodeMock));

        final DTTreeContextMenuListener dtTreeContextMenuListener = new DTTreeContextMenuListener(dtControllerMock, treeViewer);

        final IMenuManager menu = new MenuManager();
        dtTreeContextMenuListener.menuAboutToShow(menu);
        final IContributionItem[] items = menu.getItems();

        verify(nodeMock);

        int i = 0;
        assertEquals(14, items.length);
        assertTrue(items[i] instanceof GroupMarker);
        assertEquals(ContextMenuConstants.GROUP_ADD_ITEMS, items[i].getId());
        i++;
        assertTrue(items[i] instanceof ActionContributionItem);
        assertEquals(ContextMenuConstants.ADD_GLOBAL_ELEMENT_ACTION_ID, items[i].getId());
        i++;
        assertTrue(items[i] instanceof ActionContributionItem);
        assertEquals(ContextMenuConstants.ADD_SIMPLE_TYPE_ACTION_ID, items[i].getId());
        i++;
        assertTrue(items[i] instanceof ActionContributionItem);
        assertEquals(ContextMenuConstants.ADD_STRUCTURE_TYPE_ACTION_ID, items[i].getId());
        i++;
        assertTrue(items[i] instanceof Separator);
        assertEquals(ContextMenuConstants.GROUP_CONTEXT_ITEMS, items[i].getId());
        i++;
        assertTrue(items[i] instanceof ActionContributionItem);
        assertEquals(ContextMenuConstants.ADD_ELEMENT_ACTION_ID, items[i].getId());
        i++;
        assertTrue(items[i] instanceof ActionContributionItem);
        assertEquals(ContextMenuConstants.ADD_ATTRIBUTE_ACTION_ID, items[i].getId());
        i++;
        assertTrue(items[i] instanceof Separator);
        assertEquals(ContextMenuConstants.GROUP_REFACTOR, items[i].getId());
        i++;
        assertTrue(items[i] instanceof IMenuManager);
        assertRefactorMenuState((IMenuManager) items[i]);
        i++;
        assertTrue(items[i] instanceof Separator);
        assertEquals(org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.ContextMenuConstants.GROUP_OPEN_IN_NEW_EDITOR, items[i]
                .getId());
        i++;
        assertTrue(items[i] instanceof ActionContributionItem);
        assertEquals(org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.ContextMenuConstants.OPEN_IN_NEW_EDITOR_ACTION_ID, items[i]
                .getId());
        i++;
        assertTrue(items[i] instanceof Separator);
        assertEquals(ContextMenuConstants.GROUP_COPY_PASTE, items[i].getId());
        i++;
        assertTrue(items[i] instanceof ActionContributionItem);
        assertEquals(ContextMenuConstants.REMOVE_ACTION_ID, items[i].getId());
        i++;
        assertTrue(items[i] instanceof Separator);
        assertEquals(ContextMenuConstants.GROUP_EDIT, items[i].getId());
    }

    private void assertRefactorMenuState(final IMenuManager menuManager) {
        assertEquals(7, menuManager.getItems().length);
        int i = 0;
        assertEquals(ContextMenuConstants.GROUP_REFACTOR_GLOBAL_TYPE, menuManager.getItems()[i++].getId());
        assertEquals(ContextMenuConstants.CONVERT_TO_GLOBAL_TYPE_ACTION_ID, menuManager.getItems()[i++].getId());
        assertEquals(ContextMenuConstants.CONVERT_TO_ANONYMOUS_TYPE_ID, menuManager.getItems()[i++].getId());
        assertEquals(ContextMenuConstants.CONVERT_TO_ANONYMOUS_TYPE_WITH_TYPE_CONTENTS_ID, menuManager.getItems()[i++].getId());
        assertEquals(ContextMenuConstants.GROUP_REFACTOR_NAMESPACE, menuManager.getItems()[i++].getId());
        assertEquals(ContextMenuConstants.EXTRACT_NAMESPACE_ACTION_ID, menuManager.getItems()[i++].getId());
        assertEquals(ContextMenuConstants.MAKE_AN_INLINE_NAMESPACE_ACTION_ID, menuManager.getItems()[i++].getId());
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DTTreeContextMenuListener#updateActionsState(org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IDataTypesTreeNode)}
     * .
     */
    @SuppressWarnings("boxing")
    @Test
    public final void testSetActionsEnablementInSiEditor() {
        controllerMock = new SIDTControllerMock(true);
        final ITreeNode nodeMock = createNiceMock(IStructureTypeNode.class);
        final IModelObject objectMock = createISchemaMockFromSameModel();
        expect(nodeMock.getModelObject()).andReturn(objectMock).anyTimes();
        replay(nodeMock);
        controllerMock.setModelObjectForCompare(objectMock);
        controllerMock.setEditDeleteAllowed(Boolean.valueOf(false));
        final IModelObject model = EasyMock.createNiceMock(IModelObject.class);
        expect(model.getModelRoot()).andReturn(null).anyTimes();
        replay(model);
        controllerMock.setModelObject(model);
        treeViewer.setSelection(new StructuredSelection(nodeMock));

        IMenuManager menu = new MenuManager();
        DTTreeContextMenuListener dtTreeContextMenuListener = new DTTreeContextMenuListener(controllerMock, treeViewer);

        dtTreeContextMenuListener.menuAboutToShow(menu);
        IContributionItem[] items = menu.getItems();

        final IModelObject modelObjectMock = createISchemaMockFromSameModel();
        final IDataTypesTreeNode treeNodeMock = createMock(INamespaceNode.class);
        expect(treeNodeMock.isReadOnly()).andReturn(Boolean.valueOf(false)).anyTimes();
        expect(treeNodeMock.getModelObject()).andReturn(modelObjectMock).anyTimes();
        expect(treeNodeMock.getParent()).andReturn(null).anyTimes();
        expect(treeNodeMock.isImportedNode()).andReturn(false).anyTimes();
        expect(treeNodeMock.getCategories()).andReturn(0).anyTimes();
        replay(treeNodeMock);
        treeViewer.setSelection(new StructuredSelection(treeNodeMock));

        final SIDTControllerMock controller = new SIDTControllerMock(false);
        controller.setModelObjectForCompare(modelObjectMock);

        dtTreeContextMenuListener = new DTTreeContextMenuListener(controller, treeViewer);
        menu = new MenuManager();
        dtTreeContextMenuListener.menuAboutToShow(menu);

        verify(treeNodeMock);

        items = menu.getItems();
        for (final IContributionItem iContributionItem : items) {
            if (iContributionItem instanceof ActionContributionItem) {
                final boolean shouldBeEnabled = !ContextMenuConstants.ADD_ATTRIBUTE_ACTION_ID.equals(iContributionItem.getId())
                        && !ContextMenuConstants.COPY_TYPE_ACTION_ID.equals(iContributionItem.getId())
                        && !ContextMenuConstants.PASTE_TYPE_ACTION_ID.equals(iContributionItem.getId())
                        && !ContextMenuConstants.ADD_ELEMENT_ACTION_ID.equals(iContributionItem.getId())
                        && !org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.ContextMenuConstants.OPEN_IN_NEW_EDITOR_ACTION_ID
                                .equals(iContributionItem.getId());
                assertEquals(iContributionItem.toString(), shouldBeEnabled, iContributionItem.isEnabled());
            }
        }
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DTTreeContextMenuListener#updateActionsState(org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IDataTypesTreeNode)}
     * .
     */
    @SuppressWarnings("boxing")
    @Test
    public final void testSetActionsEnablementStandalone() {
        controllerMock = new SIDTControllerMock(true);
        final ITreeNode nodeMock = createNiceMock(IDataTypesTreeNode.class);
        final IModelObject objectMock = createISchemaMockFromSameModel();
        expect(nodeMock.getModelObject()).andReturn(objectMock).anyTimes();
        replay(nodeMock);
        controllerMock.setEditDeleteAllowed(Boolean.valueOf(false));
        controllerMock.setModelObjectForCompare(objectMock);
        final IModelObject mock = EasyMock.createNiceMock(IModelObject.class);
        expect(mock.getModelRoot()).andReturn(null);
        replay(mock);
        controllerMock.setModelObject(mock);
        treeViewer.setSelection(new StructuredSelection(nodeMock));

        IMenuManager menu = new MenuManager();
        DTTreeContextMenuListener dtTreeContextMenuListener = new DTTreeContextMenuListener(controllerMock, treeViewer);

        dtTreeContextMenuListener.menuAboutToShow(menu);
        IContributionItem[] items = menu.getItems();

        final IModelObject modelObjectMock = createISchemaMockFromSameModel();
        final IDataTypesTreeNode treeNodeMock = createNiceMock(INamespaceNode.class);
        expect(treeNodeMock.isReadOnly()).andReturn(Boolean.valueOf(false)).anyTimes();
        expect(treeNodeMock.getModelObject()).andReturn(modelObjectMock).anyTimes();
        expect(treeNodeMock.isImportedNode()).andReturn(false).anyTimes();
        replay(treeNodeMock);
        treeViewer.setSelection(new StructuredSelection(treeNodeMock));

        final DTControllerMock controller = new DTControllerMock(false);
        controller.setModelObjectForCompare(modelObjectMock);

        dtTreeContextMenuListener = new DTTreeContextMenuListener(controller, treeViewer);
        menu = new MenuManager();
        dtTreeContextMenuListener.menuAboutToShow(menu);

        verify(treeNodeMock);

        items = menu.getItems();
        for (final IContributionItem iContributionItem : items) {
            if (iContributionItem instanceof ActionContributionItem) {
                final boolean shouldBeEnabled = !ContextMenuConstants.ADD_ATTRIBUTE_ACTION_ID.equals(iContributionItem.getId())
                        && !ContextMenuConstants.COPY_TYPE_ACTION_ID.equals(iContributionItem.getId())
                        && !ContextMenuConstants.PASTE_TYPE_ACTION_ID.equals(iContributionItem.getId())
                        && !ContextMenuConstants.ADD_ELEMENT_ACTION_ID.equals(iContributionItem.getId())
                        && !org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.ContextMenuConstants.OPEN_IN_NEW_EDITOR_ACTION_ID
                                .equals(iContributionItem.getId());
                assertEquals(iContributionItem.toString(), shouldBeEnabled, iContributionItem.isEnabled());
            }
        }
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DTTreeContextMenuListener#addAddNamespaceMenuAction(org.eclipse.jface.action.IMenuManager)}
     * .
     */
    @SuppressWarnings("boxing")
    @Test
    public final void testAddAddNamespaceMenuAction() {

        final ITreeNode treeNodeMock = createMock(ITreeNode.class);
        treeViewer.setSelection(new StructuredSelection(treeNodeMock));
        controllerMock.setTreeNodeMatch(treeNodeMock);

        final DTTreeContextMenuListener contextMenuListener = new DTTreeContextMenuListener(controllerMock, null);
        MenuManager menuManager = new MenuManager();
        menuManager.add(new GroupMarker(ContextMenuConstants.GROUP_ADD_ITEMS));
        contextMenuListener.addAddNamespaceMenuAction(menuManager);

        assertTrue(menuManager.getItems().length == 2);
        final IContributionItem actionContributionItem = menuManager.getItems()[1];
        assertTrue(actionContributionItem instanceof ActionContributionItem);
        assertEquals(ContextMenuConstants.ADD_NAMESPACE_ACTION_ID, actionContributionItem.getId());
        assertEquals(Messages.DTTreeContextMenuListener_add_namespace_action, ((ActionContributionItem) actionContributionItem)
                .getAction().getText());

        menuManager = new MenuManager();
        menuManager.add(new GroupMarker(ContextMenuConstants.GROUP_ADD_ITEMS));
        contextMenuListener.addAddNamespaceMenuAction(menuManager);

        assertEquals(actionContributionItem, menuManager.getItems()[1]);

        controllerMock.setAddNamespaceCounter(0);
        ((ActionContributionItem) actionContributionItem).getAction().run();
        assertTrue(controllerMock.getAddNamespaceCounter() == 1);

        ((ActionContributionItem) actionContributionItem).getAction().run();
        assertTrue(controllerMock.getAddNamespaceCounter() == 2);
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DTTreeContextMenuListener#addAddElementMenuAction(org.eclipse.jface.action.IMenuManager)}
     * .
     */
    @SuppressWarnings("boxing")
    @Test
    public final void testAddAddElementMenuAction() {

        final ITreeNode treeNodeMock = createMock(ITreeNode.class);
        treeViewer.setSelection(new StructuredSelection(treeNodeMock));
        controllerMock.setTreeNodeMatch(treeNodeMock);

        final DTTreeContextMenuListener contextMenuListener = new DTTreeContextMenuListener(controllerMock, treeViewer);
        MenuManager menuManager = new MenuManager();
        menuManager.add(new GroupMarker(ContextMenuConstants.GROUP_CONTEXT_ITEMS));
        contextMenuListener.addAddElementMenuAction(menuManager);

        assertTrue(menuManager.getItems().length == 2);
        final IContributionItem actionContributionItem = menuManager.getItems()[1];
        assertTrue(actionContributionItem instanceof ActionContributionItem);
        assertEquals(ContextMenuConstants.ADD_ELEMENT_ACTION_ID, actionContributionItem.getId());
        assertEquals(Messages.DTTreeContextMenuListener_add_element_action, ((ActionContributionItem) actionContributionItem)
                .getAction().getText());

        menuManager = new MenuManager();
        menuManager.add(new GroupMarker(ContextMenuConstants.GROUP_CONTEXT_ITEMS));
        contextMenuListener.addAddElementMenuAction(menuManager);

        assertEquals(actionContributionItem, menuManager.getItems()[1]);

        controllerMock.setAddElementCounter(0);
        ((ActionContributionItem) actionContributionItem).getAction().run();
        assertTrue(controllerMock.getAddElementCounter() == 1);

        ((ActionContributionItem) actionContributionItem).getAction().run();
        assertTrue(controllerMock.getAddElementCounter() == 2);
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DTTreeContextMenuListener#addAddSimpleTypeMenuAction(org.eclipse.jface.action.IMenuManager)}
     * .
     */
    @SuppressWarnings("boxing")
    @Test
    public final void testAddAddSimpleTypeMenuAction() {
        final ITreeNode treeNodeMock = createMock(ITreeNode.class);
        treeViewer.setSelection(new StructuredSelection(treeNodeMock));
        controllerMock.setTreeNodeMatch(treeNodeMock);

        final DTTreeContextMenuListener contextMenuListener = new DTTreeContextMenuListener(controllerMock, treeViewer);
        MenuManager menuManager = new MenuManager();
        menuManager.add(new GroupMarker(ContextMenuConstants.GROUP_ADD_ITEMS));
        contextMenuListener.addAddSimpleTypeMenuAction(menuManager);

        assertTrue(menuManager.getItems().length == 2);
        final IContributionItem actionContributionItem = menuManager.getItems()[1];
        assertTrue(actionContributionItem instanceof ActionContributionItem);
        assertEquals(ContextMenuConstants.ADD_SIMPLE_TYPE_ACTION_ID, actionContributionItem.getId());
        assertEquals(Messages.DTTreeContextMenuListener_add_simple_type_action, ((ActionContributionItem) actionContributionItem)
                .getAction().getText());

        menuManager = new MenuManager();
        menuManager.add(new GroupMarker(ContextMenuConstants.GROUP_ADD_ITEMS));
        contextMenuListener.addAddSimpleTypeMenuAction(menuManager);

        assertEquals(actionContributionItem, menuManager.getItems()[1]);

        controllerMock.setAddSimpleTypeCounter(0);
        ((ActionContributionItem) actionContributionItem).getAction().run();
        assertTrue(controllerMock.getAddSimpleTypeCounter() == 1);

        ((ActionContributionItem) actionContributionItem).getAction().run();
        assertTrue(controllerMock.getAddSimpleTypeCounter() == 2);
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DTTreeContextMenuListener#addAddStructureTypeMenuAction(org.eclipse.jface.action.IMenuManager)}
     * .
     */
    @SuppressWarnings("boxing")
    @Test
    public final void testAddAddStructureTypeMenuAction() {
        final ITreeNode treeNodeMock = createMock(ITreeNode.class);
        treeViewer.setSelection(new StructuredSelection(treeNodeMock));
        controllerMock.setTreeNodeMatch(treeNodeMock);

        final DTTreeContextMenuListener contextMenuListener = new DTTreeContextMenuListener(controllerMock, treeViewer);
        MenuManager menuManager = new MenuManager();
        menuManager.add(new GroupMarker(ContextMenuConstants.GROUP_ADD_ITEMS));
        contextMenuListener.addAddStructureTypeMenuAction(menuManager);

        assertTrue(menuManager.getItems().length == 2);
        final IContributionItem actionContributionItem = menuManager.getItems()[1];
        assertTrue(actionContributionItem instanceof ActionContributionItem);
        assertEquals(ContextMenuConstants.ADD_STRUCTURE_TYPE_ACTION_ID, actionContributionItem.getId());
        assertEquals(Messages.DTTreeContextMenuListener_add_structure_type_action,
                ((ActionContributionItem) actionContributionItem).getAction().getText());

        menuManager = new MenuManager();
        menuManager.add(new GroupMarker(ContextMenuConstants.GROUP_ADD_ITEMS));
        contextMenuListener.addAddStructureTypeMenuAction(menuManager);

        assertEquals(actionContributionItem, menuManager.getItems()[1]);

        controllerMock.setAddStructureTypeCounter(0);
        ((ActionContributionItem) actionContributionItem).getAction().run();
        assertTrue(controllerMock.getAddStructureTypeCounter() == 1);

        ((ActionContributionItem) actionContributionItem).getAction().run();
        assertTrue(controllerMock.getAddStructureTypeCounter() == 2);
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DTTreeContextMenuListener#addRemoveMenuAction(org.eclipse.jface.action.IMenuManager)}
     * .
     */
    @SuppressWarnings("boxing")
    @Test
    public final void testAddDeleteMenuAction() {
        final ITreeNode treeNodeMock = createMock(ITreeNode.class);
        treeViewer.setSelection(new StructuredSelection(treeNodeMock));
        controllerMock.setTreeNodeMatch(treeNodeMock);

        final DTTreeContextMenuListener contextMenuListener = new DTTreeContextMenuListener(controllerMock, treeViewer);
        MenuManager menuManager = new MenuManager();
        menuManager.add(new GroupMarker(ContextMenuConstants.GROUP_COPY_PASTE));
        contextMenuListener.addRemoveMenuAction(menuManager);

        assertTrue(menuManager.getItems().length == 2);
        final IContributionItem actionContributionItem = menuManager.getItems()[1];
        assertTrue(actionContributionItem instanceof ActionContributionItem);
        assertEquals(ContextMenuConstants.REMOVE_ACTION_ID, actionContributionItem.getId());
        assertEquals(Messages.DTTreeContextMenuListener_remove_action, ((ActionContributionItem) actionContributionItem)
                .getAction().getText());

        menuManager = new MenuManager();
        menuManager.add(new GroupMarker(ContextMenuConstants.GROUP_COPY_PASTE));
        contextMenuListener.addRemoveMenuAction(menuManager);

        assertEquals(actionContributionItem, menuManager.getItems()[1]);

        controllerMock.setRemoveCounter(0);
        ((ActionContributionItem) actionContributionItem).getAction().run();
        assertTrue(controllerMock.getRemoveCounter() == 1);

        ((ActionContributionItem) actionContributionItem).getAction().run();
        assertTrue(controllerMock.getRemoveCounter() == 2);
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DTTreeContextMenuListener#setAddElementActionEnabled(boolean)}
     * .
     */
    @SuppressWarnings("boxing")
    @Test
    public final void testSetAddElementActionEnabled() {
        controllerMock = new SIDTControllerMock(true);
        final ITreeNode nodeMock = createNiceMock(IDataTypesTreeNode.class);
        final IModelObject objectMock = createISchemaMockFromSameModel();
        expect(nodeMock.getModelObject()).andReturn(objectMock).anyTimes();
        replay(nodeMock);
        controllerMock.setModelObjectForCompare(objectMock);
        treeViewer.setSelection(new StructuredSelection(nodeMock));

        final DTTreeContextMenuListener dtTreeContextMenuListener = new DTTreeContextMenuListener(controllerMock, treeViewer);
        final IMenuManager menu = new MenuManager();
        dtTreeContextMenuListener.menuAboutToShow(menu);

        final ActionContributionItem actionContributionItem = (ActionContributionItem) menu.getItems()[6];
        assertEquals(ContextMenuConstants.ADD_ELEMENT_ACTION_ID, actionContributionItem.getId());

        assertFalse(actionContributionItem.getAction().isEnabled());
        dtTreeContextMenuListener.getAddElementAction().setEnabled(true);
        assertTrue(actionContributionItem.getAction().isEnabled());
        dtTreeContextMenuListener.getAddElementAction().setEnabled(false);
        assertFalse(actionContributionItem.getAction().isEnabled());
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DTTreeContextMenuListener#setDeleteActionEnabled(boolean)}
     * .
     */
    @SuppressWarnings("boxing")
    @Test
    public final void testSetRemoveActionEnabled() {
        controllerMock = new SIDTControllerMock(true);
        final ITreeNode nodeMock = createNiceMock(IDataTypesTreeNode.class);
        final IModelObject objectMock = createISchemaMockFromSameModel();
        expect(nodeMock.getModelObject()).andReturn(objectMock).anyTimes();
        replay(nodeMock);
        controllerMock.setModelObjectForCompare(objectMock);
        treeViewer.setSelection(new StructuredSelection(nodeMock));

        final DTTreeContextMenuListener dtTreeContextMenuListener = new DTTreeContextMenuListener(controllerMock, treeViewer);
        final IMenuManager menu = new MenuManager();
        dtTreeContextMenuListener.menuAboutToShow(menu);

        ActionContributionItem actionContributionItem = null;
        for (final IContributionItem item : menu.getItems()) {
            if (ContextMenuConstants.REMOVE_ACTION_ID.equals(item.getId())) {
                actionContributionItem = (ActionContributionItem) item;
                break;
            }
        }

        assertNotNull(actionContributionItem);

        assertFalse(actionContributionItem.getAction().isEnabled());
        dtTreeContextMenuListener.getDeleteAction().setEnabled(true);
        assertTrue(actionContributionItem.getAction().isEnabled());
        dtTreeContextMenuListener.getDeleteAction().setEnabled(false);
        assertFalse(actionContributionItem.getAction().isEnabled());
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DTTreeContextMenuListener#setAddSimpleTypeActionEnabled(boolean)}
     * .
     */
    @SuppressWarnings("boxing")
    @Test
    public final void testSetAddSimpleTypeActionEnabled() {
        controllerMock = new SIDTControllerMock(true);
        final ITreeNode nodeMock = createNiceMock(IDataTypesTreeNode.class);
        final IModelObject objectMock = createISchemaMockFromSameModel();
        expect(nodeMock.getModelObject()).andReturn(objectMock).anyTimes();
        replay(nodeMock);
        controllerMock.setModelObjectForCompare(objectMock);
        treeViewer.setSelection(new StructuredSelection(nodeMock));

        final DTTreeContextMenuListener dtTreeContextMenuListener = new DTTreeContextMenuListener(controllerMock, treeViewer);
        final IMenuManager menu = new MenuManager();
        dtTreeContextMenuListener.menuAboutToShow(menu);
        final ActionContributionItem actionContributionItem = (ActionContributionItem) menu.getItems()[3];
        assertEquals(ContextMenuConstants.ADD_SIMPLE_TYPE_ACTION_ID, actionContributionItem.getId());

        assertFalse(actionContributionItem.getAction().isEnabled());
        dtTreeContextMenuListener.getAddSimpleTypeAction().setEnabled(true);
        assertTrue(actionContributionItem.getAction().isEnabled());
        dtTreeContextMenuListener.getAddSimpleTypeAction().setEnabled(false);
        assertFalse(actionContributionItem.getAction().isEnabled());
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DTTreeContextMenuListener#setAddStructureTypeActionEnabled(boolean)}
     * .
     */
    @SuppressWarnings("boxing")
    @Test
    public final void testSetAddStructureTypeActionEnabled() {
        controllerMock = new SIDTControllerMock(true);
        final ITreeNode nodeMock = createNiceMock(IDataTypesTreeNode.class);
        final IModelObject objectMock = createISchemaMockFromSameModel();
        expect(nodeMock.getModelObject()).andReturn(objectMock).anyTimes();
        replay(nodeMock);
        treeViewer.setSelection(new StructuredSelection(nodeMock));
        controllerMock.setModelObjectForCompare(objectMock);

        final DTTreeContextMenuListener dtTreeContextMenuListener = new DTTreeContextMenuListener(controllerMock, treeViewer);
        final IMenuManager menu = new MenuManager();
        dtTreeContextMenuListener.menuAboutToShow(menu);

        final ActionContributionItem actionContributionItem = (ActionContributionItem) menu.getItems()[4];
        assertEquals(ContextMenuConstants.ADD_STRUCTURE_TYPE_ACTION_ID, actionContributionItem.getId());

        assertFalse(actionContributionItem.getAction().isEnabled());
        dtTreeContextMenuListener.getAddStructureTypeAction().setEnabled(true);
        assertTrue(actionContributionItem.getAction().isEnabled());
        dtTreeContextMenuListener.getAddStructureTypeAction().setEnabled(false);
        assertFalse(actionContributionItem.getAction().isEnabled());
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DTTreeContextMenuListener#setAddNamespaceActionEnabled(boolean)}
     * .
     */
    @SuppressWarnings("boxing")
    @Test
    public final void testSetAddNamespaceActionEnabled() {
        controllerMock = new SIDTControllerMock(true);
        final ITreeNode nodeMock = createNiceMock(IDataTypesTreeNode.class);
        final IModelObject objectMock = createISchemaMockFromSameModel();
        expect(nodeMock.getModelObject()).andReturn(objectMock).anyTimes();
        replay(nodeMock);
        controllerMock.setModelObjectForCompare(objectMock);
        controllerMock.setEditDeleteAllowed(Boolean.valueOf(false));
        treeViewer.setSelection(new StructuredSelection(nodeMock));

        final DTTreeContextMenuListener dtTreeContextMenuListener = new DTTreeContextMenuListener(controllerMock, treeViewer);
        final IMenuManager menu = new MenuManager();
        dtTreeContextMenuListener.menuAboutToShow(menu);

        final ActionContributionItem actionContributionItem = (ActionContributionItem) menu.getItems()[1];
        assertEquals(ContextMenuConstants.ADD_NAMESPACE_ACTION_ID, actionContributionItem.getId());

        assertFalse(actionContributionItem.getAction().isEnabled());
        dtTreeContextMenuListener.getAddNamespaceAction().setEnabled(true);
        assertTrue(actionContributionItem.getAction().isEnabled());
        dtTreeContextMenuListener.getAddNamespaceAction().setEnabled(false);
        assertFalse(actionContributionItem.getAction().isEnabled());
    }

    private ISchema createISchemaMockFromSameModel() {
        final IModelRoot modelRoot = createNiceMock(IModelRoot.class);
        final ISchema modelObject = createNiceMock(ISchema.class);
        final XSDSchema eObject = createNiceMock(XSDSchema.class);

        expect(modelRoot.getModelObject()).andReturn(modelObject).anyTimes();

        expect(modelObject.getModelRoot()).andReturn(modelRoot).anyTimes();
        expect(modelObject.getComponent()).andReturn(eObject).anyTimes();

        replay(modelRoot, modelObject, eObject);
        return modelObject;
    }

}
