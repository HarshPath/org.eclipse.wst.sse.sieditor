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
import org.eclipse.wst.sse.sieditor.command.emf.xsd.CopyTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;

public class CopyTypeCommandTest extends AbstractCommandTest {
    @Override
    protected String getWsdlFilename() {
        return "ECC_IMAPPROPRIATIONREQREJRC.wsdl";
    }

    @Override
    protected String getWsdlFoldername() {
        return "pub/csns/";
    }

    private static final String SOURCE_TARGET_NAMESPACE = "http://sap.com/xi/EA-APPL/SE/Global";
    private static final String TARGET_TARGET_NAMESPACE = "http://sap.com/xi/SAPGlobal20/Global";

    private Schema sourseSchema;
    private Schema targetSchema;
    private IStructureType typeToCopy;

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        assertTrue(targetSchema.getType(false, "MyTypeCopy") instanceof IStructureType);
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        assertNull(targetSchema.getType(false, "MyTypeCopy"));
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        ISchema[] schemas = modelRoot.getDescription().getSchema(SOURCE_TARGET_NAMESPACE);
        sourseSchema = (Schema) schemas[0];
        typeToCopy = (IStructureType) sourseSchema.getType(false, "AppropriationRequestID");

        schemas = modelRoot.getDescription().getSchema(TARGET_TARGET_NAMESPACE);
        targetSchema = (Schema) schemas[0];
        
        assertNull(targetSchema.getType(false, "MyTypeCopy"));

        return new CopyTypeCommand(targetSchema.getModelRoot(), targetSchema, typeToCopy.getComponent(), targetSchema,
                "MyTypeCopy");
    }
}
