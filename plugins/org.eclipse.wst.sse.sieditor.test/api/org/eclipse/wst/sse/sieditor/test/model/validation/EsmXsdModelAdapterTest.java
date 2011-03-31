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
package org.eclipse.wst.sse.sieditor.test.model.validation;

import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.validation.EsmXsdModelAdapter;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.junit.Test;

@SuppressWarnings("nls")
public class EsmXsdModelAdapterTest extends SIEditorBaseTest {

    protected String getProjectName() {
        return "EsmXsdModelAdapterTest";
    }

    @Test
    public void testAdapter() throws Exception {
        // check for referenced documents
        IWsdlModelRoot modelRoot = copyAndGetModelRoot("EsmXsdModelAdapterTest.wsdl", "One.wsdl");
        org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription description;
        org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema schema;
        description = modelRoot.getDescription();

        EsmXsdModelAdapter esmXsdModelAdapter = new EsmXsdModelAdapter();
        assertNull(esmXsdModelAdapter.adaptToModelObject(description.getComponent()));
        schema = description.getSchema("http://namespace1")[0];
        XSDSchema xsdSchema = schema.getComponent();
        assertEquals(xsdSchema, ((org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema) esmXsdModelAdapter
                .adaptToModelObject(xsdSchema)).getComponent());

        IType simpleType = schema.getType(false, "SimpleType2");
        XSDSimpleTypeDefinition simpleTypeComponent = (XSDSimpleTypeDefinition) simpleType.getComponent();
        assertEquals(simpleTypeComponent, ((IType) esmXsdModelAdapter.adaptToModelObject(simpleTypeComponent)).getComponent());

        IType globalElement = schema.getType(true, "Element3");
        assertEquals(globalElement.getComponent(), ((IType) esmXsdModelAdapter.adaptToModelObject(globalElement.getComponent()))
                .getComponent());

        IStructureType complexType = (IStructureType) schema.getType(false, "StructureType25543s4433");
        IElement localElement = complexType.getElements("Element1").iterator().next();
        XSDConcreteComponent localElementComponent = localElement.getComponent();
        XSDNamedComponent complexTypeComponent = complexType.getComponent();
        assertEquals(complexTypeComponent, ((IType) esmXsdModelAdapter.adaptToModelObject(localElementComponent.eContainer()))
                .getComponent());
        localElement = complexType.getElements("unresolved").iterator().next();
        localElementComponent = localElement.getComponent();
        assertEquals(localElementComponent, ((IElement) esmXsdModelAdapter
                .adaptToModelObject(((XSDParticle) localElementComponent).getContent())).getComponent());

        IElement attribute = complexType.getElements("Attribute1").iterator().next();
        assertEquals(complexTypeComponent, ((IType) esmXsdModelAdapter.adaptToModelObject(attribute.getComponent().eContainer()))
                .getComponent());
    }

    private IWsdlModelRoot copyAndGetModelRoot(final String fileName, final String targetName) throws Exception {
        return getWSDLModelRoot("validation/" + fileName, targetName);
    }
}
