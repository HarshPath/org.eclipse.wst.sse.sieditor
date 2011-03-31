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
import static org.easymock.EasyMock.replay;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.DataTypesDetailsPageProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.ElementNodeDetailsPage;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.IDataTypesFormPageController;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.ITypeDisplayer;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.NamespaceDetailsPage;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.SimpleTypeNodeDetailsPage;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.StructureNodeDetailsPage;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IElementNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.INamespaceNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.ISimpleTypeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.IStructureTypeNode;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;

public class DataTypesDetailsPageProviderTest {

    private INamespaceNode namespaceNode;
    private IElementNode elementNode;
    private ISimpleTypeNode simpleTypeNode;
    private IStructureTypeNode structureTypeNode;

    private DataTypesDetailsPageProvider pageProvider;

    private ITypeDisplayer typeDisplayer;
    private IDataTypesFormPageController controller;

    private IStructureType modelObject;

    private java.util.Map<String, org.eclipse.ui.forms.IDetailsPage> pagesMap;

    @Before
    public void setUp() {
        namespaceNode = createMock(INamespaceNode.class);
        elementNode = createMock(IElementNode.class);
        simpleTypeNode = createMock(ISimpleTypeNode.class);
        structureTypeNode = createMock(IStructureTypeNode.class);
        typeDisplayer = createMock(ITypeDisplayer.class);
        controller = createMock(IDataTypesFormPageController.class);
        modelObject = createMock(IStructureType.class);

        pageProvider = new DataTypesDetailsPageProvider(controller, typeDisplayer) {
            @Override
            protected java.util.Map<String, org.eclipse.ui.forms.IDetailsPage> getPages() {
                pagesMap = super.getPages();
                return pagesMap;
            }
        };
    }

    @Test
    public void getPageKey() {
        assertEquals(DataTypesDetailsPageProvider.NAMESPACE_KEY, pageProvider.getPageKey(namespaceNode));
        assertEquals(DataTypesDetailsPageProvider.SIMPLE_TYPE_KEY, pageProvider.getPageKey(simpleTypeNode));
        assertEquals(DataTypesDetailsPageProvider.ELEMENT_KEY, pageProvider.getPageKey(elementNode));
    }
    
    @Test
    public void getPageKey_StructureType_IsElement() {
        expect(structureTypeNode.getModelObject()).andReturn(modelObject).anyTimes();
        expect(modelObject.isElement()).andReturn(false);
        replay(structureTypeNode, modelObject);
        assertEquals(DataTypesDetailsPageProvider.COMPLEX_TYPE_KEY, pageProvider.getPageKey(structureTypeNode));
    }
    
    @Test
    public void getPage_Key_StructureType_NotElement() {
        expect(structureTypeNode.getModelObject()).andReturn(modelObject);
        expect(modelObject.isElement()).andReturn(true);
        replay(structureTypeNode, modelObject);
        assertEquals(DataTypesDetailsPageProvider.ELEMENT_KEY, pageProvider.getPageKey(structureTypeNode));
    }

    @Test
    public void getPage() {
        assertNull(pagesMap);
        
        //test the namespace page
        IDetailsPage page = pageProvider.getPage(DataTypesDetailsPageProvider.NAMESPACE_KEY);
        assertTrue(page instanceof NamespaceDetailsPage);
        assertNotNull(pagesMap);
        assertNotNull(pagesMap.get(DataTypesDetailsPageProvider.NAMESPACE_KEY));
        assertSame(page, pagesMap.get(DataTypesDetailsPageProvider.NAMESPACE_KEY));

        //test the simple type page
        page = pageProvider.getPage(DataTypesDetailsPageProvider.SIMPLE_TYPE_KEY); 
        assertTrue(page instanceof SimpleTypeNodeDetailsPage);
        assertNotNull(pagesMap);
        assertNotNull(pagesMap.get(DataTypesDetailsPageProvider.SIMPLE_TYPE_KEY));
        assertSame(page, pagesMap.get(DataTypesDetailsPageProvider.SIMPLE_TYPE_KEY));
        
        //test the element type page
        page = pageProvider.getPage(DataTypesDetailsPageProvider.ELEMENT_KEY);
        assertTrue(page instanceof ElementNodeDetailsPage);
        assertNotNull(pagesMap);
        assertNotNull(pagesMap.get(DataTypesDetailsPageProvider.ELEMENT_KEY));
        assertSame(page, pagesMap.get(DataTypesDetailsPageProvider.ELEMENT_KEY));
        
        //test the structure type page
        page = pageProvider.getPage(DataTypesDetailsPageProvider.COMPLEX_TYPE_KEY);
        assertTrue(page instanceof StructureNodeDetailsPage);
        assertNotNull(pagesMap);
        assertNotNull(pagesMap.get(DataTypesDetailsPageProvider.COMPLEX_TYPE_KEY));
        assertSame(page, pagesMap.get(DataTypesDetailsPageProvider.COMPLEX_TYPE_KEY));
    }

}
