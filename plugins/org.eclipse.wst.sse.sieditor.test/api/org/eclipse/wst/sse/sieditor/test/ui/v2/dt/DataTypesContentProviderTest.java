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
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesContentProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IDataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IDataTypesTreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IImportedTypesNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.INamespaceNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.ImportedXsdTypesNode;
import org.eclipse.wst.sse.sieditor.ui.v2.factory.TreeNodeMapper;
import org.eclipse.xsd.util.XSDConstants;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

public class DataTypesContentProviderTest {

    private static final String C_LOCATION = "c:/location"; //$NON-NLS-1$

    private static final String SOME_LOCATION = "someLocation"; //$NON-NLS-1$
    
    private static final String SOME_NAMESPACE = "someNamespace"; //$NON-NLS-1$

    @Test
    public void testGetChildren() {
        final IDataTypesTreeNode node = createMock(IDataTypesTreeNode.class);
        expect(node.getChildren()).andReturn(new Object[] {createMock(IDataTypesTreeNode.class)}).once();
        replay(node);
        
        final DataTypesContentProvider provider = createContentProvider();
        final Object[] children = provider.getChildren(node);
        
        verify(node);
        assertEquals(1, children.length);        
        assertEquals(0, provider.getChildren(new Object()).length);
    }

    @Test
    public void testGetParent() {
        final IDataTypesTreeNode node = createMock(IDataTypesTreeNode.class);
        expect(node.getParent()).andReturn(createMock(IDataTypesTreeNode.class)).once();
        replay(node);
        
        final DataTypesContentProvider provider = createContentProvider();
        final Object parent = provider.getParent(node);
        
        verify(node);
        assertNotNull(parent);
        assertTrue(parent instanceof IDataTypesTreeNode);
        assertNull(provider.getParent(new Object()));
    }

    @Test
    public void testHasChildren() {
        final IDataTypesTreeNode node = createMock(IDataTypesTreeNode.class);
        expect(node.hasChildren()).andReturn(Boolean.valueOf(true)).once();
        replay(node);
        
        final DataTypesContentProvider provider = createContentProvider();
        assertTrue(provider.hasChildren(node));
        verify(node);
        
        reset(node);
        expect(node.hasChildren()).andReturn(Boolean.valueOf(false)).once();
        replay(node);
        assertFalse(provider.hasChildren(node));
        verify(node);
        
        assertFalse(provider.hasChildren(new Object()));
    }

    @Test
    public void testGetElementsXsdRoot() {
        final IXSDModelRoot modelRootMock = createMock(IXSDModelRoot.class); 
        final ISchema schemaMock = createNiceMock(ISchema.class);
        expect(modelRootMock.getSchema()).andReturn(schemaMock).once();
        
        final ISimpleType containedTypeMock = createNiceMock(ISimpleType.class);
        final Collection<IType> containedTypes = new ArrayList<IType>();
        containedTypes.add(containedTypeMock);
        expect(schemaMock.getAllContainedTypes()).andReturn(containedTypes).once();
        expect(schemaMock.getLocation()).andReturn(SOME_LOCATION).once();
        
        final Collection<ISchema> referredSchemas = new ArrayList<ISchema>();        
        final ISchema referredSchemaMock = createNiceMock(ISchema.class);
        expect(referredSchemaMock.getNamespace()).andReturn(SOME_NAMESPACE).once();
                
        referredSchemas.add(referredSchemaMock);
        expect(schemaMock.getAllReferredSchemas()).andReturn(referredSchemas).once();        
        
        final IDataTypesFormPageController controllerMock = createNiceMock(IDataTypesFormPageController.class);
        final TreeNodeMapper nodeMapper = new TreeNodeMapper();
        expect(controllerMock.getTreeNodeMapper()).andReturn(nodeMapper).once();
        
        replay(modelRootMock, schemaMock, referredSchemaMock, containedTypeMock, controllerMock);

        final DataTypesContentProvider provider = new DataTypesContentProvider(controllerMock);
        final Object[] elements = provider.getElements(modelRootMock);
        
        assertEquals(2, elements.length);
        assertTrue(elements[0] instanceof IDataTypesTreeNode);
        assertEquals(containedTypeMock, ((IDataTypesTreeNode)elements[0]).getModelObject());
        assertTrue(elements[1] instanceof ImportedXsdTypesNode);
    }
    
