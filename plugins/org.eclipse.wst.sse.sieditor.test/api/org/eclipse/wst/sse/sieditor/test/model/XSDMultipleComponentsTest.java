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
package org.eclipse.wst.sse.sieditor.test.model;

import java.util.Collection;

import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

@SuppressWarnings("nls")
public class XSDMultipleComponentsTest extends SIEditorBaseTest {

    protected String getProjectName() {
        return "XSDMultipleComponentsTest";
    }

    @Test
    public void testMultipleComponents() throws Exception {
        // check for referenced documents
        final IWsdlModelRoot modelRoot = getModelRoot("TypesMultipleComponents.wsdl");
        final IDescription description = modelRoot.getDescription();
        assertNotNull(description);

        final ISchema[] refSchemas = description.getSchema("http://www.example.org/StructureType/");
        assertEquals(2, refSchemas.length);
        ISchema schema = refSchemas[0].getAllContainedTypes().size() == 17 ? refSchemas[0] : refSchemas[1];

        final Collection<IType> types = schema.getAllContainedTypes();
        assertEquals(17, types.size());

        ISimpleType simpleType = (ISimpleType) schema.getType(false, "simpleType");
        assertNotNull("Type is null", simpleType);

        IStructureType structureType = (IStructureType) schema.getType(false, "complexType");
        assertNotNull("Type is null", structureType);
        structureType = (IStructureType) schema.getType(true, "complexType");
        assertNotNull("Type is null", structureType);

        assertNotNull("Type is null", schema.getType(false, "sameType"));

        structureType = (IStructureType) schema.getType(false, "ctExtension");
        Collection<IElement> elements = structureType.getElements("complexType");
        assertEquals(2, elements.size());
        IElement[] elementsArray = elements.toArray(new IElement[2]);
        IElement element = elementsArray[0];
        assertNotNull("Type is null", element.getType());
        element = elementsArray[1];
        assertNotNull("Type is null", element.getType());
        elements = structureType.getElements("elementParticle1");
        assertEquals(2, elements.size());
        elementsArray = elements.toArray(new IElement[2]);
        element = elementsArray[0];
        assertNotNull("Type is null", element.getType());
        assertTrue("Type is not StructureType", element.getType() instanceof IStructureType);
        element = elementsArray[1];
        assertNotNull("Type is null", element.getType());
        assertTrue("Type is not SimpleType", element.getType() instanceof ISimpleType);
        elements = structureType.getElements("elementParticle4");
        element = elements.iterator().next();
        assertNotNull("Type is null", element.getType());
        assertEquals("sameType", element.getType().getName());
        elements = structureType.getElements("attribute1");
        assertEquals(2, elements.size());
        elementsArray = elements.toArray(new IElement[2]);
        element = elementsArray[0];
        assertNotNull("Type is null", element.getType());
        assertTrue("Type is not SimpleType", element.getType() instanceof ISimpleType);
        element = elementsArray[1];
        assertNotNull("Type is null", element.getType());
        assertTrue("Type is not SimpleType", element.getType() instanceof ISimpleType);
        elements = structureType.getElements("globalAttribute1");
        assertEquals(2, elements.size());
        elementsArray = elements.toArray(new IElement[2]);
        element = elementsArray[0];
        assertNull("Type is not null", element.getType());
        element = elementsArray[1];
        assertNull("Type is not null", element.getType());
    }

    @Test
    public void testNoComponents() throws Exception {
        // check for referenced documents
        final IWsdlModelRoot modelRoot = getModelRoot("TypesMultipleComponents.wsdl");
        final IDescription description = modelRoot.getDescription();
        assertNotNull(description);

        ISchema[] refSchemas = description.getSchema("http://www.example.org/StructureType/NoComponents");
        assertEquals(1, refSchemas.length);
        ISchema schema = refSchemas[0];
        Collection<IType> types = schema.getAllContainedTypes();
        assertEquals(0, types.size());
        refSchemas = description.getSchema("http://www.example.org/StructureType/NoElements");
        assertEquals(1, refSchemas.length);
        schema = refSchemas[0];
        types = schema.getAllContainedTypes();
        assertEquals(2, types.size());
        IStructureType structureType = (IStructureType) schema.getType(false, "complexTypeWithModelGroup");
        assertEquals(0, structureType.getAllElements().size());
        structureType = (IStructureType) schema.getType(false, "complexTypeWithNoModelGroup");
        assertEquals(0, structureType.getAllElements().size());
    }

    private IWsdlModelRoot getModelRoot(final String fileName) throws Exception {
        return getWSDLModelRoot(Constants.DATA_PUBLIC_SELF_KESHAV + fileName, fileName);
    }

}