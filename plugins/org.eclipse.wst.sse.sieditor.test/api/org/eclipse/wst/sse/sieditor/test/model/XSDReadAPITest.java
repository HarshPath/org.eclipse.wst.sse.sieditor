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

import static org.eclipse.wst.sse.sieditor.test.util.SIEditorUtils.validateNamedComponents;

import java.util.Arrays;
import java.util.Collection;

import junit.framework.Assert;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.command.emf.xsd.RenameNamedComponent;
import org.eclipse.wst.sse.sieditor.core.common.IEnvironment;
import org.eclipse.wst.sse.sieditor.core.editorfwk.ModelHandler;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IFacet;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

@SuppressWarnings("nls")
public class XSDReadAPITest extends SIEditorBaseTest {

    protected static final String DATA_PUBLIC_SELF_GF_REL_PATH = "pub/self/mix2/";

    protected String getProjectName() {
        return "XSDReadAPITests";
    }

    @Test
    public void test_Schema() throws Exception {
        final IFile file = ResourceUtils.copyFileIntoTestProject("pub/self/mix2/ChangePurchaseOrder_WSD.wsdl",
                Document_FOLDER_NAME, this.getProject(), "One.wsdl");
        final ResourceSetImpl resourceSet = new ResourceSetImpl();
        IWsdlModelRoot root = (IWsdlModelRoot) ModelHandler.retrieveModelObject(resourceSet, URI.createFileURI(file.getLocation()
                .toFile().getAbsolutePath()), false);
        setupEnvironment(root);

        final IDescription description = root.getDescription();
        Collection<ISchema> containedSchemas = description.getContainedSchemas();
        description.getContainedSchemas();
        assertEquals(2, containedSchemas.size());

        ISchema[] schemas = description.getSchema("http://sap.com/xi/SAPGlobal20/Global");
        assertNotNull(schemas);
        assertEquals(1, schemas.length);

        ISchema schema = schemas[0];
        Assert.assertEquals("http://sap.com/xi/SAPGlobal20/Global", schema.getNamespace()); // @Test-0030

        Collection<IType> types = schema.getAllContainedTypes();
        Assert.assertEquals(3, types.size()); // @Test-0031
        validateNamedComponents(types, Arrays.asList("StandardMessageFault", "PurchaseOrderChangeRequest_sync",
                "PurchaseOrderChangeConfirmation_sync"));

        Collection<ISchema> referredSchemas = schema.getAllReferredSchemas();
        assertEquals(2, referredSchemas.size());

        IType type = schema.getType(true, "PurchaseOrderChangeRequest_sync");
        assertNotNull("Null returned for type PurchaseOrderChangeRequest_sync" + type);
        type = schema.getType(true, "StandardMessageFault");
        assertNotNull("Null returned for type StandardMessageFault" + type);

        schemas = description.getSchema("http://sap.com/xi/APPL/SE/Global");
        assertNotNull(schemas);
        assertEquals(1, schemas.length);

        schema = schemas[0];
        Assert.assertEquals("http://sap.com/xi/APPL/SE/Global", schema.getNamespace()); // @Test-0030

        types = schema.getAllContainedTypes();
        Assert.assertEquals(67, types.size()); // @Test-0031

        referredSchemas = schema.getAllReferredSchemas();
        assertNotNull("Empty collection must be returned when there are no referred schems", referredSchemas);
        assertEquals(1, referredSchemas.size());
        schema.getAllReferredSchemas();

        type = schema.getType(false, "LocationInternalIDContent");
        assertNotNull("Null returned for type LocationInternalIDContent" + type);
        type = schema.getType(false, "ExchangeFaultData");
        assertNotNull("Null returned for type ExchangeFaultData" + type);
        type = schema.getType(false, "PurchaseOrderChangeConfirmationMessage_sync");
        assertNotNull("Null returned for type PurchaseOrderChangeConfirmationMessage_sync" + type);
        type = schema.getType(true, "StandardMessageFault");
        assertNotNull("Null returned for element StandardMessageFault" + type);
        type = schema.getType(false, "adsdfsfdsxyz");
        assertNull("Null should be returned for not existing type", type);

        IType[] allTypes = schema.getAllTypes("LocationInternalIDContent");
        assertEquals(1, allTypes.length);
        final IType faultType = schema.getType(false, "ExchangeFaultData");

        RenameNamedComponent renameNamedComponent = new RenameNamedComponent(root, faultType, "LocationInternalIDContent");
        IEnvironment env = root.getEnv();
        env.execute(renameNamedComponent);

        allTypes = schema.getAllTypes("LocationInternalIDContent");
        assertEquals(2, allTypes.length);
        for (IType currentType : allTypes) {
            assertEquals("LocationInternalIDContent", currentType.getName());
        }

        String location = schema.getLocation();
        assertNotNull("Location is null", location);
        assertTrue("Location returned is not OK", schema.getLocation().endsWith("/data/One.wsdl"));

        assertTrue(description == schema.getParent());
        assertTrue(description == schema.getRoot());

        assertEquals("Documentation", schema.getDocumentation());
    }

