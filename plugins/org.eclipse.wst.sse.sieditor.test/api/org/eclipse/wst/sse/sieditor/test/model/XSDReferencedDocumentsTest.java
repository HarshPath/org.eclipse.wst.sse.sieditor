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

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;

@SuppressWarnings("nls")
public class XSDReferencedDocumentsTest extends SIEditorBaseTest {

    protected String getProjectName() {
        return "XSDReferencedDocumentsTest";
    }

    @Test
    public void testExternalImportedDocuments() throws Exception {
        /*
         * Check cases for below Schema - Referred schemas SimpleType - baseType
         * - both for global and anonymous types in element and attribute
         * ComplextType - baseType - restriction, extension , Simple Content
         * Element - ref, type Attribute - ref, type
         */

        IFile file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "po.xsd", Document_FOLDER_NAME,
                this.getProject(), "po.xsd");
        assertTrue(null != file && file.exists());
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "example.xsd", Document_FOLDER_NAME,
                this.getProject(), "example.xsd");
        assertTrue(null != file && file.exists());
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "Included.xsd", Document_FOLDER_NAME,
                this.getProject(), "Included.xsd");
        assertTrue(null != file && file.exists());

        // check for referenced documents
        final IWsdlModelRoot modelRoot = getModelRoot("TypesExternalImporting.wsdl");
        final IDescription description = modelRoot.getDescription();
        assertNotNull(description);

        ISchema schema = description.getSchema("http://www.example.org/TypesImporting/")[0];
        if (schema.getAllReferredSchemas().size() != 4) {
            schema = description.getSchema("http://www.example.org/TypesImporting/")[1];
        }
        final Collection<ISchema> referredSchemas = schema.getAllReferredSchemas();
        assertEquals(4, referredSchemas.size());
        final ISchema[] schemas = referredSchemas.toArray(new ISchema[4]);
        ArrayList<String> importedNamespaces = new ArrayList<String>();
        importedNamespaces.add("http://www.example.com/");
        importedNamespaces.add("http://www.example.com/IPO");
        importedNamespaces.add("http://www.example.org/TypesImporting/");
        importedNamespaces.add("http://www.w3.org/2001/XMLSchema");
        for (ISchema refSchema : schemas) {
            assertNotNull(importedNamespaces.remove(refSchema.getNamespace()));
        }
        assertTrue("Not all referred documents are returned (" + importedNamespaces + ")", 0 == importedNamespaces.size());

        ISimpleType simpleType = (ISimpleType) schema.getType(false, "simpleType");
        assertTrue("BaseType is null", null != simpleType.getBaseType());
        assertEquals("SKU", simpleType.getBaseType().getName());
        assertEquals("http://www.example.com/IPO", simpleType.getBaseType().getNamespace());

        IStructureType structureType = (IStructureType) schema.getType(false, "complexType");
        Collection<IElement> elements = structureType.getElements("elementParticle");
        IElement element = elements.iterator().next();
        structureType = (IStructureType) element.getType();
        assertEquals("Address", structureType.getName());
        assertEquals("http://www.example.com/", structureType.getNamespace());
        structureType = (IStructureType) schema.getType(true, "complexType");
        elements = structureType.getElements("elementParticle");
        element = elements.iterator().next();
        assertEquals("Address", element.getType().getName());
        assertEquals("http://www.example.com/", element.getType().getNamespace());
        elements = structureType.getElements("comment");
        element = elements.iterator().next();
        structureType = (IStructureType) element.getType();
        assertNotNull("Type must not be null for referenced Element Declaration", structureType);
        assertEquals("comment", structureType.getName());

        structureType = (IStructureType) schema.getType(false, "ctExtension");
        assertTrue("BaseType is null", null != structureType.getBaseType());
        assertEquals("Items", structureType.getBaseType().getName());
        assertEquals("http://www.example.com/", structureType.getBaseType().getNamespace());
        elements = structureType.getElements("comment");
        element = elements.iterator().next();
        structureType = (IStructureType) element.getType();

        structureType = (IStructureType) schema.getType(false, "ctRestriction");
        elements = structureType.getElements("elementParticle");
        element = elements.iterator().next();
        simpleType = (ISimpleType) element.getType();
        assertTrue("BaseType is null", null != simpleType.getBaseType());
        assertEquals("SKU", simpleType.getBaseType().getName());
        assertEquals("http://www.example.com/IPO", simpleType.getBaseType().getNamespace());

        structureType = (IStructureType) schema.getType(false, "ctSimpleContentExtension");
        simpleType = (ISimpleType) structureType.getBaseType();
        assertTrue("BaseType is null", null != simpleType);
        assertEquals("SKU", simpleType.getName());
        assertEquals("http://www.example.com/", simpleType.getNamespace());
        elements = structureType.getElements("globalAttribute");
        element = elements.iterator().next();
        assertEquals("globalAttribute", element.getName());
        elements = structureType.getElements("attribute1");
        element = elements.iterator().next();
        assertEquals("SKU", element.getType().getName());
        assertEquals("http://www.example.com/", element.getType().getNamespace());
        elements = structureType.getElements("attribute2");
        element = elements.iterator().next();
        assertEquals("SKU", element.getType().getName());
        assertEquals("http://www.example.com/IPO", element.getType().getNamespace());

        structureType = (IStructureType) schema.getType(false, "ctSimpleContentRestriction");
        elements = structureType.getElements("attribute1");
        element = elements.iterator().next();
        assertEquals("attribute1", element.getName());
        simpleType = (ISimpleType) element.getType().getBaseType();
        assertTrue("BaseType is null", null != simpleType);
        assertEquals("SKU", simpleType.getName());
        assertEquals("http://www.example.com/", simpleType.getNamespace());

        structureType = (IStructureType) schema.getType(true, "globalElementWithElements");
        elements = structureType.getElements("elementParticle");
        element = elements.iterator().next();
        structureType = (IStructureType) element.getType();
        elements = structureType.getElements("globalAttributeDeclaration");
        element = elements.iterator().next();
        assertEquals("globalAttributeDeclaration", element.getName());
        assertNull("Type for referred attribute must be null", element.getType());

        structureType = (IStructureType) schema.getType(false, "ctImportedExtension");
        structureType = (IStructureType) structureType.getBaseType();
        assertTrue("BaseType is null", null != structureType);
        assertEquals("Address", structureType.getName());
        assertEquals("http://www.example.com/", structureType.getNamespace());
        structureType = (IStructureType) schema.getType(false, "ctImportedRestriction");
        structureType = (IStructureType) structureType.getBaseType();
        assertTrue("BaseType is null", null != structureType);
        assertEquals("Address", structureType.getName());
        assertEquals("http://www.example.com/IPO", structureType.getNamespace());
    }

    @Test
    public void testInternalImportedDocuments() throws Exception {
        /*
         * Check cases for below Schema - Referred schemas SimpleType - baseType
         * - both for global and anonymous types in element and attribute
         * ComplextType - baseType - restriction, extension , Simple Content
         * Element - ref, type Attribute - ref, type
         */

        // check for referenced documents
        final IWsdlModelRoot modelRoot = getModelRoot("TypesInternalImporting.wsdl");
        final IDescription description = modelRoot.getDescription();
        assertNotNull(description);

        final ISchema[] refSchemas = description.getSchema("http://www.example.org/TypesImporting/");
        assertEquals(2, refSchemas.length);
        ISchema schema = refSchemas[0].getAllReferredSchemas().size() == 4 ? refSchemas[0] : refSchemas[1];
        final Collection<ISchema> referredSchemas = schema.getAllReferredSchemas();
        assertEquals(4, referredSchemas.size());
        final ISchema[] schemas = referredSchemas.toArray(new ISchema[4]);
        ArrayList<String> importedNamespaces = new ArrayList<String>();
        importedNamespaces.add("http://www.example.com/");
        importedNamespaces.add("http://www.example.com/IPO");
        importedNamespaces.add("http://www.example.org/TypesImporting/");
        importedNamespaces.add("http://www.w3.org/2001/XMLSchema");
        for (ISchema refSchema : schemas) {
            assertTrue(importedNamespaces.remove(refSchema.getNamespace()));
        }
        assertTrue("Not all referred documents are returned (" + importedNamespaces + ")", 0 == importedNamespaces.size());

        ISimpleType simpleType = (ISimpleType) schema.getType(false, "simpleType");
        assertTrue("BaseType is null", null != simpleType.getBaseType());
        assertEquals("SKU", simpleType.getBaseType().getName());
        assertEquals("http://www.example.com/IPO", simpleType.getBaseType().getNamespace());

        IStructureType structureType = (IStructureType) schema.getType(false, "complexType");
        Collection<IElement> elements = structureType.getElements("elementParticle");
        IElement element = elements.iterator().next();
        structureType = (IStructureType) element.getType();
        assertEquals("Address", structureType.getName());
        assertEquals("http://www.example.com/", structureType.getNamespace());
        structureType = (IStructureType) schema.getType(true, "complexType");
        elements = structureType.getElements("elementParticle");
        element = elements.iterator().next();
        assertEquals("Address", element.getType().getName());
        assertEquals("http://www.example.com/", element.getType().getNamespace());
        elements = structureType.getElements("comment");
        element = elements.iterator().next();
        structureType = (IStructureType) element.getType();
        assertNotNull("Type must not be null for referenced Element Declaration", structureType);
        assertEquals("comment", structureType.getName());

        structureType = (IStructureType) schema.getType(false, "ctExtension");
        assertTrue("BaseType is null", null != structureType.getBaseType());
        assertEquals("Items", structureType.getBaseType().getName());
        assertEquals("http://www.example.com/", structureType.getBaseType().getNamespace());
        elements = structureType.getElements("comment");
        element = elements.iterator().next();
        structureType = (IStructureType) element.getType();

        structureType = (IStructureType) schema.getType(false, "ctRestriction");
        elements = structureType.getElements("elementParticle");
        element = elements.iterator().next();
        simpleType = (ISimpleType) element.getType();
        assertTrue("BaseType is null", null != simpleType.getBaseType());
        assertEquals("SKU", simpleType.getBaseType().getName());
        assertEquals("http://www.example.com/IPO", simpleType.getBaseType().getNamespace());

        structureType = (IStructureType) schema.getType(false, "ctSimpleContentExtension");
        simpleType = (ISimpleType) structureType.getBaseType();
        assertTrue("BaseType is null", null != simpleType);
        assertEquals("SKU", simpleType.getName());
        assertEquals("http://www.example.com/", simpleType.getNamespace());
        elements = structureType.getElements("globalAttribute");
        element = elements.iterator().next();
        assertEquals("globalAttribute", element.getName());
        elements = structureType.getElements("attribute1");
        element = elements.iterator().next();
        assertEquals("SKU", element.getType().getName());
        assertEquals("http://www.example.com/", element.getType().getNamespace());
        elements = structureType.getElements("attribute2");
        element = elements.iterator().next();
        assertEquals("SKU", element.getType().getName());
        assertEquals("http://www.example.com/IPO", element.getType().getNamespace());

        structureType = (IStructureType) schema.getType(false, "ctSimpleContentRestriction");
        elements = structureType.getElements("attribute1");
        element = elements.iterator().next();
        assertEquals("attribute1", element.getName());
        simpleType = (ISimpleType) element.getType().getBaseType();
        assertTrue("BaseType is null", null != simpleType);
        assertEquals("SKU", simpleType.getName());
        assertEquals("http://www.example.com/", simpleType.getNamespace());

        structureType = (IStructureType) schema.getType(true, "globalElementWithElements");
        elements = structureType.getElements("elementParticle");
        element = elements.iterator().next();
        structureType = (IStructureType) element.getType();
        elements = structureType.getElements("globalAttributeDeclaration");
        element = elements.iterator().next();
        assertEquals("globalAttributeDeclaration", element.getName());
        assertNull("Type for referred attribute must be null", element.getType());

        structureType = (IStructureType) schema.getType(false, "ctImportedExtension");
        structureType = (IStructureType) structureType.getBaseType();
        assertTrue("BaseType is null", null != structureType);
        assertEquals("Address", structureType.getName());
        assertEquals("http://www.example.com/", structureType.getNamespace());
        structureType = (IStructureType) schema.getType(false, "ctImportedRestriction");
        structureType = (IStructureType) structureType.getBaseType();
        assertTrue("BaseType is null", null != structureType);
        assertEquals("Address", structureType.getName());
        assertEquals("http://www.example.com/IPO", structureType.getNamespace());
    }

    @Test
    public void testExternalIncludedDocuments() throws Exception {
        /*
         * Check cases for below Schema - Referred schemas SimpleType - baseType
         * - both for global and anonymous types in element and attribute
         * ComplextType - baseType - restriction, extension , Simple Content
         * Element - ref, type Attribute - ref, type
         */

        // check for referenced documents
        final IWsdlModelRoot modelRoot = getModelRoot("TypesExternalIncluding.wsdl");
        final IDescription description = modelRoot.getDescription();
        assertNotNull(description);

        final ISchema[] refSchemas = description.getSchema("http://www.example.org/TypesImporting/");
        assertEquals(2, refSchemas.length);
        ISchema schema = refSchemas[0];
        if (schema.getType(false, "simpleType") == null) {
            schema = refSchemas[1];
        }

        final Collection<ISchema> referredSchemas = schema.getAllReferredSchemas();
        assertEquals(2, referredSchemas.size());
        final ISchema[] schemas = referredSchemas.toArray(new ISchema[2]);
        ArrayList<String> importedNamespaces = new ArrayList<String>();
        importedNamespaces.add("http://www.example.org/TypesImporting/");
        importedNamespaces.add("http://www.w3.org/2001/XMLSchema");
        for (ISchema refSchema : schemas) {
            assertTrue(importedNamespaces.remove(refSchema.getNamespace()));
        }
        assertTrue("Not all referred documents are returned (" + importedNamespaces + ")", 0 == importedNamespaces.size());

        ISimpleType simpleType = (ISimpleType) schema.getType(false, "simpleType");
        assertTrue("BaseType is null", null != simpleType.getBaseType());
        assertEquals("SKU", simpleType.getBaseType().getName());
        assertEquals("http://www.example.org/TypesImporting/", simpleType.getBaseType().getNamespace());

        IStructureType structureType = (IStructureType) schema.getType(true, "globalElementWithSt");
        assertTrue("BaseType is null", null != structureType.getBaseType());
        assertEquals("SKU", simpleType.getBaseType().getName());
        assertEquals("http://www.example.org/TypesImporting/", simpleType.getBaseType().getNamespace());

        structureType = (IStructureType) schema.getType(false, "complexType");
        Collection<IElement> elements = structureType.getElements("attribute1");
        IElement element = elements.iterator().next();
        simpleType = (ISimpleType) element.getType();
        assertEquals("SKU", simpleType.getName());
        assertEquals("http://www.example.org/TypesImporting/", simpleType.getNamespace());
        elements = structureType.getElements("elementParticle1");
        element = elements.iterator().next();
        simpleType = (ISimpleType) element.getType();
        assertEquals("SKU", simpleType.getName());
        assertEquals("http://www.example.org/TypesImporting/", simpleType.getNamespace());
        elements = structureType.getElements("comment");
        element = elements.iterator().next();
        assertEquals("comment", element.getName());
        elements = structureType.getElements("attribute2");
        element = elements.iterator().next();
        simpleType = (ISimpleType) element.getType();
        assertEquals("SKU", simpleType.getBaseType().getName());
        assertEquals("http://www.example.org/TypesImporting/", simpleType.getBaseType().getNamespace());

        structureType = (IStructureType) schema.getType(false, "ctImportedExtension");
        assertEquals("Address", structureType.getBaseType().getName());
        assertEquals("http://www.example.org/TypesImporting/", structureType.getBaseType().getNamespace());
        structureType = (IStructureType) schema.getType(false, "ctImportedRestriction");
        assertEquals("Address", structureType.getBaseType().getName());
        assertEquals("http://www.example.org/TypesImporting/", structureType.getBaseType().getNamespace());

        structureType = (IStructureType) schema.getType(false, "ctSimpleContentExtension");
        assertEquals("SKU", simpleType.getBaseType().getName());
        assertEquals("http://www.example.org/TypesImporting/", simpleType.getBaseType().getNamespace());
        elements = structureType.getElements("globalAttributeDeclaration");
        element = elements.iterator().next();
        assertEquals("globalAttributeDeclaration", element.getName());
    }

    @Test
    public void testInternalIncludedDocuments() throws Exception {
        /*
         * Check cases for below Schema - Referred schemas SimpleType - baseType
         * - both for global and anonymous types in element and attribute
         * ComplextType - baseType - restriction, extension , Simple Content
         * Element - ref, type Attribute - ref, type
         */

        // check for referenced documents
        final IWsdlModelRoot modelRoot = getModelRoot("TypesInternalIncluding.wsdl");
        final IDescription description = modelRoot.getDescription();
        assertNotNull(description);

        final ISchema[] refSchemas = description.getSchema("http://www.example.org/TypesImporting/");
        assertEquals(2, refSchemas.length);
        ISchema schema = refSchemas[0].getType(false, "simpleType") == null ? refSchemas[1] : refSchemas[0];
        final Collection<ISchema> referredSchemas = schema.getAllReferredSchemas();
        assertEquals(2, referredSchemas.size());
        final ISchema[] schemas = referredSchemas.toArray(new ISchema[2]);
        ArrayList<String> importedNamespaces = new ArrayList<String>();
        importedNamespaces.add("http://www.example.org/TypesImporting/");
        importedNamespaces.add("http://www.w3.org/2001/XMLSchema");
        for (ISchema refSchema : schemas) {
            assertTrue(importedNamespaces.remove(refSchema.getNamespace()));
        }
        assertTrue("Not all referred documents are returned (" + importedNamespaces + ")", 0 == importedNamespaces.size());

        ISimpleType simpleType = (ISimpleType) schema.getType(false, "simpleType");
        assertTrue("BaseType is null", null != simpleType.getBaseType());
        assertEquals("SKU", simpleType.getBaseType().getName());
        assertEquals("http://www.example.org/TypesImporting/", simpleType.getBaseType().getNamespace());

        IStructureType structureType = (IStructureType) schema.getType(true, "globalElementWithSt");
        assertTrue("BaseType is null", null != structureType.getBaseType());
        assertEquals("SKU", simpleType.getBaseType().getName());
        assertEquals("http://www.example.org/TypesImporting/", simpleType.getBaseType().getNamespace());

        structureType = (IStructureType) schema.getType(false, "complexType");
        Collection<IElement> elements = structureType.getElements("attribute1");
        IElement element = elements.iterator().next();
        simpleType = (ISimpleType) element.getType();
        assertEquals("SKU", simpleType.getName());
        assertEquals("http://www.example.org/TypesImporting/", simpleType.getNamespace());
        elements = structureType.getElements("elementParticle1");
        element = elements.iterator().next();
        simpleType = (ISimpleType) element.getType();
        assertEquals("SKU", simpleType.getName());
        assertEquals("http://www.example.org/TypesImporting/", simpleType.getNamespace());
        elements = structureType.getElements("comment");
        element = elements.iterator().next();
        assertEquals("comment", element.getName());
        elements = structureType.getElements("attribute2");
        element = elements.iterator().next();
        simpleType = (ISimpleType) element.getType();
        assertEquals("SKU", simpleType.getBaseType().getName());
        assertEquals("http://www.example.org/TypesImporting/", simpleType.getBaseType().getNamespace());

        structureType = (IStructureType) schema.getType(false, "ctImportedExtension");
        assertEquals("Address", structureType.getBaseType().getName());
        assertEquals("http://www.example.org/TypesImporting/", structureType.getBaseType().getNamespace());
        structureType = (IStructureType) schema.getType(false, "ctImportedRestriction");
        assertEquals("Address", structureType.getBaseType().getName());
        assertEquals("http://www.example.org/TypesImporting/", structureType.getBaseType().getNamespace());

        structureType = (IStructureType) schema.getType(false, "ctSimpleContentExtension");
        assertEquals("SKU", simpleType.getBaseType().getName());
        assertEquals("http://www.example.org/TypesImporting/", simpleType.getBaseType().getNamespace());
        elements = structureType.getElements("globalAttributeDeclaration");
        element = elements.iterator().next();
        assertEquals("globalAttributeDeclaration", element.getName());
    }

    private IWsdlModelRoot getModelRoot(final String fileName) throws Exception {
        return getWSDLModelRoot(Constants.DATA_PUBLIC_SELF_KESHAV + fileName, fileName);
    }
}