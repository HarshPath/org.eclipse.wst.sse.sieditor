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
package org.eclipse.wst.sse.sieditor.test.model.commands.common;

import static org.junit.Assert.*;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.common.AbstractNewTypeCompositeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.common.NewStructureTypeCompositeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddElementCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddStructureTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetStructureTypeCommand;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

public class NewStructureTypeCompositeCommandTest extends AbstractNewTypeCompositeCommandTest {

    private boolean intermediateCalled = false;

    @Override
    protected AbstractNewTypeCompositeCommand createCompositeCommand() {
        return new NewStructureTypeCompositeCommand(modelRoot, modelObject, OPERATION_LABEL, schema, TYPE_NAME, commandBuilder) {
            @Override
            protected IType extractCreatedType(final AbstractNotificationOperation operation) {
                addOperationResult = operation;
                assertTrue(operation instanceof AddStructureTypeCommand);
                assertEquals(Messages.AddStructureTypeCommand_create_new_structure_type_command_label, operation.getLabel());
                return typeMock;
            }

            @Override
            protected AbstractNotificationOperation getNextIntermediateCommand() {
                final AbstractNotificationOperation operation = super.getNextIntermediateCommand();
                intermediateCalled = true;
                return operation;
            }
        };
    }

    @Override
    protected void intermediateAsserts(final AbstractNewTypeCompositeCommand command) {
        final AbstractNotificationOperation intermediateOperation = command.getNextOperation(null);
        assertTrue(intermediateCalled);
        assertTrue(intermediateOperation instanceof AddElementCommand);
    }

    @Override
    protected AbstractNotificationOperation createSetTypeCommand() {
        return new SetStructureTypeCommand(modelRoot, null, typeMock);
    }

}
