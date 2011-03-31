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
package org.eclipse.wst.sse.sieditor.test.model.commands.wsdl;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddNewSchemaCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

public class AddNewSchemaCommandTest extends AbstractCommandTest {
    private static final String SCHEMA_TARGET_NAMESPACE = "http://www.emu.org/myNamespace";

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        assertUndo(modelRoot);
    }

    static void assertUndo(final IWsdlModelRoot modelRoot) {
        assertEquals(0, modelRoot.getDescription().getSchema(SCHEMA_TARGET_NAMESPACE).length);
    }

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        assertRedo(modelRoot);
    }

    static void assertRedo(final IWsdlModelRoot modelRoot) {
        final ISchema[] matchingSchemas = modelRoot.getDescription().getSchema(SCHEMA_TARGET_NAMESPACE);
        assertEquals(1, matchingSchemas.length);
        assertEquals(SCHEMA_TARGET_NAMESPACE, matchingSchemas[0].getNamespace());
    }


    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        return new AddNewSchemaCommand(modelRoot, SCHEMA_TARGET_NAMESPACE);
    }
}
