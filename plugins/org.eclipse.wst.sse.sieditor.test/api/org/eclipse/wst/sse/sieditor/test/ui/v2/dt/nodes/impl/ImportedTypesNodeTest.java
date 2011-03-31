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
package org.eclipse.wst.sse.sieditor.test.ui.v2.dt.nodes.impl;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.ImportedSchemaNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.ImportedTypesNode;
import org.eclipse.wst.sse.sieditor.ui.v2.factory.TreeNodeMapper;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.test.util.EasymockModelUtils;
import org.eclipse.xsd.util.XSDConstants;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.ui.Activator;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;

public class ImportedTypesNodeTest {

    private ImportedTypesNode node;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
        IDescription description = EasymockModelUtils.createDefinitionMockFromSameModel();
        replay(description);
        node = new ImportedTypesNode(description, new TreeNodeMapper());
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetDisplayName() {
        assertEquals(Messages.ImportedTypesNode_node_name, node.getDisplayName());
    }

    @Test
    public void testGetImage() {
        final Display display = Display.getDefault();
        final Image importedTypesNodeImage = new Image(display, 10, 10);

        final ImportedTypesNode node = new ImportedTypesNode(createMock(IDescription.class), createMock(ITreeNode.class),
                new TreeNodeMapper()) {
            protected ImageRegistry getImageRegistry() {
                final ImageRegistry registry = new ImageRegistry(display);
                registry.put(Activator.NODE_IMPORTED_TYPES, importedTypesNodeImage);
                return registry;
            }
        };
        assertEquals(importedTypesNodeImage, node.getImage());
    }

    @Test
    public void testGetChildren() {
        IDescription description = EasymockModelUtils.createDefinitionMockFromSameModel();
        
        final List<ISchema> schemas = new ArrayList<ISchema>();
        final ISchema schema1 = createMock(ISchema.class);
        final ISchema schema2 = createMock(ISchema.class);
        final ISchema wsdlImportSchema = createNiceMock(ISchema.class);
        final List<ISchema> allVisibleSchemas = new ArrayList<ISchema>();
        allVisibleSchemas.add(wsdlImportSchema);
        
        final ISchema builtInTypesSchema = createMock(ISchema.class);
        expect(builtInTypesSchema.getNamespace()).andReturn(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001).times(2);
        
        final Collection<ISchema> referredSchemas = new ArrayList<ISchema>();
        referredSchemas.add(builtInTypesSchema);
        expect(schema1.getAllReferredSchemas()).andReturn(referredSchemas);
        expect(schema2.getAllReferredSchemas()).andReturn(referredSchemas);
        
        schemas.add(schema1);
        schemas.add(schema2);
        expect(description.getContainedSchemas()).andReturn(schemas).anyTimes();
        expect(description.getAllVisibleSchemas()).andReturn(allVisibleSchemas).anyTimes();
        
        replay(description, schema1, schema2, builtInTypesSchema, wsdlImportSchema);
        
        node = new ImportedTypesNode(description, new TreeNodeMapper());
        Object[] children = node.getChildren();
        
        verify(description);
        verify(schema1);
        verify(schema2);
        verify(builtInTypesSchema);
        assertEquals(1, children.length);
        
        //
        reset(schema1, schema2, builtInTypesSchema, wsdlImportSchema);
        description = EasymockModelUtils.createDefinitionMockFromSameModel();
        
        final ISchema aReferredSchema = EasymockModelUtils.createISchemaMockFromSameModel();
        expect(aReferredSchema.getNamespace()).andReturn("someNamespace").times(2);  //$NON-NLS-1$
        referredSchemas.add(aReferredSchema);
        
        expect(builtInTypesSchema.getNamespace()).andReturn(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001).times(2);
        expect(schema1.getAllReferredSchemas()).andReturn(referredSchemas).once();
        expect(schema2.getAllReferredSchemas()).andReturn(referredSchemas).once();

        expect(description.getContainedSchemas()).andReturn(schemas).anyTimes();
        expect(description.getAllVisibleSchemas()).andReturn(allVisibleSchemas).anyTimes();
        replay(description, schema1, schema2, builtInTypesSchema, aReferredSchema, wsdlImportSchema);
        node = new ImportedTypesNode(description, new TreeNodeMapper());
        
        children = node.getChildren();
        
        verify(description, schema1, schema2, builtInTypesSchema, aReferredSchema);
        assertEquals(2, children.length);
        assertTrue(children[0] instanceof ImportedSchemaNode);
    }

    @Test
    public void testIsReadOnly() {
        assertTrue(node.isReadOnly());
    }

    @Test
    public void testGetParent() {
        assertNull(node.getParent());
    }

    @Test
    public void testGetModelObject() {
        assertTrue(node.getModelObject() instanceof IDescription);
    }
    
}
