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

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IDataTypesTreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IElementNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.INamespaceNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.ISimpleTypeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IStructureTypeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.NamespaceNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.StructureTypeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.eventing.ISIEvent;
import org.eclipse.wst.sse.sieditor.ui.v2.eventing.ISIEventListener;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.utils.StatusUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

/**
 *
 * 
 */
public class DataTypesFormPageControllerPlugInTest extends SIEditorBaseTest {

    private IWsdlModelRoot wsdlModelRoot;
    private TestDataTypesFormPageController controller;
    private List<ISchema> schemas;

    /**
     * @throws java.lang.Exception
     */
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        wsdlModelRoot = getWSDLModelRoot("pub/self/mix2/PurchaseOrderConfirmation.wsdl", "PurchaseOrderConfirmation.wsdl");
        schemas = wsdlModelRoot.getDescription().getContainedSchemas();
        controller = new TestDataTypesFormPageController(wsdlModelRoot, false);
    }

    /**
     * @throws java.lang.Exception
     */
    @Override
    @After
    public void tearDown() throws Exception {

    }

    private static class TestDataTypesFormPageController extends DataTypesFormPageController {

        public static final String NEW_GLOBAL_ELEMENT_CHILD_TYPE_NAME_TEST = "string";

        public TestDataTypesFormPageController(final IModelRoot model, final boolean readOnly) {
            super(model, readOnly);
        }

        public void setReadOnly(final boolean readOnly) {
            this.readOnly = readOnly;
        }

        @Override
        public ITreeNode getNextTreeNode(final ITreeNode selectedTreeNode) {
            // TODO Auto-generated method stub
            return super.getNextTreeNode(selectedTreeNode);
        }
    }

    class ErrorListener implements ISIEventListener {
        @Override
        public void notifyEvent(final ISIEvent event) {
            assertEquals(ISIEvent.ID_ERROR_MSG, event.getEventId());
            assertTrue(event.getEventParams().length == 1);
            assertTrue(event.getEventParams()[0] instanceof String);
            assertFalse(((String) event.getEventParams()[0]).isEmpty());
            listenerCallCounter++;
        }
    };

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController#editDocumentation(org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode, java.lang.String)}
     * .
     */
    @Test
    public final void testEditDocumentation() {
        assertEquals("http://sap.com/xi/Purchasing", schemas.get(0).getNamespace()); //$NON-NLS-1$
        final IType type = schemas.get(0).getType(false, "AcceptanceStatusCode"); //$NON-NLS-1$

        final ITreeNode treeNodeMock = createNiceMock(ITreeNode.class);
        expect(treeNodeMock.getModelObject()).andReturn(type).anyTimes();
        replay(treeNodeMock);
        assertEquals(UIConstants.EMPTY_STRING, type.getDocumentation());
        listenerCallCounter = 0;
        controller.getTreeNodeMapper().addToNodeMap(type, treeNodeMock);
        final String DOC_TEXT_1 = "test edit documentation text 1"; //$NON-NLS-1$
        controller.editDocumentation(treeNodeMock, DOC_TEXT_1);
        assertEquals(DOC_TEXT_1, type.getDocumentation());
        final String DOC_TEXT_2 = "test edit documentation text 2"; //$NON-NLS-1$
        controller.editDocumentation(treeNodeMock, DOC_TEXT_2);
        assertEquals(DOC_TEXT_2, type.getDocumentation());
        controller.editDocumentation(treeNodeMock, UIConstants.EMPTY_STRING);
        assertEquals(UIConstants.EMPTY_STRING, type.getDocumentation());

        controller.setReadOnly(true);
        listenerCallCounter = 0;
        controller.addEventListener(new ErrorListener());
        final long oldShowErrorCals = StatusUtils.getShowStatusDialog_calls();
        controller.editDocumentation(treeNodeMock, DOC_TEXT_1);
        assertEquals(oldShowErrorCals + 1, StatusUtils.getShowStatusDialog_calls());
    }

    private int listenerCallCounter = 0;

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController#handleAddElementAction(org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode)}
     * .
     */
    @Test
    public final void testHandleAddElementActionGlobal() {
        assertEquals("http://sap.com/xi/SRM/Basis/Global", schemas.get(1).getNamespace()); //$NON-NLS-1$
        final ISchema schema = schemas.get(1);
        final String GLOBAL_ELEMENT_NAME = controller.getNewElementName(schema);
        assertNull(schema.getType(true, GLOBAL_ELEMENT_NAME));
        final Collection<IType> existingTypes = schema.getAllContainedTypes();
        assertTrue(existingTypes.size() == 1);

        final INamespaceNode namespaceNode = new NamespaceNode(schema, controller.getTreeNodeMapper());

        final ISIEventListener eventListener = new ISIEventListener() {
            @Override
            public void notifyEvent(final ISIEvent event) {
                listenerCallCounter += 1;
                assertTrue(event.getEventId() == ISIEvent.ID_SELECT_TREENODE
                        || event.getEventId() == ISIEvent.ID_TREE_NODE_EXPAND
                        || event.getEventId() == ISIEvent.ID_EDIT_TREENODE);
                assertTrue(event.getEventParams().length == 1);
                final Object treeNodeObject = event.getEventParams()[0];
                assertNotNull(treeNodeObject);
                assertTrue(treeNodeObject instanceof IStructureTypeNode);
                final IModelObject newElement = ((ITreeNode) treeNodeObject).getModelObject();
                assertNotNull(newElement);
                assertTrue(newElement instanceof IStructureType);
                final IStructureType newGlobalElement = (IStructureType) newElement;
                assertTrue(schema.getAllContainedTypes().contains(newGlobalElement));
                assertTrue(newGlobalElement.isElement());
                assertTrue(newGlobalElement.isAnonymous());
                assertNotNull(newGlobalElement.getType());
                assertTrue(newGlobalElement.getType().isAnonymous());
                assertTrue(newGlobalElement.getDocumentation().isEmpty());
                // child element asserts
                final Collection<IElement> newElementElements = newGlobalElement.getAllElements();
                assertTrue(newElementElements.size() == 1);
                final IElement childElement = newElementElements.iterator().next();
                assertNotNull(childElement);
                assertFalse(childElement.isAttribute());
                final IType childType = childElement.getType();
                assertFalse(childType.isAnonymous());
                assertTrue(childElement.getDocumentation().isEmpty());
                assertTrue(childType instanceof ISimpleType);
                assertEquals("string", childType.getName()); //$NON-NLS-1$
                assertEquals(EmfXsdUtils.getSchemaForSchemaNS(), childType.getNamespace());

                // tree node asserts
                final ITreeNode newElementNode = controller.getTreeNodeMapper().getTreeNode(newElement);
                assertNotNull(newElementNode);
                assertEquals(newElementNode, event.getEventParams()[0]);
                assertEquals(GLOBAL_ELEMENT_NAME, newElementNode.getDisplayName());
                assertTrue(newElementNode instanceof IStructureTypeNode);
                assertEquals(newElement, newElementNode.getModelObject());
                assertEquals(namespaceNode, newElementNode.getParent());
                final Object[] treeNodeChildren = newElementNode.getChildren();
                assertTrue(treeNodeChildren.length == 1);
                assertTrue(treeNodeChildren[0] instanceof IElementNode);
                final IElementNode childTreeNode = (IElementNode) treeNodeChildren[0];
                assertEquals("string", childTreeNode.getDisplayName()); //$NON-NLS-1$
                assertEquals(childElement, childTreeNode.getModelObject());
                assertEquals(newElementNode, childTreeNode.getParent());
            }
        };
        listenerCallCounter = 0;
        controller.addEventListener(eventListener);
        controller.getTreeNodeMapper().addToNodeMap(schema, namespaceNode);
        controller.handleAddElementAction(namespaceNode);

        assertTrue(listenerCallCounter != 0);
        assertTrue(schema.getAllContainedTypes().size() == 2);
        for (final IType iType : schema.getAllContainedTypes()) {
            if (existingTypes.contains(iType)) {
                continue;
            }
            assertEquals(GLOBAL_ELEMENT_NAME, iType.getName());
        }

        controller.setReadOnly(true);
        assertTrue(listenerCallCounter != 0);
        controller.removeEventListener(eventListener);

        listenerCallCounter = 0;
        final ISIEventListener errorListener = new ErrorListener();
        controller.addEventListener(errorListener);
        final long oldShowErrorCals = StatusUtils.getShowStatusDialog_calls();
        controller.handleAddElementAction(namespaceNode);
        assertEquals(oldShowErrorCals + 1, StatusUtils.getShowStatusDialog_calls());

        controller.removeEventListener(errorListener);
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController#handleAddElementAction(org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode)}
     * .
     */
    @Test
    public final void testHandleAddElementActionLocal() {
        assertEquals("http://sap.com/xi/Purchasing", schemas.get(0).getNamespace()); //$NON-NLS-1$
        final ISchema schema = schemas.get(0);
        final String parentTypeName = "Amount"; //$NON-NLS-1$
        final IStructureType parentType = (IStructureType) schema.getType(false, parentTypeName);
        assertNotNull(parentType);
        final Collection<IElement> existingElements = parentType.getAllElements();
        final String localElementName = controller.getNewElementName(parentType);
        assertTrue(existingElements.size() == 1);

        final INamespaceNode namespaceNode = new NamespaceNode(schema, controller.getTreeNodeMapper());
        final Object[] schemaChildren = namespaceNode.getChildren();
        IStructureTypeNode parentNode = null;
        for (final Object object : schemaChildren) {
            if (parentTypeName.equals(((IDataTypesTreeNode) object).getDisplayName())) {
                parentNode = (IStructureTypeNode) object;
                break;
            }
        }
        final ITreeNode structureTypeNode = parentNode;
        assertNotNull(parentNode);

        final ISIEventListener eventListener = new ISIEventListener() {
            @Override
            public void notifyEvent(final ISIEvent event) {
                listenerCallCounter += 1;
                assertTrue(event.getEventId() == ISIEvent.ID_SELECT_TREENODE
                        || event.getEventId() == ISIEvent.ID_TREE_NODE_EXPAND
                        || event.getEventId() == ISIEvent.ID_EDIT_TREENODE);
                assertTrue(event.getEventParams().length == 1);
                final Object treeNodeObject = event.getEventParams()[0];
                assertNotNull(treeNodeObject);
                assertTrue(treeNodeObject instanceof IElementNode);
                final IModelObject newElement = ((ITreeNode) treeNodeObject).getModelObject();
                assertNotNull(newElement);
                assertTrue(newElement instanceof IElement);
                final IElement newlocalElement = (IElement) newElement;
                assertTrue(parentType.getAllElements().contains(newlocalElement));
                assertFalse(newlocalElement.isAttribute());
                assertTrue(newlocalElement.getDocumentation().isEmpty());
                // type asserts
                assertNotNull(newlocalElement.getType());
                final IType elementType = newlocalElement.getType();
                assertFalse(elementType.isAnonymous());
                assertTrue(elementType instanceof ISimpleType);
                assertEquals("string", elementType.getName()); //$NON-NLS-1$
                assertEquals(EmfXsdUtils.getSchemaForSchemaNS(), elementType.getNamespace());

                // tree node asserts
                final ITreeNode newElementNode = controller.getTreeNodeMapper().getTreeNode(newElement);
                assertNotNull(newElementNode);
                assertEquals(newElementNode, event.getEventParams()[0]);
                assertEquals(localElementName, newElementNode.getDisplayName());
                assertTrue(newElementNode instanceof IElementNode);
                assertEquals(newElement, newElementNode.getModelObject());
                assertEquals(structureTypeNode, newElementNode.getParent());
                assertFalse(newElementNode.hasChildren());
                final Object[] treeNodeChildren = newElementNode.getChildren();
                assertTrue(treeNodeChildren.length == 0);
            }
        };
        listenerCallCounter = 0;
        controller.addEventListener(eventListener);
        controller.getTreeNodeMapper().addToNodeMap(schema, namespaceNode);
        controller.handleAddElementAction(parentNode);

        assertTrue(listenerCallCounter != 0);
        assertTrue(parentType.getAllElements().size() == 2);
        for (final IElement element : parentType.getAllElements()) {
            if (existingElements.contains(element)) {
                continue;
            }
            assertEquals(localElementName, element.getName());
        }

        controller.setReadOnly(true);
        assertTrue(listenerCallCounter != 0);
        controller.removeEventListener(eventListener);
        listenerCallCounter = 0;
        final ISIEventListener errorListener = new ErrorListener();
        controller.addEventListener(errorListener);
        final long oldShowErrorCals = StatusUtils.getShowStatusDialog_calls();
        controller.handleAddElementAction(parentNode);
        assertEquals(oldShowErrorCals + 1, StatusUtils.getShowStatusDialog_calls());

        controller.removeEventListener(eventListener);
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController#handleAddSimpleTypeAction(org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode)}
     * .
     */
    @Test
    public final void testHandleAddSimpleTypeAction() {
        assertEquals("http://sap.com/xi/SRM/Basis/Global", schemas.get(1).getNamespace()); //$NON-NLS-1$
        final ISchema schema = schemas.get(1);
        final String simpleTypeName = controller.getNewSimpleTypeName(schema);
        assertNull(schema.getType(false, simpleTypeName));
        final Collection<IType> existingTypes = schema.getAllContainedTypes();
        assertTrue(existingTypes.size() == 1);

        final INamespaceNode namespaceNode = new NamespaceNode(schema, controller.getTreeNodeMapper());

        final ISIEventListener eventListener = new ISIEventListener() {
            @Override
            public void notifyEvent(final ISIEvent event) {
                listenerCallCounter += 1;
                assertTrue(event.getEventId() == ISIEvent.ID_SELECT_TREENODE
                        || event.getEventId() == ISIEvent.ID_EDIT_TREENODE);
                assertTrue(event.getEventParams().length == 1);
                final Object treeNodeObject = event.getEventParams()[0];
                assertNotNull(treeNodeObject);
                assertTrue(treeNodeObject instanceof ISimpleTypeNode);
                final IModelObject newType = ((ITreeNode) treeNodeObject).getModelObject();
                assertNotNull(newType);
                assertTrue(newType instanceof ISimpleType);
                final ISimpleType newGlobalType = (ISimpleType) newType;
                assertTrue(schema.getAllContainedTypes().contains(newGlobalType));
                assertTrue(newGlobalType.getPatterns().length == 0);
                assertFalse(newGlobalType.isAnonymous());
                assertTrue(newGlobalType.getDocumentation().isEmpty());
                final IType baseType = newGlobalType.getBaseType();
                assertNotNull(baseType);
                assertFalse(baseType.isAnonymous());
                // child element asserts
                assertEquals("string", baseType.getName()); //$NON-NLS-1$
                assertEquals(EmfXsdUtils.getSchemaForSchemaNS(), baseType.getNamespace());

                // tree node asserts
                final ITreeNode newTypeNode = controller.getTreeNodeMapper().getTreeNode(newType);
                assertNotNull(newTypeNode);
                assertEquals(newTypeNode, event.getEventParams()[0]);
                assertEquals(simpleTypeName, newTypeNode.getDisplayName());
                assertTrue(newTypeNode instanceof ISimpleTypeNode);
                assertEquals(newType, newTypeNode.getModelObject());
                assertEquals(namespaceNode, newTypeNode.getParent());
                assertFalse(newTypeNode.hasChildren());
            }
        };
        listenerCallCounter = 0;
        controller.addEventListener(eventListener);
        controller.getTreeNodeMapper().addToNodeMap(schema, namespaceNode);

        controller.handleAddSimpleTypeAction(namespaceNode);

        assertTrue(listenerCallCounter == 2);
        assertTrue(schema.getAllContainedTypes().size() == 2);
        for (final IType iType : schema.getAllContainedTypes()) {
            if (existingTypes.contains(iType)) {
                continue;
            }
            assertEquals(simpleTypeName, iType.getName());
        }

        controller.setReadOnly(true);
        assertTrue(listenerCallCounter == 2);
        controller.removeEventListener(eventListener);

        listenerCallCounter = 0;
        final ISIEventListener errorListener = new ErrorListener();
        controller.addEventListener(errorListener);
        final long oldShowErrorCals = StatusUtils.getShowStatusDialog_calls();
        controller.handleAddSimpleTypeAction(namespaceNode);
        assertEquals(oldShowErrorCals + 1, StatusUtils.getShowStatusDialog_calls());

        controller.removeEventListener(errorListener);
    }

//    /**
//     * Test method for
//     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController#handleAddStructureTypeAction(org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode)}
//     * .
//     * 
//     * @throws ExecutionException
//     */
//    @Test
//    public final void testHandleAddStructureTypeAction() throws ExecutionException {
//        final String namespace = "http://sap.com/xi/SRM/Basis/Global";
//        assertEquals(namespace, schemas.get(1).getNamespace()); //$NON-NLS-1$
//        final ISchema schema = schemas.get(1);
//        final String structureTypeName = controller.getNewStructureTypeName(schema);
//        assertNull(schema.getType(false, structureTypeName));
//        final Collection<IType> existingTypes = schema.getAllContainedTypes();
//        assertTrue(existingTypes.size() == 1);
//
//        final INamespaceNode namespaceNode = new NamespaceNode(schema, controller.getTreeNodeMapper());
//
//        final ISIEventListener eventListener = new ISIEventListener() {
//            @Override
//            public void notifyEvent(final ISIEvent event) {
//                listenerCallCounter += 1;
//                assertTrue(event.getEventId() == ISIEvent.ID_SELECT_TREENODE
//                        || event.getEventId() == ISIEvent.ID_TREE_NODE_EXPAND
//                        || event.getEventId() == ISIEvent.ID_EDIT_TREENODE);
//                assertTrue(event.getEventParams().length == 1);
//                final Object treeNodeObject = event.getEventParams()[0];
//                assertNotNull(treeNodeObject);
//                assertTrue(treeNodeObject instanceof IStructureTypeNode);
//                final IModelObject newElement = ((ITreeNode) treeNodeObject).getModelObject();
//                assertTrue(newElement instanceof IStructureType);
//                final IStructureType newType = (IStructureType) newElement;
//                assertEquals(structureTypeName, newType.getName());
//                assertEquals(namespace, newType.getNamespace());
//                assertTrue(schema.getAllContainedTypes().contains(newType));
//                assertFalse(newType.isElement());
//                assertFalse(newType.isAnonymous());
//                assertTrue(newType.getDocumentation().isEmpty());
//                assertEquals(1, newType.getAllElements().size());
//                final IElement element = newType.getAllElements().iterator().next();
//                assertEquals(Schema.getSchemaForSchema().getType(false,
//                        TestDataTypesFormPageController.NEW_GLOBAL_ELEMENT_CHILD_TYPE_NAME_TEST), element.getType());
//                assertTrue(newType.getBaseType() instanceof IStructureType);
//                final IStructureType baseType = (IStructureType) newType.getBaseType();
//                assertFalse(baseType.isAnonymous());
//                // assertFalse(baseType.isElement());
//                // assertTrue(baseType.getAllElements().isEmpty());
//
//                // tree node asserts
//                final ITreeNode newNode = controller.getTreeNodeMapper().getTreeNode(newElement);
//                assertNotNull(newNode);
//                assertEquals(newNode, event.getEventParams()[0]);
//                assertEquals(structureTypeName, newNode.getDisplayName());
//                assertTrue(newNode instanceof IStructureTypeNode);
//                assertEquals(newElement, newNode.getModelObject());
//                assertEquals(namespaceNode, newNode.getParent());
//                assertTrue(newNode.hasChildren());
//                assertEquals(1, newNode.getChildren().length);
//                assertTrue(newNode.getChildren()[0] instanceof IElementNode);
//            }
//        };
//        listenerCallCounter = 0;
//        controller.addEventListener(eventListener);
//        controller.getTreeNodeMapper().addToNodeMap(schema, namespaceNode);
//        controller.handleAddStructureTypeAction(namespaceNode);
//
//        assertTrue(listenerCallCounter != 0);
//        assertTrue(schema.getAllContainedTypes().size() == 2);
//        for (final IType iType : schema.getAllContainedTypes()) {
//            if (existingTypes.contains(iType)) {
//                continue;
//            }
//            assertEquals(structureTypeName, iType.getName());
//
//            // Assert the type is not empty (one default element should be
//            // created)
//            final IStructureType structure = (IStructureType) iType;
//            assertEquals(1, structure.getAllElements().size());
//        }
//
//        controller.setReadOnly(true);
//        assertTrue(listenerCallCounter != 0);
//        controller.removeEventListener(eventListener);
//
//        listenerCallCounter = 0;
//        final ISIEventListener errorListener = new ErrorListener();
//        controller.addEventListener(errorListener);
//        final long oldShowErrorCals = StatusUtils.getShowStatusDialog_calls();
//        controller.handleAddStructureTypeAction(namespaceNode);
//        assertEquals(oldShowErrorCals + 1, StatusUtils.getShowStatusDialog_calls());
//
//        controller.removeEventListener(errorListener);
//
//        controller.setReadOnly(false);
//
//        controller.handleAddStructureTypeAction(namespaceNode);
//        assertNotNull(schema.getAllTypes("StructureType2"));
//        final IEnvironment env = schema.getModelRoot().getEnv();
//        env.getOperationHistory().undo(env.getUndoContext(), null, null);
//        assertNull(schema.getAllTypes("StructureType2"));
//        env.getOperationHistory().redo(env.getUndoContext(), null, null);
//        assertNotNull(schema.getAllTypes("StructureType2"));
//    }

//    @Test
//    public void testSetElementFacet() throws ExecutionException {
//
//        IWsdlModelRoot wsdlModelRoot = null;
//        try {
//            wsdlModelRoot = getWSDLModelRoot("pub/self/mix/TypesInternalImporting.wsdl", "TypesInternalImporting.wsdl");
//        } catch (final Exception e) {
//            fail(e);
//        }
//
//        final ISchema schema = wsdlModelRoot.getDescription().getSchema("http://www.example.com/")[0];
//
//        final IStructureType structure = (IStructureType) schema.getType(false, "Address");
//        IElement element = structure.getElements("name").iterator().next();
//
//        DataTypesFormPageController controller = new DataTypesFormPageController(wsdlModelRoot, false);
//        controller.setElementFacet(element, "aaa", XSDPackage.XSD_ENUMERATION_FACET);
//
//        IEnvironment env = schema.getModelRoot().getEnv();
//        XSDElementDeclaration xsdElementDeclaration = (XSDElementDeclaration) ((XSDParticle) element.getComponent()).getContent();
//        assertEquals(1, ((XSDSimpleTypeDefinition) xsdElementDeclaration.getAnonymousTypeDefinition()).getEnumerationFacets()
//                .size());
//
//        env.getOperationHistory().undo(env.getUndoContext(), null, null);
//        assertNull(xsdElementDeclaration.getAnonymousTypeDefinition());
//
//        env.getOperationHistory().redo(env.getUndoContext(), null, null);
//        assertNotNull(xsdElementDeclaration.getAnonymousTypeDefinition());
//        assertEquals(1, ((XSDSimpleTypeDefinition) xsdElementDeclaration.getAnonymousTypeDefinition()).getEnumerationFacets()
//                .size());
//
//        final IStructureType globalElement = (IStructureType) schema.getType(true, "comment");
//        controller.setGlobalElementFacet(globalElement, "ggg", XSDPackage.XSD_ENUMERATION_FACET);
//        xsdElementDeclaration = (XSDElementDeclaration) globalElement.getComponent();
//        assertEquals(1, ((XSDSimpleTypeDefinition) xsdElementDeclaration.getAnonymousTypeDefinition()).getEnumerationFacets()
//                .size());
//
//        env.getOperationHistory().undo(env.getUndoContext(), null, null);
//        assertNull(xsdElementDeclaration.getAnonymousTypeDefinition());
//
//        env.getOperationHistory().redo(env.getUndoContext(), null, null);
//        assertNotNull(xsdElementDeclaration.getAnonymousTypeDefinition());
//        assertEquals(1, ((XSDSimpleTypeDefinition) xsdElementDeclaration.getAnonymousTypeDefinition()).getEnumerationFacets()
//                .size());
//
//        final IStructureType typeWithAttribute = (IStructureType) schema.getType(false, "ItemsExtended");
//        element = typeWithAttribute.getElements("specialAtt").iterator().next();
//
//        controller = new DataTypesFormPageController(wsdlModelRoot, false);
//        controller.setElementFacet(element, "aaa", XSDPackage.XSD_ENUMERATION_FACET);
//
//        env = schema.getModelRoot().getEnv();
//        final XSDAttributeDeclaration xsdAttributeDeclaration = (XSDAttributeDeclaration) element.getComponent();
//        assertEquals(1, (xsdAttributeDeclaration.getAnonymousTypeDefinition()).getEnumerationFacets().size());
//
//        env.getOperationHistory().undo(env.getUndoContext(), null, null);
//        assertNull(xsdAttributeDeclaration.getAnonymousTypeDefinition());
//
//        env.getOperationHistory().redo(env.getUndoContext(), null, null);
//        assertNotNull(xsdAttributeDeclaration.getAnonymousTypeDefinition());
//        assertEquals(1, (xsdAttributeDeclaration.getAnonymousTypeDefinition()).getEnumerationFacets().size());
//    }

    @Test
    public void testHandleAddGlobalElementAction() {

        IXSDModelRoot xsdModelRoot = null;
        try {
            xsdModelRoot = getXSDModelRoot("pub/xsd/example.xsd", "example.xsd");
        } catch (final Exception e) {
            fail(e.toString());
        }

        final ISchema schema = xsdModelRoot.getSchema();
        final int size = schema.getAllContainedTypes().size();

        final DataTypesFormPageController controller = new DataTypesFormPageController(xsdModelRoot, false);

        assertEquals(true, controller.isAddGlobalElementEnabled(null));

        controller.handleAddGlobalElementAction(null);
        assertEquals(size + 1, schema.getAllContainedTypes().size());
    }

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesFormPageController#handleRemoveAction(org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode)}
     * .
     */
    @Test
    public final void testHandleRemoveAction() {
        assertEquals("http://sap.com/xi/Purchasing", schemas.get(0).getNamespace()); //$NON-NLS-1$
        final ISchema schema = schemas.get(0);
        final IType simpleTypeToDelete = schema.getType(false, "ActionCode"); //$NON-NLS-1$
        assertNotNull(simpleTypeToDelete);
        final Collection<IType> existingTypes = schema.getAllContainedTypes();
        assertTrue(existingTypes.size() == 104);

        final INamespaceNode namespaceNode = new NamespaceNode(schema, controller.getTreeNodeMapper());
        controller.getTreeNodeMapper().addToNodeMap(schema, namespaceNode);
        namespaceNode.getChildren();
        final ITreeNode nodeToDelete = controller.getTreeNodeMapper().getTreeNode(simpleTypeToDelete);
        final ITreeNode nextTreeNode = controller.getNextTreeNode(nodeToDelete);
        assertTrue(nodeToDelete instanceof ISimpleTypeNode);
        assertEquals(simpleTypeToDelete, nodeToDelete.getModelObject());

        final ISIEventListener eventListener = new ISIEventListener() {
            @Override
            public void notifyEvent(final ISIEvent event) {
                listenerCallCounter += 1;
                if (event.getEventId() == ISIEvent.ID_SELECT_TREENODE) {
                    assertTrue(event.getEventParams().length == 1);
                    assertEquals(nextTreeNode, event.getEventParams()[0]);
                    final Object[] namespaceChildren = namespaceNode.getChildren();
                    boolean containsNext = false;
                    for (final Object child : namespaceChildren) {
                        assertFalse(nodeToDelete.equals(child));
                        if (nextTreeNode.equals(child)) {
                            containsNext = true;
                        }
                    }
                    assertTrue(containsNext);
                }
            }
        };
        listenerCallCounter = 0;
        controller.addEventListener(eventListener);
        controller.handleRemoveAction(Arrays.asList(nodeToDelete));

        assertEquals(1, listenerCallCounter);
        assertNull(controller.getTreeNodeMapper().getTreeNode(simpleTypeToDelete));

        final Collection<IType> allContainedTypes = schema.getAllContainedTypes();
        assertTrue(allContainedTypes.size() == 103);
        for (final IType iType : existingTypes) {
            if (allContainedTypes.contains(iType)) {
                continue;
            }
            assertEquals(simpleTypeToDelete, iType);
        }

        controller.removeEventListener(eventListener);
        controller.setReadOnly(true);

        final ISIEventListener errorListener = new ErrorListener();
        controller.addEventListener(errorListener);
        listenerCallCounter = 0;
        final long oldShowErrorCals = StatusUtils.getShowStatusDialog_calls();
        controller.handleRemoveAction(Arrays.asList(nodeToDelete));
        assertEquals(oldShowErrorCals, StatusUtils.getShowStatusDialog_calls());

        controller.removeEventListener(eventListener);
    }

    @Test
    public void testHandlePasteTypeAction() throws IOException, CoreException {
        wsdlModelRoot = getWSDLModelRoot("pub/import/simple/test_xsd_modeled_AS2.wsdl", "test_xsd_modeled_AS2.wsdl");

        final ISchema[] sourceSchemas = wsdlModelRoot.getDescription().getSchema(
                "http://www.sap.com/caf/demo.sap.com/test_xsd/modeled_wsdl");
        assertEquals(1, sourceSchemas.length);

        final IType typeToCopy = sourceSchemas[0].getType(false, "AS2_String");
        assertNotNull(typeToCopy);

        final ISchema[] targetSchemas = wsdlModelRoot.getDescription().getSchema(
                "http://www.sap.com/caf/demo.sap.com/test_xsd/modeled/AS2");
        assertEquals(1, targetSchemas.length);

        assertNull(targetSchemas[0].getType(false, "AS2_String"));

        final DataTypesFormPageController controller = new DataTypesFormPageController(wsdlModelRoot, false);

        final IDataTypesTreeNode selectedTreeNode = createNiceMock(IDataTypesTreeNode.class);
        expect(selectedTreeNode.getModelObject()).andReturn(typeToCopy).anyTimes();
        replay(selectedTreeNode);

        assertTrue(controller.isCopyEnabled(selectedTreeNode));

        controller.handleCopyTypeAction(selectedTreeNode);

        final IDataTypesTreeNode targetTreeNode = createNiceMock(IDataTypesTreeNode.class);
        expect(targetTreeNode.getModelObject()).andReturn(targetSchemas[0]).anyTimes();
        replay(targetTreeNode);

        controller.handlePasteTypeAction(targetTreeNode);

        assertNotNull(targetSchemas[0].getType(false, "AS2_String"));
    }

    /*
     * Tests that when the selected node is a local element of complex type, the
     * new element is added not to the complex type but to the parent of the
     * selection; same with adding a new attribute
     */
    @Test
    public void testAddElementOnLocalElementSelection() {
        final ISchema schema = schemas.get(0);

        final IStructureType address = (IStructureType) schema.getType(false, "Address");

        final NamespaceNode namespace = new NamespaceNode(schema, controller.getTreeNodeMapper());
        controller.getTreeNodeMapper().addToNodeMap(schema, namespace);

        final StructureTypeNode parent = new StructureTypeNode(address, namespace, controller.getTreeNodeMapper());
        ITreeNode treeNode = null;
        final Object[] children = parent.getChildren();
        final int oldSize = children.length;
        for(final Object child: children){
            if(child instanceof ITreeNode){
                if ("PersonName".equals(((ITreeNode) child).getDisplayName())) {
                     treeNode = (ITreeNode)child;
                    break;
                }
            }
        }
        assertNotNull(treeNode);
        // test add element
        controller.handleAddElementAction(treeNode);
        assertEquals(oldSize + 1, parent.getChildren().length);
        // test add attribute
        controller.handleAddAttributeAction(treeNode);
        assertEquals(oldSize + 2, parent.getChildren().length);
    }
}
