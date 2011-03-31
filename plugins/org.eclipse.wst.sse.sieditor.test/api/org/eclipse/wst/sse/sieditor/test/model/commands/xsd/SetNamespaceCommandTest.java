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

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddNewSchemaCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetNamespaceCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;

public class SetNamespaceCommandTest extends AbstractCommandTest {
    private static final String HTTP_WWW_EXAMPLE_ORG = "http://www.example.org/";
    private static final String INITIAL_SCHEMA_TARGET_NAMESPACE = "http://www.emu.org/myNamespaceToBeChanged";
    private static final String NEW_SCHEMA_TARGET_NAMESPACE = "http://www.emu.org/myNamespaceAlreadyChanged";
    private ISchema schema;

    protected String getInitialSchemaNamespace() {
        return INITIAL_SCHEMA_TARGET_NAMESPACE;
    }

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        final ISchema[] matchingSchemas = modelRoot.getDescription().getSchema(NEW_SCHEMA_TARGET_NAMESPACE);
        assertEquals(1, matchingSchemas.length);
        assertEquals(NEW_SCHEMA_TARGET_NAMESPACE, schema.getNamespace());
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        final ISchema[] matchingSchemas = modelRoot.getDescription().getSchema(getInitialSchemaNamespace());
        assertEquals(1, matchingSchemas.length);
        assertEquals(getInitialSchemaNamespace(), schema.getNamespace());
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final AddNewSchemaCommand addNewSchemaCommand = new AddNewSchemaCommand(modelRoot, getInitialSchemaNamespace()) {
            @Override
            public boolean canRedo() {
                return false;
            }

            @Override
            public boolean canUndo() {
                return false;
            }
        };

        modelRoot.getEnv().execute(addNewSchemaCommand);
        schema = addNewSchemaCommand.getNewSchema();

        return new SetNamespaceCommand(modelRoot, schema, NEW_SCHEMA_TARGET_NAMESPACE);
    }
}
