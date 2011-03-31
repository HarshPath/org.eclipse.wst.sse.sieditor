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
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddSchemaForSchemaCommand;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractXSDCommandTest;

public class AddSchemaForSchemaXSDCommandTest extends AbstractXSDCommandTest {

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IXSDModelRoot modelRoot) {
        assertTrue(modelRoot.getSchema().getComponent().getQNamePrefixToNamespaceMap().containsKey(EmfXsdUtils.XSD_PREFIX));
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IXSDModelRoot modelRoot) {
        assertFalse(modelRoot.getSchema().getComponent().getQNamePrefixToNamespaceMap().containsKey(EmfXsdUtils.XSD_PREFIX));
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IXSDModelRoot modelRoot) throws Exception {
        final ISchema schema = modelRoot.getSchema();
        assertNotNull(schema);
        assertNull(schema.getComponent().getSchemaForSchema());
        return new AddSchemaForSchemaCommand(schema);
    }

    @Override
    protected String getXSDFilename() {
        return "InvalidSchema.xsd";
    }

    @Override
    protected String getXSDFoldername() {
        return "";
    }

}
