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
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.extractwizard.ExtractNamespaceWizard;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IDataTypesTreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IElementNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.INamespaceNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.ISimpleTypeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IStructureTypeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.ElementNode;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.test.util.EasymockModelUtils;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.impl.XSDComplexTypeDefinitionImpl;
import org.eclipse.xsd.impl.XSDSimpleTypeDefinitionImpl;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.common.DeleteSetCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.InlineNamespaceCompositeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddSimpleTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddStructureTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.InlineStructureTypeContentsCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.MakeAnonymousTypeGlobalCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.MakeGlobalTypeAnonymousCommand;
import org.eclipse.wst.sse.sieditor.core.common.IEnvironment;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.INamedObject;
import org.eclipse.wst.sse.sieditor.model.api.INamespacedObject;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.utils.StatusUtils;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;

/**
 *
 * 
 */
public class DataTypesFormPageControllerJUnitTest {

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        StatusUtils.isUnderJunitExecution = true;
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

    private static class TestDataTypesFormPageController extends DataTypesFormPageController {

        private boolean editAllowed; // flag determining the isEditAllowed
        // method return value
        private Object editedMatch; // used to confirm the object for which the
        // isEditAllowed check is called
        private IXSDModelRoot xsdModelRoot; // returned by the
        // getXSDModelRoot(ISchema) method
        private Object selectObjectMatch; // used to confirm the object fired
        // for selection
        private int fireNodeSelectionCounter; // counts the calls to
        // fireNodeSelectionEvent(IModelObject)
        private ISchema retrievedSchema; // the returned result of
        // getSchema(IModelObject)
        private int fireErrorMsgCounter; // counts the calls to
        private boolean isDeleteAllowed;
        private Object deleteMatch;
        private ITreeNode nextTreeNode;
        private ITreeNode nextTreeNodeMatch;
        private Object removeNodeMatch;
        private int editCounter;
        private IModelObject partOfDocumentMatch;
        private Boolean isPartOfEdittedDocument;
        private int partOfEditedDocCounter;
        private int isDeleteCounter;
        private int resourceReadOnlyCounter;

        // fireErrorMsgEvent(String)

        public TestDataTypesFormPageController(final IModelRoot model, final boolean readOnly) {
            super(model, readOnly);
            editAllowed = true;
            isDeleteAllowed = true;
        }

        // the folowing 3 methods override manage the mocking of the
        // isEditAllowed method
        @Override
        protected boolean isEditAllowed(final Object editedObject) {
            assertEquals(editedMatch, editedObject);
            editCounter++;
            return editAllowed;
        }

        public void setEditAllowed(final boolean editAllowed) {
            this.editAllowed = editAllowed;
        }

        public void setEditedMatch(final Object editedMatch) {
            this.editedMatch = editedMatch;
        }

        @Override
        protected IXSDModelRoot getXSDModelRoot(final ISchema schema) {
            assertEquals(editedMatch, schema);
            return xsdModelRoot;
        }

        public void setRetrievedXsdModelRoot(final IXSDModelRoot xsdModelRoot) {
            this.xsdModelRoot = xsdModelRoot;
        }

        @Override
        public void fireTreeNodeSelectionEvent(final IModelObject modelObject) {
            assertEquals(selectObjectMatch, modelObject);
            fireNodeSelectionCounter++;
        }

        /**
         * //used to confirm the object fired for selection <br>
         * -set this object to assert that the same will be givven to the <br>
         * fireTreeNodeSelectionEvent(IModelObject) method
         * 
         * @param selectObjectMatch
         */
        public void setSelectObjectMatch(final Object selectObjectMatch) {
            this.selectObjectMatch = selectObjectMatch;
        }

        /**
         * used to set value of the field counting
         * "fireTreeNodeSelectionEvent(IModelObject) calls
         * 
         * @param fireNodeSelectionCounter
         */
        public void setFireNodeSelectionCounter(final int fireNodeSelectionCounter) {
            this.fireNodeSelectionCounter = fireNodeSelectionCounter;
        }

        /**
         * used to get value of the field counting
         * "fireTreeNodeSelectionEvent(IModelObject) calls
         * 
         * @param fireNodeSelectionCounter
         */
        public int getFireNodeSelectionCounter() {
            return fireNodeSelectionCounter;
        }

        @Override
        protected ISchema getSchema(final ITreeNode node) {
            if (retrievedSchema == null) {
                return super.getSchema(node);
            }
            return retrievedSchema;
        }

        public void setRetrievedSchema(final ISchema retrievedSchema) {
            this.retrievedSchema = retrievedSchema;
        }

        @Override
        public void fireShowErrorMsgEvent(final String message) {
            fireErrorMsgCounter++;
        }

        public void setFireErrorMsgCounter(final int i) {
            fireErrorMsgCounter = i;

        }

        @Override
        protected boolean isDeleteAllowed(final Object editedObject) {
            assertEquals(deleteMatch, editedObject);
            isDeleteCounter++;
            return isDeleteAllowed;
        }

        public void setDeleteMatch(final Object deleteMatch) {
            this.deleteMatch = deleteMatch;
        }

        public void setDeleteAllowed(final boolean isDeleteAllowed) {
            this.isDeleteAllowed = isDeleteAllowed;
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

        @Override
        public boolean isPartOfEdittedDocument(final IModelObject modelObject) {
            if (isPartOfEdittedDocument != null) {
                assertEquals(partOfDocumentMatch, modelObject);
                partOfEditedDocCounter++;
                return isPartOfEdittedDocument.booleanValue();
            }
            return super.isPartOfEdittedDocument(modelObject);
        }

        public int getPartOfEditedDocCounter() {
            return partOfEditedDocCounter;
        }

        public void setPartOfEditedDocCounter(final int partOfEditedDocCounter) {
            this.partOfEditedDocCounter = partOfEditedDocCounter;
        }

        public void setPartOfDocumentMatch(final IModelObject partOfDocumentMatch) {
            this.partOfDocumentMatch = partOfDocumentMatch;
        }

        public void setPartOfEdittedDocument(final Boolean isPartOfEdittedDocument) {
            this.isPartOfEdittedDocument = isPartOfEdittedDocument;
        }

        // for testing purpouses
        @Override
        public boolean isAddTypeEnabled(final IDataTypesTreeNode selectedNode) {
            return super.isAddTypeEnabled(selectedNode);
        }

        @Override
        public IModelObject getModelObject() {
            return super.getModelObject();
        }

        @Override
        public boolean isResourceReadOnly() {
            resourceReadOnlyCounter++;
            return readOnly;
        }

        public int getResourceReadOnlyCounter() {
            return resourceReadOnlyCounter;
        }

        public void setResourceReadOnlyCounter(final int resourceReadOnlyCounter) {
            this.resourceReadOnlyCounter = resourceReadOnlyCounter;
        }

        public void setReadOnly(final boolean readOnly) {
            this.readOnly = readOnly;
        }
    }

