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
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.SetFaultTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;

public class SetFaultTypeCommandTest extends AbstractCommandTest {

    private IFault fault;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        fault = null;
    }

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        assertEquals(1, fault.getParameters().size());
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        assertEquals(0, fault.getParameters().size());
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final IWsdlModelRoot wsdlModelRoot = getModelRoot();

        final IDescription description = wsdlModelRoot.getDescription();
        final IServiceInterface serviceInterface = description.getAllInterfaces().iterator().next();
        final IOperation operation = serviceInterface.getAllOperations().iterator().next();
        this.fault = operation.getAllFaults().iterator().next();

        assertEquals(0, fault.getParameters().size());

        final ISchema schema = description.getSchema("http://sap.com/xi/SAPGlobal20/Global")[0];
        final IStructureType globalElement = (IStructureType) schema.getType(true, "AppropriationRequestRejectConfirmation_sync");

        return new SetFaultTypeCommand(wsdlModelRoot, fault, globalElement);
    }

    @Override
    protected String getWsdlFilename() {
        return "ECC_IMAPPROPRIATIONREQREJRC.wsdl";
    }

    @Override
    protected String getWsdlFoldername() {
        return "pub/csns/";
    }
}