    @Test
    public void testGetElementsWsdlRoot() {
        final IWsdlModelRoot modelRoot = createMock(IWsdlModelRoot.class);
        final IDescription description = createNiceMock(IDescription.class);
        expect(modelRoot.getDescription()).andReturn(description).anyTimes();
        
        final List<ISchema> schemas = new ArrayList<ISchema>();
        final ISchema schema1 = createNiceMock(ISchema.class);
        final ISchema schema2 = createNiceMock(ISchema.class);
        final ISchema wsdlImportSchema = createMock(ISchema.class);
        expect(wsdlImportSchema.getNamespace()).andReturn("").anyTimes();
        expect(wsdlImportSchema.getComponent()).andReturn(null).anyTimes();
        
        final ISchema builtInTypesSchema = createNiceMock(ISchema.class);
        expect(builtInTypesSchema.getNamespace()).andReturn(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001).anyTimes();
        final Collection<ISchema> referredSchemas = new ArrayList<ISchema>();
        referredSchemas.add(builtInTypesSchema);
        expect(schema1.getAllReferredSchemas()).andReturn(referredSchemas);
        expect(schema1.getLocation()).andReturn(C_LOCATION);
        expect(schema2.getAllReferredSchemas()).andReturn(referredSchemas);
        
        schemas.add(schema1);
        schemas.add(schema2);
        
        final List<ISchema> allVisibleSchemas = new ArrayList<ISchema>();
        allVisibleSchemas.add(wsdlImportSchema);
        
        expect(description.getContainedSchemas()).andReturn(schemas).anyTimes();
        expect(description.getAllVisibleSchemas()).andReturn(allVisibleSchemas).anyTimes();
        
        final IDataTypesFormPageController controller = createMock(IDataTypesFormPageController.class);
        final TreeNodeMapper nodeMapper = new TreeNodeMapper();
        expect(controller.getTreeNodeMapper()).andReturn(nodeMapper).once();
        replay(modelRoot, description, controller, schema1, schema2, builtInTypesSchema, wsdlImportSchema);
        
        final DataTypesContentProvider provider = new DataTypesContentProvider(controller);
        final Object[] elements = provider.getElements(modelRoot);
        
        assertEquals(3, elements.length);
        assertTrue(elements[0] instanceof INamespaceNode);
        assertTrue(elements[1] instanceof INamespaceNode);
        assertTrue(elements[2] instanceof IImportedTypesNode);
        assertNotNull(nodeMapper.getTreeNode(schema1));
        assertNotNull(nodeMapper.getTreeNode(schema2));
        
        //
        reset(modelRoot, description, controller, schema1, schema2, builtInTypesSchema, wsdlImportSchema);
        expect(wsdlImportSchema.getNamespace()).andReturn("").anyTimes();
        expect(wsdlImportSchema.getComponent()).andReturn(null).anyTimes();
        
        expect(modelRoot.getDescription()).andReturn(description).anyTimes();
        
        final ISchema aReferredSchema = createNiceMock(ISchema.class);
        expect(aReferredSchema.getNamespace()).andReturn(SOME_NAMESPACE).anyTimes();
        referredSchemas.add(aReferredSchema);
        
        expect(builtInTypesSchema.getNamespace()).andReturn(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001).times(2);
        expect(schema1.getAllReferredSchemas()).andReturn(referredSchemas).anyTimes();
        expect(schema1.getLocation()).andReturn(C_LOCATION);
        expect(schema2.getAllReferredSchemas()).andReturn(referredSchemas).anyTimes();

        expect(description.getContainedSchemas()).andReturn(schemas).anyTimes();
        expect(description.getAllVisibleSchemas()).andReturn(allVisibleSchemas).anyTimes();
        
        expect(controller.getTreeNodeMapper()).andReturn(nodeMapper).once();
        replay(modelRoot, description, controller, schema1, schema2, builtInTypesSchema, aReferredSchema, wsdlImportSchema);
        
        final Object[] newElements = provider.getElements(modelRoot);
        
        assertEquals(3, newElements.length);
        assertTrue(newElements[0] instanceof INamespaceNode);
        assertTrue(newElements[1] instanceof INamespaceNode);
        assertTrue(newElements[2] instanceof IImportedTypesNode);
        assertEquals(elements[0], newElements[0]);
        assertEquals(elements[1], newElements[1]);
        assertNotNull(nodeMapper.getTreeNode(schema1));
        assertNotNull(nodeMapper.getTreeNode(schema2));
        assertNotNull(nodeMapper.getTreeNode(description));
        
        assertEquals(0, provider.getChildren(new Object()).length);
    }

    private DataTypesContentProvider createContentProvider() {
        final IDataTypesFormPageController controller = createMock(IDataTypesFormPageController.class);
        final DataTypesContentProvider provider = new DataTypesContentProvider(controller);
        return provider;
    }
}
