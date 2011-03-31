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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDDerivationMethod;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.mm.ModelManager;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.generic.IllegalInputException;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.write.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.write.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.write.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.write.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.StructureType;

/*
 * Those tests should be removed after the xsd write API gets removed.
 */
@SuppressWarnings("nls")
public class SetAPIXSDWriteTest extends SIEditorBaseTest {

    private static final String SOURCE_FOLDER = "src/wsdl";

    protected String getProjectName() {
        return "XSDWriteTest";
    }

    @Test
    public void testSchema() throws Exception {
        IWsdlModelRoot modelRoot = copyAndGetModelRoot("PurchaseOrderConfirmation.wsdl", "TestWriteSchema1.wsdl");
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription description;
        org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema schema;
        description = modelRoot.getDescription();
        final ModelManager mm = ModelManager.getInstance();

        description = modelRoot.getDescription();
        schema = description.getSchema("http://sap.com/xi/Purchasing")[0];
        ISchema wSchema = (ISchema) mm.getWriteSupport(schema);
        assertEquals("http://sap.com/xi/Purchasing", schema.getNamespace());
        wSchema.setNamespace("http://sap.com/xi/Purchasing/New");
        assertEquals("http://sap.com/xi/Purchasing/New", schema.getNamespace());
        assertEquals("", schema.getDocumentation());
        wSchema.setDocumentation("Documentation");
        assertEquals("Documentation", schema.getDocumentation());
        wSchema.setDocumentation("NewDocumentation");
        assertEquals("NewDocumentation", schema.getDocumentation());

        // test creation of global type from anonymous
        org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType address = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema
                .getType(false, "Address");
        IElement officeElement = address.getElements("Office").iterator().next();
        wSchema.createGlobalTypeFromAnonymous(officeElement, "Office");
        assertNotNull(schema.getType(false, "Office"));

        IDescription wDescription = (IDescription) mm.getWriteSupport(description);
        wDescription.save();

        modelRoot = getModelRoot("TestWriteSchema1.wsdl");
        description = modelRoot.getDescription();
        schema = description.getSchema("http://sap.com/xi/Purchasing/New")[0];
        assertEquals("NewDocumentation", schema.getDocumentation());

        modelRoot = copyAndGetModelRoot("PurchaseOrderConfirmation.wsdl", "TestWriteSchema2.wsdl");
        description = modelRoot.getDescription();
        wDescription = (IDescription) mm.getWriteSupport(description);
        schema = description.getSchema("http://sap.com/xi/Purchasing")[0];
        wSchema = (ISchema) mm.getWriteSupport(schema);
        org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType simpleType = wSchema.addSimpleType("NewSimpleType");
        assertNotNull("Could not find Newly added type NewSimpleType", schema.getType(false, "NewSimpleType"));
        assertEquals("string", simpleType.getBaseType().getName());
        wSchema.addStructureType("NewStructureType", false);
        assertNotNull("Could not find Newly added type NewStructureType", schema.getType(false, "NewStructureType"));
        assertNotNull("Could not find element PurchaseOrderConfirmationRequest", schema.getType(true,
                "PurchaseOrderConfirmationRequest"));
        wSchema.removeType(schema.getType(true, "PurchaseOrderConfirmationRequest"));
        assertNull("Element PurchaseOrderConfirmationRequest is not deleted", schema.getType(true,
                "PurchaseOrderConfirmationRequest"));
        assertNotNull("Could not find SimpleType ActionCode", schema.getType(false, "ActionCode"));
        wSchema.removeType(schema.getType(false, "ActionCode"));
        assertNull("SimpleType ActionCode is not deleted", schema.getType(false, "ActionCode"));
        assertNotNull("Could not find StructureType Attachment", schema.getType(false, "Attachment"));
        wSchema.removeType(schema.getType(false, "Attachment"));
        assertNull("StructureType Attachment is not deleted", schema.getType(false, "Attachment"));
        wDescription.save();

        modelRoot = getModelRoot("TestWriteSchema2.wsdl");
        description = modelRoot.getDescription();
        schema = description.getSchema("http://sap.com/xi/Purchasing")[0];
        assertNotNull("Could not find Newly added type NewSimpleType", schema.getType(false, "NewSimpleType"));
        assertNotNull("Could not find Newly added type NewStructureType", schema.getType(false, "NewStructureType"));
        assertNull("Element PurchaseOrderConfirmationRequest is not deleted", schema.getType(true,
                "PurchaseOrderConfirmationRequest"));
        assertNull("SimpleType ActionCode is not deleted", schema.getType(false, "ActionCode"));
        assertNull("StructureType Attachment is not deleted", schema.getType(false, "Attachment"));
    }

