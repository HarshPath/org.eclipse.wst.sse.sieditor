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
package org.eclipse.wst.sse.sieditor.test.model.commands.xsd;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.xsd.XSDComplexTypeDefinition;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddAttributeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;

public class AddAttributeToComplexTypeSimpleContentTest extends AbstractCommandTest {

    protected static final String TYPES_NAMESPACE = "http://www.example.org/NewWSDLFile/";
    protected static final String COMPLEX_TYPES_NAMESPACE = "http://namespace1";

    protected static final String COMPLEX_TYPE_COMPLEX_CONTENT_NAME = "ComplexTypeComplexContent";
    protected static final String CLASSIS_COMPLEX_TYPE_NAME = "ClassicComplexType";
    protected static final String COMPLEX_TYPE_SIMPLE_CONTENT_NAME = "ComplexTypeSimpleContent";

    protected static final String NEW_SIMPLE_TYPE = "SimpleType1";
    protected static final String NEW_COMPLEX_TYPE = "StructureType1";
    protected static final String BASE_SIMPLE_TYPE = "BaseSimpleType";
    protected static final String BASE_COMPLEX_TYPE = "BaseStructureType";

    private static final String NEW_ATTRIBUTE_NAME = "testAttribute";

    protected IStructureType complexType;
    protected int initialAttributesCount;

    @Override
    protected String getWsdlFilename() {
        return "DefinitionWithTNSPrefixWSDL.wsdl";
    }

    @Override
    protected String getWsdlFoldername() {
        return "pub/csns/setnamespace/";
    }

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        assertEquals(initialAttributesCount + 1, complexType.getAllElements().size());
        assertEquals(initialAttributesCount + 1, ((XSDComplexTypeDefinition) complexType.getComponent()).getAttributeUses()
                .size());
        assertThereAreNoValidationErrors();
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        assertEquals(initialAttributesCount, complexType.getAllElements().size());
        assertEquals(initialAttributesCount, ((XSDComplexTypeDefinition) complexType.getComponent()).getAttributeUses().size());
        assertThereAreNoValidationErrors();
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final ISchema schema = modelRoot.getDescription().getSchema(COMPLEX_TYPES_NAMESPACE)[0];

        complexType = (IStructureType) schema.getType(false, COMPLEX_TYPE_SIMPLE_CONTENT_NAME);
        initialAttributesCount = ((XSDComplexTypeDefinition) complexType.getComponent()).getAttributeUses().size();

        return new AddAttributeCommand(complexType.getModelRoot(), complexType, NEW_ATTRIBUTE_NAME);
    }

}
