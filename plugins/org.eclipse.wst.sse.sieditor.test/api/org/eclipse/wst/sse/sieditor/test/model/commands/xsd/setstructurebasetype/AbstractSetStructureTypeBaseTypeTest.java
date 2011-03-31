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
package org.eclipse.wst.sse.sieditor.test.model.commands.xsd.setstructurebasetype;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.xsd.XSDComplexTypeDefinition;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetStructureTypeBaseTypeCompositeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;

public abstract class AbstractSetStructureTypeBaseTypeTest extends AbstractCommandTest {

    protected static final String TYPES_NAMESPACE = "http://www.example.org/NewWSDLFile/";
    protected static final String COMPLEX_TYPES_NAMESPACE = "http://namespace1";

    protected static final String COMPLEX_TYPE_COMPLEX_CONTENT_NAME = "ComplexTypeComplexContent";
    protected static final String CLASSIS_COMPLEX_TYPE_NAME = "ClassicComplexType";
    protected static final String COMPLEX_TYPE_SIMPLE_CONTENT_NAME = "ComplexTypeSimpleContent";

    protected static final String NEW_SIMPLE_TYPE = "SimpleType1";
    protected static final String NEW_COMPLEX_TYPE = "StructureType1";
    protected static final String BASE_SIMPLE_TYPE = "BaseSimpleType";
    protected static final String BASE_COMPLEX_TYPE = "BaseStructureType";

    protected IStructureType complexType;

    protected int initialContentsCount;
    protected int initialAttributesCount;

    @Override
    protected String getWsdlFilename() {
        return "DefinitionWithTNSPrefixWSDL.wsdl";
    }

    @Override
    protected String getWsdlFoldername() {
        return "pub/csns/setnamespace/";
    }

    protected void additionalPostRedoStateChecks(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        // dummy default implementation
    }

    protected void additionalPostUndoStateChecks(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        // dummy default implementation
    }

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        assertEquals(isNewContentSimpleContent(), complexType.isComplexTypeSimpleContent());
        assertTrue(getNewTypeName().equals(complexType.getBaseType().getName()));

        if (checkInitialElementsCount()) {
            assertEquals(initialContentsCount, complexType.getAllElements().size());
        }
        additionalPostRedoStateChecks(redoStatus, modelRoot);
        assertThereAreNoValidationErrors();
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        assertEquals(isInitialContentSimpleContent(), complexType.isComplexTypeSimpleContent());
        assertFalse(getNewTypeName().equals(complexType.getBaseType().getName()));

        if (checkInitialElementsCount()) {
            assertEquals(initialContentsCount, complexType.getAllElements().size());
        }
        additionalPostUndoStateChecks(undoStatus, modelRoot);
        assertThereAreNoValidationErrors();
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        assertThereAreNoValidationErrors();

        final ISchema structureTypeSchema = modelRoot.getDescription().getSchema(COMPLEX_TYPES_NAMESPACE)[0];
        complexType = (IStructureType) structureTypeSchema.getType(false, getComplexTypeName());
        initialContentsCount = complexType.getAllElements().size();
        initialAttributesCount = ((XSDComplexTypeDefinition) complexType.getComponent()).getAttributeUses().size();
        
        assertTrue(initialContentsCount >= 1 || initialAttributesCount >= 1);

        assertEquals(isInitialContentSimpleContent(), complexType.isComplexTypeSimpleContent());
        assertFalse("type already set", getNewTypeName().equals(complexType.getBaseType().getName()));

        final ISchema typesSchema = modelRoot.getDescription().getSchema(TYPES_NAMESPACE)[0];
        final IType simpleType = typesSchema.getType(false, getNewTypeName());

        return new SetStructureTypeBaseTypeCompositeCommand(modelRoot, complexType, simpleType);
    }

    // =========================================================
    // helpes
    // =========================================================

    protected abstract String getComplexTypeName();

    protected abstract String getNewTypeName();

    protected abstract boolean isInitialContentSimpleContent();

    protected abstract boolean isNewContentSimpleContent();

    protected abstract boolean checkInitialElementsCount();

}