    @Test
    public void testSimpleType() throws Exception {
        IWsdlModelRoot modelRoot = copyAndGetModelRoot("SimpleType.wsdl", "TestSimpleType.wsdl");
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription description;
        org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema schema;
        ISimpleType wSimpleType;
        org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType simpleType;
        description = modelRoot.getDescription();
        final ModelManager mm = ModelManager.getInstance();

        description = modelRoot.getDescription();
        schema = description.getSchema("http://www.example.org/SimpleType/")[0];
        assertNotNull("Could not find schema http://www.example.org/SimpleType/", schema);

        simpleType = (org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType) schema.getType(false, "stringType");
        assertEquals("stringType", simpleType.getName());
        wSimpleType = (ISimpleType) mm.getWriteSupport(simpleType);
        wSimpleType.setName("NewType");
        assertEquals("NewType", simpleType.getName());
        assertNotNull("Could not find SimpleType NewType", schema.getType(false, "NewType"));
        assertEquals("Documentation", simpleType.getDocumentation());
        wSimpleType.setDocumentation("DocumentationOne");
        assertEquals("DocumentationOne", simpleType.getDocumentation());
        wSimpleType.setDocumentation("NewDocumentation");
        assertEquals("NewDocumentation", simpleType.getDocumentation());
        IDescription wDescription = (IDescription) mm.getWriteSupport(description);
        wDescription.save();

        modelRoot = getModelRoot("TestSimpleType.wsdl");
        description = modelRoot.getDescription();
        schema = description.getSchema("http://www.example.org/SimpleType/")[0];
        simpleType = (org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType) schema.getType(false, "NewType");
        assertEquals("NewType", simpleType.getName());
        assertEquals("NewDocumentation", simpleType.getDocumentation());

        modelRoot = copyAndGetModelRoot("SimpleType.wsdl", "TestSimpleType.wsdl");
        description = modelRoot.getDescription();
        schema = description.getSchema("http://www.example.org/SimpleType/")[0];
        simpleType = (org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType) schema.getType(false, "stringType");
        wSimpleType = (ISimpleType) mm.getWriteSupport(simpleType);
        assertEquals("1", simpleType.getMinLength());
        wSimpleType.setMinLength(2);
        assertEquals("2", simpleType.getMinLength());
        assertEquals("10", simpleType.getMaxLength());
        wSimpleType.setMaxLength(11);
        assertEquals("11", simpleType.getMaxLength());
        assertEquals(2, simpleType.getEnumerations().length);
        assertEquals("value1", simpleType.getEnumerations()[0].getValue());
        assertEquals("value2", simpleType.getEnumerations()[1].getValue());
        wSimpleType.addEnumeration("value4");
        assertEquals(3, simpleType.getEnumerations().length);
        assertEquals("value4", simpleType.getEnumerations()[2].getValue());
        wSimpleType.removeEnumeration(simpleType.getEnumerations()[0]);
        assertEquals(2, simpleType.getEnumerations().length);
        assertEquals(2, simpleType.getPatterns().length);
        assertEquals("value*", simpleType.getPatterns()[0].getValue());
        assertEquals("v*", simpleType.getPatterns()[1].getValue());
        wSimpleType.addPattern("va*");
        assertEquals(3, simpleType.getPatterns().length);
        assertEquals("va*", simpleType.getPatterns()[2].getValue());
        wSimpleType.removePattern(simpleType.getPatterns()[0]);
        assertEquals(2, simpleType.getPatterns().length);

        simpleType = (org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType) schema.getType(false, "intType");
        wSimpleType = (ISimpleType) mm.getWriteSupport(simpleType);
        assertEquals("1", simpleType.getMinLength());
        assertEquals("10", simpleType.getMaxLength());
        wSimpleType.setMaxLength(11);
        assertEquals("11", simpleType.getMaxLength());
        assertEquals("1", simpleType.getMinInclusive());
        wSimpleType.setMinInclusive(4 + "");
        assertEquals("4", simpleType.getMinInclusive());
        assertEquals("1098098988", simpleType.getMaxInclusive());
        wSimpleType.setMaxInclusive("1098098989");
        assertEquals("1098098989", simpleType.getMaxInclusive());
        assertEquals("", simpleType.getDocumentation());
        wSimpleType.setDocumentation("Documentation");
        assertEquals("Documentation", simpleType.getDocumentation());
        wSimpleType.setDocumentation("NewDocumentation");
        assertEquals("NewDocumentation", simpleType.getDocumentation());

        simpleType = (org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType) schema.getType(false, "intType1");
        wSimpleType = (ISimpleType) mm.getWriteSupport(simpleType);
        assertEquals("1", simpleType.getMinLength());
        assertEquals("10", simpleType.getMaxLength());
        wSimpleType.setMaxLength(11);
        assertEquals("11", simpleType.getMaxLength());
        assertEquals("1", simpleType.getMinExclusive());
        wSimpleType.setMinExclusive("4");
        assertEquals("4", simpleType.getMinExclusive());
        assertEquals("1098098988", simpleType.getMaxExclusive());
        wSimpleType.setMaxExclusive("1098098989");
        assertEquals("1098098989", simpleType.getMaxExclusive());
        assertEquals("", simpleType.getDocumentation());
        wSimpleType.setDocumentation("Documentation");
        assertEquals("Documentation", simpleType.getDocumentation());
        wSimpleType.setDocumentation("NewDocumentation");
        assertEquals("NewDocumentation", simpleType.getDocumentation());

        simpleType = (org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType) schema.getType(false, "stringType1");
        wSimpleType = (ISimpleType) mm.getWriteSupport(simpleType);
        assertEquals("10", simpleType.getLength());
        wSimpleType.setLength(11);
        assertEquals("11", simpleType.getLength());

        SchemaNamespaceCondition condition = new SchemaNamespaceCondition("");

        condition.setNamespace(EmfXsdUtils.getSchemaForSchemaNS());
        org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema schemaForSchema = getSchema(description.getAllVisibleSchemas(),
                condition);
        assertEquals("string", simpleType.getBaseType().getName());
        wSimpleType.setBaseType(schemaForSchema.getType(false, "int"));
        assertEquals("int", simpleType.getBaseType().getName());
        wSimpleType.setBaseType(schema.getType(false, "stringType"));
        assertEquals("stringType", simpleType.getBaseType().getName());
        wSimpleType.setBaseType(schema.getType(false, "intType"));
        assertEquals("intType", simpleType.getBaseType().getName());
        wSimpleType.setBaseType(schema.getType(false, "complexType"));
        assertEquals("intType", simpleType.getBaseType().getName());
        wSimpleType.setBaseType(schema.getType(true, "NewOperation"));
        assertEquals("intType", simpleType.getBaseType().getName());

        ResourceUtils.createFolderInProject(getProject(), "src");
        IFile file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "Imported.xsd", SOURCE_FOLDER,
                this.getProject(), "Imported.xsd");
        assertTrue(file.exists());
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "Imported.xsd", Document_FOLDER_NAME,
                this.getProject(), "Imported.xsd");
        assertTrue(file.exists());
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "SupplierInvoice.wsdl", SOURCE_FOLDER,
                this.getProject(), "SupplierInvoice.wsdl");
        assertTrue(file.exists());
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "CopyTypeTestSingleSchema.wsdl",
                SOURCE_FOLDER, this.getProject(), "CopyTypeTestSingleSchema.wsdl");
        assertTrue(file.exists());
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "CopyTypeTestMultipleSchema.wsdl",
                SOURCE_FOLDER, this.getProject(), "CopyTypeTestMultipleSchema.wsdl");
        assertTrue(file.exists());
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "CopyTypeTestExternal.xsd",
                SOURCE_FOLDER, this.getProject(), "CopyTypeTestExternal.xsd");
        assertTrue(file.exists());
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "CopyTypeTestImportedSchema.wsdl",
                SOURCE_FOLDER, this.getProject(), "CopyTypeTestImportedSchema.wsdl");
        assertTrue(file.exists());
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "CopyTypeTestIncludedSchema.wsdl",
                SOURCE_FOLDER, this.getProject(), "CopyTypeTestIncludedSchema.wsdl");
        assertTrue(file.exists());

        modelRoot = copyAndGetModelRoot("SimpleTypeWriteTest.wsdl", "TestSimpleType1.wsdl");
        description = modelRoot.getDescription();
        schema = description.getSchema("http://www.example.org/SimpleType/")[0];
        org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema schema2 = description.getSchema("http://sap.com/xi/Purchasing")[0];
        simpleType = (org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType) schema.getType(false, "stringType");
        wSimpleType = (ISimpleType) mm.getWriteSupport(simpleType);
        assertEquals("string", simpleType.getBaseType().getName());
        wSimpleType.setBaseType(schema2.getType(false, "AcceptanceStatusCode"));
        assertEquals("AcceptanceStatusCode", simpleType.getBaseType().getName());
        wSimpleType.setBaseType(schema2.getType(false, "ActionCode"));
        assertEquals("ActionCode", simpleType.getBaseType().getName());
        condition.setNamespace("http://www.example.com/Imported.xsd");
        schema2 = getSchema1(schema.getAllReferredSchemas(), condition);
        wSimpleType.setBaseType(schema2.getType(false, "SKU"));
        assertEquals("SKU", simpleType.getBaseType().getName());
    }

    private org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema getSchema1(
            Collection<org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema> collection, SchemaNamespaceCondition condition) {
        for (org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema schema : collection) {
            if (condition.isSatisfied(schema))
                return schema;
        }
        return null;
    }

    @Test
    public void testStructureType() throws Exception {
        IWsdlModelRoot modelRoot = copyAndGetModelRoot("StructureType.wsdl", "TestStructureType.wsdl");
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription description;
        org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema schema;
        IStructureType wStructureType;
        org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType structureType;
        description = modelRoot.getDescription();
        final ModelManager mm = ModelManager.getInstance();

        description = modelRoot.getDescription();
        schema = description.getSchema("http://www.example.org/StructureType/")[0];
        assertNotNull("Could not find schema http://www.example.org/StructureType/", schema);
        // Test documentation and name
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "complexType");
        assertEquals("complexType", structureType.getName());
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        wStructureType.setName("NewType");
        assertEquals("NewType", structureType.getName());
        assertNotNull("Could not find SimpleType NewType", schema.getType(false, "NewType"));
        assertEquals("Documentation", structureType.getDocumentation());
        wStructureType.setDocumentation("DocumentationOne");
        assertEquals("DocumentationOne", structureType.getDocumentation());
        wStructureType.setDocumentation("NewDocumentation");
        assertEquals("NewDocumentation", structureType.getDocumentation());
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(true, "complexType");
        assertEquals("complexType", structureType.getName());
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        wStructureType.setName("NewElement");
        assertEquals("NewElement", structureType.getName());
        assertNotNull("Could not find StructureType NewElement", schema.getType(true, "NewElement"));
        assertEquals("Documentation", structureType.getDocumentation());
        wStructureType.setDocumentation("DocumentationOne");
        assertEquals("DocumentationOne", structureType.getDocumentation());
        wStructureType.setDocumentation("NewDocumentation");
        assertEquals("NewDocumentation", structureType.getDocumentation());
        IDescription wDescription = (IDescription) mm.getWriteSupport(description);
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "ctWithAttributes");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        assertEquals("", structureType.getDocumentation());
        wStructureType.setDocumentation("Documentation");
        assertEquals("Documentation", structureType.getDocumentation());
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "ctExtension");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        assertEquals("", structureType.getDocumentation());
        wStructureType.setDocumentation("Documentation");
        assertEquals("Documentation", structureType.getDocumentation());
        wDescription.save();

        modelRoot = getModelRoot("TestStructureType.wsdl");
        description = modelRoot.getDescription();
        schema = description.getSchema("http://www.example.org/StructureType/")[0];
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "NewType");
        assertEquals("NewType", structureType.getName());
        assertEquals("NewDocumentation", structureType.getDocumentation());
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(true, "NewElement");
        assertEquals("NewElement", structureType.getName());
        assertEquals("NewDocumentation", structureType.getDocumentation());
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "ctWithAttributes");
        assertEquals("Documentation", structureType.getDocumentation());
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "ctExtension");
        assertEquals("Documentation", structureType.getDocumentation());

        modelRoot = copyAndGetModelRoot("StructureType.wsdl", "TestStructureType1.wsdl");
        description = modelRoot.getDescription();
        schema = description.getSchema("http://www.example.org/StructureType/")[0];
        // add an element in complexType with sequence
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "complexType");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        assertEquals(2, structureType.getAllElements().size());
        assertEquals(0, structureType.getElements("elementParticle2").size());
        IElement element = wStructureType.addElement("elementParticle2");
        assertEquals(3, structureType.getAllElements().size());
        assertEquals(1, structureType.getElements("elementParticle2").size());
        wStructureType.removeElement("elementParticle1");
        assertEquals(0, structureType.getElements("elementParticle1").size());
        wStructureType.removeElement(element);
        assertEquals(0, structureType.getElements("elementParticle2").size());
        // add an element in complexType with choice
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema
                .getType(false, "groupDefinitionType");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        assertEquals(6, structureType.getAllElements().size());
        assertEquals(0, structureType.getElements("elementParticle5").size());
        element = wStructureType.addElement("elementParticle5");
        assertEquals(7, structureType.getAllElements().size());
        assertEquals(1, structureType.getElements("elementParticle5").size());
        wStructureType.removeElement("elementParticle");
        assertEquals(0, structureType.getElements("elementParticle").size());
        wStructureType.removeElement(element);
        assertEquals(0, structureType.getElements("elementParticle5").size());
        // add an element in complexType with all
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "allModelGroupType");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        assertEquals(6, structureType.getAllElements().size());
        assertEquals(0, structureType.getElements("elementParticle5").size());
        element = wStructureType.addElement("elementParticle5");
        assertEquals(7, structureType.getAllElements().size());
        assertEquals(1, structureType.getElements("elementParticle5").size());
        wStructureType.removeElement("elementParticle");
        assertEquals(0, structureType.getElements("elementParticle").size());
        wStructureType.removeElement(element);
        assertEquals(0, structureType.getElements("elementParticle5").size());
        // add an element in element declaration with sequence
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(true, "complexType");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        assertEquals(2, structureType.getAllElements().size());
        assertEquals(0, structureType.getElements("elementParticle2").size());
        element = wStructureType.addElement("elementParticle2");
        assertEquals(3, structureType.getAllElements().size());
        assertEquals(1, structureType.getElements("elementParticle2").size());
        wStructureType.removeElement("elementParticle");
        assertEquals(0, structureType.getElements("elementParticle").size());
        wStructureType.removeElement(element);
        assertEquals(0, structureType.getElements("elementParticle2").size());
        // add an element in element declaration with simpleType reference
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(true,
                "elementWithSimpleTypeRef");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        assertEquals(0, structureType.getAllElements().size());
        XSDElementDeclaration elementDecl = (XSDElementDeclaration) structureType.getComponent();
        assertNull("element has anonymous type", elementDecl.getAnonymousTypeDefinition());
        element = wStructureType.addElement("elementParticle");
        assertEquals(1, structureType.getAllElements().size());
        assertEquals(1, structureType.getElements("elementParticle").size());
        assertNotNull("element doesnt have anonymous type", elementDecl.getAnonymousTypeDefinition());
        assertNull("referred type name is not null", elementDecl.getTypeDefinition().getName());
        assertTrue("element doesnt have anonymous type",
                elementDecl.getAnonymousTypeDefinition() instanceof XSDComplexTypeDefinition);
        wStructureType.removeElement(element);
        assertEquals(0, structureType.getElements("elementParticle").size());
        // add an element in element declaration with complexType reference
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(true,
                "elementWithComplexTypeRef");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        assertEquals(0, structureType.getAllElements().size());
        elementDecl = (XSDElementDeclaration) structureType.getComponent();
        assertNull("element has anonymous type", elementDecl.getAnonymousTypeDefinition());
        element = wStructureType.addElement("elementParticle");
        assertEquals(1, structureType.getAllElements().size());
        assertEquals(1, structureType.getElements("elementParticle").size());
        assertNotNull("element doesnt have anonymous type", elementDecl.getAnonymousTypeDefinition());
        assertNull("referred type name is not null", elementDecl.getTypeDefinition().getName());
        assertTrue("element doesnt have anonymous type",
                elementDecl.getAnonymousTypeDefinition() instanceof XSDComplexTypeDefinition);
        wStructureType.removeElement("elementParticle");
        assertEquals(0, structureType.getElements("elementParticle").size());
        // add an element in element declaration with open content
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(true, "openContentElement");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        assertEquals(0, structureType.getAllElements().size());
        elementDecl = (XSDElementDeclaration) structureType.getComponent();
        assertNull("element has anonymous type", elementDecl.getAnonymousTypeDefinition());
        element = wStructureType.addElement("elementParticle");
        assertEquals(1, structureType.getAllElements().size());
        assertEquals(1, structureType.getElements("elementParticle").size());
        assertNotNull("element doesnt have anonymous type", elementDecl.getAnonymousTypeDefinition());
        assertNull("referred type name is not null", elementDecl.getTypeDefinition().getName());
        assertTrue("element doesnt have anonymous type",
                elementDecl.getAnonymousTypeDefinition() instanceof XSDComplexTypeDefinition);
        wStructureType.removeElement(element);
        assertEquals(0, structureType.getElements("elementParticle").size());
        // add an element in element declaration with anonymous simpleType
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(true,
                "elementWithAnonymousSimpleType");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        assertEquals(0, structureType.getAllElements().size());
        elementDecl = (XSDElementDeclaration) structureType.getComponent();
        assertNotNull("element doesnt have anonymous type", elementDecl.getAnonymousTypeDefinition());
        assertTrue("element doesnt have anonymous type",
                elementDecl.getAnonymousTypeDefinition() instanceof XSDSimpleTypeDefinition);
        element = wStructureType.addElement("elementParticle");
        assertEquals(1, structureType.getAllElements().size());
        assertEquals(1, structureType.getElements("elementParticle").size());
        assertNotNull("element doesnt have anonymous type", elementDecl.getAnonymousTypeDefinition());
        assertTrue("element doesnt have anonymous type",
                elementDecl.getAnonymousTypeDefinition() instanceof XSDComplexTypeDefinition);
        assertEquals("", structureType.getDocumentation());
        wStructureType.setDocumentation("Documentation");
        assertEquals("Documentation", structureType.getDocumentation());
        wStructureType.removeElement("elementParticle");
        assertEquals(0, structureType.getElements("elementParticle").size());
        // add an element in complexType with complexType extension
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "ctExtension");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        assertEquals(4, structureType.getAllElements().size());
        assertEquals(0, structureType.getElements("elementParticle5").size());
        element = wStructureType.addElement("elementParticle5");
        assertEquals(5, structureType.getAllElements().size());
        assertEquals(1, structureType.getElements("elementParticle5").size());
        wStructureType.removeElement("elementParticle3");
        assertEquals(0, structureType.getElements("elementParticle3").size());
        wStructureType.removeElement(element);
        assertEquals(0, structureType.getElements("elementParticle5").size());
        // add an element in complexType with complexType restriction
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "ctRestriction");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        assertEquals(2, structureType.getAllElements().size());
        assertEquals(0, structureType.getElements("elementParticle2").size());
        element = wStructureType.addElement("elementParticle2");
        assertEquals(3, structureType.getAllElements().size());
        assertEquals(1, structureType.getElements("elementParticle2").size());
        wStructureType.removeElement("elementParticle1");
        assertEquals(0, structureType.getElements("elementParticle1").size());
        wStructureType.removeElement(element);
        assertEquals(0, structureType.getElements("elementParticle2").size());
        // add an element in element with complexType with complexType extension
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(true, "elementCtExtension");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        assertEquals(4, structureType.getAllElements().size());
        assertEquals(0, structureType.getElements("elementParticle5").size());
        element = wStructureType.addElement("elementParticle5");
        assertEquals(5, structureType.getAllElements().size());
        assertEquals(1, structureType.getElements("elementParticle5").size());
        wStructureType.removeElement("elementParticle3");
        assertEquals(0, structureType.getElements("elementParticle3").size());
        wStructureType.removeElement(element);
        assertEquals(0, structureType.getElements("elementParticle5").size());
        // add an element in complexType with simpleContent extension
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false,
                "ctSimpleContentExtension");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) structureType.getComponent();
        assertTrue("ComplexType doesnt have simple content", complexType.getContent() instanceof XSDSimpleTypeDefinition);
        assertEquals(3, structureType.getAllElements().size());
        assertEquals(0, structureType.getElements("elementParticle").size());
        element = wStructureType.addElement("elementParticle");
        assertEquals(4, structureType.getAllElements().size());
        assertEquals(1, structureType.getElements("elementParticle").size());
        assertTrue("ComplexType doesnt have simple content", complexType.getContent() instanceof XSDParticle);
        wStructureType.removeElement(element);
        assertEquals(0, structureType.getElements("elementParticle").size());
        // add an element in complexType with simpleContent restriction
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false,
                "ctSimpleContentRestriction");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        complexType = (XSDComplexTypeDefinition) structureType.getComponent();
        assertTrue("ComplexType doesnt have simple content", complexType.getContent() instanceof XSDSimpleTypeDefinition);
        assertEquals(2, structureType.getAllElements().size());
        assertEquals(0, structureType.getElements("elementParticle").size());
        element = wStructureType.addElement("elementParticle");
        assertEquals(3, structureType.getAllElements().size());
        assertEquals(1, structureType.getElements("elementParticle").size());
        assertTrue("ComplexType doesnt have simple content", complexType.getContent() instanceof XSDParticle);
        wStructureType.removeElement(element);
        assertEquals(0, structureType.getElements("elementParticle").size());
        // add an element in complexType with sequence
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "nestedElements");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        assertEquals(8, structureType.getAllElements().size());
        assertEquals(0, structureType.getElements("elementParticle5").size());
        element = wStructureType.addElement("elementParticle5");
        assertEquals(9, structureType.getAllElements().size());
        assertEquals(1, structureType.getElements("elementParticle5").size());
        wStructureType.removeElement("elementParticle1");
        assertEquals(0, structureType.getElements("elementParticle1").size());
        wStructureType.removeElement(element);
        assertEquals(0, structureType.getElements("elementParticle5").size());

        // Test Removal
        modelRoot = copyAndGetModelRoot("StructureType.wsdl", "TestStructureType1.wsdl");
        description = modelRoot.getDescription();
        schema = description.getSchema("http://www.example.org/StructureType/")[0];
        // Remove an element in complexType with sequence
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "complexType");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        assertEquals(1, structureType.getElements("elementParticle1").size());
        wStructureType.removeElement("elementParticle1");
        assertEquals(0, structureType.getElements("elementParticle1").size());
        // Remove an element in complexType with choice
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema
                .getType(false, "groupDefinitionType");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        assertEquals(1, structureType.getElements("elementParticle").size());
        wStructureType.removeElement("elementParticle");
        assertEquals(0, structureType.getElements("elementParticle").size());
        // Remove an element in complexType with all
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "allModelGroupType");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        assertEquals(1, structureType.getElements("elementParticle").size());
        wStructureType.removeElement("elementParticle");
        assertEquals(0, structureType.getElements("elementParticle").size());
        // Remove an element in element declaration with sequence
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(true, "complexType");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        assertEquals(1, structureType.getElements("elementParticle").size());
        wStructureType.removeElement("elementParticle");
        assertEquals(0, structureType.getElements("elementParticle").size());
        // Remove an element in complexType with complexType extension
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "ctExtension");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        assertEquals(1, structureType.getElements("elementParticle3").size());
        wStructureType.removeElement("elementParticle3");
        assertEquals(0, structureType.getElements("elementParticle3").size());
        // Remove an element in complexType with complexType restriction
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "ctRestriction");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        assertEquals(1, structureType.getElements("elementParticle1").size());
        wStructureType.removeElement("elementParticle1");
        assertEquals(0, structureType.getElements("elementParticle1").size());
        // Remove an element in element with complexType with complexType
        // extension
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(true, "elementCtExtension");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        assertEquals(1, structureType.getElements("elementParticle3").size());
        wStructureType.removeElement("elementParticle3");
        assertEquals(0, structureType.getElements("elementParticle3").size());
        // Remove an element in complexType with simpleContent extension
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false,
                "ctSimpleContentExtension");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        assertEquals(1, structureType.getElements("attribute1").size());
        wStructureType.removeElement("attribute1");
        assertEquals(0, structureType.getElements("attribute1").size());
        // Remove an element in complexType with simpleContent restriction
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false,
                "ctSimpleContentRestriction");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        assertEquals(1, structureType.getElements("attribute1").size());
        wStructureType.removeElement("attribute1");
        assertEquals(0, structureType.getElements("attribute1").size());
        // Remove an element in complexType with sequence
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "nestedElements");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        assertEquals(1, structureType.getElements("attribute1").size());
        wStructureType.removeElement("elementParticle1");
        assertEquals(0, structureType.getElements("elementParticle1").size());
        assertEquals(1, structureType.getElements("attribute1").size());
        wStructureType.removeElement("attribute1");
        assertEquals(0, structureType.getElements("attribute1").size());

        // Test Set baseType
        modelRoot = copyAndGetModelRoot("StructureType.wsdl", "TestStructureType2.wsdl");
        description = modelRoot.getDescription();
        schema = description.getSchema("http://www.example.org/StructureType/")[0];
        // set ST as baseType for element with ST Reference
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(true,
                "elementWithSimpleTypeRef");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        elementDecl = (XSDElementDeclaration) structureType.getComponent();
        wStructureType.setBaseType(schema.getType(false, "simpleType"));
        assertNotNull("anonymous simpleType must be created as type of this element", elementDecl.getAnonymousTypeDefinition());
        assertTrue("Anonymous type must be simpleType",
                elementDecl.getAnonymousTypeDefinition() instanceof XSDSimpleTypeDefinition);
        assertEquals("simpleType", elementDecl.getAnonymousTypeDefinition().getBaseType().getName());
        // set CT as baseType for element with CT Reference
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(true,
                "elementWithComplexTypeRef");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        elementDecl = (XSDElementDeclaration) structureType.getComponent();
        wStructureType.setBaseType(schema.getType(false, "complexType"));
        assertNotNull("anonymous complexType must be created as type of this element", elementDecl.getAnonymousTypeDefinition());
        assertTrue("Anonymous type must be simpleType",
                elementDecl.getAnonymousTypeDefinition() instanceof XSDComplexTypeDefinition);
        assertEquals("complexType", elementDecl.getAnonymousTypeDefinition().getBaseType().getName());
        assertTrue(XSDDerivationMethod.EXTENSION_LITERAL.equals(((XSDComplexTypeDefinition) elementDecl
                .getAnonymousTypeDefinition()).getDerivationMethod()));

        modelRoot = copyAndGetModelRoot("StructureType.wsdl", "TestStructureType4.wsdl");
        description = modelRoot.getDescription();
        schema = description.getSchema("http://www.example.org/StructureType/")[0];
        // set CT as baseType for element with ST Reference
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(true,
                "elementWithSimpleTypeRef");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        elementDecl = (XSDElementDeclaration) structureType.getComponent();
        wStructureType.setBaseType(schema.getType(false, "complexType"));
        assertNotNull("anonymous complexType must be created as type of this element", elementDecl.getAnonymousTypeDefinition());
        assertTrue("Anonymous type must be simpleType",
                elementDecl.getAnonymousTypeDefinition() instanceof XSDComplexTypeDefinition);
        assertEquals("complexType", elementDecl.getAnonymousTypeDefinition().getBaseType().getName());
        assertTrue(XSDDerivationMethod.EXTENSION_LITERAL.equals(((XSDComplexTypeDefinition) elementDecl
                .getAnonymousTypeDefinition()).getDerivationMethod()));
        // set ST as baseType for element with CT Reference
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(true,
                "elementWithComplexTypeRef");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        elementDecl = (XSDElementDeclaration) structureType.getComponent();
        wStructureType.setBaseType(schema.getType(false, "simpleType"));
        assertNotNull("anonymous simpleType must be created as type of this element", elementDecl.getAnonymousTypeDefinition());
        assertTrue("Anonymous type must be simpleType",
                elementDecl.getAnonymousTypeDefinition() instanceof XSDSimpleTypeDefinition);
        assertEquals("simpleType", elementDecl.getAnonymousTypeDefinition().getBaseType().getName());

        modelRoot = copyAndGetModelRoot("StructureType.wsdl", "TestStructureTypeOne.wsdl");
        description = modelRoot.getDescription();
        schema = description.getSchema("http://www.example.org/StructureType/")[0];
        // set ST as baseType for element with Anonymous ST
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(true, "globalElementWithSt");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        elementDecl = (XSDElementDeclaration) structureType.getComponent();
        wStructureType.setBaseType(schema.getType(false, "simpleType"));
        assertNotNull("anonymous simpleType must be created as type of this element", elementDecl.getAnonymousTypeDefinition());
        assertTrue("Anonymous type must be simpleType",
                elementDecl.getAnonymousTypeDefinition() instanceof XSDSimpleTypeDefinition);
        assertEquals("simpleType", elementDecl.getAnonymousTypeDefinition().getBaseType().getName());
        // set ST as baseType for element with Anonymous CT
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(true, "complexType");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        elementDecl = (XSDElementDeclaration) structureType.getComponent();
        wStructureType.setBaseType(schema.getType(false, "simpleType"));
        assertNotNull("anonymous simpleType must be created as type of this element", elementDecl.getAnonymousTypeDefinition());
        assertTrue("Anonymous type must be complexType",
                elementDecl.getAnonymousTypeDefinition() instanceof XSDComplexTypeDefinition);
        assertEquals("simpleType", elementDecl.getAnonymousTypeDefinition().getBaseType().getName());
        assertTrue(XSDDerivationMethod.EXTENSION_LITERAL.equals(((XSDComplexTypeDefinition) elementDecl
                .getAnonymousTypeDefinition()).getDerivationMethod()));

        modelRoot = copyAndGetModelRoot("StructureType.wsdl", "TestStructureTypeTwo.wsdl");
        description = modelRoot.getDescription();
        schema = description.getSchema("http://www.example.org/StructureType/")[0];
        // set CT as baseType for element with Anonymous ST
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(true, "globalElementWithSt");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        elementDecl = (XSDElementDeclaration) structureType.getComponent();
        wStructureType.setBaseType(schema.getType(false, "complexType"));
        assertNotNull("anonymous complexType must be created as type of this element", elementDecl.getAnonymousTypeDefinition());
        assertTrue("Anonymous type must be complexType",
                elementDecl.getAnonymousTypeDefinition() instanceof XSDComplexTypeDefinition);
        assertEquals("complexType", elementDecl.getAnonymousTypeDefinition().getBaseType().getName());
        // set CT as baseType for element with Anonymous CT
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(true, "complexType");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        elementDecl = (XSDElementDeclaration) structureType.getComponent();
        wStructureType.setBaseType(schema.getType(false, "complexType"));
        assertNotNull("anonymous complexType must be created as type of this element", elementDecl.getAnonymousTypeDefinition());
        assertTrue("Anonymous type must be complexType",
                elementDecl.getAnonymousTypeDefinition() instanceof XSDComplexTypeDefinition);
        assertEquals("complexType", elementDecl.getAnonymousTypeDefinition().getBaseType().getName());
        assertTrue(XSDDerivationMethod.RESTRICTION_LITERAL.equals(((XSDComplexTypeDefinition) elementDecl
                .getAnonymousTypeDefinition()).getDerivationMethod()));

        modelRoot = copyAndGetModelRoot("StructureType.wsdl", "TestStructureTypeFour.wsdl");
        description = modelRoot.getDescription();
        schema = description.getSchema("http://www.example.org/StructureType/")[0];
        // set ST as baseType for CT with Sequence
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "complexType");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        complexType = (XSDComplexTypeDefinition) structureType.getComponent();
        wStructureType.setBaseType(schema.getType(false, "simpleType"));
        assertTrue("content must be complexType", complexType.getContent() instanceof XSDSimpleTypeDefinition);
        assertEquals("simpleType", complexType.getBaseType().getName());
        assertEquals(XSDDerivationMethod.EXTENSION_LITERAL, complexType.getDerivationMethod());
        // set ST as baseType for CT with ctExtension
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "ctExtension");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        complexType = (XSDComplexTypeDefinition) structureType.getComponent();
        wStructureType.setBaseType(schema.getType(false, "simpleType"));
        assertTrue("content must be complexType", complexType.getContent() instanceof XSDSimpleTypeDefinition);
        assertEquals("simpleType", complexType.getBaseType().getName());
        assertEquals(XSDDerivationMethod.EXTENSION_LITERAL, complexType.getDerivationMethod());
        // set ST as baseType for CT with Restriction
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "ctRestriction");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        complexType = (XSDComplexTypeDefinition) structureType.getComponent();
        wStructureType.setBaseType(schema.getType(false, "simpleType"));
        assertTrue("content must be complexType", complexType.getContent() instanceof XSDSimpleTypeDefinition);
        assertEquals("simpleType", complexType.getBaseType().getName());
        assertEquals(XSDDerivationMethod.EXTENSION_LITERAL, complexType.getDerivationMethod());

        modelRoot = copyAndGetModelRoot("StructureType.wsdl", "TestStructureTypeFive.wsdl");
        description = modelRoot.getDescription();
        schema = description.getSchema("http://www.example.org/StructureType/")[0];
        // set ST as baseType for CT with Sequence
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "complexType");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        complexType = (XSDComplexTypeDefinition) structureType.getComponent();
        wStructureType.setBaseType(schema.getType(false, "complexType"));
        assertEquals("complexType", complexType.getBaseType().getName());
        assertEquals(XSDDerivationMethod.RESTRICTION_LITERAL, complexType.getDerivationMethod());
        // set ST as baseType for CT with ctExtension
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "ctExtension");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        complexType = (XSDComplexTypeDefinition) structureType.getComponent();
        wStructureType.setBaseType(schema.getType(false, "complexType"));
        assertEquals("complexType", complexType.getBaseType().getName());
        assertEquals(XSDDerivationMethod.EXTENSION_LITERAL, complexType.getDerivationMethod());
        // set ST as baseType for CT with Restriction
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "ctRestriction");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        complexType = (XSDComplexTypeDefinition) structureType.getComponent();
        wStructureType.setBaseType(schema.getType(false, "complexType"));
        assertEquals("complexType", complexType.getBaseType().getName());
        assertEquals(XSDDerivationMethod.RESTRICTION_LITERAL, complexType.getDerivationMethod());

        modelRoot = getModelRoot("TestStructureTypeFive.wsdl");
        description = modelRoot.getDescription();
        schema = description.getSchema("http://www.example.org/StructureType/")[0];
        // set CT as baseType for CT with simpleContent extension
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false,
                "ctSimpleContentExtension");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        complexType = (XSDComplexTypeDefinition) structureType.getComponent();
        wStructureType.setBaseType(schema.getType(false, "complexType"));
        assertTrue("content must be XSDParticle", complexType.getContent() instanceof XSDParticle);
        assertEquals("complexType", complexType.getBaseType().getName());
        assertEquals(XSDDerivationMethod.EXTENSION_LITERAL, complexType.getDerivationMethod());
        // set CT as baseType for CT with simpleContent restriction
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false,
                "ctSimpleContentRestriction");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        complexType = (XSDComplexTypeDefinition) structureType.getComponent();
        wStructureType.setBaseType(schema.getType(false, "complexType"));
        assertTrue("content must be XSDParticle", complexType.getContent() instanceof XSDParticle);
        assertEquals("complexType", complexType.getBaseType().getName());
        assertEquals(XSDDerivationMethod.RESTRICTION_LITERAL, complexType.getDerivationMethod());

        modelRoot = getModelRoot("TestStructureTypeFive.wsdl");
        description = modelRoot.getDescription();
        schema = description.getSchema("http://www.example.org/StructureType/")[0];
        // set ST as baseType for CT with simpleContent extension
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false,
                "ctSimpleContentExtension");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        complexType = (XSDComplexTypeDefinition) structureType.getComponent();
        wStructureType.setBaseType(schema.getType(false, "simpleType"));
        assertTrue("content must be simpleType", complexType.getContent() instanceof XSDSimpleTypeDefinition);
        assertEquals("simpleType", complexType.getBaseType().getName());
        assertEquals(XSDDerivationMethod.EXTENSION_LITERAL, complexType.getDerivationMethod());
        // set ST as baseType for CT with simpleContent restriction
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false,
                "ctSimpleContentRestriction");
        wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        complexType = (XSDComplexTypeDefinition) structureType.getComponent();
        wStructureType.setBaseType(schema.getType(false, "simpleType"));
        assertTrue("content must be simpleType", complexType.getContent() instanceof XSDSimpleTypeDefinition);
        assertEquals("simpleType", complexType.getBaseType().getName());
        assertEquals(XSDDerivationMethod.EXTENSION_LITERAL, complexType.getDerivationMethod());
        assertEquals(false, structureType.isAnonymous());
    }

    @Test
    public void testElement() throws Exception {
        IWsdlModelRoot modelRoot = copyAndGetModelRoot("StructureType.wsdl", "TestElement.wsdl");
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription description;
        org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema schema;
        org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType structureType;
        org.eclipse.wst.sse.sieditor.model.write.xsd.api.IElement wElement;
        description = modelRoot.getDescription();
        final ModelManager mm = ModelManager.getInstance();

        description = modelRoot.getDescription();
        schema = description.getSchema("http://www.example.org/StructureType/")[0];
        assertNotNull("Could not find schema http://www.example.org/StructureType/", schema);
        // Test documentation and name
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "complexType");
        IElement element = structureType.getElements("elementParticle").iterator().next();
        wElement = (org.eclipse.wst.sse.sieditor.model.write.xsd.api.IElement) mm.getWriteSupport(element);
        assertEquals("elementParticle", element.getName());
        wElement.setName("newName");
        assertEquals("newName", element.getName());
        assertEquals("Documentation", element.getDocumentation());
        wElement.setDocumentation("NewDocumentation");
        assertEquals("NewDocumentation", element.getDocumentation());
        assertEquals(1, element.getMinOccurs());
        wElement.setMinOccurs(2);
        assertEquals(2, element.getMinOccurs());
        assertEquals(1, element.getMaxOccurs());
        wElement.setMaxOccurs(4);
        assertEquals(4, element.getMaxOccurs());
        assertEquals(false, element.getNillable());
        wElement.setNillable(true);
        assertEquals(true, element.getNillable());

        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "ctWithAttributes");
        element = structureType.getElements("elementParticle3").iterator().next();
        wElement = (org.eclipse.wst.sse.sieditor.model.write.xsd.api.IElement) mm.getWriteSupport(element);
        assertEquals("", element.getDocumentation());
        wElement.setDocumentation("NewDocumentation");
        assertEquals("NewDocumentation", element.getDocumentation());
        element = structureType.getElements("globalElement").iterator().next();
        wElement = (org.eclipse.wst.sse.sieditor.model.write.xsd.api.IElement) mm.getWriteSupport(element);
        assertEquals(10, element.getMinOccurs());
        wElement.setMinOccurs(13);
        assertEquals(13, element.getMinOccurs());
        assertEquals(100, element.getMaxOccurs());
        wElement.setMaxOccurs(26);
        assertEquals(26, element.getMaxOccurs());
        assertEquals(false, element.getNillable());
        wElement.setNillable(true);
        assertEquals(true, element.getNillable());
        wElement = (org.eclipse.wst.sse.sieditor.model.write.xsd.api.IElement) mm.getWriteSupport(element);
        assertEquals("", element.getDocumentation());
        wElement.setDocumentation("NewDocumentation");
        assertEquals("NewDocumentation", element.getDocumentation());
        element = structureType.getElements("attribute1").iterator().next();
        wElement = (org.eclipse.wst.sse.sieditor.model.write.xsd.api.IElement) mm.getWriteSupport(element);
        assertEquals("", element.getDocumentation());
        wElement.setDocumentation("NewDocumentation");
        assertEquals("NewDocumentation", element.getDocumentation());
        assertEquals(0, element.getMinOccurs());
        wElement.setMinOccurs(2);
        assertEquals(0, element.getMinOccurs());
        assertEquals(1, element.getMaxOccurs());
        wElement.setMaxOccurs(4);
        assertEquals(1, element.getMaxOccurs());
        assertEquals(false, element.getNillable());
        wElement.setNillable(true);
        assertEquals(false, element.getNillable());

        modelRoot = getModelRoot("TestElement.wsdl");
        description = modelRoot.getDescription();
        schema = description.getSchema("http://www.example.org/StructureType/")[0];
        assertNotNull("Could not find schema http://www.example.org/StructureType/", schema);
        // Test type assignment for element
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "ctWithAttributes");
        element = structureType.getElements("elementParticle3").iterator().next();
        wElement = (org.eclipse.wst.sse.sieditor.model.write.xsd.api.IElement) mm.getWriteSupport(element);
        assertEquals("string", element.getType().getName());
        wElement.setType(schema.getType(false, "complexType"));
        assertEquals("complexType", element.getType().getName());
        assertEquals("elementParticle3", element.getName());
        wElement.setType(schema.getType(false, "simpleType"));
        assertEquals("simpleType", element.getType().getName());
        assertEquals("elementParticle3", element.getName());
        wElement.setAnonymousType(true);
        wElement.setAnonymousType(false);

        element = structureType.getElements("globalElement").iterator().next();
        wElement = (org.eclipse.wst.sse.sieditor.model.write.xsd.api.IElement) mm.getWriteSupport(element);
        assertEquals("globalElement", element.getType().getName());
        wElement.setType(schema.getType(false, "complexType"));
        assertEquals("complexType", element.getType().getName());
        assertEquals("globalElement", element.getName());
        wElement.setType(schema.getType(false, "simpleType"));
        assertEquals("simpleType", element.getType().getName());
        assertEquals("globalElement", element.getName());
        wElement.setAnonymousType(true);
        wElement.setAnonymousType(false);

        element = structureType.getElements("attribute1").iterator().next();
        wElement = (org.eclipse.wst.sse.sieditor.model.write.xsd.api.IElement) mm.getWriteSupport(element);
        assertEquals("string", element.getType().getName());
        wElement.setType(schema.getType(false, "complexType"));
        assertEquals("string", element.getType().getName());
        assertEquals("attribute1", element.getName());
        wElement.setType(schema.getType(false, "simpleType"));
        assertEquals("simpleType", element.getType().getName());
        assertEquals("attribute1", element.getName());
        wElement.setAnonymousType(true);
        wElement.setAnonymousType(false);

        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false,
                "ctSimpleContentExtension");
        element = structureType.getElements("globalAttribute").iterator().next();
        wElement = (org.eclipse.wst.sse.sieditor.model.write.xsd.api.IElement) mm.getWriteSupport(element);
        assertNull("Type must be null for attributes", element.getType());
        wElement.setType(schema.getType(false, "complexType"));
        assertNull("ComplexType must not be set", element.getType());
        assertEquals("globalAttribute", element.getName());
        wElement.setType(schema.getType(false, "simpleType"));
        assertEquals("simpleType", element.getType().getName());
        assertEquals("globalAttribute", element.getName());
    }

    @Test
    public void testTypeCopyPaste() throws Exception {
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription description;
        org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema schema;
        IWsdlModelRoot modelRoot;

        ResourceUtils.createFolderInProject(getProject(), "src");
        IFile file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "Imported_copy.xsd",
                SOURCE_FOLDER, this.getProject(), "Imported_copy.xsd");
        assertTrue(file.exists());
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "Imported1_copy.xsd",
                Document_FOLDER_NAME, this.getProject(), "Imported1_copy.xsd");
        assertTrue(file.exists());
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "Importing_copy.xsd", SOURCE_FOLDER,
                this.getProject(), "Importing_copy.xsd");
        assertTrue(file.exists());
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "Included_copy.xsd", SOURCE_FOLDER, this
                .getProject(), "Included_copy.xsd");
        assertTrue(file.exists());
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "IncludedTypesInSchema_copy.wsdl",
                SOURCE_FOLDER, this.getProject(), "IncludedTypesInSchema_copy.wsdl");
        assertTrue(file.exists());
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "IncludedTypesInWSDL_copy.wsdl",
                SOURCE_FOLDER, this.getProject(), "IncludedTypesInWSDL_copy.wsdl");
        assertTrue(file.exists());
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "MultipleNamespacesTypes_copy.wsdl",
                SOURCE_FOLDER, this.getProject(), "MultipleNamespacesTypes_copy.wsdl");
        assertTrue(file.exists());
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "NullNamespaceWSDL_copy.wsdl",
                SOURCE_FOLDER, this.getProject(), "NullNamespaceWSDL_copy.wsdl");
        assertTrue(file.exists());
        file = ResourceUtils.copyFileIntoTestProject(
                Constants.DATA_PUBLIC_SELF_KESHAV + "TypesReferringExternalSchema_copy.wsdl", SOURCE_FOLDER, this.getProject(),
                "TypesReferringExternalSchema_copy.wsdl");
        assertTrue(file.exists());

        modelRoot = copyAndGetModelRoot("TestSI.wsdl", "TestSI.wsdl");
        description = modelRoot.getDescription();
        schema = description.getSchema("http://www.example.org/")[0];
        assertEquals(2, schema.getAllContainedTypes().size());

        // Multiple Namespaces WSDL
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/SupplierInvoice.wsdl", "http://www.example.org/",
                "http://sap.com/xi/SupplierInvoicing", "SupplierInvoiceRequest");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/SupplierInvoice.wsdl", "http://www.example.org/",
                "http://sap.com/xi/SupplierInvoicing", "SupplierInvoiceRequestResponse");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/MultipleNamespacesTypes_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "ComplexCompositeType");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/MultipleNamespacesTypes_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "complexType");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/MultipleNamespacesTypes_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "simpleType");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/MultipleNamespacesTypes_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "secondSimpleType");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/MultipleNamespacesTypes_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "simpleTypeWithTypeRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/MultipleNamespacesTypes_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "simpleTypeWithExternalTypeRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/MultipleNamespacesTypes_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "simpleTypeWithAnonymousRefType");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/MultipleNamespacesTypes_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "simpleTypeWithAnonymousRefExternalType");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/MultipleNamespacesTypes_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "globalElement");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/MultipleNamespacesTypes_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "globalElementWithSimpleTypeRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/MultipleNamespacesTypes_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "globalElementWithComplexTypeRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/MultipleNamespacesTypes_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "globalElementWithComplexTypeRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/MultipleNamespacesTypes_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "TypeWithElementRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/MultipleNamespacesTypes_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "TypeWithAttributeRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/MultipleNamespacesTypes_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "TypeWithAttributes");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/MultipleNamespacesTypes_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "TypeWithAttribtueGroupRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/MultipleNamespacesTypes_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "TypeWithRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/MultipleNamespacesTypes_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "ComplexTypeWithSimpleTypeRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/MultipleNamespacesTypes_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "ComplexTypeWithComplexTypeRef");

        // Schema Types
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/Importing_copy.xsd", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "ComplexCompositeType");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/Importing_copy.xsd", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "complexType");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/Importing_copy.xsd", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "simpleType");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/Importing_copy.xsd", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "secondSimpleType");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/Importing_copy.xsd", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "simpleTypeWithTypeRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/Importing_copy.xsd", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "simpleTypeWithExternalTypeRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/Importing_copy.xsd", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "simpleTypeWithAnonymousRefType");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/Importing_copy.xsd", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "simpleTypeWithAnonymousRefExternalType");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/Importing_copy.xsd", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "globalElement");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/Importing_copy.xsd", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "globalElementWithSimpleTypeRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/Importing_copy.xsd", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "globalElementWithComplexTypeRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/Importing_copy.xsd", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "globalElementWithComplexTypeRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/Importing_copy.xsd", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "TypeWithElementRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/Importing_copy.xsd", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "TypeWithAttributeRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/Importing_copy.xsd", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "TypeWithAttributes");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/Importing_copy.xsd", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "TypeWithAttribtueGroupRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/Importing_copy.xsd", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "TypeWithRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/Importing_copy.xsd", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "ComplexTypeWithSimpleTypeRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/Importing_copy.xsd", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "ComplexTypeWithComplexTypeRef");

        // WSDL importing Schema Types
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/TypesReferringExternalSchema_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "ComplexCompositeType");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/TypesReferringExternalSchema_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "complexType");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/TypesReferringExternalSchema_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "simpleType");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/TypesReferringExternalSchema_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "secondSimpleType");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/TypesReferringExternalSchema_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "simpleTypeWithTypeRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/TypesReferringExternalSchema_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "simpleTypeWithExternalTypeRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/TypesReferringExternalSchema_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "simpleTypeWithAnonymousRefType");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/TypesReferringExternalSchema_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "simpleTypeWithAnonymousRefExternalType");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/TypesReferringExternalSchema_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "globalElement");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/TypesReferringExternalSchema_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "globalElementWithSimpleTypeRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/TypesReferringExternalSchema_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "globalElementWithComplexTypeRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/TypesReferringExternalSchema_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "globalElementWithComplexTypeRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/TypesReferringExternalSchema_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "TypeWithElementRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/TypesReferringExternalSchema_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "TypeWithAttributeRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/TypesReferringExternalSchema_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "TypeWithAttributes");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/TypesReferringExternalSchema_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "TypeWithAttribtueGroupRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/TypesReferringExternalSchema_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "TypeWithRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/TypesReferringExternalSchema_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "ComplexTypeWithSimpleTypeRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/TypesReferringExternalSchema_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "ComplexTypeWithComplexTypeRef");

        // Including types from WSDL
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/IncludedTypesInWSDL_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "simpleTypeWithTypeRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/IncludedTypesInWSDL_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "simpleTypeWithExternalTypeRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/IncludedTypesInWSDL_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "simpleTypeWithAnonymousRefType");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/IncludedTypesInWSDL_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "simpleTypeWithAnonymousRefExternalType");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/IncludedTypesInWSDL_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "globalElementWithAnonymousComplexType");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/IncludedTypesInWSDL_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "TypeWithAttributes");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/IncludedTypesInWSDL_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "TypeWithAttribtueGroupRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/IncludedTypesInWSDL_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "TypeWithRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/IncludedTypesInWSDL_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "ComplexTypeWithSimpleTypeRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/IncludedTypesInWSDL_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "ComplexTypeWithComplexTypeRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/IncludedTypesInWSDL_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "ComplexCompositeType");

        // Including types from Schema
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/IncludedTypesInSchema_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "simpleTypeWithTypeRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/IncludedTypesInSchema_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "simpleTypeWithExternalTypeRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/IncludedTypesInSchema_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "simpleTypeWithAnonymousRefType");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/IncludedTypesInSchema_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "simpleTypeWithAnonymousRefExternalType");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/IncludedTypesInSchema_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "globalElementWithAnonymousComplexType");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/IncludedTypesInSchema_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "TypeWithAttributes");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/IncludedTypesInSchema_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "TypeWithAttribtueGroupRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/IncludedTypesInSchema_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "TypeWithRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/IncludedTypesInSchema_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "ComplexTypeWithSimpleTypeRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/IncludedTypesInSchema_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "ComplexTypeWithComplexTypeRef");
        assertCopyWithNewFile("TestSI.wsdl", "src/wsdl/IncludedTypesInSchema_copy.wsdl", "http://www.example.org/",
                "http://www.example.org/MultipleNamespaces/", "ComplexCompositeType");

        modelRoot = copyAndGetModelRoot("TestSI.wsdl", "TestSI.wsdl");
        description = modelRoot.getDescription();
        schema = description.getSchema("http://www.example.org/")[0];
        org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema
                .getType(true, "NewOperation");
        assertEquals(1, structureType.getElements("in").size());

        // test if form copy is fine
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "Source.wsdl", SOURCE_FOLDER, this
                .getProject(), "Source.wsdl");
        assertTrue(file.exists());
        file = ResourceUtils.copyFileIntoTestProject(Constants.DATA_PUBLIC_SELF_KESHAV + "Target.wsdl", SOURCE_FOLDER, this
                .getProject(), "Target.wsdl");
        assertTrue(file.exists());
    }

    private Map<String, XSDElementDeclaration> getElements(final StructureType type) {
        XSDNamedComponent component = type.getComponent();
        component = component instanceof XSDElementDeclaration ? ((XSDElementDeclaration) component).getAnonymousTypeDefinition()
                : component;
        if (null != component) {
            XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) component;
            if (complexType.getContent() instanceof XSDParticle) {
                XSDParticle particle = (XSDParticle) complexType.getContent();
                if (particle.getContent() instanceof XSDModelGroup) {
                    XSDModelGroup modelGroup = (XSDModelGroup) particle.getContent();
                    List<XSDParticle> particles = modelGroup.getParticles();
                    Map<String, XSDElementDeclaration> result = new HashMap<String, XSDElementDeclaration>();
                    XSDElementDeclaration element;
                    for (XSDParticle elementParticle : particles) {
                        if (elementParticle.getContent() instanceof XSDElementDeclaration) {
                            element = (XSDElementDeclaration) elementParticle.getContent();
                            result.put(element.getName(), element);
                        }
                    }
                    return result;
                }
            }
        }
        return Collections.emptyMap();
    }

    public void assertCopyWithNewFile(final String targetFile, final String sourceFile, final String targetNs,
            final String sourceNs, final String typeName) throws Exception {
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription description;
        org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema schema;
        ISchema wSchema;
        IWsdlModelRoot modelRoot;
        final ModelManager mm = ModelManager.getInstance();

        modelRoot = getModelRoot(targetFile);
        description = modelRoot.getDescription();
        schema = description.getSchema(targetNs)[0];
        assertNotNull(schema);
        wSchema = (ISchema) mm.getWriteSupport(schema);
        assertNotNull(wSchema);
    }

    public void assertCopyElement(org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType structureType, IElement element)
            throws IllegalInputException {
        final ModelManager mm = ModelManager.getInstance();
        IStructureType wStructureType = (IStructureType) mm.getWriteSupport(structureType);
        try {
            wStructureType.copyElement(element);
        } catch (ExecutionException e) {
            throw new IllegalStateException("The command execution failed. See nested exception", e);
        }
        assertTrue(structureType.getElements(element.getName()).size() > 0);
    }

    private org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema getSchema(
            final Collection<org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema> schemas,
            ICondition<org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema> condition) {
        for (org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema schema : schemas) {
            if (condition.isSatisfied(schema))
                return schema;
        }
        return null;
    }

    interface ICondition<T> {
        boolean isSatisfied(T in);
    }

    private class SchemaNamespaceCondition implements ICondition<org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema> {

        private String _namespace;

        public SchemaNamespaceCondition(final String namespace) {
            this._namespace = namespace;
        }

        public boolean isSatisfied(org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema in) {
            if ((null == _namespace && null == in.getNamespace()) || (null != _namespace && _namespace.equals(in.getNamespace())))
                return true;
            return false;
        }

        public void setNamespace(final String namespace) {
            this._namespace = namespace;
        }

    }

    private IWsdlModelRoot copyAndGetModelRoot(final String fileName, final String targetName) throws Exception {
        return getWSDLModelRoot(Constants.DATA_PUBLIC_SELF_KESHAV + fileName, targetName);
    }

    private IWsdlModelRoot getModelRoot(final String fileName) throws Exception {
        final IFile file = (IFile) getProject().findMember(new Path(Document_FOLDER_NAME + IPath.SEPARATOR + fileName));
        assertTrue(file.exists());
        IWsdlModelRoot modelRoot = ModelManager.getInstance().getWsdlModelRoot(new FileEditorInput(file));
        setupEnvironment(modelRoot);
        return modelRoot;
    }
}