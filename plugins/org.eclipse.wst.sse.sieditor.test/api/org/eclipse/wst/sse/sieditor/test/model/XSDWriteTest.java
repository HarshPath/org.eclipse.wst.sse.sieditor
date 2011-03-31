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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDDerivationMethod;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddElementCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddFacetCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddSimpleTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddStructureTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.CopyElementCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.CreateGlobalTypeFromAnonymousCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.DeleteElementCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.DeleteFacetCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.FacetsCommandFactory;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.RemoveTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.RenameElementCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.RenameNamedComponent;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetAnonymousCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetBaseTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetDocumentationCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetElementNillableCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetElementOccurences;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetElementTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetNamespaceCommand;
import org.eclipse.wst.sse.sieditor.mm.ModelManager;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.INamedObject;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.generic.IllegalInputException;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IFacet;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractXSDComponent;

@SuppressWarnings("nls")
public class XSDWriteTest extends SIEditorBaseTest {

    private static final String SOURCE_FOLDER = "src/wsdl";

    @Override
    protected String getProjectName() {
        return "XSDWriteTest";
    }

    @Test
    public void testSchema() throws Exception {
        IWsdlModelRoot modelRoot = copyAndGetModelRoot("PurchaseOrderConfirmation.wsdl", "TestWriteSchema1.wsdl");
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription description;
        org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema schema;
        description = modelRoot.getDescription();

        description = modelRoot.getDescription();
        schema = description.getSchema("http://sap.com/xi/Purchasing")[0];
        assertEquals("http://sap.com/xi/Purchasing", schema.getNamespace());
        setNamespace(modelRoot, schema, "http://sap.com/xi/Purchasing/New");
        assertEquals("http://sap.com/xi/Purchasing/New", schema.getNamespace());
        assertEquals("", schema.getDocumentation());
        setDocumentation(modelRoot, schema, "Documentation");
        assertEquals("Documentation", schema.getDocumentation());
        setDocumentation(modelRoot, schema, "NewDocumentation");
        assertEquals("NewDocumentation", schema.getDocumentation());

        // test creation of global type from anonymous
        final org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType address = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema
                .getType(false, "Address");
        final IElement officeElement = address.getElements("Office").iterator().next();
        createGlobalTypeFromAnonymous(modelRoot, schema, officeElement, "Office");
        assertNotNull(schema.getType(false, "Office"));

        ((Description) description).save();

        modelRoot = getWSDLModelRoot("TestWriteSchema1.wsdl");
        description = modelRoot.getDescription();
        schema = description.getSchema("http://sap.com/xi/Purchasing/New")[0];
        assertEquals("NewDocumentation", schema.getDocumentation());

        modelRoot = copyAndGetModelRoot("PurchaseOrderConfirmation.wsdl", "TestWriteSchema2.wsdl");
        description = modelRoot.getDescription();
        schema = description.getSchema("http://sap.com/xi/Purchasing")[0];
        final org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType simpleType = addSimpleType(((AbstractXSDComponent) schema)
                .getModelRoot(), schema, "NewSimpleType");
        assertNotNull("Could not find Newly added type NewSimpleType", schema.getType(false, "NewSimpleType"));
        assertEquals("string", simpleType.getBaseType().getName());
        addStructureType(((AbstractXSDComponent) schema).getModelRoot(), schema, "NewStructureType", false, null);
        assertNotNull("Could not find Newly added type NewStructureType", schema.getType(false, "NewStructureType"));
        assertNotNull("Could not find element PurchaseOrderConfirmationRequest", schema.getType(true,
                "PurchaseOrderConfirmationRequest"));
        removeType(modelRoot, schema, schema.getType(true, "PurchaseOrderConfirmationRequest"));
        assertNull("Element PurchaseOrderConfirmationRequest is not deleted", schema.getType(true,
                "PurchaseOrderConfirmationRequest"));
        assertNotNull("Could not find SimpleType ActionCode", schema.getType(false, "ActionCode"));
        removeType(modelRoot, schema, schema.getType(false, "ActionCode"));
        assertNull("SimpleType ActionCode is not deleted", schema.getType(false, "ActionCode"));
        assertNotNull("Could not find StructureType Attachment", schema.getType(false, "Attachment"));
        removeType(modelRoot, schema, schema.getType(false, "Attachment"));
        assertNull("StructureType Attachment is not deleted", schema.getType(false, "Attachment"));
        ((Description) description).save();

        modelRoot = getWSDLModelRoot("TestWriteSchema2.wsdl");
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
        org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType simpleType;
        description = modelRoot.getDescription();

        description = modelRoot.getDescription();
        schema = description.getSchema("http://www.example.org/SimpleType/")[0];
        assertNotNull("Could not find schema http://www.example.org/SimpleType/", schema);

        simpleType = (org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType) schema.getType(false, "stringType");
        assertEquals("stringType", simpleType.getName());
        renameSimpleType(modelRoot, simpleType, "NewType");
        assertEquals("NewType", simpleType.getName());
        assertNotNull("Could not find SimpleType NewType", schema.getType(false, "NewType"));
        assertEquals("Documentation", simpleType.getDocumentation());
        setDocumentation(modelRoot, simpleType, "Documentation");
        assertEquals("Documentation", simpleType.getDocumentation());
        setDocumentation(modelRoot, simpleType, "NewDocumentation");
        assertEquals("NewDocumentation", simpleType.getDocumentation());
        ((Description) description).save();

        modelRoot = getWSDLModelRoot("TestSimpleType.wsdl");
        description = modelRoot.getDescription();
        schema = description.getSchema("http://www.example.org/SimpleType/")[0];
        simpleType = (org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType) schema.getType(false, "NewType");
        assertEquals("NewType", simpleType.getName());
        assertEquals("NewDocumentation", simpleType.getDocumentation());

        modelRoot = copyAndGetModelRoot("SimpleType.wsdl", "TestSimpleType.wsdl");
        description = modelRoot.getDescription();
        schema = description.getSchema("http://www.example.org/SimpleType/")[0];
        simpleType = (org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType) schema.getType(false, "stringType");
        assertEquals("1", simpleType.getMinLength());
        setMinLength(modelRoot, simpleType, "2");
        assertEquals("2", simpleType.getMinLength());
        assertEquals("10", simpleType.getMaxLength());
        setMaxLength(modelRoot, simpleType, "11");
        assertEquals("11", simpleType.getMaxLength());
        assertEquals(2, simpleType.getEnumerations().length);
        assertEquals("value1", simpleType.getEnumerations()[0].getValue());
        assertEquals("value2", simpleType.getEnumerations()[1].getValue());
        addEnumeration(modelRoot, simpleType, "value4");
        assertEquals(3, simpleType.getEnumerations().length);
        assertEquals("value4", simpleType.getEnumerations()[2].getValue());
        deleteEnumeration(modelRoot, simpleType, simpleType.getEnumerations()[0]);
        assertEquals(2, simpleType.getEnumerations().length);
        assertEquals(2, simpleType.getPatterns().length);
        assertEquals("value*", simpleType.getPatterns()[0].getValue());
        assertEquals("v*", simpleType.getPatterns()[1].getValue());
        addPattern(modelRoot, simpleType, "va*");
        assertEquals(3, simpleType.getPatterns().length);
        assertEquals("va*", simpleType.getPatterns()[2].getValue());
        deletePattern(modelRoot, simpleType, simpleType.getPatterns()[0]);
        assertEquals(2, simpleType.getPatterns().length);

        simpleType = (org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType) schema.getType(false, "intType");
        assertEquals("1", simpleType.getMinLength());
        assertEquals("10", simpleType.getMaxLength());
        setMaxLength(modelRoot, simpleType, "11");
        assertEquals("11", simpleType.getMaxLength());
        assertEquals("1", simpleType.getMinInclusive());
        setMinInclusive(modelRoot, simpleType, "4");
        assertEquals("4", simpleType.getMinInclusive());
        assertEquals("1098098988", simpleType.getMaxInclusive());
        setMaxInclusive(modelRoot, simpleType, "1098098989");
        assertEquals("1098098989", simpleType.getMaxInclusive());
        assertEquals("", simpleType.getDocumentation());
        setDocumentation(modelRoot, simpleType, "Documentation");
        assertEquals("Documentation", simpleType.getDocumentation());
        setDocumentation(modelRoot, simpleType, "NewDocumentation");
        assertEquals("NewDocumentation", simpleType.getDocumentation());

        simpleType = (org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType) schema.getType(false, "intType1");
        assertEquals("1", simpleType.getMinLength());
        assertEquals("10", simpleType.getMaxLength());
        setMaxLength(modelRoot, simpleType, "11");
        assertEquals("11", simpleType.getMaxLength());
        assertEquals("1", simpleType.getMinExclusive());
        setMinExclusive(modelRoot, simpleType, "4");
        assertEquals("4", simpleType.getMinExclusive());
        assertEquals("1098098988", simpleType.getMaxExclusive());
        setMaxExclusive(modelRoot, simpleType, "1098098989");
        assertEquals("1098098989", simpleType.getMaxExclusive());
        assertEquals("", simpleType.getDocumentation());
        setDocumentation(modelRoot, simpleType, "Documentation");
        assertEquals("Documentation", simpleType.getDocumentation());
        setDocumentation(modelRoot, simpleType, "NewDocumentation");
        assertEquals("NewDocumentation", simpleType.getDocumentation());

        simpleType = (org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType) schema.getType(false, "stringType1");
        assertEquals("10", simpleType.getLength());
        setLength(modelRoot, simpleType, "11");
        assertEquals("11", simpleType.getLength());

        final SchemaNamespaceCondition condition = new SchemaNamespaceCondition("");

        condition.setNamespace(EmfXsdUtils.getSchemaForSchemaNS());
        final org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema schemaForSchema = getSchema(
                description.getAllVisibleSchemas(), condition);
        assertEquals("string", simpleType.getBaseType().getName());
        setBaseType(modelRoot, simpleType, schemaForSchema.getType(false, "int"));
        assertEquals("int", simpleType.getBaseType().getName());
        setBaseType(modelRoot, simpleType, schema.getType(false, "stringType"));
        assertEquals("stringType", simpleType.getBaseType().getName());
        setBaseType(modelRoot, simpleType, schema.getType(false, "intType"));
        assertEquals("intType", simpleType.getBaseType().getName());
        // cannot set complex type as base type
        setBaseType(modelRoot, simpleType, schema.getType(false, "complexType"), IStatus.ERROR);
        assertEquals("intType", simpleType.getBaseType().getName());
        setBaseType(modelRoot, simpleType, schema.getType(true, "NewOperation"), IStatus.ERROR);
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
        assertEquals("string", simpleType.getBaseType().getName());
        setBaseType(modelRoot, simpleType, schema2.getType(false, "AcceptanceStatusCode"));
        assertEquals("AcceptanceStatusCode", simpleType.getBaseType().getName());
        setBaseType(modelRoot, simpleType, schema2.getType(false, "ActionCode"));
        assertEquals("ActionCode", simpleType.getBaseType().getName());
        condition.setNamespace("http://www.example.com/Imported.xsd");
        schema2 = getSchema1(schema.getAllReferredSchemas(), condition);
        setBaseType(modelRoot, simpleType, schema2.getType(false, "SKU"));
        assertEquals("SKU", simpleType.getBaseType().getName());
    }