    @Test
    public void test_SimpleType() throws Exception {
        IWsdlModelRoot root = getModelRoot(Constants.DATA_PUBLIC_SELF_KESHAV, "SimpleType.wsdl");
        IDescription description = root.getDescription();

        ISchema schema = description.getSchema("http://www.example.org/SimpleType/")[0];
        IType type = schema.getType(false, "stringType");
        assertTrue("Type is not instanceof ISimpleType", type instanceof ISimpleType);
        assertTrue("Wrong type returned for stringType", "stringType".equals(type.getName()));
        assertTrue("Wrong type returned for stringType", "http://www.example.org/SimpleType/".equals(type.getNamespace()));

        ISimpleType simpleType = (ISimpleType) type;
        assertTrue("BaseType is null", null != simpleType.getBaseType());
        assertEquals("string", simpleType.getBaseType().getName());
        assertTrue("Wrong parent returned", schema.equals(simpleType.getParent()));
        assertTrue("Wrong root returned", description.equals(simpleType.getRoot()));
        assertEquals("Documentation returned is wrong", "Documentation", simpleType.getDocumentation());
        IFacet[] facets = simpleType.getEnumerations();
        assertEquals(2, facets.length);
        assertEquals("value1", facets[0].getValue());
        assertEquals("value2", facets[1].getValue());
        facets = simpleType.getPatterns();
        assertEquals(2, facets.length);
        assertEquals("value*", facets[0].getValue());
        assertEquals("v*", facets[1].getValue());
        assertEquals("1", simpleType.getMinLength());
        assertEquals("10", simpleType.getMaxLength());
        assertNull("length Facet is not null", simpleType.getLength());
        assertNull("minInclusive Facet is not null", simpleType.getMinInclusive());
        assertNull("minExclusive Facet is not null", simpleType.getMinExclusive());
        assertNull("maxInclusive Facet is not null", simpleType.getMaxInclusive());
        assertNull("maxExclusive Facet is not null", simpleType.getMaxExclusive());

        simpleType = (ISimpleType) schema.getType(false, "intType");
        assertTrue("BaseType is null", null != simpleType.getBaseType());
        assertEquals("int", simpleType.getBaseType().getName());
        facets = simpleType.getEnumerations();
        assertEquals(3, facets.length);
        assertEquals("1", facets[0].getValue());
        assertEquals("2", facets[1].getValue());
        assertEquals("43647489", facets[2].getValue());
        facets = simpleType.getPatterns();
        assertEquals(2, facets.length);
        assertEquals("*9*", facets[0].getValue());
        assertEquals("*8*", facets[1].getValue());
        assertEquals("1", simpleType.getMinLength());
        assertEquals("10", simpleType.getMaxLength());
        assertNull("length Facet is not null", simpleType.getLength());
        assertEquals("1", simpleType.getMinInclusive());
        assertEquals("1098098988", simpleType.getMaxInclusive());
        assertNull("minExclusive Facet is not null", simpleType.getMinExclusive());
        assertNull("maxExclusive Facet is not null", simpleType.getMaxExclusive());

        simpleType = (ISimpleType) schema.getType(false, "intType1");
        assertEquals("1", simpleType.getMinExclusive());
        assertEquals("1098098988", simpleType.getMaxExclusive());
        assertNull("minExclusive Facet is not null", simpleType.getMinInclusive());
        assertNull("maxExclusive Facet is not null", simpleType.getMaxInclusive());

        simpleType = (ISimpleType) schema.getType(false, "stWithSimpleBaseType");
        assertTrue("BaseType is null", null != simpleType.getBaseType());
        assertEquals("stringType", simpleType.getBaseType().getName());

        simpleType = (ISimpleType) schema.getType(false, "stListType");
        assertEquals("anySimpleType", simpleType.getBaseType().getName());

        simpleType = (ISimpleType) schema.getType(false, "stUnionType");
        assertEquals("anySimpleType", simpleType.getBaseType().getName());
    }

