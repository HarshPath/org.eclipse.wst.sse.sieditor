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
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.SetParameterTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.OperationParameter;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;

public class SetUnwrappedParameterTypeCommandTest extends AbstractCommandTest {
    private static final String SCHEMA_NAMESPACE = "http://sap.com/xi/Purchasing";
    private static final String NEW_PARAMETER_TYPE_NAME = "Address";
    private AbstractType originalType;

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        final IDescription description = modelRoot.getDescription();

        final IOperation operation;

        operation = description.getAllInterfaces().iterator().next().getAllOperations().iterator().next();
        final OperationParameter parameter = (OperationParameter) operation.getAllOutputParameters().iterator().next();
        final IType newType = parameter.getType();

        assertNotNull(newType);
        assertNotSame(originalType, newType);
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        final IDescription description = modelRoot.getDescription();

        final IOperation operation;

        operation = description.getAllInterfaces().iterator().next().getAllOperations().iterator().next();
        final OperationParameter parameter = (OperationParameter) operation.getAllOutputParameters().iterator().next();
        final IType type = parameter.getType();

        assertNotNull(type);
        assertEquals(originalType, type);
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final IDescription description = modelRoot.getDescription();
        final IType newType = description.getSchema(SCHEMA_NAMESPACE)[0].getType(false, NEW_PARAMETER_TYPE_NAME);

        final IOperation operation;

        operation = description.getAllInterfaces().iterator().next().getAllOperations().iterator().next();
        final OperationParameter parameter = (OperationParameter) operation.getAllOutputParameters().iterator().next();
        originalType = (AbstractType) parameter.getType();

        return new SetParameterTypeCommand(parameter, newType);
    }
}