    private org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema getSchema1(
            final Collection<org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema> collection,
            final SchemaNamespaceCondition condition) {
        for (final org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema schema : collection) {
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
        org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType structureType;
        description = modelRoot.getDescription();

        description = modelRoot.getDescription();
        schema = description.getSchema("http://www.example.org/StructureType/")[0];
        assertNotNull("Could not find schema http://www.example.org/StructureType/", schema);
        // Test documentation and name
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "complexType");
        assertEquals("complexType", structureType.getName());
        rename(modelRoot, structureType, "NewType");
        assertEquals("NewType", structureType.getName());
        assertNotNull("Could not find SimpleType NewType", schema.getType(false, "NewType"));
        assertEquals("Documentation", structureType.getDocumentation());
        setDocumentation(modelRoot, structureType, "Documentation");
        assertEquals("Documentation", structureType.getDocumentation());
        setDocumentation(modelRoot, structureType, "NewDocumentation");
        assertEquals("NewDocumentation", structureType.getDocumentation());
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(true, "complexType");
        assertEquals("complexType", structureType.getName());
        rename(modelRoot, structureType, "NewElement");
        assertEquals("NewElement", structureType.getName());
        assertNotNull("Could not find StructureType NewElement", schema.getType(true, "NewElement"));
        assertEquals("Documentation", structureType.getDocumentation());
        setDocumentation(modelRoot, structureType, "DocumentationOne");
        assertEquals("DocumentationOne", structureType.getDocumentation());
        setDocumentation(modelRoot, structureType, "NewDocumentation");
        assertEquals("NewDocumentation", structureType.getDocumentation());
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "ctWithAttributes");
        assertEquals("", structureType.getDocumentation());
        setDocumentation(modelRoot, structureType, "Documentation");
        assertEquals("Documentation", structureType.getDocumentation());
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "ctExtension");
        assertEquals("", structureType.getDocumentation());
        setDocumentation(modelRoot, structureType, "Documentation");
        assertEquals("Documentation", structureType.getDocumentation());
        ((Description) description).save();

        modelRoot = getWSDLModelRoot("TestStructureType.wsdl");
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
        assertEquals(2, structureType.getAllElements().size());
        assertEquals(0, structureType.getElements("elementParticle2").size());
        IElement element = addElement(modelRoot, structureType, "elementParticle2");
        assertEquals(3, structureType.getAllElements().size());
        assertEquals(1, structureType.getElements("elementParticle2").size());
        deleteElement(modelRoot, structureType, structureType.getElements("elementParticle1").iterator().next());
        assertEquals(0, structureType.getElements("elementParticle1").size());
        deleteElement(modelRoot, structureType, element);
        assertEquals(0, structureType.getElements("elementParticle2").size());
        // add an element in complexType with choice
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema
                .getType(false, "groupDefinitionType");
        assertEquals(6, structureType.getAllElements().size());
        assertEquals(0, structureType.getElements("elementParticle5").size());
        element = addElement(modelRoot, structureType, "elementParticle5");
        assertEquals(7, structureType.getAllElements().size());
        assertEquals(1, structureType.getElements("elementParticle5").size());
        deleteElement(modelRoot, structureType, "elementParticle");
        assertEquals(0, structureType.getElements("elementParticle").size());
        deleteElement(modelRoot, structureType, element);
        assertEquals(0, structureType.getElements("elementParticle5").size());
        // add an element in complexType with all
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "allModelGroupType");
        assertEquals(6, structureType.getAllElements().size());
        assertEquals(0, structureType.getElements("elementParticle5").size());
        element = addElement(modelRoot, structureType, "elementParticle5");
        assertEquals(7, structureType.getAllElements().size());
        assertEquals(1, structureType.getElements("elementParticle5").size());
        deleteElement(modelRoot, structureType, "elementParticle");
        assertEquals(0, structureType.getElements("elementParticle").size());
        deleteElement(modelRoot, structureType, element);
        assertEquals(0, structureType.getElements("elementParticle5").size());
        // add an element in element declaration with sequence
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(true, "complexType");
        assertEquals(2, structureType.getAllElements().size());
        assertEquals(0, structureType.getElements("elementParticle2").size());
        element = addElement(modelRoot, structureType, "elementParticle2");
        assertEquals(3, structureType.getAllElements().size());
        assertEquals(1, structureType.getElements("elementParticle2").size());
        deleteElement(modelRoot, structureType, "elementParticle");
        assertEquals(0, structureType.getElements("elementParticle").size());
        deleteElement(modelRoot, structureType, element);
        assertEquals(0, structureType.getElements("elementParticle2").size());
        // add an element in element declaration with simpleType reference
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(true,
                "elementWithSimpleTypeRef");
        assertEquals(0, structureType.getAllElements().size());
        XSDElementDeclaration elementDecl = (XSDElementDeclaration) structureType.getComponent();
        assertNull("element has anonymous type", elementDecl.getAnonymousTypeDefinition());
        element = addElement(modelRoot, structureType, "elementParticle");
        assertEquals(1, structureType.getAllElements().size());
        assertEquals(1, structureType.getElements("elementParticle").size());
        assertNotNull("element doesnt have anonymous type", elementDecl.getAnonymousTypeDefinition());
        assertNull("referred type name is not null", elementDecl.getTypeDefinition().getName());
        assertTrue("element doesnt have anonymous type",
                elementDecl.getAnonymousTypeDefinition() instanceof XSDComplexTypeDefinition);
        deleteElement(modelRoot, structureType, element);
        assertEquals(0, structureType.getElements("elementParticle").size());
        // add an element in element declaration with complexType reference
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(true,
                "elementWithComplexTypeRef");
        assertEquals(0, structureType.getAllElements().size());
        elementDecl = (XSDElementDeclaration) structureType.getComponent();
        assertNull("element has anonymous type", elementDecl.getAnonymousTypeDefinition());
        element = addElement(modelRoot, structureType, "elementParticle");
        assertEquals(1, structureType.getAllElements().size());
        assertEquals(1, structureType.getElements("elementParticle").size());
        assertNotNull("element doesnt have anonymous type", elementDecl.getAnonymousTypeDefinition());
        assertNull("referred type name is not null", elementDecl.getTypeDefinition().getName());
        assertTrue("element doesnt have anonymous type",
                elementDecl.getAnonymousTypeDefinition() instanceof XSDComplexTypeDefinition);
        deleteElement(modelRoot, structureType, "elementParticle");
        assertEquals(0, structureType.getElements("elementParticle").size());
        // add an element in element declaration with open content
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(true, "openContentElement");
        assertEquals(0, structureType.getAllElements().size());
        elementDecl = (XSDElementDeclaration) structureType.getComponent();
        assertNull("element has anonymous type", elementDecl.getAnonymousTypeDefinition());
        element = addElement(modelRoot, structureType, "elementParticle");
        assertEquals(1, structureType.getAllElements().size());
        assertEquals(1, structureType.getElements("elementParticle").size());
        assertNotNull("element doesnt have anonymous type", elementDecl.getAnonymousTypeDefinition());
        assertNull("referred type name is not null", elementDecl.getTypeDefinition().getName());
        assertTrue("element doesnt have anonymous type",
                elementDecl.getAnonymousTypeDefinition() instanceof XSDComplexTypeDefinition);
        deleteElement(modelRoot, structureType, element);
        assertEquals(0, structureType.getElements("elementParticle").size());
        // add an element in element declaration with anonymous simpleType
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(true,
                "elementWithAnonymousSimpleType");
        assertEquals(0, structureType.getAllElements().size());
        elementDecl = (XSDElementDeclaration) structureType.getComponent();
        assertNotNull("element doesnt have anonymous type", elementDecl.getAnonymousTypeDefinition());
        assertTrue("element doesnt have anonymous type",
                elementDecl.getAnonymousTypeDefinition() instanceof XSDSimpleTypeDefinition);
        element = addElement(modelRoot, structureType, "elementParticle");
        assertEquals(1, structureType.getAllElements().size());
        assertEquals(1, structureType.getElements("elementParticle").size());
        assertNotNull("element doesnt have anonymous type", elementDecl.getAnonymousTypeDefinition());
        assertTrue("element doesnt have anonymous type",
                elementDecl.getAnonymousTypeDefinition() instanceof XSDComplexTypeDefinition);
        assertEquals("", structureType.getDocumentation());
        setDocumentation(modelRoot, structureType, "Documentation");
        assertEquals("Documentation", structureType.getDocumentation());
        deleteElement(modelRoot, structureType, "elementParticle");
        assertEquals(0, structureType.getElements("elementParticle").size());
        // add an element in complexType with complexType extension
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "ctExtension");
        assertEquals(4, structureType.getAllElements().size());
        assertEquals(0, structureType.getElements("elementParticle5").size());
        element = addElement(modelRoot, structureType, "elementParticle5");
        assertEquals(5, structureType.getAllElements().size());
        assertEquals(1, structureType.getElements("elementParticle5").size());
        deleteElement(modelRoot, structureType, "elementParticle3");
        assertEquals(0, structureType.getElements("elementParticle3").size());
        deleteElement(modelRoot, structureType, element);
        assertEquals(0, structureType.getElements("elementParticle5").size());
        // add an element in complexType with complexType restriction
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "ctRestriction");
        assertEquals(2, structureType.getAllElements().size());
        assertEquals(0, structureType.getElements("elementParticle2").size());
        element = addElement(modelRoot, structureType, "elementParticle2");
        assertEquals(3, structureType.getAllElements().size());
        assertEquals(1, structureType.getElements("elementParticle2").size());
        deleteElement(modelRoot, structureType, "elementParticle1");
        assertEquals(0, structureType.getElements("elementParticle1").size());
        deleteElement(modelRoot, structureType, element);
        assertEquals(0, structureType.getElements("elementParticle2").size());
        // add an element in element with complexType with complexType extension
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(true, "elementCtExtension");
        assertEquals(4, structureType.getAllElements().size());
        assertEquals(0, structureType.getElements("elementParticle5").size());
        element = addElement(modelRoot, structureType, "elementParticle5");
        assertEquals(5, structureType.getAllElements().size());
        assertEquals(1, structureType.getElements("elementParticle5").size());
        deleteElement(modelRoot, structureType, "elementParticle3");
        assertEquals(0, structureType.getElements("elementParticle3").size());
        deleteElement(modelRoot, structureType, element);
        assertEquals(0, structureType.getElements("elementParticle5").size());
        // add an element in complexType with simpleContent extension
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false,
                "ctSimpleContentExtension");
        XSDComplexTypeDefinition complexType = (XSDComplexTypeDefinition) structureType.getComponent();
        assertTrue("ComplexType doesnt have simple content", complexType.getContent() instanceof XSDSimpleTypeDefinition);
        assertEquals(3, structureType.getAllElements().size());
        assertEquals(0, structureType.getElements("elementParticle").size());
        element = addElement(modelRoot, structureType, "elementParticle");
        assertEquals(4, structureType.getAllElements().size());
        assertEquals(1, structureType.getElements("elementParticle").size());
        assertTrue("ComplexType doesnt have simple content", complexType.getContent() instanceof XSDParticle);
        deleteElement(modelRoot, structureType, element);
        assertEquals(0, structureType.getElements("elementParticle").size());
        // add an element in complexType with simpleContent restriction
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false,
                "ctSimpleContentRestriction");
        complexType = (XSDComplexTypeDefinition) structureType.getComponent();
        assertTrue("ComplexType doesnt have simple content", complexType.getContent() instanceof XSDSimpleTypeDefinition);
        assertEquals(2, structureType.getAllElements().size());
        assertEquals(0, structureType.getElements("elementParticle").size());
        element = addElement(modelRoot, structureType, "elementParticle");
        assertEquals(3, structureType.getAllElements().size());
        assertEquals(1, structureType.getElements("elementParticle").size());
        assertTrue("ComplexType doesnt have simple content", complexType.getContent() instanceof XSDParticle);
        deleteElement(modelRoot, structureType, element);
        assertEquals(0, structureType.getElements("elementParticle").size());
        // add an element in complexType with sequence
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "nestedElements");
        assertEquals(8, structureType.getAllElements().size());
        assertEquals(0, structureType.getElements("elementParticle5").size());
        element = addElement(modelRoot, structureType, "elementParticle5");
        assertEquals(9, structureType.getAllElements().size());
        assertEquals(1, structureType.getElements("elementParticle5").size());
        deleteElement(modelRoot, structureType, "elementParticle1");
        assertEquals(0, structureType.getElements("elementParticle1").size());
        deleteElement(modelRoot, structureType, element);
        assertEquals(0, structureType.getElements("elementParticle5").size());

        // Test Removal
        modelRoot = copyAndGetModelRoot("StructureType.wsdl", "TestStructureType1.wsdl");
        description = modelRoot.getDescription();
        schema = description.getSchema("http://www.example.org/StructureType/")[0];
        // Remove an element in complexType with sequence
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "complexType");
        assertEquals(1, structureType.getElements("elementParticle1").size());
        deleteElement(modelRoot, structureType, "elementParticle1");
        assertEquals(0, structureType.getElements("elementParticle1").size());
        // Remove an element in complexType with choice
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema
                .getType(false, "groupDefinitionType");
        assertEquals(1, structureType.getElements("elementParticle").size());
        deleteElement(modelRoot, structureType, "elementParticle");
        assertEquals(0, structureType.getElements("elementParticle").size());
        // Remove an element in complexType with all
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "allModelGroupType");

        assertEquals(1, structureType.getElements("elementParticle").size());
        deleteElement(modelRoot, structureType, "elementParticle");
        assertEquals(0, structureType.getElements("elementParticle").size());
        // Remove an element in element declaration with sequence
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(true, "complexType");

        assertEquals(1, structureType.getElements("elementParticle").size());
        deleteElement(modelRoot, structureType, "elementParticle");
        assertEquals(0, structureType.getElements("elementParticle").size());
        // Remove an element in complexType with complexType extension
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "ctExtension");

        assertEquals(1, structureType.getElements("elementParticle3").size());
        deleteElement(modelRoot, structureType, "elementParticle3");
        assertEquals(0, structureType.getElements("elementParticle3").size());
        // Remove an element in complexType with complexType restriction
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "ctRestriction");

        assertEquals(1, structureType.getElements("elementParticle1").size());
        deleteElement(modelRoot, structureType, "elementParticle1");
        assertEquals(0, structureType.getElements("elementParticle1").size());
        // Remove an element in element with complexType with complexType
        // extension
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(true, "elementCtExtension");

        assertEquals(1, structureType.getElements("elementParticle3").size());
        deleteElement(modelRoot, structureType, "elementParticle3");
        assertEquals(0, structureType.getElements("elementParticle3").size());
        // Remove an element in complexType with simpleContent extension
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false,
                "ctSimpleContentExtension");

        assertEquals(1, structureType.getElements("attribute1").size());
        deleteElement(modelRoot, structureType, "attribute1");
        assertEquals(0, structureType.getElements("attribute1").size());
        // Remove an element in complexType with simpleContent restriction
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false,
                "ctSimpleContentRestriction");

        assertEquals(1, structureType.getElements("attribute1").size());
        deleteElement(modelRoot, structureType, "attribute1");
        assertEquals(0, structureType.getElements("attribute1").size());
        // Remove an element in complexType with sequence
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "nestedElements");

        assertEquals(1, structureType.getElements("attribute1").size());
        deleteElement(modelRoot, structureType, "elementParticle1");
        assertEquals(0, structureType.getElements("elementParticle1").size());
        assertEquals(1, structureType.getElements("attribute1").size());
        deleteElement(modelRoot, structureType, "attribute1");
        assertEquals(0, structureType.getElements("attribute1").size());

        // Test Set baseType
        modelRoot = copyAndGetModelRoot("StructureType.wsdl", "TestStructureType2.wsdl");
        description = modelRoot.getDescription();
        schema = description.getSchema("http://www.example.org/StructureType/")[0];
        // set ST as baseType for element with ST Reference
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(true,
                "elementWithSimpleTypeRef");

        elementDecl = (XSDElementDeclaration) structureType.getComponent();
        setBaseType(modelRoot, structureType, schema.getType(false, "simpleType"));
        assertNotNull("anonymous simpleType must be created as type of this element", elementDecl.getAnonymousTypeDefinition());
        assertTrue("Anonymous type must be simpleType",
                elementDecl.getAnonymousTypeDefinition() instanceof XSDSimpleTypeDefinition);
        assertEquals("simpleType", elementDecl.getAnonymousTypeDefinition().getBaseType().getName());
        // set CT as baseType for element with CT Reference
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(true,
                "elementWithComplexTypeRef");

        elementDecl = (XSDElementDeclaration) structureType.getComponent();
        setBaseType(modelRoot, structureType, schema.getType(false, "complexType"));
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

        elementDecl = (XSDElementDeclaration) structureType.getComponent();

        setBaseType(modelRoot, structureType, schema.getType(false, "complexType"));
        assertNotNull("anonymous complexType must be created as type of this element", elementDecl.getAnonymousTypeDefinition());
        assertTrue("Anonymous type must be simpleType",
                elementDecl.getAnonymousTypeDefinition() instanceof XSDComplexTypeDefinition);
        assertEquals("complexType", elementDecl.getAnonymousTypeDefinition().getBaseType().getName());
        assertTrue(XSDDerivationMethod.EXTENSION_LITERAL.equals(((XSDComplexTypeDefinition) elementDecl
                .getAnonymousTypeDefinition()).getDerivationMethod()));
        // set ST as baseType for element with CT Reference
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(true,
                "elementWithComplexTypeRef");

        elementDecl = (XSDElementDeclaration) structureType.getComponent();
        setBaseType(modelRoot, structureType, schema.getType(false, "simpleType"));
        assertNotNull("anonymous simpleType must be created as type of this element", elementDecl.getAnonymousTypeDefinition());
        assertTrue("Anonymous type must be simpleType",
                elementDecl.getAnonymousTypeDefinition() instanceof XSDSimpleTypeDefinition);
        assertEquals("simpleType", elementDecl.getAnonymousTypeDefinition().getBaseType().getName());

        modelRoot = copyAndGetModelRoot("StructureType.wsdl", "TestStructureTypeOne.wsdl");
        description = modelRoot.getDescription();
        schema = description.getSchema("http://www.example.org/StructureType/")[0];
        // set ST as baseType for element with Anonymous ST
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(true, "globalElementWithSt");

        elementDecl = (XSDElementDeclaration) structureType.getComponent();
        setBaseType(modelRoot, structureType, schema.getType(false, "simpleType"));
        assertNotNull("anonymous simpleType must be created as type of this element", elementDecl.getAnonymousTypeDefinition());
        assertTrue("Anonymous type must be simpleType",
                elementDecl.getAnonymousTypeDefinition() instanceof XSDSimpleTypeDefinition);
        assertEquals("simpleType", elementDecl.getAnonymousTypeDefinition().getBaseType().getName());
        // set ST as baseType for element with Anonymous CT
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(true, "complexType");

        elementDecl = (XSDElementDeclaration) structureType.getComponent();
        setBaseType(modelRoot, structureType, schema.getType(false, "simpleType"));
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

        elementDecl = (XSDElementDeclaration) structureType.getComponent();
        setBaseType(modelRoot, structureType, schema.getType(false, "complexType"));
        assertNotNull("anonymous complexType must be created as type of this element", elementDecl.getAnonymousTypeDefinition());
        assertTrue("Anonymous type must be complexType",
                elementDecl.getAnonymousTypeDefinition() instanceof XSDComplexTypeDefinition);
        assertEquals("complexType", elementDecl.getAnonymousTypeDefinition().getBaseType().getName());
        // set CT as baseType for element with Anonymous CT
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(true, "complexType");

        elementDecl = (XSDElementDeclaration) structureType.getComponent();
        setBaseType(modelRoot, structureType, schema.getType(false, "complexType"));
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

        complexType = (XSDComplexTypeDefinition) structureType.getComponent();
        setBaseType(modelRoot, structureType, schema.getType(false, "simpleType"));
        assertTrue("content must be complexType", complexType.getContent() instanceof XSDSimpleTypeDefinition);
        assertEquals("simpleType", complexType.getBaseType().getName());
        assertEquals(XSDDerivationMethod.EXTENSION_LITERAL, complexType.getDerivationMethod());
        // set ST as baseType for CT with ctExtension
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "ctExtension");

        complexType = (XSDComplexTypeDefinition) structureType.getComponent();
        setBaseType(modelRoot, structureType, schema.getType(false, "simpleType"));
        assertTrue("content must be complexType", complexType.getContent() instanceof XSDSimpleTypeDefinition);
        assertEquals("simpleType", complexType.getBaseType().getName());
        assertEquals(XSDDerivationMethod.EXTENSION_LITERAL, complexType.getDerivationMethod());
        // set ST as baseType for CT with Restriction
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "ctRestriction");

        complexType = (XSDComplexTypeDefinition) structureType.getComponent();
        setBaseType(modelRoot, structureType, schema.getType(false, "simpleType"));
        assertTrue("content must be complexType", complexType.getContent() instanceof XSDSimpleTypeDefinition);
        assertEquals("simpleType", complexType.getBaseType().getName());
        assertEquals(XSDDerivationMethod.EXTENSION_LITERAL, complexType.getDerivationMethod());

        modelRoot = copyAndGetModelRoot("StructureType.wsdl", "TestStructureTypeFive.wsdl");
        description = modelRoot.getDescription();
        schema = description.getSchema("http://www.example.org/StructureType/")[0];
        // set ST as baseType for CT with Sequence
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "complexType");

        complexType = (XSDComplexTypeDefinition) structureType.getComponent();
        setBaseType(modelRoot, structureType, schema.getType(false, "complexType"));
        assertEquals("complexType", complexType.getBaseType().getName());
        assertEquals(XSDDerivationMethod.RESTRICTION_LITERAL, complexType.getDerivationMethod());
        // set ST as baseType for CT with ctExtension
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "ctExtension");

        complexType = (XSDComplexTypeDefinition) structureType.getComponent();
        setBaseType(modelRoot, structureType, schema.getType(false, "complexType"));
        assertEquals("complexType", complexType.getBaseType().getName());
        assertEquals(XSDDerivationMethod.EXTENSION_LITERAL, complexType.getDerivationMethod());
        // set ST as baseType for CT with Restriction
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "ctRestriction");

        complexType = (XSDComplexTypeDefinition) structureType.getComponent();
        setBaseType(modelRoot, structureType, schema.getType(false, "complexType"));
        assertEquals("complexType", complexType.getBaseType().getName());
        assertEquals(XSDDerivationMethod.RESTRICTION_LITERAL, complexType.getDerivationMethod());

        modelRoot = getWSDLModelRoot("TestStructureTypeFive.wsdl");
        description = modelRoot.getDescription();
        schema = description.getSchema("http://www.example.org/StructureType/")[0];
        // set CT as baseType for CT with simpleContent extension
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false,
                "ctSimpleContentExtension");

        complexType = (XSDComplexTypeDefinition) structureType.getComponent();
        setBaseType(modelRoot, structureType, schema.getType(false, "complexType"));
        assertTrue("content must be XSDParticle", complexType.getContent() instanceof XSDParticle);
        assertEquals("complexType", complexType.getBaseType().getName());
        assertEquals(XSDDerivationMethod.EXTENSION_LITERAL, complexType.getDerivationMethod());
        // set CT as baseType for CT with simpleContent restriction
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false,
                "ctSimpleContentRestriction");

        complexType = (XSDComplexTypeDefinition) structureType.getComponent();
        setBaseType(modelRoot, structureType, schema.getType(false, "complexType"));
        assertTrue("content must be XSDParticle", complexType.getContent() instanceof XSDParticle);
        assertEquals("complexType", complexType.getBaseType().getName());
        assertEquals(XSDDerivationMethod.RESTRICTION_LITERAL, complexType.getDerivationMethod());

        modelRoot = getWSDLModelRoot("TestStructureTypeFive.wsdl");
        description = modelRoot.getDescription();
        schema = description.getSchema("http://www.example.org/StructureType/")[0];
        // set ST as baseType for CT with simpleContent extension
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false,
                "ctSimpleContentExtension");

        complexType = (XSDComplexTypeDefinition) structureType.getComponent();
        setBaseType(modelRoot, structureType, schema.getType(false, "simpleType"));
        assertTrue("content must be simpleType", complexType.getContent() instanceof XSDSimpleTypeDefinition);
        assertEquals("simpleType", complexType.getBaseType().getName());
        assertEquals(XSDDerivationMethod.EXTENSION_LITERAL, complexType.getDerivationMethod());
        // set ST as baseType for CT with simpleContent restriction
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false,
                "ctSimpleContentRestriction");

        complexType = (XSDComplexTypeDefinition) structureType.getComponent();
        setBaseType(modelRoot, structureType, schema.getType(false, "simpleType"));
        assertTrue("content must be simpleType", complexType.getContent() instanceof XSDSimpleTypeDefinition);
        assertEquals("simpleType", complexType.getBaseType().getName());
        assertEquals(XSDDerivationMethod.EXTENSION_LITERAL, complexType.getDerivationMethod());
        assertEquals(false, structureType.isAnonymous());

        // TODO Howto implement setAnonymous?
        // wStructureType.setAnonymous(true);
    }

    @Test
    public void testElement() throws Exception {
        IWsdlModelRoot modelRoot = copyAndGetModelRoot("StructureType.wsdl", "TestElement.wsdl");
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription description;
        org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema schema;
        org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType structureType;
        description = modelRoot.getDescription();

        description = modelRoot.getDescription();
        schema = description.getSchema("http://www.example.org/StructureType/")[0];
        assertNotNull("Could not find schema http://www.example.org/StructureType/", schema);
        // Test documentation and name
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "complexType");
        IElement element = structureType.getElements("elementParticle").iterator().next();
        assertEquals("elementParticle", element.getName());
        renameElement(modelRoot, element, "newName");
        assertEquals("newName", element.getName());
        assertEquals("Documentation", element.getDocumentation());
        setDocumentation(modelRoot, element, "NewDocumentation");
        assertEquals("NewDocumentation", element.getDocumentation());
        assertEquals(1, element.getMinOccurs());
        setMinOccurs(modelRoot, element, 2);
        assertEquals(2, element.getMinOccurs());
        assertEquals(1, element.getMaxOccurs());
        setMaxOccurs(modelRoot, element, 4);
        assertEquals(4, element.getMaxOccurs());
        assertEquals(false, element.getNillable());
        setNillable(modelRoot, element, true);
        assertEquals(true, element.getNillable());

        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "ctWithAttributes");
        element = structureType.getElements("elementParticle3").iterator().next();
        assertEquals("", element.getDocumentation());
        setDocumentation(modelRoot, element, "NewDocumentation");
        assertEquals("NewDocumentation", element.getDocumentation());
        element = structureType.getElements("globalElement").iterator().next();
        assertEquals(10, element.getMinOccurs());
        setMinOccurs(modelRoot, element, 13);
        assertEquals(13, element.getMinOccurs());
        assertEquals(100, element.getMaxOccurs());
        setMaxOccurs(modelRoot, element, 26);
        assertEquals(26, element.getMaxOccurs());
        assertEquals(false, element.getNillable());
        setNillable(modelRoot, element, true);
        assertEquals(true, element.getNillable());
        assertEquals("", element.getDocumentation());
        setDocumentation(modelRoot, element, "NewDocumentation");
        assertEquals("NewDocumentation", element.getDocumentation());
        element = structureType.getElements("attribute1").iterator().next();
        assertEquals("", element.getDocumentation());
        setDocumentation(modelRoot, element, "NewDocumentation");
        assertEquals("NewDocumentation", element.getDocumentation());
        assertEquals(0, element.getMinOccurs());
        setMinOccurs(modelRoot, element, 2, IStatus.ERROR);
        assertEquals(0, element.getMinOccurs());
        assertEquals(1, element.getMaxOccurs());
        // //fails with exception instead of fail with an error
        // setMaxOccurs(modelRoot, element, 4, IStatus.ERROR);
        assertEquals(1, element.getMaxOccurs());
        assertEquals(false, element.getNillable());
        // //fails with exception instead of fail with an error in status
        // setNillable(modelRoot, element, true, IStatus.ERROR);
        assertEquals(false, element.getNillable());

        modelRoot = getWSDLModelRoot("TestElement.wsdl");
        description = modelRoot.getDescription();
        schema = description.getSchema("http://www.example.org/StructureType/")[0];
        assertNotNull("Could not find schema http://www.example.org/StructureType/", schema);
        // Test type assignment for element
        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false, "ctWithAttributes");
        element = structureType.getElements("elementParticle3").iterator().next();
        assertEquals("string", element.getType().getName());
        setType(modelRoot, element, schema.getType(false, "complexType"));
        assertEquals("complexType", element.getType().getName());
        assertEquals("elementParticle3", element.getName());
        setType(modelRoot, element, schema.getType(false, "simpleType"));
        assertEquals("simpleType", element.getType().getName());
        assertEquals("elementParticle3", element.getName());
        setElementAnonymousType(((AbstractXSDComponent) schema).getModelRoot(), element, true);
        setElementAnonymousType(((AbstractXSDComponent) schema).getModelRoot(), element, false);

        element = structureType.getElements("globalElement").iterator().next();
        assertEquals("globalElement", element.getType().getName());
        setType(modelRoot, element, schema.getType(false, "complexType"));
        assertEquals("complexType", element.getType().getName());
        assertEquals("globalElement", element.getName());
        setType(modelRoot, element, schema.getType(false, "simpleType"));
        assertEquals("simpleType", element.getType().getName());
        assertEquals("globalElement", element.getName());
        setElementAnonymousType(((AbstractXSDComponent) schema).getModelRoot(), element, true);
        setElementAnonymousType(((AbstractXSDComponent) schema).getModelRoot(), element, false);

        element = structureType.getElements("attribute1").iterator().next();
        assertEquals("string", element.getType().getName());
        setType(modelRoot, element, schema.getType(false, "complexType"));
        assertEquals("string", element.getType().getName());
        assertEquals("attribute1", element.getName());
        setType(modelRoot, element, schema.getType(false, "simpleType"));
        assertEquals("simpleType", element.getType().getName());
        assertEquals("attribute1", element.getName());
        setElementAnonymousType(((AbstractXSDComponent) schema).getModelRoot(), element, true);
        // setElementAnonymousType(((AbstractXSDComponent)schema).getModelRoot(),
        // element, false);

        structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema.getType(false,
                "ctSimpleContentExtension");
        element = structureType.getElements("globalAttribute").iterator().next();
        assertNull("Type must be null for attributes", element.getType());
        setType(modelRoot, element, schema.getType(false, "complexType"));
        assertNull("ComplexType must not be set", element.getType());
        assertEquals("globalAttribute", element.getName());
        setType(modelRoot, element, schema.getType(false, "simpleType"));
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
        final org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType structureType = (org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType) schema
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

    public void assertCopyWithNewFile(final String targetFile, final String sourceFile, final String targetNs,
            final String sourceNs, final String typeName) throws Exception {
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription description;
        org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema schema;
        IWsdlModelRoot modelRoot;

        modelRoot = getWSDLModelRoot(targetFile);
        description = modelRoot.getDescription();
        schema = description.getSchema(targetNs)[0];
        assertNotNull(schema);
    }

    public void assertCopyElement(final org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType structureType,
            final IElement element) throws IllegalInputException {
        try {
            final ISchema schema = structureType.getParent();
            final IXSDModelRoot modelRoot = ((AbstractXSDComponent) schema).getModelRoot();
            final CopyElementCommand cmd = new CopyElementCommand(modelRoot, structureType, element, schema);
            assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
        } catch (final ExecutionException e) {
            throw new IllegalStateException("The command execution failed. See nested exception", e);
        }
        assertTrue(structureType.getElements(element.getName()).size() > 0);
    }

    private org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema getSchema(
            final Collection<org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema> schemas,
            final ICondition<org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema> condition) {
        for (final org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema schema : schemas) {
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

        public boolean isSatisfied(final org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema in) {
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

    private IWsdlModelRoot getWSDLModelRoot(final String fileName) {
        final IFile file = (IFile) getProject().findMember(new Path(Document_FOLDER_NAME + IPath.SEPARATOR + fileName));
        assertTrue(file.exists());
        final IWsdlModelRoot modelRoot = ModelManager.getInstance().getWsdlModelRoot(new FileEditorInput(file));
        setupEnvironment(modelRoot);
        return modelRoot;
    }

    private static void setNamespace(final IModelRoot modelRoot,
            final org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema schema, final String namespace) throws ExecutionException {
        final SetNamespaceCommand cmd = new SetNamespaceCommand(modelRoot, schema, namespace);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
    }

    private static void createGlobalTypeFromAnonymous(final IModelRoot modelRoot,
            final org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema schema, final IElement element, final String name)
            throws ExecutionException {
        final CreateGlobalTypeFromAnonymousCommand cmd = new CreateGlobalTypeFromAnonymousCommand(modelRoot, schema, element,
                name);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
    }

    private static org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType addSimpleType(final IXSDModelRoot modelRoot,
            final org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema schema, final String name) throws ExecutionException {
        final AddSimpleTypeCommand cmd = new AddSimpleTypeCommand(modelRoot, schema, name);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
        return cmd.getSimpleType();
    }

    private static org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType addStructureType(final IXSDModelRoot modelRoot,
            final org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema schema, final String name, final boolean element,
            final AbstractType referencedType) throws ExecutionException {
        final AddStructureTypeCommand cmd = new AddStructureTypeCommand(modelRoot, schema, name, element, referencedType);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
        return cmd.getStructureType();
    }

    private static final void removeType(final IModelRoot modelRoot,
            final org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema schema, final IType type) throws ExecutionException {
        final RemoveTypeCommand cmd = new RemoveTypeCommand(modelRoot, schema, type);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
    }

    private static final void renameSimpleType(final IModelRoot modelRoot,
            final org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType namedObject, final String name)
            throws ExecutionException {
        final RenameNamedComponent cmd = new RenameNamedComponent(modelRoot, namedObject, name);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
    }

    private static final void setMinLength(final IModelRoot modelRoot,
            final org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType type, final String length) throws ExecutionException {
        final AddFacetCommand cmd = FacetsCommandFactory.createAddMinLengthFacetCommand(modelRoot, type, length);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
    }

    private static final void setMaxLength(final IModelRoot modelRoot,
            final org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType type, final String length) throws ExecutionException {
        final AddFacetCommand cmd = FacetsCommandFactory.createAddMaxLengthFacetCommand(modelRoot, type, length);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
    }

    private static void addEnumeration(final IModelRoot modelRoot,
            final org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType type, final String value) throws ExecutionException {
        final AddFacetCommand cmd = FacetsCommandFactory.createAddEnumerationFacetCommand(modelRoot, type, value);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
    }

    private static void deleteEnumeration(final IModelRoot modelRoot,
            final org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType type, final IFacet facet) throws ExecutionException {
        final DeleteFacetCommand cmd = FacetsCommandFactory.createDeleteEnumerationFacetCommand(modelRoot, type, facet);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
    }

    private static void addPattern(final IModelRoot modelRoot,
            final org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType type, final String value) throws ExecutionException {
        final AddFacetCommand cmd = FacetsCommandFactory.createAddPatternFacetCommand(modelRoot, type, value);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
    }

    private static void deletePattern(final IModelRoot modelRoot,
            final org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType type, final IFacet facet) throws ExecutionException {
        final DeleteFacetCommand cmd = FacetsCommandFactory.createDeletePatternFacetCommand(modelRoot, type, facet);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
    }

    private static final void setMinInclusive(final IModelRoot modelRoot,
            final org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType type, final String length) throws ExecutionException {
        final AddFacetCommand cmd = FacetsCommandFactory.createAddMinInclusiveFacetCommand(modelRoot, type, length);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
    }

    private static final void setMaxInclusive(final IModelRoot modelRoot,
            final org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType type, final String length) throws ExecutionException {
        final AddFacetCommand cmd = FacetsCommandFactory.createAddMaxInclusiveFacetCommand(modelRoot, type, length);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
    }

    private static final void setMinExclusive(final IModelRoot modelRoot,
            final org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType type, final String length) throws ExecutionException {
        final AddFacetCommand cmd = FacetsCommandFactory.createAddMinExclusiveFacetCommand(modelRoot, type, length);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
    }

    private static final void setMaxExclusive(final IModelRoot modelRoot,
            final org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType type, final String length) throws ExecutionException {
        final AddFacetCommand cmd = FacetsCommandFactory.createAddMaxExclusiveFacetCommand(modelRoot, type, length);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
    }

    private static final void setLength(final IModelRoot modelRoot,
            final org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType type, final String length) throws ExecutionException {
        final AddFacetCommand cmd = FacetsCommandFactory.createAddLengthFacetCommand(modelRoot, type, length);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
    }

    private static void setBaseType(final IModelRoot modelRoot, final IType type, final IType baseType) throws ExecutionException {
        setBaseType(modelRoot, type, baseType, IStatus.OK);
    }

    private static void setBaseType(final IModelRoot modelRoot, final IType type, final IType baseType,
            final int expectedStatusSeverity) throws ExecutionException {
        final SetBaseTypeCommand cmd = new SetBaseTypeCommand(modelRoot, type, baseType);
        final IStatus status = modelRoot.getEnv().execute(cmd);
        assertEquals(status.toString(), expectedStatusSeverity, status.getSeverity());
    }

    public static void rename(final IModelRoot modelRoot, final INamedObject namedObject, final String name)
            throws ExecutionException {
        final RenameNamedComponent cmd = new RenameNamedComponent(modelRoot, namedObject, name);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
    }

    public static IElement addElement(final IModelRoot modelRoot,
            final org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType type, final String name) throws ExecutionException {
        final AddElementCommand cmd = new AddElementCommand(modelRoot, type, name);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
        return cmd.getElement();
    }

    public static void deleteElement(final IModelRoot modelRoot,
            final org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType type, final IElement element)
            throws ExecutionException {
        final DeleteElementCommand cmd = new DeleteElementCommand(modelRoot, type, element);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
    }

    public static void renameElement(final IModelRoot modelRoot, final IElement element, final String name)
            throws ExecutionException {
        final RenameElementCommand cmd = new RenameElementCommand(modelRoot, element, name);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
    }

    public static void deleteElement(final IModelRoot modelRoot,
            final org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType type, final String elementName)
            throws ExecutionException {
        final IElement element = type.getElements(elementName).iterator().next();
        final DeleteElementCommand cmd = new DeleteElementCommand(modelRoot, type, element);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
    }

    public static void setAnonymous(final IXSDModelRoot modelRoot, final IElement element, final AbstractType type,
            final boolean isSimple) throws ExecutionException {
        final SetAnonymousCommand cmd = new SetAnonymousCommand(modelRoot, element, type, isSimple);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
    }

    private static void setMinOccurs(final IModelRoot modelRoot, final IElement element, final int value,
            final int expectedSeverity) throws ExecutionException {
        final SetElementOccurences cmd = new SetElementOccurences(modelRoot, element, value, element.getMaxOccurs());
        final IStatus status = modelRoot.getEnv().execute(cmd);
        assertEquals(expectedSeverity, status.getSeverity());
    }

    private static void setMinOccurs(final IModelRoot modelRoot, final IElement element, final int value)
            throws ExecutionException {
        setMinOccurs(modelRoot, element, value, Status.OK);
    }

    private static void setMaxOccurs(final IModelRoot modelRoot, final IElement element, final int value,
            final int expectedSeverity) throws ExecutionException {
        final SetElementOccurences cmd = new SetElementOccurences(modelRoot, element, element.getMinOccurs(), value);
        final IStatus status = modelRoot.getEnv().execute(cmd);
        assertEquals(expectedSeverity, status.getSeverity());
    }

    private static void setMaxOccurs(final IModelRoot modelRoot, final IElement element, final int value)
            throws ExecutionException {
        setMaxOccurs(modelRoot, element, value, IStatus.OK);
    }

    private static void setNillable(final IModelRoot modelRoot, final IElement element, final boolean nillable,
            final int expectedSeverity) throws ExecutionException {
        final SetElementNillableCommand cmd = new SetElementNillableCommand(modelRoot, element, nillable);
        final IStatus status = modelRoot.getEnv().execute(cmd);
        assertEquals(expectedSeverity, status.getSeverity());
    }

    private static void setNillable(final IModelRoot modelRoot, final IElement element, final boolean nillable)
            throws ExecutionException {
        setNillable(modelRoot, element, nillable, IStatus.OK);
    }

    private static void setType(final IModelRoot modelRoot, final IElement element, final IType type) throws ExecutionException {
        final SetElementTypeCommand cmd = new SetElementTypeCommand(modelRoot, element, type);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
    }

    private static void setElementAnonymousType(final IXSDModelRoot modelRoot, final IElement element, final boolean anonymous)
            throws ExecutionException {
        final SetAnonymousCommand cmd = new SetAnonymousCommand(modelRoot, element, (AbstractType) element.getType(), anonymous);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
    }

    private static void setDocumentation(final IModelRoot modelRoot, final IModelObject modelObject, final String text)
            throws ExecutionException {
        final SetDocumentationCommand cmd = new SetDocumentationCommand(modelRoot, modelObject, text);
        assertEquals(Status.OK_STATUS, modelRoot.getEnv().execute(cmd));
    }
}