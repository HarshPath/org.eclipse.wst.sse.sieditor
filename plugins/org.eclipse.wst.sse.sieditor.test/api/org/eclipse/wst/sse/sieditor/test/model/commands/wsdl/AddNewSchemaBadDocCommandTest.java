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

import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AbstractCompositeEnsuringDefinitionNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddNewSchemaCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;

public class AddNewSchemaBadDocCommandTest extends AbstractEnsureDefinitionCommandTest {
    private static final String SCHEMA_TARGET_NAMESPACE = "http://www.emu.org/myNamespace";

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        super.assertPostUndoState(undoStatus, modelRoot);
        AddNewSchemaCommandTest.assertUndo(modelRoot);
    }

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        super.assertPostRedoState(redoStatus, modelRoot);
        AddNewSchemaCommandTest.assertRedo(modelRoot);
    }

    @Override
    protected AbstractCompositeEnsuringDefinitionNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        return new AddNewSchemaCommand(modelRoot, SCHEMA_TARGET_NAMESPACE);
    }

}