    @Test
    public final void testGetSchema() {
        final TestDataTypesFormPageController controller = new TestDataTypesFormPageController(createMock(IModelRoot.class),
                false);
        final INamespaceNode namespaceMock = createMock(INamespaceNode.class);
        final ISchema schemaMock = createMock(ISchema.class);
        expect(namespaceMock.getModelObject()).andReturn(schemaMock);
        replay(namespaceMock);
        assertEquals(schemaMock, controller.getSchema(namespaceMock));
        verify(namespaceMock);

        reset(namespaceMock);
        final ISimpleTypeNode simpleTypeNodeMock = createMock(ISimpleTypeNode.class);
        expect(simpleTypeNodeMock.getParent()).andReturn(namespaceMock);
        expect(namespaceMock.getModelObject()).andReturn(schemaMock);
        replay(simpleTypeNodeMock, namespaceMock);
        assertEquals(schemaMock, controller.getSchema(simpleTypeNodeMock));
        verify(simpleTypeNodeMock, namespaceMock);

        reset(namespaceMock);
        final IStructureTypeNode structureTypeNodeMock = createMock(IStructureTypeNode.class);
        expect(structureTypeNodeMock.getParent()).andReturn(namespaceMock);
        expect(namespaceMock.getModelObject()).andReturn(schemaMock);
        replay(structureTypeNodeMock, namespaceMock);
        assertEquals(schemaMock, controller.getSchema(structureTypeNodeMock));
        verify(structureTypeNodeMock, namespaceMock);

        reset(namespaceMock);
        final IElementNode elementNodeMock = createMock(IElementNode.class);
        final IDataTypesTreeNode parentMock = createMock(IDataTypesTreeNode.class);
        expect(elementNodeMock.getParent()).andReturn(parentMock);
        expect(parentMock.getParent()).andReturn(namespaceMock);
        expect(namespaceMock.getModelObject()).andReturn(schemaMock);
        replay(elementNodeMock, namespaceMock, parentMock);
        assertEquals(schemaMock, controller.getSchema(elementNodeMock));
        verify(elementNodeMock, namespaceMock, parentMock);
        assertEquals(null, controller.getSchema(createMock(ITreeNode.class)));
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController#getModelObject()}
     * .
     */
    @Test
    public final void testGetModelObject() {
        final IModelRoot modelRoot = createNiceMock(IModelRoot.class);
        TestDataTypesFormPageController controller = new TestDataTypesFormPageController(modelRoot, false);

        assertNull(controller.getModelObject());

        // if the model root is an IWisdlModelRoot
        final IWsdlModelRoot wsdlModelRootMock = createMock(IWsdlModelRoot.class);
        final IDescription descriptionMock = createMock(IDescription.class);
        expect(wsdlModelRootMock.getModelObject()).andReturn(descriptionMock);
        replay(wsdlModelRootMock);

        controller = new TestDataTypesFormPageController(wsdlModelRootMock, false);
        assertEquals(descriptionMock, controller.getModelObject());
        verify(wsdlModelRootMock);

        // if the model root is an IXSDModelRoot
        final IXSDModelRoot xsdModelRootMock = createMock(IXSDModelRoot.class);
        final ISchema schemaMock = createMock(ISchema.class);
        expect(xsdModelRootMock.getModelObject()).andReturn(schemaMock);
        replay(xsdModelRootMock);

        controller = new TestDataTypesFormPageController(xsdModelRootMock, false);
        assertEquals(schemaMock, controller.getModelObject());
        verify(xsdModelRootMock);
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController#getNextTreeNode(org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode)}
     * .
     */
    @Test
    public final void testGetNextTreeNode() {
        final ITreeNode selectedNodeMock = createNiceMock(ITreeNode.class);
        final ITreeNode resultTreeNode = createNiceMock(ITreeNode.class);
        final Object[] siblingsMocks = new Object[] { createNiceMock(ITreeNode.class), selectedNodeMock,
                createNiceMock(ITreeNode.class) };

        class DataTypesFormPageControllerExt extends DataTypesFormPageController {
            private Object selectedNodeMatch;

            public DataTypesFormPageControllerExt(final IModelRoot model) {
                super(model, false);
            }

            @Override
            public ITreeNode getNextTreeNode(final ITreeNode selectedTreeNode) {
                return super.getNextTreeNode(selectedTreeNode);
            }

            @Override
            protected ITreeNode getNextSiblingTreeNode(final ITreeNode selectedTreeNode, final Object[] siblings) {
                assertEquals(selectedNodeMatch, selectedTreeNode);
                for (int i = 0; i < siblingsMocks.length; i++) {
                    assertEquals(siblingsMocks[i], siblings[i]);
                }
                return resultTreeNode;
            }

            public void setSelectedNodeMatch(final Object selectedNodeMatch) {
                this.selectedNodeMatch = selectedNodeMatch;
            }
        }
        ;

        DataTypesFormPageControllerExt controller = new DataTypesFormPageControllerExt(createMock(IXSDModelRoot.class));

        controller.setSelectedNodeMatch(selectedNodeMock);

        assertNull(controller.getNextTreeNode(null));
        // if the selected node has no parents but it is not a Namespace node
        expect(selectedNodeMock.getParent()).andReturn(null);

        replay(selectedNodeMock);
        boolean caughtFlag = false;
        try {
            controller.getNextTreeNode(selectedNodeMock);
        } catch (final InvalidParameterException e) {
            caughtFlag = true;
        }
        assertTrue(caughtFlag);
        verify(selectedNodeMock);

        // if the selected node hasn't got a parent but it is a namespace node
        final ITreeNode namespaceNodeMock = createMock(INamespaceNode.class);
        expect(namespaceNodeMock.getParent()).andReturn(null);
        replay(namespaceNodeMock);

        assertNull(controller.getNextTreeNode(namespaceNodeMock));

        // if the node has a parent
        reset(selectedNodeMock);

        final ITreeNode parentNodeMock = createMock(ITreeNode.class);
        expect(selectedNodeMock.getParent()).andReturn(parentNodeMock).times(2);
        expect(parentNodeMock.getChildren()).andReturn(siblingsMocks);
        replay(selectedNodeMock, parentNodeMock);

        assertEquals(resultTreeNode, controller.getNextTreeNode(selectedNodeMock));

        verify(selectedNodeMock, parentNodeMock);

        reset(namespaceNodeMock);

        final IWsdlModelRoot wsdlRootMock = createMock(IWsdlModelRoot.class);
        controller = new DataTypesFormPageControllerExt(wsdlRootMock);
        controller.setSelectedNodeMatch(namespaceNodeMock);

        final IDescription descriptionMock = createMock(IDescription.class);
        expect(wsdlRootMock.getDescription()).andReturn(descriptionMock);
        final List<ISchema> schemaList = new ArrayList<ISchema>();
        schemaList.add(createMock(ISchema.class));
        schemaList.add(createMock(ISchema.class));
        schemaList.add(createMock(ISchema.class));
        expect(descriptionMock.getContainedSchemas()).andReturn(schemaList);
        controller.getTreeNodeMapper().addToNodeMap(schemaList.get(0), (ITreeNode) siblingsMocks[0]);
        controller.getTreeNodeMapper().addToNodeMap(schemaList.get(1), (ITreeNode) siblingsMocks[1]);
        controller.getTreeNodeMapper().addToNodeMap(schemaList.get(2), (ITreeNode) siblingsMocks[2]);

        expect(namespaceNodeMock.getParent()).andReturn(null);
        expect(namespaceNodeMock.getCategories()).andReturn(ITreeNode.CATEGORY_MAIN).anyTimes();

        replay(namespaceNodeMock, wsdlRootMock, descriptionMock);

        assertEquals(resultTreeNode, controller.getNextTreeNode(namespaceNodeMock));

        verify(namespaceNodeMock, wsdlRootMock, descriptionMock);
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController#removeNodeAndItsChildrenFromMap(org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode)}
     * .
     */
    @SuppressWarnings("boxing")
    @Test
    public final void testRemoveNodeNItsChildrenFromMap() {
        final DataTypesFormPageController controller = new DataTypesFormPageController(null, false);

        final ITreeNode rootTreeNodeMock = createNiceMock(ITreeNode.class);
        final IModelObject rootModelObjectMock = createNiceMock(IModelObject.class);

        final ITreeNode child1TreeNodeMock = createNiceMock(ITreeNode.class);
        final IModelObject child1ModelObjectMock = createNiceMock(IModelObject.class);
        final ITreeNode child2TreeNodeMock = createNiceMock(ITreeNode.class);
        final IModelObject child2ModelObjectMock = createNiceMock(IModelObject.class);
        final ITreeNode child1ChildTreeNodeMock = createNiceMock(ITreeNode.class);
        final IModelObject child1ChildModelObjectMock = createNiceMock(IModelObject.class);

        controller.getTreeNodeMapper().addToNodeMap(rootModelObjectMock, rootTreeNodeMock);
        controller.getTreeNodeMapper().addToNodeMap(child1ModelObjectMock, child1TreeNodeMock);
        controller.getTreeNodeMapper().addToNodeMap(child2ModelObjectMock, child2TreeNodeMock);
        controller.getTreeNodeMapper().addToNodeMap(child1ChildModelObjectMock, child1ChildTreeNodeMock);

        expect(rootTreeNodeMock.hasChildren()).andReturn(Boolean.valueOf(false));
        expect(rootTreeNodeMock.getModelObject()).andReturn(rootModelObjectMock).atLeastOnce();

        expect(rootTreeNodeMock.hasChildren()).andReturn(Boolean.valueOf(true));
        expect(rootTreeNodeMock.getChildren()).andReturn(new Object[] { child1TreeNodeMock, child2TreeNodeMock });

        expect(child1TreeNodeMock.hasChildren()).andReturn(Boolean.valueOf(true));
        expect(child1TreeNodeMock.getChildren()).andReturn(new Object[] { child1ChildTreeNodeMock });

        expect(child1ChildTreeNodeMock.hasChildren()).andReturn(Boolean.valueOf(false));
        expect(child1ChildTreeNodeMock.getModelObject()).andReturn(child1ChildModelObjectMock).atLeastOnce();

        expect(child1TreeNodeMock.getModelObject()).andReturn(child1ModelObjectMock).atLeastOnce();

        expect(child2TreeNodeMock.hasChildren()).andReturn(Boolean.valueOf(false));
        expect(child2TreeNodeMock.getModelObject()).andReturn(child2ModelObjectMock).atLeastOnce();

        replay(rootTreeNodeMock, child1TreeNodeMock, child2TreeNodeMock, child1ChildTreeNodeMock);

        assertNotNull(controller.getTreeNodeMapper().getTreeNode(rootModelObjectMock));

        controller.removeNodeAndItsChildrenFromMap(rootTreeNodeMock);

        assertNull(controller.getTreeNodeMapper().getTreeNode(rootModelObjectMock));

        controller.getTreeNodeMapper().addToNodeMap(rootModelObjectMock, rootTreeNodeMock);

        assertNotNull(controller.getTreeNodeMapper().getTreeNode(rootModelObjectMock));
        assertNotNull(controller.getTreeNodeMapper().getTreeNode(child1ModelObjectMock));
        assertNotNull(controller.getTreeNodeMapper().getTreeNode(child2ModelObjectMock));

        controller.removeNodeAndItsChildrenFromMap(rootTreeNodeMock);

        assertNull(controller.getTreeNodeMapper().getTreeNode(rootModelObjectMock));
        assertNull(controller.getTreeNodeMapper().getTreeNode(child1ModelObjectMock));
        assertNull(controller.getTreeNodeMapper().getTreeNode(child2ModelObjectMock));
        assertNull(controller.getTreeNodeMapper().getTreeNode(child1ChildModelObjectMock));

        verify(rootTreeNodeMock);
        verify(child1TreeNodeMock);
        verify(child2TreeNodeMock);
        verify(child1ChildTreeNodeMock);
    }

    @Test
    public final void testRemoveLocalElementRefGlobalFromMap() {
        final IElement elementMock = EasymockModelUtils.createIElementMockFromSameModel();
        final IStructureType typeMock = createMock(IStructureType.class);
        expect(elementMock.getType()).andReturn(typeMock).atLeastOnce();
        expect(typeMock.isElement()).andReturn(Boolean.valueOf(true));

        final DataTypesFormPageController controller = new DataTypesFormPageController(null, false);
        replay(elementMock, typeMock);

        // mock the behaviour of a Global Element
        final ITreeNode parentNode = createNiceMock(ITreeNode.class);
        replay(parentNode);

        final ElementNode node = new ElementNode(elementMock, parentNode, controller.getTreeNodeMapper()) {
            @Override
            public boolean hasChildren() {
                return false;
            }

            @Override
            public int getCategories() {
                return super.getCategories() | ITreeNode.CATEGORY_REFERENCE;
            }
        };
        controller.getTreeNodeMapper().addToNodeMap(elementMock, node);

        assertEquals(node, controller.getTreeNodeMapper().getTreeNode(elementMock));

        controller.removeNodeAndItsChildrenFromMap(node);

        assertNull(controller.getTreeNodeMapper().getTreeNode(elementMock));

        // mock the behaviour of a ComplexType
        reset(typeMock);
        expect(typeMock.isElement()).andReturn(Boolean.valueOf(false));
        replay(typeMock);

        controller.getTreeNodeMapper().removeNodeFromMap(elementMock);

        assertNull(controller.getTreeNodeMapper().getTreeNode(elementMock));
    }

    @Test
    public final void testRemoveLocalElementOfSimpleTypeFromMap() {
        final IElement elementMock = createNiceMock(IElement.class);
        expect(elementMock.getType()).andReturn(createMock(ISimpleType.class)).atLeastOnce();

        replay(elementMock);

        final DataTypesFormPageController controller = new DataTypesFormPageController(null, false);

        // mock the behaviour of a SimpleType
        final ITreeNode parentNode = createNiceMock(ITreeNode.class);
        replay(parentNode);

        final ElementNode node = new ElementNode(elementMock, parentNode, controller.getTreeNodeMapper());

        controller.getTreeNodeMapper().addToNodeMap(elementMock, node);

        assertEquals(node, controller.getTreeNodeMapper().getTreeNode(elementMock));

        controller.removeNodeAndItsChildrenFromMap(node);

        assertNull(controller.getTreeNodeMapper().getTreeNode(elementMock));

        verify(elementMock);
    }

    @Test
    public void isAddElementEnabledForStructureType_SimpleContent() {
        final IDataTypesTreeNode node = createMock(IDataTypesTreeNode.class);
        expect(node.getCategories()).andReturn(0);
        expect(node.isReadOnly()).andReturn(false);

        final IStructureType structureType = createMock(IStructureType.class);
        expect(node.getModelObject()).andReturn(structureType);
        expect(structureType.isComplexTypeSimpleContent()).andReturn(true);

        replay(node, structureType);

        final DataTypesFormPageController controller = new DataTypesFormPageController(null, false);
        assertFalse(controller.isAddElementEnabled(node));
    }

    @Test
    public void isAddElementEnabledForStructureType_ComplexContent() {
        final IDataTypesTreeNode node = createMock(IDataTypesTreeNode.class);
        expect(node.getCategories()).andReturn(0);
        expect(node.isReadOnly()).andReturn(false);

        final IStructureType structureType = createMock(IStructureType.class);
        expect(node.getModelObject()).andReturn(structureType);
        expect(structureType.isComplexTypeSimpleContent()).andReturn(false);
        expect(structureType.getComponent()).andReturn(null);
        expect(structureType.isElement()).andReturn(false);

        replay(node, structureType);

        final DataTypesFormPageController controller = new DataTypesFormPageController(null, false);
        assertTrue(controller.isAddElementEnabled(node));
    }

    /**
     * Test method for the handleAddElementActionInvalidAndGlobalElement() -
     * tests the logic verifying the parameters<br>
     * And if the method constructs a command adding an element for an ISchema
     * and calls selection of the new tree node. Does not verify if the command
     * is properly setup and executed. Does not verify if the newly created
     * element is the one to be selected. To do so, probably a plugin test
     * should be written.
     * 
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController#handleAddElementAction(org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode)}
     * .
     * 
     * @throws ExecutionException
     */
    @Test
    public final void testHandleAddElementActionInvalidAndGlobalElement() throws ExecutionException {
        final IModelRoot modelRootMock = createMock(IWsdlModelRoot.class);
        final IEnvironment envMock = createMock(IEnvironment.class);
        expect(modelRootMock.getEnv()).andReturn(envMock).atLeastOnce();
        expect(envMock.getEditingDomain()).andReturn(null).atLeastOnce();
        try {
            expect(envMock.execute(isA(AddStructureTypeCommand.class))).andReturn(Status.OK_STATUS);
        } catch (final ExecutionException e) {
            fail();// this should never fail
        }

        final ITreeNode selectedNodeMock = createNiceMock(ITreeNode.class);
        expect(selectedNodeMock.getModelObject()).andReturn(null);

        replay(modelRootMock, selectedNodeMock, envMock);

        final TestDataTypesFormPageController dataTypesFormPageController = new TestDataTypesFormPageController(modelRootMock,
                false);
        // checks the case when the selectedNode's model object is null
        boolean caught = false;
        try {
            dataTypesFormPageController.handleAddElementAction(selectedNodeMock);
        } catch (final IllegalArgumentException e) {
            caught = true;
        }
        assertTrue(caught);

        verify(selectedNodeMock);
        // when the model object of the selected node is read only or not
        // applicable of type ISchema
        reset(selectedNodeMock);
        // returns just a mock to test cases when isEditAllowed returns false,
        // or the model object is
        // not an applicable for the action
        final IModelObject modelObjectMock = createMock(IModelObject.class);
        expect(selectedNodeMock.getModelObject()).andReturn(modelObjectMock).atLeastOnce();
        replay(selectedNodeMock);
        dataTypesFormPageController.getTreeNodeMapper().addToNodeMap(modelObjectMock, selectedNodeMock);
        // when the node is read only
        dataTypesFormPageController.setEditedMatch(modelObjectMock);
        dataTypesFormPageController.setEditAllowed(false);
        dataTypesFormPageController.handleAddElementAction(selectedNodeMock);
        dataTypesFormPageController.setEditAllowed(true);
        // ...
        // test the case when the model object of the selected node is not an
        // acceptable parameter(for element addition)
        caught = false;
        try {
            dataTypesFormPageController.handleAddElementAction(selectedNodeMock);
        } catch (final IllegalStateException e) {
            caught = true;
        }
        assertTrue(caught);

        verify(selectedNodeMock);
        reset(selectedNodeMock);
        // returns ISchema mock to test the case when an element is added to a
        // schema
        final ISchema schemaMock = createNiceMock(ISchema.class);
        expect(schemaMock.getAllTypes("Element1")).andReturn(new IType[0]);
        expect(selectedNodeMock.getModelObject()).andReturn(schemaMock);
        final IXSDModelRoot xsdRootMock = createMock(IXSDModelRoot.class);
        expect(schemaMock.getModelRoot()).andReturn(xsdRootMock);
        expect(xsdRootMock.getEnv()).andReturn(envMock).atLeastOnce();
        replay(selectedNodeMock, xsdRootMock, schemaMock);
        // test with a wsdl model root
        dataTypesFormPageController.setEditedMatch(schemaMock);
        dataTypesFormPageController.setFireNodeSelectionCounter(0);
        dataTypesFormPageController.setSelectObjectMatch(null);
        dataTypesFormPageController.setRetrievedXsdModelRoot(xsdRootMock);
        dataTypesFormPageController.getTreeNodeMapper().addToNodeMap(schemaMock, selectedNodeMock);

        dataTypesFormPageController.handleAddElementAction(selectedNodeMock);

        // place here any code verifying command execution

        // case when the model object to which elements are added is an ISchema

        verify(modelRootMock);
        verify(selectedNodeMock);
        verify(xsdRootMock);
    }

    /*
     * Test method for the handleAddElementActionInvalidAndGlobalElement()
     * -tests creation of commands and attempt for execution for adding elements
     * to StructureTypes and anonymous type elements and calls selection of the
     * new tree node. Does not verify if the command is properly setup and
     * executed. Does not verify if the newly created element is the one to be
     * selected. To do so, probably a plugin test should be written. {@link
     * org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController#
     * handleAddSimpleTypeAction
     * (org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode)}
     * 
     * TEST COVERED BY ActionsSelectionTest
     * 
     * @SuppressWarnings("boxing")
     * 
     * @Test public final void
     * testHandleAddElementActionToStructureTypeAndElement() { IXSDModelRoot
     * modelRootMock = createMock(IXSDModelRoot.class); IEnvironment envMock =
     * createMock(IEnvironment.class); ITreeNode selectedNodeMock =
     * createMock(ITreeNode.class); // when the model object of the slecdted
     * node is of type IStructyreType
     * 
     * expect(modelRootMock.getEnv()).andReturn(envMock).times(2);
     * expect(envMock.getEditingDomain()).andReturn(null); try {
     * expect(envMock.execute
     * (isA(AddElementCommand.class))).andReturn(Status.OK_STATUS); } catch
     * (ExecutionException e) { fail();// this should never fail }
     * 
     * IStructureType structureTypeMock = createMock(IStructureType.class);
     * expect(structureTypeMock.getModelRoot()).andReturn(modelRootMock);
     * expect(
     * structureTypeMock.getElements(isA(String.class))).andStubReturn(Collections
     * .emptyList());
     * expect(selectedNodeMock.getModelObject()).andReturn(structureTypeMock);
     * replay(selectedNodeMock, modelRootMock, envMock, structureTypeMock);
     * 
     * TestDataTypesFormPageController dataTypesFormPageController = new
     * TestDataTypesFormPageController(modelRootMock, false);
     * 
     * dataTypesFormPageController.setEditedMatch(structureTypeMock);
     * dataTypesFormPageController.handleAddElementAction(selectedNodeMock);
     * 
     * verify(selectedNodeMock, modelRootMock, envMock, structureTypeMock); //
     * when the model object of the slecdted node is of type IStructyreType
     * reset(selectedNodeMock, modelRootMock, envMock, structureTypeMock);
     * 
     * expect(modelRootMock.getEnv()).andReturn(envMock).times(2);
     * expect(envMock.getEditingDomain()).andReturn(null); try {
     * expect(envMock.execute
     * (isA(AddElementCommand.class))).andReturn(Status.OK_STATUS); } catch
     * (ExecutionException e) { fail();// this should never fail }
     * 
     * IElement elementMock = createMock(IElement.class);
     * 
     * expect(elementMock.getType()).andReturn(structureTypeMock);
     * expect(structureTypeMock.isAnonymous()).andReturn(Boolean.valueOf(true));
     * expect(elementMock.getModelRoot()).andReturn(modelRootMock);
     * expect(structureTypeMock
     * .getElements(isA(String.class))).andStubReturn(Collections.emptyList());
     * expect(selectedNodeMock.getModelObject()).andReturn(elementMock);
     * replay(selectedNodeMock, modelRootMock, envMock, elementMock,
     * structureTypeMock);
     * 
     * dataTypesFormPageController.setEditedMatch(elementMock);
     * dataTypesFormPageController.handleAddElementAction(selectedNodeMock);
     * 
     * verify(modelRootMock, selectedNodeMock, envMock, structureTypeMock); }
     */

    /**
     * Test Method tests the gandleAddSimpleTypeAction(ITreeNode) method - the
     * test verifies exceptional<br>
     * cases and creation of addSimpleTypeCommand. The test does not verify the
     * command execution, effect<br>
     * and command state. There is no check if the node intended to be selected
     * via fireTreeNodeSelectionEvent()
     */
    @Test
    public final void testHandleAddSimpleTypeAction() {
        final IModelRoot modelRootMock = createMock(IModelRoot.class);

        final ITreeNode selectedNodeMock = createNiceMock(ITreeNode.class);
        // when the model root is not an XSDModelRoot
        // when when isEditAllowed returns false
        TestDataTypesFormPageController controller = new TestDataTypesFormPageController(modelRootMock, false);
        final ISchema schemaMock = createNiceMock(ISchema.class);
        replay(schemaMock, selectedNodeMock);
        controller.setRetrievedSchema(schemaMock);
        controller.setEditedMatch(schemaMock);
        controller.setEditAllowed(false);
        controller.setFireErrorMsgCounter(0);
        controller.getTreeNodeMapper().addToNodeMap(schemaMock, selectedNodeMock);
        long oldShowErrorCals = StatusUtils.getShowStatusDialog_calls();
        controller.handleAddSimpleTypeAction(selectedNodeMock);
        assertEquals(oldShowErrorCals + 1, StatusUtils.getShowStatusDialog_calls());

        // when the isEditAllowed returns true
        controller.setEditAllowed(true);
        controller.setRetrievedXsdModelRoot(null);
        oldShowErrorCals = StatusUtils.getShowStatusDialog_calls();
        controller.handleAddSimpleTypeAction(selectedNodeMock);
        assertEquals(oldShowErrorCals + 1, StatusUtils.getShowStatusDialog_calls());

        final IXSDModelRoot xsdModelRootMock = createNiceMock(IXSDModelRoot.class);

        // if the model root is an IXSDModelRoot and returns a null schema
        expect(xsdModelRootMock.getSchema()).andReturn(null);
        replay(xsdModelRootMock);

        controller = new TestDataTypesFormPageController(xsdModelRootMock, false);

        boolean caughtFlag = false;
        try {
            controller.handleAddSimpleTypeAction(selectedNodeMock);
        } catch (final IllegalArgumentException e) {
            caughtFlag = true;
        }
        assertTrue(caughtFlag);

        verify(xsdModelRootMock);
        // when isEditAllowed() returns true and a command is constructed and
        // executed
        reset(xsdModelRootMock);
        final IWsdlModelRoot wsdlModelRootMock = createNiceMock(IWsdlModelRoot.class);
        final ISchema schemaMock2 = createNiceMock(ISchema.class);
        expect(schemaMock2.getModelRoot()).andReturn(xsdModelRootMock);
        final IEnvironment envMock = createMock(IEnvironment.class);
        expect(xsdModelRootMock.getEnv()).andReturn(envMock).times(2);
        expect(envMock.getEditingDomain()).andReturn(null);
        try {
            expect(envMock.execute(isA(AddSimpleTypeCommand.class))).andReturn(Status.OK_STATUS);
        } catch (final ExecutionException e) {
            fail();// this line should never be called
        }
        final ITreeNode treeNodeMock = createNiceMock(ITreeNode.class);
        replay(xsdModelRootMock, envMock, schemaMock2, treeNodeMock);

        controller = new TestDataTypesFormPageController(wsdlModelRootMock, false);
        controller.setRetrievedSchema(schemaMock2);
        controller.setEditAllowed(true);
        controller.setEditedMatch(schemaMock2);
        controller.setRetrievedXsdModelRoot(xsdModelRootMock);
        controller.getTreeNodeMapper().addToNodeMap(schemaMock2, treeNodeMock);

        controller.handleAddSimpleTypeAction(selectedNodeMock);

        verify(xsdModelRootMock);
        verify(envMock);
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController#handleAddStructureTypeAction(org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode)}
     * .
     */
    @Test
    public final void testHandleAddStructureTypeAction() {
        final IModelRoot modelRootMock = createMock(IModelRoot.class);

        final ITreeNode selectedNodeMock = createNiceMock(ITreeNode.class);
        replay(selectedNodeMock);
        // when the model root is not an XSDModelRoot
        // when when isEditAllowed returns false
        TestDataTypesFormPageController controller = new TestDataTypesFormPageController(modelRootMock, false);
        final ISchema schemaMock = createNiceMock(ISchema.class);
        replay(schemaMock);
        controller.setRetrievedSchema(schemaMock);
        controller.setEditedMatch(schemaMock);
        controller.setEditAllowed(false);
        controller.setFireErrorMsgCounter(0);
        controller.getTreeNodeMapper().addToNodeMap(schemaMock, selectedNodeMock);
        long oldShowErrorCals = StatusUtils.getShowStatusDialog_calls();
        controller.handleAddStructureTypeAction(selectedNodeMock);
        assertEquals(oldShowErrorCals + 1, StatusUtils.getShowStatusDialog_calls());

        // when the isEditAllowed returns true
        controller.setEditAllowed(true);
        controller.setRetrievedXsdModelRoot(null);
        oldShowErrorCals = StatusUtils.getShowStatusDialog_calls();
        controller.handleAddStructureTypeAction(selectedNodeMock);
        assertEquals(oldShowErrorCals + 1, StatusUtils.getShowStatusDialog_calls());

        final IXSDModelRoot xsdModelRootMock = createNiceMock(IXSDModelRoot.class);

        // if the model root is an IXSDModelRoot and returns a null schema
        expect(xsdModelRootMock.getSchema()).andReturn(null);
        replay(xsdModelRootMock);

        controller = new TestDataTypesFormPageController(xsdModelRootMock, false);

        boolean caughtFlag = false;
        try {
            controller.handleAddStructureTypeAction(selectedNodeMock);
        } catch (final IllegalArgumentException e) {
            caughtFlag = true;
        }
        assertTrue(caughtFlag);

        verify(xsdModelRootMock);
        // when isEditAllowed() returns true and a command is constructed and
        // executed
        reset(xsdModelRootMock);

        final IEnvironment envMock = createMock(IEnvironment.class);
        expect(xsdModelRootMock.getEnv()).andReturn(envMock);
        try {
            expect(envMock.execute(isA(AddStructureTypeCommand.class))).andReturn(Status.OK_STATUS);
        } catch (final ExecutionException e) {
            fail();// this line should never be called
        }

        replay(xsdModelRootMock, envMock);
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController#handleRemoveAction(org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode)}
     * .
     */
    @Test
    public final void testHandleRemoveAction() {
        final ITreeNode treeNodeMock = createMock(ITreeNode.class);
        final IModelObject modelObjectMock = createMock(IModelObject.class);
        final IEnvironment envMock = createMock(IEnvironment.class);
        IModelObject xsdComponentMock = createMock(ISchema.class);
        IModelRoot modelRootMock = createMock(IWsdlModelRoot.class);

        expect(modelRootMock.getEnv()).andReturn(envMock).times(2);
        expect(treeNodeMock.getModelObject()).andReturn(modelObjectMock).times(2);
        expect(modelObjectMock.getParent()).andReturn(null).anyTimes();
        expect(envMock.getEditingDomain()).andReturn(null).anyTimes();
        replay(treeNodeMock, modelRootMock, modelObjectMock, envMock);

        TestDataTypesFormPageController controller = new TestDataTypesFormPageController(modelRootMock, false);
        controller.setEditAllowed(false);
        controller.setEditedMatch(modelObjectMock);
        controller.handleRemoveAction(Arrays.asList(treeNodeMock));

        controller.setEditAllowed(true);
        controller.setFireErrorMsgCounter(0);
        controller.setNextTreeNodeMatch(treeNodeMock);
        final long oldShowErrorCals = StatusUtils.getShowStatusDialog_calls();

        controller.handleRemoveAction(Arrays.asList(treeNodeMock));

        assertEquals(oldShowErrorCals + 1, StatusUtils.getShowStatusDialog_calls());
        verify(treeNodeMock);

        // when a ISchema is to be removed
        reset(treeNodeMock);

        controller.setNextTreeNodeMatch(treeNodeMock);
        controller.setNextTreeNode(createMock(ITreeNode.class));
        controller.setRemoveNodeMatch(treeNodeMock);

        // case when a simple or complex type is to be removed
        reset(treeNodeMock, modelRootMock, envMock, modelObjectMock);

        xsdComponentMock = createMock(IType.class);
        prepairHandleRemoveCommandMocks(treeNodeMock, modelRootMock, envMock, xsdComponentMock, DeleteSetCommand.class,
                Status.OK_STATUS);
        expect(xsdComponentMock.getParent()).andReturn(createMock(ISchema.class)).anyTimes();

        replay(treeNodeMock, modelRootMock, envMock, modelObjectMock, xsdComponentMock);

        controller.setEditedMatch(xsdComponentMock);
        controller.handleRemoveAction((Arrays.asList(treeNodeMock)));

        verify(treeNodeMock, modelRootMock, envMock);
        // case when an element is to be removed
        reset(treeNodeMock, modelRootMock, envMock);

        xsdComponentMock = createMock(IElement.class);
        prepairHandleRemoveCommandMocks(treeNodeMock, modelRootMock, envMock, xsdComponentMock, DeleteSetCommand.class,
                Status.OK_STATUS);
        final IStructureType type = createMock(IStructureType.class);
        expect(xsdComponentMock.getParent()).andReturn(type).times(4);
        expect(type.getAllElements()).andReturn(Arrays.asList((IElement) xsdComponentMock)).anyTimes();
        expect(type.getParent()).andReturn(null).anyTimes();

        replay(treeNodeMock, modelRootMock, envMock, xsdComponentMock, type);

        controller.setEditedMatch(xsdComponentMock);
        controller.handleRemoveAction((Arrays.asList(treeNodeMock)));

        verify(treeNodeMock, modelRootMock, envMock, xsdComponentMock);

        // simulate command execution failure
        reset(treeNodeMock, modelRootMock, envMock, xsdComponentMock);
        prepairHandleRemoveCommandMocks(treeNodeMock, modelRootMock, envMock, xsdComponentMock, DeleteSetCommand.class,
                Status.CANCEL_STATUS);
        final IStructureType typeMock = createMock(IStructureType.class);
        expect(xsdComponentMock.getParent()).andReturn(typeMock).times(4);
        expect(typeMock.getAllElements()).andReturn(Arrays.asList((IElement) xsdComponentMock)).anyTimes();
        expect(typeMock.getParent()).andReturn(null).anyTimes();
        replay(treeNodeMock, modelRootMock, envMock, xsdComponentMock, typeMock);

        controller.setEditedMatch(xsdComponentMock);
        controller.handleRemoveAction((Arrays.asList(treeNodeMock)));

        verify(treeNodeMock, modelRootMock, envMock, xsdComponentMock);

        // tests the case when a schema is requested to be removed, but the
        // model root is not a Wsdl root but an xsd
        reset(treeNodeMock, modelRootMock, envMock, xsdComponentMock);

        modelRootMock = createMock(IModelRoot.class);
        xsdComponentMock = createMock(ISchema.class);
        expect(treeNodeMock.getModelObject()).andReturn(xsdComponentMock);
        expect(modelRootMock.getEnv()).andReturn(envMock).times(4);
        expect(envMock.getEditingDomain()).andReturn(null).times(3);
        expect(xsdComponentMock.getParent()).andReturn(null).anyTimes();

        replay(treeNodeMock, modelRootMock, envMock, xsdComponentMock);

        controller = new TestDataTypesFormPageController(modelRootMock, false);
        controller.setEditedMatch(xsdComponentMock);
        controller.setNextTreeNodeMatch(treeNodeMock);
        controller.handleRemoveAction((Arrays.asList(treeNodeMock)));

        verify(treeNodeMock);
    }

    private void prepairHandleRemoveCommandMocks(final ITreeNode treeNodeMock, final IModelRoot modelRootMock,
            final IEnvironment envMock, final IModelObject xsdComponentMock,
            final Class<? extends AbstractNotificationOperation> commandToBeExecuted, final IStatus returnStatus) {
        expect(treeNodeMock.getModelObject()).andReturn(xsdComponentMock);
        expect(modelRootMock.getEnv()).andReturn(envMock).times(3);
        expect(envMock.getEditingDomain()).andReturn(null).times(2);

        try {
            expect(envMock.execute(isA(commandToBeExecuted))).andReturn(returnStatus);
        } catch (final ExecutionException e) {
            fail();
        }
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController#isRemoveItemEnabled(org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IDataTypesTreeNode)}
     * .
     */
    @SuppressWarnings("boxing")
    @Test
    public final void testIsRemoveItemEnabled() {
        final IXSDModelRoot xsdRootMock = createMock(IXSDModelRoot.class);
        final TestDataTypesFormPageController controller = new TestDataTypesFormPageController(xsdRootMock, false);
        assertFalse(controller.isRemoveItemsEnabled(null));

        ITreeNode parentNode = createMock(ITreeNode.class);
        expect(parentNode.isReadOnly()).andReturn(Boolean.valueOf(true)).anyTimes();
        replay(parentNode);

        IDataTypesTreeNode treeNodeMock = createMock(IDataTypesTreeNode.class);
        expect(treeNodeMock.getParent()).andReturn(parentNode).anyTimes();

        replay(treeNodeMock);

        assertFalse(controller.isRemoveItemsEnabled(Arrays.asList(treeNodeMock)));

        verify(treeNodeMock);

        final List<Class<? extends IDataTypesTreeNode>> typesClassList = new ArrayList<Class<? extends IDataTypesTreeNode>>();
        typesClassList.add(ISimpleTypeNode.class);
        typesClassList.add(IStructureTypeNode.class);
        typesClassList.add(IElementNode.class);
        typesClassList.add(INamespaceNode.class);

        final IModelObject modelObjectMock = createMock(IModelObject.class);
        final ISchema schemaMock = createMock(ISchema.class);

        for (final Class<? extends IDataTypesTreeNode> type : typesClassList) {
            parentNode = createMock(ITreeNode.class);
            expect(parentNode.isReadOnly()).andReturn(Boolean.valueOf(false)).anyTimes();
            replay(parentNode);

            treeNodeMock = createMock(type);
            // when isDeleteAllowed is false
            expect(treeNodeMock.getParent()).andReturn(parentNode).anyTimes();
            replay(treeNodeMock);

            controller.setResourceReadOnlyCounter(0);
            controller.setReadOnly(true);

            controller.setPartOfDocumentMatch(modelObjectMock);
            controller.setPartOfEditedDocCounter(0);
            controller.setPartOfEdittedDocument(Boolean.valueOf(true));

            assertFalse(controller.isRemoveItemsEnabled(Arrays.asList(treeNodeMock)));
            assertTrue(controller.getResourceReadOnlyCounter() == 1);
            assertTrue(controller.getPartOfEditedDocCounter() == 0);

            verify(treeNodeMock);

            // when isPartOfEditedDocument is false
            reset(treeNodeMock);

            expect(treeNodeMock.getParent()).andReturn(parentNode).anyTimes();
            expect(treeNodeMock.getModelObject()).andReturn(modelObjectMock);
            replay(treeNodeMock);

            // when isDeleteAllowed returns true but isPartOfEdittedDocument is
            // false

            controller.setReadOnly(false);
            controller.setResourceReadOnlyCounter(0);
            controller.setPartOfEditedDocCounter(0);
            assertTrue(controller.isRemoveItemsEnabled((Arrays.asList(treeNodeMock))));
            assertTrue(controller.getResourceReadOnlyCounter() == 1);

            verify(treeNodeMock);

            // whent is part of edited doc is true, but model object is document
            // schema
            reset(treeNodeMock, xsdRootMock);

            expect(treeNodeMock.getParent()).andReturn(parentNode).anyTimes();
            expect(treeNodeMock.getModelObject()).andReturn(schemaMock).anyTimes();
            expect(xsdRootMock.getSchema()).andReturn(schemaMock).anyTimes();

            replay(treeNodeMock, xsdRootMock);

            controller.setResourceReadOnlyCounter(0);
            controller.setPartOfEdittedDocument(Boolean.valueOf(true));
            assertFalse(controller.isRemoveItemsEnabled(Arrays.asList(treeNodeMock)));
            assertTrue(controller.getResourceReadOnlyCounter() == 1);

            verify(treeNodeMock, xsdRootMock);

            reset(treeNodeMock);
            // when isDocumentSchema is false
            expect(treeNodeMock.getParent()).andReturn(parentNode).anyTimes();
            expect(treeNodeMock.getModelObject()).andReturn(modelObjectMock).anyTimes();

            replay(treeNodeMock);

            controller.setResourceReadOnlyCounter(0);
            assertTrue(controller.isRemoveItemsEnabled(Arrays.asList(treeNodeMock)));
            assertTrue(controller.getResourceReadOnlyCounter() == 1);

            verify(treeNodeMock);
        }
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController#isRenameItemEnabled(org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IDataTypesTreeNode)}
     * .
     */
    @SuppressWarnings("boxing")
    @Test
    public final void testIsRenameItemEnabled() {
        final IXSDModelRoot xsdRootMock = createMock(IXSDModelRoot.class);
        final TestDataTypesFormPageController controller = new TestDataTypesFormPageController(xsdRootMock, false);
        assertFalse(controller.isRenameItemEnabled(null));

        IDataTypesTreeNode treeNodeMock = createMock(IDataTypesTreeNode.class);
        expect(treeNodeMock.isReadOnly()).andReturn(Boolean.valueOf(true));

        replay(treeNodeMock);

        assertFalse(controller.isRenameItemEnabled(treeNodeMock));

        verify(treeNodeMock);

        final List<Class<? extends IDataTypesTreeNode>> typesClassList = new ArrayList<Class<? extends IDataTypesTreeNode>>();
        typesClassList.add(ISimpleTypeNode.class);
        typesClassList.add(IStructureTypeNode.class);
        typesClassList.add(IElementNode.class);
        typesClassList.add(INamespaceNode.class);

        final INamedObject namedObjectMock = createMock(INamedObject.class);
        final INamespacedObject namespacedObjectMock = createMock(INamespacedObject.class);

        for (final Class<? extends IDataTypesTreeNode> type : typesClassList) {
            // when readOnly is true
            controller.setResourceReadOnlyCounter(0);
            controller.setReadOnly(true);

            controller.setPartOfDocumentMatch(namedObjectMock);
            controller.setPartOfEditedDocCounter(0);
            controller.setPartOfEdittedDocument(Boolean.valueOf(true));

            assertFalse(controller.isRenameItemEnabled(treeNodeMock));
            assertTrue(controller.getResourceReadOnlyCounter() == 1);
            assertTrue(controller.getPartOfEditedDocCounter() == 0);

            // when the edited model object is a namespaced model object and
            // isReadOnly == true
            treeNodeMock = createMock(type);

            controller.setResourceReadOnlyCounter(0);
            controller.setReadOnly(true);

            controller.setPartOfDocumentMatch(namespacedObjectMock);
            controller.setPartOfEditedDocCounter(0);
            controller.setPartOfEdittedDocument(Boolean.valueOf(true));

            assertFalse(controller.isRenameItemEnabled(treeNodeMock));
            assertTrue(controller.getResourceReadOnlyCounter() == 1);
            assertTrue(controller.getPartOfEditedDocCounter() == 0);

            // when isPartOfEditedDocument is false

            expect(treeNodeMock.isReadOnly()).andReturn(Boolean.valueOf(false));
            expect(treeNodeMock.getModelObject()).andReturn(namedObjectMock).anyTimes();
            replay(treeNodeMock);

            controller.setResourceReadOnlyCounter(0);
            controller.setReadOnly(false);
            assertTrue(controller.isRenameItemEnabled(treeNodeMock));
            assertTrue(controller.getResourceReadOnlyCounter() == 1);

            verify(treeNodeMock);

            // whent is part of edited doc is true, but model object is document
            // schema
            reset(treeNodeMock, xsdRootMock);

            expect(treeNodeMock.isReadOnly()).andReturn(Boolean.valueOf(false));
            expect(treeNodeMock.getModelObject()).andReturn(namespacedObjectMock).anyTimes();

            replay(treeNodeMock, xsdRootMock);

            controller.setResourceReadOnlyCounter(0);
            controller.setReadOnly(false);

            controller.setPartOfEdittedDocument(Boolean.valueOf(true));

            assertTrue(controller.isRenameItemEnabled(treeNodeMock));
            assertTrue(controller.getResourceReadOnlyCounter() == 1);

            verify(treeNodeMock, xsdRootMock);

            // when the treNode.isReadOnly is true
            reset(treeNodeMock);

            expect(treeNodeMock.isReadOnly()).andReturn(Boolean.valueOf(true));
            replay(treeNodeMock);

            controller.setResourceReadOnlyCounter(0);
            controller.setReadOnly(false);

            controller.setPartOfEditedDocCounter(0);
            controller.setPartOfDocumentMatch(namespacedObjectMock);

            assertFalse(controller.isRenameItemEnabled(treeNodeMock));
            assertTrue(controller.getResourceReadOnlyCounter() == 1);

            verify(treeNodeMock);
        }
    }

    /*
     * Test method for {@link
     * org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController
     * #isAddElementEnabled
     * (org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IDataTypesTreeNode)}
     * 
     * TEST COVERED BY ActionsSelectionTest
     * 
     * @SuppressWarnings("boxing")
     * 
     * @Test public final void testIsAddElementEnabled() {
     * TestDataTypesFormPageController controller = new
     * TestDataTypesFormPageController(createMock(IModelRoot.class), true);
     * 
     * assertFalse(controller.isAddElementEnabled(null));
     * 
     * // when the isReadOnly() method returns true IDataTypesTreeNode
     * dtNodeMock = createMock(IDataTypesTreeNode.class);
     * expect(dtNodeMock.isReadOnly()).andReturn(Boolean.valueOf(true));
     * replay(dtNodeMock);
     * assertFalse(controller.isAddElementEnabled(dtNodeMock));
     * verify(dtNodeMock);
     * 
     * // when the DTNode is not one of the allegeable for element addition
     * reset(dtNodeMock);
     * expect(dtNodeMock.isReadOnly()).andReturn(Boolean.valueOf(false));
     * IModelObject modelObjectMock = createMock(IModelObject.class);
     * expect(dtNodeMock.getModelObject()).andReturn(modelObjectMock);
     * replay(dtNodeMock); controller.setPartOfDocumentMatch(modelObjectMock);
     * controller.setPartOfEdittedDocument(Boolean.valueOf(true));
     * assertFalse(controller.isAddElementEnabled(dtNodeMock));
     * verify(dtNodeMock);
     * 
     * // when the tree node is of allegable type List<Class<? extends
     * IDataTypesTreeNode>> allegableNodes = new ArrayList<Class<? extends
     * IDataTypesTreeNode>>(); allegableNodes.add(IStructureTypeNode.class);
     * allegableNodes.add(INamespaceNode.class);
     * allegableNodes.add(IElementNode.class); for (Class<? extends
     * IDataTypesTreeNode> type : allegableNodes) { dtNodeMock =
     * createMock(type);
     * expect(dtNodeMock.isReadOnly()).andReturn(Boolean.valueOf
     * (false)).times(2); modelObjectMock = createMock(IModelObject.class);
     * expect(dtNodeMock.getModelObject()).andReturn(modelObjectMock).times(2);
     * replay(dtNodeMock);
     * 
     * controller.setReadOnly(true);
     * 
     * controller.setResourceReadOnlyCounter(0);
     * controller.setPartOfEdittedDocument(Boolean.valueOf(true));
     * controller.setPartOfDocumentMatch(modelObjectMock);
     * assertFalse(controller.isAddElementEnabled(dtNodeMock));
     * assertTrue(controller.getResourceReadOnlyCounter() == 1);
     * controller.setReadOnly(false);
     * assertTrue(controller.isAddElementEnabled(dtNodeMock));
     * assertTrue(controller.getResourceReadOnlyCounter() == 2);
     * 
     * verify(dtNodeMock); }
     * 
     * // when the model object is an element and when it is an attribute
     * dtNodeMock = createMock(IElementNode.class); IElement elementMock =
     * createMock(IElement.class);
     * 
     * expect(dtNodeMock.isReadOnly()).andReturn(Boolean.valueOf(false));
     * expect(dtNodeMock.getModelObject()).andReturn(elementMock);
     * 
     * expect(elementMock.getType()).andReturn(createMock(IType.class));
     * expect(elementMock.isAttribute()).andReturn(Boolean.valueOf(true));
     * 
     * replay(dtNodeMock, elementMock); // prepare the test controller
     * controller.setReadOnly(false);
     * controller.setPartOfDocumentMatch(elementMock);
     * controller.setPartOfEdittedDocument(Boolean.valueOf(true)); // run the
     * test controller.setResourceReadOnlyCounter(0);
     * assertFalse(controller.isAddElementEnabled(dtNodeMock));
     * assertTrue(controller.getResourceReadOnlyCounter() == 1);
     * 
     * verify(dtNodeMock, elementMock);
     * 
     * // when type of the element is null reset(dtNodeMock, elementMock);
     * expect(dtNodeMock.isReadOnly()).andReturn(Boolean.valueOf(false));
     * expect(dtNodeMock.getModelObject()).andReturn(elementMock);
     * 
     * expect(elementMock.getType()).andReturn(null);
     * expect(elementMock.isAttribute()).andReturn(Boolean.valueOf(false));
     * 
     * replay(dtNodeMock, elementMock);
     * 
     * controller.setResourceReadOnlyCounter(0);
     * assertFalse(controller.isAddElementEnabled(dtNodeMock));
     * assertTrue(controller.getResourceReadOnlyCounter() == 1);
     * 
     * verify(dtNodeMock, elementMock);
     * 
     * // when type of the element is not null, but is not anonymous
     * reset(dtNodeMock, elementMock);
     * expect(dtNodeMock.isReadOnly()).andReturn(Boolean.valueOf(false));
     * expect(dtNodeMock.getModelObject()).andReturn(elementMock);
     * 
     * IType typeMock = createMock(IType.class);
     * expect(elementMock.getType()).andReturn(typeMock);
     * expect(elementMock.isAttribute()).andReturn(Boolean.valueOf(false));
     * expect(typeMock.isAnonymous()).andReturn(Boolean.valueOf(false));
     * 
     * replay(dtNodeMock, elementMock, typeMock);
     * 
     * controller.setResourceReadOnlyCounter(0);
     * assertFalse(controller.isAddElementEnabled(dtNodeMock));
     * assertTrue(controller.getResourceReadOnlyCounter() == 1);
     * 
     * verify(dtNodeMock, elementMock, typeMock);
     * 
     * // when the type is anonymous, but element is simple type
     * reset(dtNodeMock, elementMock);
     * expect(dtNodeMock.isReadOnly()).andReturn(Boolean.valueOf(false));
     * expect(dtNodeMock.getModelObject()).andReturn(elementMock);
     * 
     * typeMock = createMock(ISimpleType.class);
     * expect(elementMock.getType()).andReturn(typeMock);
     * expect(elementMock.isAttribute()).andReturn(Boolean.valueOf(false));
     * expect(typeMock.isAnonymous()).andReturn(Boolean.valueOf(true)).times(2);
     * 
     * replay(dtNodeMock, elementMock, typeMock);
     * 
     * controller.setResourceReadOnlyCounter(0);
     * assertFalse(controller.isAddElementEnabled(dtNodeMock));
     * assertTrue(controller.getResourceReadOnlyCounter() == 1);
     * 
     * verify(dtNodeMock, elementMock, typeMock);
     * 
     * // when the type is anonymous, but element is simple type
     * reset(dtNodeMock, elementMock);
     * expect(dtNodeMock.isReadOnly()).andReturn(Boolean.valueOf(false));
     * expect(dtNodeMock.getModelObject()).andReturn(elementMock);
     * 
     * typeMock = createMock(IStructureType.class);
     * expect(elementMock.getType()).andReturn(typeMock);
     * expect(elementMock.isAttribute()).andReturn(Boolean.valueOf(false));
     * expect(typeMock.isAnonymous()).andReturn(Boolean.valueOf(true)).times(2);
     * 
     * replay(dtNodeMock, elementMock, typeMock);
     * 
     * controller.setResourceReadOnlyCounter(0);
     * assertTrue(controller.isAddElementEnabled(dtNodeMock));
     * assertTrue(controller.getResourceReadOnlyCounter() == 1);
     * 
     * verify(dtNodeMock, elementMock, typeMock); }
     */

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController#isAddSimpleTypeEnabled(org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IDataTypesTreeNode)}
     * .
     */
    @SuppressWarnings("boxing")
    @Test
    public final void testIsAddTypeEnabled() {
        testIsAddTypeEnabledStandaloneXSDEditor();
        // when the model object does not have a parent ISchema
        final IDataTypesTreeNode dtNodeMock = createMock(IDataTypesTreeNode.class);
        final IModelObject modelObjectMock = createMock(IModelObject.class);
        expect(dtNodeMock.isReadOnly()).andReturn(Boolean.valueOf(false)).anyTimes();
        expect(dtNodeMock.getCategories()).andReturn(0).anyTimes();
        expect(dtNodeMock.getModelObject()).andReturn(null);

        replay(dtNodeMock, modelObjectMock);
        final TestDataTypesFormPageController controller = new TestDataTypesFormPageController(null, false);

        assertTrue(controller.isAddTypeEnabled(dtNodeMock));
        verify(dtNodeMock, modelObjectMock);

        // when the model object has a parent schema but the node is read only
        reset(dtNodeMock, modelObjectMock);
        expect(dtNodeMock.getModelObject()).andReturn(modelObjectMock);
        expect(dtNodeMock.isReadOnly()).andReturn(Boolean.valueOf(true));
        expect(dtNodeMock.getCategories()).andReturn(0).anyTimes();
        replay(dtNodeMock, modelObjectMock);

        assertFalse(controller.isAddTypeEnabled(dtNodeMock));

        verify(dtNodeMock, modelObjectMock);
        // when the tree node is not read only but isEditAlowed() is false
        reset(dtNodeMock, modelObjectMock);
        expect(dtNodeMock.getModelObject()).andReturn(modelObjectMock);
        expect(dtNodeMock.isReadOnly()).andReturn(Boolean.valueOf(false));
        expect(dtNodeMock.getCategories()).andReturn(0).anyTimes();
        replay(dtNodeMock, modelObjectMock);

        controller.setReadOnly(true);
        controller.setResourceReadOnlyCounter(0);
        assertFalse(controller.isAddTypeEnabled(dtNodeMock));
        assertTrue(controller.getResourceReadOnlyCounter() == 1);

        verify(dtNodeMock, modelObjectMock);

        // when the tree node does not belong to this document
        reset(dtNodeMock, modelObjectMock);
        expect(dtNodeMock.getModelObject()).andReturn(modelObjectMock).anyTimes();
        expect(dtNodeMock.isReadOnly()).andReturn(Boolean.valueOf(false)).anyTimes();
        expect(dtNodeMock.getCategories()).andReturn(0).anyTimes();

        replay(dtNodeMock, modelObjectMock);

        controller.setReadOnly(false);
        controller.setResourceReadOnlyCounter(0);
        assertTrue(controller.isAddTypeEnabled(dtNodeMock));
        assertTrue(controller.getResourceReadOnlyCounter() == 1);
        verify(dtNodeMock, modelObjectMock);
    }

    @Test
    public void isAddTypeEnabledOnReferencedElement() {
        final IDataTypesTreeNode dtNodeMock = createMock(IDataTypesTreeNode.class);
        final IModelObject modelObjectMock = createMock(IModelObject.class);
        expect(dtNodeMock.isReadOnly()).andReturn(Boolean.valueOf(true)).anyTimes();
        expect(dtNodeMock.getCategories()).andReturn(ITreeNode.CATEGORY_REFERENCE).anyTimes();
        expect(dtNodeMock.getModelObject()).andReturn(null);

        replay(dtNodeMock, modelObjectMock);
        final TestDataTypesFormPageController controller = new TestDataTypesFormPageController(null, false);

        assertTrue(controller.isAddTypeEnabled(dtNodeMock));
    }

    private void testIsAddTypeEnabledStandaloneXSDEditor() {
        TestDataTypesFormPageController controller = new TestDataTypesFormPageController(null, false);

        assertFalse(controller.isAddTypeEnabled(null));

        controller = new TestDataTypesFormPageController(createMock(IModelRoot.class), false);
        assertFalse(controller.isAddTypeEnabled(null));

        final IXSDModelRoot xsdRootMock = createMock(IXSDModelRoot.class);

        expect(xsdRootMock.getSchema()).andReturn(null);
        replay(xsdRootMock);

        controller = new TestDataTypesFormPageController(xsdRootMock, false);

        assertFalse(controller.isAddTypeEnabled(null));

        verify(xsdRootMock);

        reset(xsdRootMock);
        final ISchema schemaMock = createMock(ISchema.class);
        expect(xsdRootMock.getSchema()).andReturn(schemaMock);
        replay(xsdRootMock);

        controller.setReadOnly(true);
        controller.setResourceReadOnlyCounter(0);
        assertFalse(controller.isAddTypeEnabled(null));
        assertTrue(controller.getResourceReadOnlyCounter() == 1);

        verify(xsdRootMock);

        reset(xsdRootMock, schemaMock);
        expect(xsdRootMock.getSchema()).andReturn(schemaMock).times(2);
        replay(xsdRootMock);

        controller.setReadOnly(false);
        controller.setResourceReadOnlyCounter(0);
        assertTrue(controller.isAddTypeEnabled(null));

        assertTrue(controller.isAddTypeEnabled(null));
        assertTrue(controller.getResourceReadOnlyCounter() == 2);
        verify(xsdRootMock);
    }

    @Test
    public void isMakeGlobalEnabled() {
        final TestDataTypesFormPageController controller = new TestDataTypesFormPageController(null, false);

        assertFalse(controller.isConvertToGlobalTypeEnabled(null));
        testStructureType_MakeGlobalEnablement(controller);
        testAnonymousElement_MakeGlobalEnablement_NoChildren(controller);
        testAnonymousElement_MakeGlobalEnablement_WithChildren(controller);
        testGlobalElement_MakeGlobalEnablement(controller);
        testReadonlyGlobalElement_MakeGlobalEnablement(controller);
    }

    // =========================================================
    // make type global helpers
    // =========================================================

    private void testStructureType_MakeGlobalEnablement(final TestDataTypesFormPageController controller) {
        final IStructureType structureType = createMock(IStructureType.class);
        expect(structureType.isElement()).andReturn(false);
        final IDataTypesTreeNode structureTypeTreeNodeMock = createMock(IDataTypesTreeNode.class);
        expect(structureTypeTreeNodeMock.getModelObject()).andReturn(structureType).anyTimes();
        expect(structureTypeTreeNodeMock.isReadOnly()).andReturn(false).anyTimes();
        replay(structureType, structureTypeTreeNodeMock);
        assertFalse(controller.isConvertToGlobalTypeEnabled(structureTypeTreeNodeMock));
    }

    private void testAnonymousElement_MakeGlobalEnablement_NoChildren(final TestDataTypesFormPageController controller) {
        final IStructureType anonymousElement = createMock(IStructureType.class);
        expect(anonymousElement.isElement()).andReturn(true);
        expect(anonymousElement.getAllElements()).andReturn(new HashSet<IElement>());
        expect(anonymousElement.isAnonymous()).andReturn(true);
        final IDataTypesTreeNode elementTreeNodeMock = createMock(IDataTypesTreeNode.class);
        expect(elementTreeNodeMock.getModelObject()).andReturn(anonymousElement).anyTimes();
        expect(elementTreeNodeMock.isReadOnly()).andReturn(false).anyTimes();
        replay(anonymousElement, elementTreeNodeMock);
        assertFalse(controller.isConvertToGlobalTypeEnabled(elementTreeNodeMock));
    }

    private void testAnonymousElement_MakeGlobalEnablement_WithChildren(final TestDataTypesFormPageController controller) {
        final IStructureType anonymousElement = createMock(IStructureType.class);
        expect(anonymousElement.isElement()).andReturn(true);
        final Set<IElement> set = new HashSet<IElement>();
        final IElement element = createNiceMock(IElement.class);
        set.add(element);
        expect(anonymousElement.getAllElements()).andReturn(set);
        expect(anonymousElement.isAnonymous()).andReturn(true);
        final IDataTypesTreeNode elementTreeNodeMock = createMock(IDataTypesTreeNode.class);
        expect(elementTreeNodeMock.getModelObject()).andReturn(anonymousElement).anyTimes();
        expect(elementTreeNodeMock.isReadOnly()).andReturn(false).anyTimes();
        replay(anonymousElement, elementTreeNodeMock);
        assertTrue(controller.isConvertToGlobalTypeEnabled(elementTreeNodeMock));
    }

    private void testGlobalElement_MakeGlobalEnablement(final TestDataTypesFormPageController controller) {
        final IStructureType globalElement = createMock(IStructureType.class);
        expect(globalElement.isElement()).andReturn(true);
        expect(globalElement.isAnonymous()).andReturn(false);
        final IDataTypesTreeNode globalElementTreeNodeMock = createMock(IDataTypesTreeNode.class);
        expect(globalElementTreeNodeMock.getModelObject()).andReturn(globalElement).anyTimes();
        expect(globalElementTreeNodeMock.isReadOnly()).andReturn(false).anyTimes();
        replay(globalElement, globalElementTreeNodeMock);
        assertFalse(controller.isConvertToGlobalTypeEnabled(globalElementTreeNodeMock));
    }

    private void testReadonlyGlobalElement_MakeGlobalEnablement(final TestDataTypesFormPageController controller) {
        final IStructureType readOnlyAnonymous = createMock(IStructureType.class);
        expect(readOnlyAnonymous.isElement()).andReturn(true);
        expect(readOnlyAnonymous.isAnonymous()).andReturn(false);
        final IDataTypesTreeNode readonlyAnonymousElementTreeNodeMock = createMock(IDataTypesTreeNode.class);
        expect(readonlyAnonymousElementTreeNodeMock.getModelObject()).andReturn(readOnlyAnonymous).anyTimes();
        expect(readonlyAnonymousElementTreeNodeMock.isReadOnly()).andReturn(true).anyTimes();
        replay(readOnlyAnonymous, readonlyAnonymousElementTreeNodeMock);
        assertFalse(controller.isConvertToGlobalTypeEnabled(readonlyAnonymousElementTreeNodeMock));
    }

    // =========================================================
    // end of make type global helpers
    // =========================================================

    @Test
    public void isMakeAnonymousEnabled() {
        final TestDataTypesFormPageController controller = new TestDataTypesFormPageController(null, false);

        assertFalse(controller.isConvertToGlobalTypeEnabled(null));
        testStructureType_MakeAnonymousEnablement(controller);
        testAnonymousElement_MakeAnonymousEnablement(controller);
        testNonAnonymousElement_MakeAnonymousEnablement(controller);
        testReadonlyElement_MakeAnonymousEnablement(controller);
    }

    // =========================================================
    // make anonymous helpers
    // =========================================================

    private void testStructureType_MakeAnonymousEnablement(final TestDataTypesFormPageController controller) {
        final IStructureType structureType = createMock(IStructureType.class);
        expect(structureType.isElement()).andReturn(false);
        final IDataTypesTreeNode structureTypeTreeNodeMock = createMock(IDataTypesTreeNode.class);
        expect(structureTypeTreeNodeMock.getModelObject()).andReturn(structureType).anyTimes();
        expect(structureTypeTreeNodeMock.isReadOnly()).andReturn(false).anyTimes();
        replay(structureType, structureTypeTreeNodeMock);
        assertFalse(controller.isConvertToAnonymousTypeEnabled(structureTypeTreeNodeMock));
    }

    private void testAnonymousElement_MakeAnonymousEnablement(final TestDataTypesFormPageController controller) {
        final IStructureType anonymousElement = createMock(IStructureType.class);
        expect(anonymousElement.isElement()).andReturn(true);
        expect(anonymousElement.isAnonymous()).andReturn(true);
        final IDataTypesTreeNode elementTreeNodeMock = createMock(IDataTypesTreeNode.class);
        expect(elementTreeNodeMock.getModelObject()).andReturn(anonymousElement).anyTimes();
        expect(elementTreeNodeMock.isReadOnly()).andReturn(false).anyTimes();
        replay(anonymousElement, elementTreeNodeMock);
        assertFalse(controller.isConvertToAnonymousTypeEnabled(elementTreeNodeMock));
    }

    private void testNonAnonymousElement_MakeAnonymousEnablement(final TestDataTypesFormPageController controller) {
        final IStructureType globalTypeElement = createMock(IStructureType.class);
        expect(globalTypeElement.isElement()).andReturn(true);
        expect(globalTypeElement.isAnonymous()).andReturn(false);
        final IDataTypesTreeNode globalTypeElementTreeNodeMock = createMock(IDataTypesTreeNode.class);
        expect(globalTypeElementTreeNodeMock.getModelObject()).andReturn(globalTypeElement).anyTimes();
        expect(globalTypeElementTreeNodeMock.isReadOnly()).andReturn(false).anyTimes();
        replay(globalTypeElement, globalTypeElementTreeNodeMock);
        assertTrue(controller.isConvertToAnonymousTypeEnabled(globalTypeElementTreeNodeMock));
    }

    private void testReadonlyElement_MakeAnonymousEnablement(final TestDataTypesFormPageController controller) {
        final IStructureType readonlyGlobalTypeElement = createMock(IStructureType.class);
        expect(readonlyGlobalTypeElement.isElement()).andReturn(true);
        expect(readonlyGlobalTypeElement.isAnonymous()).andReturn(false);
        final IDataTypesTreeNode readlonlyGlobalTypeElementTreeNodeMock = createMock(IDataTypesTreeNode.class);
        expect(readlonlyGlobalTypeElementTreeNodeMock.getModelObject()).andReturn(readonlyGlobalTypeElement).anyTimes();
        expect(readlonlyGlobalTypeElementTreeNodeMock.isReadOnly()).andReturn(true).anyTimes();
        replay(readonlyGlobalTypeElement, readlonlyGlobalTypeElementTreeNodeMock);
        assertFalse(controller.isConvertToAnonymousTypeEnabled(readlonlyGlobalTypeElementTreeNodeMock));
    }

    // =========================================================
    // end of make anonymous helpers
    // =========================================================

    // =========================================================
    // inline structure type
    // =========================================================

    @Test
    public void isInlineStructureTypeEnabled() {
        final TestDataTypesFormPageController controller = new TestDataTypesFormPageController(null, false);

        assertFalse(controller.isConvertToAnonymousTypeWithTypeContentsEnabled(null));
        testStructureType_InlineStructureEnablement(controller);
        testAnonymousElement_InlineStructureEnablement(controller);
        testNonAnonymousElement_WithSimpleType_InlineStructureEnablement(controller);
        testNonAnonymousElement_WithStructureType_InlineStructureEnablement(controller);
        testReadonlyElement_InlineStructureEnablement(controller);
    }

    // =========================================================
    // inline structure helpers
    // =========================================================

    private void testStructureType_InlineStructureEnablement(final TestDataTypesFormPageController controller) {
        final IStructureType structureType = createMock(IStructureType.class);
        expect(structureType.isElement()).andReturn(false);
        final IDataTypesTreeNode structureTypeTreeNodeMock = createMock(IDataTypesTreeNode.class);
        expect(structureTypeTreeNodeMock.getModelObject()).andReturn(structureType).anyTimes();
        expect(structureTypeTreeNodeMock.isReadOnly()).andReturn(false).anyTimes();
        replay(structureType, structureTypeTreeNodeMock);
        assertFalse(controller.isConvertToAnonymousTypeWithTypeContentsEnabled(structureTypeTreeNodeMock));
    }

    private void testAnonymousElement_InlineStructureEnablement(final TestDataTypesFormPageController controller) {
        final IStructureType anonymousElement = createMock(IStructureType.class);
        expect(anonymousElement.isElement()).andReturn(true);
        expect(anonymousElement.isAnonymous()).andReturn(true);
        final IDataTypesTreeNode elementTreeNodeMock = createMock(IDataTypesTreeNode.class);
        expect(elementTreeNodeMock.getModelObject()).andReturn(anonymousElement).anyTimes();
        expect(elementTreeNodeMock.isReadOnly()).andReturn(false).anyTimes();
        replay(anonymousElement, elementTreeNodeMock);
        assertFalse(controller.isConvertToAnonymousTypeWithTypeContentsEnabled(elementTreeNodeMock));
    }

    private void testNonAnonymousElement_WithSimpleType_InlineStructureEnablement(final TestDataTypesFormPageController controller) {
        final IStructureType globalTypeElement = createMock(IStructureType.class);
        expect(globalTypeElement.isElement()).andReturn(true);
        expect(globalTypeElement.isAnonymous()).andReturn(false);
        expect(globalTypeElement.getType()).andReturn(Schema.getDefaultSimpleType());
        final IDataTypesTreeNode globalTypeElementTreeNodeMock = createMock(IDataTypesTreeNode.class);
        expect(globalTypeElementTreeNodeMock.getModelObject()).andReturn(globalTypeElement).anyTimes();
        expect(globalTypeElementTreeNodeMock.isReadOnly()).andReturn(false).anyTimes();
        replay(globalTypeElement, globalTypeElementTreeNodeMock);
        assertFalse(controller.isConvertToAnonymousTypeWithTypeContentsEnabled(globalTypeElementTreeNodeMock));
    }

    private void testNonAnonymousElement_WithStructureType_InlineStructureEnablement(
            final TestDataTypesFormPageController controller) {
        final XSDComplexTypeDefinitionImpl complexType = new XSDComplexTypeDefinitionImpl() {
            @Override
            public XSDTypeDefinition getBaseType() {
                return new XSDSimpleTypeDefinitionImpl() {
                    @Override
                    public String getName() {
                        return "anyType";
                    }
                };
            }
        };
        final IType type = createMock(IType.class);
        expect(type.getComponent()).andReturn(complexType).anyTimes();

        final IStructureType globalTypeElement = createMock(IStructureType.class);
        expect(globalTypeElement.isElement()).andReturn(true);
        expect(globalTypeElement.isAnonymous()).andReturn(false);
        final IDataTypesTreeNode globalTypeElementTreeNodeMock = createMock(IDataTypesTreeNode.class);
        expect(globalTypeElementTreeNodeMock.getModelObject()).andReturn(globalTypeElement).anyTimes();
        expect(globalTypeElementTreeNodeMock.isReadOnly()).andReturn(false).anyTimes();
        expect(globalTypeElement.getType()).andReturn(type).anyTimes();
        replay(globalTypeElement, globalTypeElementTreeNodeMock, type);
        assertTrue(controller.isConvertToAnonymousTypeWithTypeContentsEnabled(globalTypeElementTreeNodeMock));
    }

    private void testReadonlyElement_InlineStructureEnablement(final TestDataTypesFormPageController controller) {
        final IStructureType readonlyGlobalTypeElement = createMock(IStructureType.class);
        expect(readonlyGlobalTypeElement.isElement()).andReturn(true);
        expect(readonlyGlobalTypeElement.isAnonymous()).andReturn(false);
        final IDataTypesTreeNode readlonlyGlobalTypeElementTreeNodeMock = createMock(IDataTypesTreeNode.class);
        expect(readlonlyGlobalTypeElementTreeNodeMock.getModelObject()).andReturn(readonlyGlobalTypeElement).anyTimes();
        expect(readlonlyGlobalTypeElementTreeNodeMock.isReadOnly()).andReturn(true).anyTimes();
        replay(readonlyGlobalTypeElement, readlonlyGlobalTypeElementTreeNodeMock);
        assertFalse(controller.isConvertToAnonymousTypeWithTypeContentsEnabled(readlonlyGlobalTypeElementTreeNodeMock));
    }

    // =========================================================
    // end of inline structure helpers
    // =========================================================

    @Test
    public void testIsCopyEnabled() {
        final IModelRoot modelRoot = createNiceMock(IModelRoot.class);
        replay(modelRoot);

        final IType type = createMock(IType.class);
        replay(type);

        final IDataTypesTreeNode selectedNode = createMock(IDataTypesTreeNode.class);
        expect(selectedNode.getModelObject()).andReturn(type);
        replay(selectedNode);

        final DataTypesFormPageController controller = new DataTypesFormPageController(modelRoot, true);

        assertTrue(controller.isCopyEnabled(selectedNode));
        assertFalse(controller.isCopyEnabled(null));

    }

    @Test
    public void testIsPasteEnabled() {
        final ISchema schema = createNiceMock(ISchema.class);
        replay(schema);

        final IXSDModelRoot modelRoot = createNiceMock(IXSDModelRoot.class);
        expect(modelRoot.getSchema()).andReturn(schema).anyTimes();
        replay(modelRoot);

        final IDataTypesTreeNode selectedNode = createNiceMock(IDataTypesTreeNode.class);
        expect(selectedNode.getModelObject()).andReturn(schema).anyTimes();
        replay(selectedNode);

        final DataTypesFormPageController controller = new DataTypesFormPageController(modelRoot, false) {
            @Override
            public boolean isResourceReadOnly() {
                return false;
            }
        };

        // nothing copied
        assertFalse(controller.isPasteEnabled(null));
        assertFalse(controller.isPasteEnabled(selectedNode));

        // copy a type
        final IType sourceType = createMock(IType.class);
        final XSDNamedComponent component = createMock(XSDNamedComponent.class);
        final Resource resoure = createMock(Resource.class);
        expect(component.eResource()).andReturn(resoure);
        expect(sourceType.getComponent()).andReturn(component);
        replay(sourceType, component);

        final IDataTypesTreeNode copySourceNode = createMock(IDataTypesTreeNode.class);
        expect(copySourceNode.getModelObject()).andReturn(sourceType);
        replay(copySourceNode);

        controller.handleCopyTypeAction(copySourceNode);

        // check whether can paste it on "schema"
        assertTrue(controller.isPasteEnabled(selectedNode));
    }

    @Test
    public void handleMakeAnonymous_AddChildWithOldType() {
        testMakeAnonymous(false);
    }

    private void testMakeAnonymous(final boolean addChild) {
        final ITreeNode elementNode = createMock(ITreeNode.class);
        final IStructureType element = createMock(IStructureType.class);

        final IXSDModelRoot modelRoot = createNiceMock(IXSDModelRoot.class);
        final IEnvironment env = createNiceMock(IEnvironment.class);

        expect(elementNode.getModelObject()).andReturn(element).anyTimes();
        expect(element.getModelRoot()).andReturn(modelRoot).anyTimes();
        expect(element.getName()).andReturn("").anyTimes();
        expect(modelRoot.getEnv()).andReturn(env);

        replay(modelRoot, elementNode, element);

        final boolean[] executed = { false };
        final DataTypesFormPageController controller = new DataTypesFormPageController(modelRoot, false) {
            @Override
            protected void executeSetElementAnonymousCommand(final String elementName, final boolean anonymous,
                    final AbstractNotificationOperation setAnonymousCommand, final String dialogTitle) {
                assertTrue(setAnonymousCommand instanceof MakeGlobalTypeAnonymousCommand);
                final MakeGlobalTypeAnonymousCommand command = (MakeGlobalTypeAnonymousCommand) setAnonymousCommand;
                assertSame(modelRoot, command.getModelRoot());
                assertSame(element, command.getStructureType());
                assertTrue(command.isAddChild() == addChild);
                executed[0] = true;
            }

            @Override
            protected ISchema getElementSchema(final IElement element) {
                return null;
            }
        };

        controller.handleConvertToAnonymousTypeAction(elementNode);
        assertTrue(executed[0]);
    }

    @Test
    public void handleMakeGlobal() {
        final ITreeNode elementNode = createMock(ITreeNode.class);
        final IStructureType element = createMock(IStructureType.class);

        final IXSDModelRoot modelRoot = createNiceMock(IXSDModelRoot.class);
        final IEnvironment env = createNiceMock(IEnvironment.class);

        expect(elementNode.getModelObject()).andReturn(element).anyTimes();
        expect(element.getModelRoot()).andReturn(modelRoot).anyTimes();
        expect(element.getName()).andReturn("").anyTimes();
        expect(modelRoot.getEnv()).andReturn(env);

        replay(modelRoot, element, elementNode);

        final boolean[] executed = { false };
        final DataTypesFormPageController controller = new DataTypesFormPageController(modelRoot, false) {
            @Override
            protected void executeSetElementAnonymousCommand(final String elementName, final boolean anonymous,
                    final AbstractNotificationOperation setAnonymousCommand, final String dialogTitle) {
                assertTrue(setAnonymousCommand instanceof MakeAnonymousTypeGlobalCommand);
                final MakeAnonymousTypeGlobalCommand command = (MakeAnonymousTypeGlobalCommand) setAnonymousCommand;
                assertSame(modelRoot, command.getModelRoot());
                assertSame(element, command.getStructureType());
                executed[0] = true;
            }

            @Override
            protected ISchema getElementSchema(final IElement element) {
                return null;
            }
        };

        controller.handleConvertToGlobalAction(elementNode);
        assertTrue(executed[0]);
    }

    @Test
    public void handleInlineStructureType() {
        final ITreeNode elementNode = createMock(ITreeNode.class);
        final IStructureType element = createMock(IStructureType.class);

        final IXSDModelRoot modelRoot = createNiceMock(IXSDModelRoot.class);
        final IEnvironment env = createNiceMock(IEnvironment.class);

        expect(elementNode.getModelObject()).andReturn(element).anyTimes();
        expect(element.getModelRoot()).andReturn(modelRoot).anyTimes();
        expect(element.getName()).andReturn("").anyTimes();
        expect(modelRoot.getEnv()).andReturn(env);

        replay(modelRoot, element, elementNode);

        final boolean[] executed = { false, false };
        final DataTypesFormPageController controller = new DataTypesFormPageController(modelRoot, false) {
            @Override
            protected IStatus executeCommand(final AbstractNotificationOperation inlineCommand) {
                assertTrue(inlineCommand instanceof InlineStructureTypeContentsCommand);
                final InlineStructureTypeContentsCommand command = (InlineStructureTypeContentsCommand) inlineCommand;
                assertSame(modelRoot, command.getModelRoot());
                assertSame(element, command.getStructureType());
                executed[0] = true;
                return Status.OK_STATUS;
            }

            @Override
            protected void fireTreeNodeExpandEvent(final IModelObject modelObject) {
                executed[1] = true;
                assertSame(element, modelObject);
            }

            @Override
            protected ISchema getElementSchema(final IElement element) {
                return null;
            }
        };

        controller.handleConvertToAnonymousTypeWithTypeContentsAction(elementNode);
        assertTrue(executed[0]);
        assertTrue(executed[1]);
    }

    // =========================================================
    // extract namespace
    // =========================================================

    @Test
    public void isExtractNamespaceEnabled() {
        final TestDataTypesFormPageController controller = new TestDataTypesFormPageController(null, false);

        assertFalse(controller.isExtractNamespaceEnabled(null));
        testReadonlySchema(controller);
        testEdittableSchema(controller);
    }

    private void testReadonlySchema(final TestDataTypesFormPageController controller) {
        final IDataTypesTreeNode node = createMock(IDataTypesTreeNode.class);
        final ISchema schema = createNiceMock(ISchema.class);

        expect(node.isReadOnly()).andReturn(true);
        expect(node.getModelObject()).andReturn(schema);

        replay(node);

        assertFalse(controller.isExtractNamespaceEnabled(node));
    }

    private void testEdittableSchema(final TestDataTypesFormPageController controller) {
        final IDataTypesTreeNode node = createMock(IDataTypesTreeNode.class);
        final ISchema schema = createNiceMock(ISchema.class);

        expect(node.isReadOnly()).andReturn(false);
        expect(node.getModelObject()).andReturn(schema);

        replay(node);

        assertTrue(controller.isExtractNamespaceEnabled(node));
    }

    @Test
    public void handleExtractNamespace() {
        final IDataTypesTreeNode nodeMock = createMock(IDataTypesTreeNode.class);

        final boolean[] called = { false, false };
        final ExtractNamespaceWizard[] extractWizard = { null };
        final TestDataTypesFormPageController controller = new TestDataTypesFormPageController(null, false) {

            @Override
            protected IStatus initWizard(final ITreeNode node, final ExtractNamespaceWizard wizard) {
                assertFalse("open wizard called before init", called[1]);
                called[0] = true;
                assertSame(nodeMock, node);
                extractWizard[0] = wizard;
                return Status.OK_STATUS;
            }

            @Override
            protected void openWizardDialog(final WizardDialog dialog) {
                assertTrue("init was not called before open", called[0]);
                called[1] = true;
                assertNotNull(extractWizard[0].getWizardDialog());
            }
        };
        controller.handleExtractNamespace(nodeMock);

        assertTrue("init wizard was not called", called[0]);
        assertTrue("open wizard dialog was not called", called[1]);
    }

    // =========================================================
    // inline namespace
    // =========================================================

    @Test
    public void isMakeAnInlineNamespaceEnabled() {
        final TestDataTypesFormPageController controller = new TestDataTypesFormPageController(null, false);

        assertFalse(controller.isMakeAnInlineNamespaceEnabled(null));
        testInlineNonImportedSchema(controller);
        testInlineImportedSchema(controller);
    }

    private void testInlineNonImportedSchema(final TestDataTypesFormPageController controller) {
        final IDataTypesTreeNode node = createMock(IDataTypesTreeNode.class);
        final ISchema schema = createNiceMock(ISchema.class);

        expect(node.getModelObject()).andReturn(schema).anyTimes();
        expect(node.getCategories()).andReturn(0);

        replay(node);

        assertFalse(controller.isMakeAnInlineNamespaceEnabled(node));
    }

    private void testInlineImportedSchema(final TestDataTypesFormPageController controller) {
        final IDataTypesTreeNode node = createMock(IDataTypesTreeNode.class);
        final ISchema schema = createMock(ISchema.class);
        final IWsdlModelRoot wsdlModelRoot = createMock(IWsdlModelRoot.class);

        expect(schema.getModelRoot()).andReturn(wsdlModelRoot);
        expect(wsdlModelRoot.getRoot()).andReturn(wsdlModelRoot);
        expect(node.getModelObject()).andReturn(schema).anyTimes();
        expect(node.getCategories()).andReturn(ITreeNode.CATEGORY_IMPORTED);

        replay(node, schema, wsdlModelRoot);

        assertTrue(controller.isMakeAnInlineNamespaceEnabled(node));
    }

    @Test
    public void handleInlineNamespace() {
        final ITreeNode schemaNode = createMock(ITreeNode.class);
        final ISchema schema = createMock(ISchema.class);

        final IWsdlModelRoot modelRoot = createMock(IWsdlModelRoot.class);
        final IEnvironment env = createNiceMock(IEnvironment.class);

        expect(schemaNode.getModelObject()).andReturn(schema).anyTimes();
        expect(schema.getModelRoot()).andReturn(modelRoot).anyTimes();
        expect(modelRoot.getRoot()).andReturn(modelRoot);
        expect(modelRoot.getEnv()).andReturn(env);

        replay(modelRoot, schema, schemaNode);

        final boolean[] executed = { false, false };
        final DataTypesFormPageController controller = new DataTypesFormPageController(modelRoot, false) {
            @Override
            protected IStatus executeCommand(final AbstractNotificationOperation inlineCommand) {
                assertTrue(inlineCommand instanceof InlineNamespaceCompositeCommand);
                final InlineNamespaceCompositeCommand command = (InlineNamespaceCompositeCommand) inlineCommand;
                assertSame(modelRoot, command.getModelRoot());
                assertSame(schema, command.getSourceSchema());
                executed[0] = true;
                return Status.OK_STATUS;
            }

            @Override
            protected void fireTreeNodeExpandEvent(final IModelObject modelObject) {
                executed[1] = true;
                assertNull(modelObject);
            }

        };

        controller.handleMakeAnInlineNamespace(schemaNode);
        assertTrue(executed[0]);
        assertTrue(executed[1]);
    }

}
