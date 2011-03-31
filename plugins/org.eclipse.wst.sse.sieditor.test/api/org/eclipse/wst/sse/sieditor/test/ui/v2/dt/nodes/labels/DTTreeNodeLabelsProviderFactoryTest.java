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
package org.eclipse.wst.sse.sieditor.test.ui.v2.dt.nodes.labels;

import static org.junit.Assert.assertTrue;

import org.easymock.EasyMock;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IElementNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.ISimpleTypeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IStructureTypeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.ElementNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.ImportedSchemaNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.ImportedTypesNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.SimpleTypeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.StructureTypeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.labels.DTTreeNodeLabelsProviderFactory;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.labels.provider.ElementNodeLabelsProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.labels.provider.ImportedSchemaNodeLabelsProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.labels.provider.ImportedTypesNodeLabelsProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.labels.provider.SimpleTypeNodeLabelsProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.labels.provider.StructureTypeNodeLabelsProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.labels.provider.DefaultNodeLabelsProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.labels.provider.NullNodeLabelsProvider;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;

public class DTTreeNodeLabelsProviderFactoryTest {

    private final DTTreeNodeLabelsProviderFactory factory = DTTreeNodeLabelsProviderFactory.instance();

    private ISchema iSchema;
    private IElement iElement;
    private IStructureType iStructureType;
    private ISimpleType iSimpleType;

    private ImportedTypesNode importedTypesNode;
    private ImportedSchemaNode importedSchemaNode;
    private ISimpleTypeNode simpleTypeNode;
    private IStructureTypeNode structureTypeNode;
    private IElementNode elementNode;
    private IElementNode elementNodeWithNullModelObject;

    @Before
    public void setUp() {
        iSchema = EasyMock.createNiceMock(ISchema.class);
        iElement = EasyMock.createNiceMock(IElement.class);
        iStructureType = EasyMock.createNiceMock(IStructureType.class);
        iSimpleType = EasyMock.createNiceMock(ISimpleType.class);

        importedTypesNode = new ImportedTypesNode(null, null);
        importedSchemaNode = new ImportedSchemaNode(iSchema, null, null);
        simpleTypeNode = new SimpleTypeNode(iSimpleType, null);
        structureTypeNode = new StructureTypeNode(iStructureType, null, null);
        elementNode = new ElementNode(iElement, null, null);
        elementNodeWithNullModelObject = new ElementNode(null, null, null);
    }

    @Test
    public void getLabelsProviderForTreeNode() {
        assertTrue(factory.getLabelsProvider(importedTypesNode) instanceof ImportedTypesNodeLabelsProvider);
        assertTrue(factory.getLabelsProvider(importedSchemaNode) instanceof ImportedSchemaNodeLabelsProvider);
        assertTrue(factory.getLabelsProvider(simpleTypeNode) instanceof SimpleTypeNodeLabelsProvider);
        assertTrue(factory.getLabelsProvider(structureTypeNode) instanceof StructureTypeNodeLabelsProvider);
        assertTrue(factory.getLabelsProvider(elementNode) instanceof ElementNodeLabelsProvider);
        assertTrue(factory.getLabelsProvider(elementNodeWithNullModelObject) instanceof NullNodeLabelsProvider);
    }

    @Test
    public void getLabelsProviderForModelObject() {
        assertTrue(factory.getLabelsProvider(iSchema) instanceof DefaultNodeLabelsProvider);
        assertTrue(factory.getLabelsProvider(iSimpleType) instanceof SimpleTypeNodeLabelsProvider);
        assertTrue(factory.getLabelsProvider(iStructureType) instanceof StructureTypeNodeLabelsProvider);
        assertTrue(factory.getLabelsProvider(iElement) instanceof ElementNodeLabelsProvider);
        assertTrue(factory.getLabelsProvider((IModelObject) null) instanceof NullNodeLabelsProvider);
    }

}
