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

import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;

public class SetStructureTypeBaseTypeTest_ComplexToSimple extends AbstractSetStructureTypeBaseTypeTest {

    @Override
    protected String getComplexTypeName() {
        return COMPLEX_TYPE_COMPLEX_CONTENT_NAME;
    }

    @Override
    protected String getNewTypeName() {
        return NEW_SIMPLE_TYPE;
    }

    @Override
    protected boolean isInitialContentSimpleContent() {
        return false;
    }

    @Override
    protected boolean isNewContentSimpleContent() {
        return true;
    }

    @Override
    protected boolean checkInitialElementsCount() {
        return false;
    }

    @Override
    protected void additionalPostRedoStateChecks(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        assertEquals(initialAttributesCount, complexType.getAllElements().size());
        assertEquals(initialAttributesCount, ((XSDComplexTypeDefinition) complexType.getComponent()).getAttributeUses().size());
    }

    @Override
    protected void additionalPostUndoStateChecks(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        assertEquals(initialAttributesCount, ((XSDComplexTypeDefinition) complexType.getComponent()).getAttributeUses().size());
        assertEquals(initialContentsCount, complexType.getAllElements().size());
    }
}
