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
import org.eclipse.xsd.XSDElementDeclaration;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.MakeAnonymousTypeGlobalCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;

/**
 * Simple test case: <or> <li>Open existing WSDL document</li> <li>Select single
 * global (non-anonymous) element</li> <li>Make it anonymous</li> <li>Execute
 * undo/redo of the operation</li> </or> <br>
 * <br>
 * 
 * 
 * 
 */
public class MakeTypeGlobalCommandTest extends AbstractCommandTest {

    private static final String TARGET_NAMESPACE = "http://sap.com/xi/SAPGlobal20/Global";
    private static final String ELEMENT_NAME = "StandardMessageFault";
    private static final String EXPECTED_EXTRACTED_STRUCTURE_TYPE_NAME = "StructureType1";

    private MakeAnonymousTypeGlobalCommand command;

    private IStructureType element;
    private IType oldType;
    private ISchema schema;

    @Override
    protected String getWsdlFilename() {
        return "ECC_IMAPPROPRIATIONREQREJRC_VALID.wsdl";
    }

    @Override
    protected String getWsdlFoldername() {
        return "pub/csns/";
    }

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        assertFalse(element.isAnonymous());
        final IStructureType type = (IStructureType) schema.getType(false, EXPECTED_EXTRACTED_STRUCTURE_TYPE_NAME);
        assertEquals(EXPECTED_EXTRACTED_STRUCTURE_TYPE_NAME, type.getName());
        assertEquals(type.getName(), ((XSDElementDeclaration) element.getComponent())
                .getTypeDefinition().getName());
        assertTrue(schema.getComponent().getContents().contains(type.getComponent()));
        assertEquals(2, type.getAllElements().size());
        assertThereAreNoValidationErrors();
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        assertTrue(element.isAnonymous());
        assertEquals(2, element.getAllElements().size());
        assertThereAreNoValidationErrors();
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final ISchema[] schemas = modelRoot.getDescription().getSchema(TARGET_NAMESPACE);
        schema = schemas[0];
        element = (IStructureType) schema.getType(true, ELEMENT_NAME);
        assertNotNull(element);

        assertTrue(element.isAnonymous());
        command = new MakeAnonymousTypeGlobalCommand((IXSDModelRoot) element.getModelRoot(), element);
        return command;
    }

}
