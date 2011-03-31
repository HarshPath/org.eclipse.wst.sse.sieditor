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
package org.eclipse.wst.sse.sieditor.test.model.commands.common.settargetnamespace;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractXSDCommandTest;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetNamespaceCommand;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

public class SetSchemaTargetNamespaceTest3 extends AbstractXSDCommandTest {

    private static final String NEW_TNS = "http://test/" + System.currentTimeMillis();

    private ISchema schema;

    @Override
    protected String getXSDFilename() {
        return "NamespaceIncludesXSD.xsd";
    }

    @Override
    protected String getXSDFoldername() {
        return "pub/extract/";
    }

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IXSDModelRoot modelRoot) {
        assertTrue(NEW_TNS.equals(schema.getNamespace()));
        assertThereAreNoValidationErrors();
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IXSDModelRoot modelRoot) {
        assertFalse(NEW_TNS.equals(schema.getNamespace()));
        assertThereAreNoValidationErrors();
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IXSDModelRoot modelRoot) throws Exception {
        schema = modelRoot.getSchema();
        assertFalse(NEW_TNS.equals(schema.getNamespace()));
        return new SetNamespaceCommand(modelRoot, schema, NEW_TNS);
    }

}
