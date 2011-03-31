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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.LinkedList;

import org.eclipse.emf.transaction.impl.InternalTransactionalEditingDomain;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.common.AbstractNewTypeCompositeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.common.setcommandbuilder.ISetTypeCommandBuilder;
import org.eclipse.wst.sse.sieditor.core.common.IEnvironment;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;

public abstract class AbstractNewTypeCompositeCommandTest {

    protected ISetTypeCommandBuilder commandBuilder;
    protected IXSDModelRoot modelRoot;
    protected ISchema schema;
    protected IModelObject modelObject;
    protected IStructureType typeMock;
    protected ISimpleType baseTypeMock;

    private IEnvironment environment;
    private InternalTransactionalEditingDomain editingDomain;

    private AbstractNewTypeCompositeCommand command;
    private AbstractNotificationOperation setTypeCommand;

    protected static final String TYPE_NAME = "ala-bala" + System.currentTimeMillis(); //$NON-NLS-1$
    protected static final String OPERATION_LABEL = "operation_label_" + System.currentTimeMillis(); //$NON-NLS-1$

    protected AbstractNotificationOperation addOperationResult;

    @Before
    public void setUp() {
        schema = createMock(ISchema.class);
        modelRoot = createMock(IXSDModelRoot.class);
        modelObject = createMock(IModelObject.class);
        typeMock = createMock(IStructureType.class);
        baseTypeMock = createMock(ISimpleType.class);

        environment = createMock(IEnvironment.class);
        editingDomain = createMock(InternalTransactionalEditingDomain.class);

        commandBuilder = createMock(ISetTypeCommandBuilder.class);
    }

    @Test
    public void getNextOperation() {
        expect(schema.getModelRoot()).andReturn(modelRoot).anyTimes();
        expect(modelRoot.getEnv()).andReturn(environment).anyTimes();
        expect(environment.getEditingDomain()).andReturn(editingDomain).anyTimes();
        expect(typeMock.getElements(isA(String.class))).andReturn(new LinkedList<IElement>());
        expect(typeMock.getBaseType()).andReturn(baseTypeMock);
        replay(schema, modelRoot, environment, typeMock);

        setTypeCommand = createSetTypeCommand();
        expect(commandBuilder.createSetTypeCommand(typeMock)).andReturn(setTypeCommand);

        replay(commandBuilder);

        command = createCompositeCommand();

        final AbstractNotificationOperation addOperation = command.getNextOperation(null);

        intermediateAsserts(command);

        final AbstractNotificationOperation setOperation = command.getNextOperation(null);
        assertSame(addOperationResult, addOperation); // test the add operation
                                                      // here
        assertSame(setTypeCommand, setOperation);

        assertNull(command.getNextOperation(null));

        verify(commandBuilder);
    }

    protected abstract AbstractNotificationOperation createSetTypeCommand();

    protected abstract AbstractNewTypeCompositeCommand createCompositeCommand();

    protected abstract void intermediateAsserts(AbstractNewTypeCompositeCommand command);

}