    @Test
    public void test_StructureType() throws Exception {
        IWsdlModelRoot root = getModelRoot(Constants.DATA_PUBLIC_SELF_KESHAV, "StructureType.wsdl");
        IDescription description = root.getDescription();

        ISchema schema = description.getSchema("http://www.example.org/StructureType/")[0];
        IType type = schema.getType(false, "complexType");
        assertTrue("Type is not instanceof IStructureType", type instanceof IStructureType);
        assertTrue("Wrong type returned for complexType", "complexType".equals(type.getName()));
        assertTrue("Wrong type returned for complexType", "http://www.example.org/StructureType/".equals(type.getNamespace()));

        IStructureType structureType = (IStructureType) type;
        assertTrue("BaseType is null", null != structureType.getBaseType());
        assertEquals("anyType", structureType.getBaseType().getName());
        assertTrue("Wrong parent returned", schema.equals(structureType.getParent()));
        assertTrue("Wrong root returned", description.equals(structureType.getRoot()));
        assertEquals("Documentation returned is wrong", "Documentation", structureType.getDocumentation());
        Collection<IElement> elements = structureType.getAllElements();
        assertEquals(2, elements.size());
        IElement[] elementsArray = elements.toArray(new IElement[2]);
        assertEquals("elementParticle", elementsArray[0].getName());
        assertEquals("elementParticle1", elementsArray[1].getName());
        elements = structureType.getElements("elementParticle");
        assertEquals(1, elements.size());
        assertEquals("elementParticle", elements.iterator().next().getName());
        elements = structureType.getElements("elementParticle1");
        assertEquals(1, elements.size());
        assertEquals("elementParticle1", elements.iterator().next().getName());

        structureType = (IStructureType) schema.getType(true, "complexType");
        assertNotNull("Could not find element 'complexType'", structureType);
        assertNotNull("BaseType for element complexType is null", structureType.getBaseType());
        assertEquals("anyType", structureType.getBaseType().getName());
        elements = structureType.getAllElements();
        assertEquals(2, elements.size());
        elementsArray = elements.toArray(new IElement[2]);
        assertEquals("elementParticle", elementsArray[0].getName());
        assertEquals("elementParticle1", elementsArray[1].getName());
        elements = structureType.getElements("elementParticle");
        assertEquals(1, elements.size());
        assertEquals("elementParticle", elements.iterator().next().getName());
        elements = structureType.getElements("elementParticle1");
        assertEquals(1, elements.size());
        assertEquals("elementParticle1", elements.iterator().next().getName());

        structureType = (IStructureType) schema.getType(true, "globalElement");
        assertNotNull("Could not find element 'globalElement'", structureType);
        assertNull(structureType.getBaseType());
        elements = structureType.getAllElements();
        assertNotNull("Empty collection must be returned when there are not elements", elements);
        assertEquals(0, elements.size());
        assertEquals("Documentation", structureType.getDocumentation());

        structureType = (IStructureType) schema.getType(true, "globalElementWithSt");
        assertNotNull("Could not find element 'globalElementWithSt'", structureType);
        assertNotNull("BaseType for element complexType is null", structureType.getBaseType());
        assertEquals("string", structureType.getBaseType().getName());
        elements = structureType.getAllElements();
        assertNotNull("Empty collection must be returned when there are not elements", elements);
        assertEquals(0, elements.size());

        structureType = (IStructureType) schema.getType(false, "ctWithAttributes");
        assertNotNull("Could not find element 'ctWithAttributes'", structureType);
        assertNotNull("BaseType for complexType ctWithAttributes is null", structureType.getBaseType());
        assertEquals("anyType", structureType.getBaseType().getName());
        elements = structureType.getAllElements();
        assertEquals(3, elements.size());
        elementsArray = elements.toArray(new IElement[3]);
        assertEquals("globalElement", elementsArray[0].getName());
        assertEquals("elementParticle3", elementsArray[1].getName());
        assertEquals("attribute1", elementsArray[2].getName());

        structureType = (IStructureType) schema.getType(false, "ctExtension");
        assertNotNull("Could not find element 'ctExtension'", structureType);
        assertNotNull("BaseType for complexType ctExtension is null", structureType.getBaseType());
        assertEquals("complexType", structureType.getBaseType().getName());
        elements = structureType.getAllElements();
        structureType.getAllElements();
        assertEquals(4, elements.size());
        elementsArray = elements.toArray(new IElement[4]);
        assertEquals("globalElement", elementsArray[0].getName());
        assertEquals("elementParticle3", elementsArray[1].getName());
        assertEquals("elementParticle4", elementsArray[2].getName());
        assertEquals("attribute1", elementsArray[3].getName());
        structureType = (IStructureType) schema.getType(true, "elementCtExtension");
        assertNotNull("Could not find element 'elementCtExtension'", structureType);
        assertNotNull("BaseType for element elementCtExtension is null", structureType.getBaseType());
        assertEquals("complexType", structureType.getBaseType().getName());
        elements = structureType.getAllElements();
        assertEquals(4, elements.size());
        elementsArray = elements.toArray(new IElement[4]);
        assertEquals("globalElement", elementsArray[0].getName());
        assertEquals("elementParticle3", elementsArray[1].getName());
        assertEquals("attribute1", elementsArray[2].getName());
        assertEquals("attribute2", elementsArray[3].getName());

        structureType = (IStructureType) schema.getType(false, "ctRestriction");
        assertNotNull("Could not find type 'ctRestriction'", structureType);
        assertNotNull("BaseType for type ctRestriction is null", structureType.getBaseType());
        assertEquals("complexType", structureType.getBaseType().getName());
        elements = structureType.getAllElements();
        assertEquals(2, elements.size());
        elementsArray = elements.toArray(new IElement[2]);
        assertEquals("elementParticle", elementsArray[0].getName());
        assertEquals("elementParticle1", elementsArray[1].getName());

        structureType = (IStructureType) schema.getType(false, "ctSimpleContentExtension");
        assertNotNull("Could not find type 'ctSimpleContentExtension'", structureType);
        assertNotNull("BaseType for type ctSimpleContentExtension is null", structureType.getBaseType());
        assertEquals("string", structureType.getBaseType().getName());
        elements = structureType.getAllElements();
        assertEquals(3, elements.size());
        elementsArray = elements.toArray(new IElement[3]);
        assertEquals("attribute1", elementsArray[0].getName());
        assertEquals("attribute2", elementsArray[1].getName());
        assertEquals("globalAttribute", elementsArray[2].getName());

        structureType = (IStructureType) schema.getType(false, "ctSimpleContentRestriction");
        assertNotNull("Could not find type 'ctSimpleContentRestriction'", structureType);
        assertNotNull("BaseType for type ctSimpleContentRestriction is null", structureType.getBaseType());
        assertEquals("ctSimpleContentExtension", structureType.getBaseType().getName());
        elements = structureType.getAllElements();
        assertEquals(2, elements.size());
        elementsArray = elements.toArray(new IElement[2]);
        assertEquals("attribute1", elementsArray[0].getName());
        assertEquals("attribute2", elementsArray[1].getName());

        structureType = (IStructureType) schema.getType(false, "nestedElements");
        assertNotNull("Could not find type 'nestedElements'", structureType);
        elements = structureType.getAllElements();
        assertEquals(8, elements.size());
        elementsArray = elements.toArray(new IElement[8]);
        assertEquals("globalElement", elementsArray[0].getName());
        assertEquals("elementParticle", elementsArray[1].getName());
        assertEquals("elementParticle1", elementsArray[2].getName());
        assertEquals("elementParticle2", elementsArray[3].getName());
        assertEquals("elementParticle3", elementsArray[4].getName());
        assertEquals("elementParticle4", elementsArray[5].getName());
        assertEquals("attribute1", elementsArray[6].getName());
        assertEquals("attribute2", elementsArray[7].getName());

        structureType = (IStructureType) schema.getType(false, "allModelGroupType");
        assertNotNull("Could not find type 'allModelGroupType'", structureType);
        elements = structureType.getAllElements();
        assertEquals(6, elements.size());
        elementsArray = elements.toArray(new IElement[6]);
        assertEquals("globalElement", elementsArray[0].getName());
        assertEquals("elementParticle", elementsArray[1].getName());
        assertEquals("elementParticle1", elementsArray[2].getName());
        assertEquals("elementParticle2", elementsArray[3].getName());
        assertEquals("elementParticle3", elementsArray[4].getName());
        assertEquals("elementParticle4", elementsArray[5].getName());

        structureType = (IStructureType) schema.getType(true, "elementWithGlobalType");
        assertNotNull("Could not find type 'elementWithGlobalType'", structureType);
        elements = structureType.getAllElements();
        assertEquals(0, elements.size());
    }

    @Test
    public void test_Element() throws Exception {
        IWsdlModelRoot root = getModelRoot(Constants.DATA_PUBLIC_SELF_KESHAV, "StructureType.wsdl");
        IDescription description = root.getDescription();

        ISchema schema = description.getSchema("http://www.example.org/StructureType/")[0];

        IStructureType structureType = (IStructureType) schema.getType(false, "complexType");
        Collection<IElement> elements = structureType.getAllElements();
        IElement[] elementsArray = elements.toArray(new IElement[2]);
        IElement element = elementsArray[0];
        assertEquals("elementParticle", element.getName());
        assertEquals("Documentation", element.getDocumentation());
        assertEquals(1, element.getMinOccurs());
        assertEquals(1, element.getMaxOccurs());
        assertFalse(element.getNillable());
        assertEquals(structureType, element.getParent());
        assertEquals(description, element.getRoot());
        element = elementsArray[1];
        assertEquals("elementParticle1", element.getName());
        assertEquals("", element.getDocumentation());
        assertEquals(2, element.getMinOccurs());
        assertEquals(5, element.getMaxOccurs());
        assertTrue(element.getNillable());

        structureType = (IStructureType) schema.getType(false, "ctWithAttributes");
        elements = structureType.getElements("globalElement");
        assertEquals(1, elements.size());
        element = elements.iterator().next();
        assertEquals(10, element.getMinOccurs());
        assertEquals(100, element.getMaxOccurs());
        assertFalse(element.getNillable());
        elements = structureType.getElements("elementParticle3");
        assertEquals(1, elements.size());
        element = elements.iterator().next();
        assertEquals(1, element.getMinOccurs());
        assertEquals(1, element.getMaxOccurs());
        elements = structureType.getElements("attribute1");
        assertEquals(1, elements.size());
        element = elements.iterator().next();
        assertEquals(0, element.getMinOccurs());
        assertEquals(1, element.getMaxOccurs());
        assertFalse(element.getNillable());

        structureType = (IStructureType) schema.getType(true, "elementCtExtension");
        elements = structureType.getElements("attribute2");
        assertEquals(1, elements.size());
        element = elements.iterator().next();
        IType type = element.getType();
        assertNotNull("Type for element attribute2 is null", type);
        assertTrue("type returned is not a simpleType", type instanceof ISimpleType);
        assertNull("Type is not anonymous", type.getName());
        ISimpleType simpleType = (ISimpleType) type;
        assertTrue("Type is not anonymous", null == simpleType.getName());
        structureType = (IStructureType) schema.getType(false, "ctRestriction");
        elements = structureType.getElements("elementParticle");
        assertEquals(1, elements.size());
        element = elements.iterator().next();
        type = element.getType();
        element.getType();
        assertNotNull("Type for element elementParticle is null", type);
        assertTrue("type returned is not a simpleType", type instanceof ISimpleType);
        assertNull("Type is not anonymous", type.getName());
        simpleType = (ISimpleType) type;
        assertTrue("Type is not anonymous", null == simpleType.getName());
        structureType = (IStructureType) schema.getType(false, "ctExtension");
        elements = structureType.getElements("elementParticle4");
        assertEquals(1, elements.size());
        element = elements.iterator().next();
        type = element.getType();
        assertNotNull("Type for element elementParticle is null", type);
        assertTrue("type returned is not a simpleType", type instanceof ISimpleType);
        assertEquals("simpleType", type.getName());

        structureType = (IStructureType) schema.getType(true, "globalElementWithElements");
        elements = structureType.getElements("elementParticle");
        assertEquals(1, elements.size());
        element = elements.iterator().next();
        structureType = (IStructureType) element.getType();
        assertNull("Type is not anonymous", structureType.getName());
        assertTrue("Elements do not match", 11 == structureType.getAllElements().size());
        elements = structureType.getElements("anyContentElement");
        element = elements.iterator().next();
        type = element.getType();
        assertEquals("anyType", type.getName());
        elements = structureType.getElements("globalAttribute");
        element = elements.iterator().next();
        assertNull("Type must be null for referenced AttributeDeclaration", element.getType());
        elements = structureType.getElements("attribute4");
        element = elements.iterator().next();
        assertEquals("simpleType", element.getType().getName());
        assertEquals("Documentation", element.getDocumentation());
        elements = structureType.getElements("globalElement");
        element = elements.iterator().next();
        assertNotNull("Type must be not null for referenced ElementDeclaration", element.getType());
        assertEquals("globalElement", element.getType().getName());
    }

    private IWsdlModelRoot getModelRoot(final String folder, final String fileName) throws Exception {
        return getWSDLModelRoot(folder + fileName, fileName);
    }
}