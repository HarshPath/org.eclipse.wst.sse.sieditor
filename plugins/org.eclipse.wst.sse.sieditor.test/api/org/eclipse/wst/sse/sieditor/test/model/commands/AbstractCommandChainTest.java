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
package org.eclipse.wst.sse.sieditor.test.model.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.workspace.AbstractEMFOperation;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;

public abstract class AbstractCommandChainTest extends AbstractCommandTest {

    protected abstract AbstractNotificationOperation getNextOperation(final IWsdlModelRoot modelRoot) throws Exception;

    // ******************************
    // Override (optionally) the following methods in order to assert at finer
    // grained execution

    protected void assertPostOperationUndoState(final IUndoableOperation operation, final IWsdlModelRoot modelRoot) {
        // EMPTY. Implementors might want to add some assertions here
    }

    protected void assertPostOperationRedoState(final IUndoableOperation operation, final IWsdlModelRoot modelRoot) {
        // EMPTY. Implementors might want to add some assertions here
    }

    @Override
    protected void assertPostExecuteState(final IStatus status, final IWsdlModelRoot modelRoot) {
        assertEquals(Status.OK_STATUS, status);
    }

    // *****************************

    private List<AbstractEMFOperation> operations;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        operations = new ArrayList<AbstractEMFOperation>();
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        return getNextOperation(modelRoot);
    }

    @Test
    @Override
    public void testCommandExecution() throws Throwable {
        try {
            final IWsdlModelRoot modelRoot = getModelRoot();

            assertEquals(0, operationHistory.getUndoHistory(undoContext).length);
            assertEquals(0, operationHistory.getRedoHistory(undoContext).length);

            AbstractNotificationOperation operation = null;
            while ((operation = getNextOperation(modelRoot)) != null) {
                executeOperation(operation, modelRoot);
            }

            this.executeUndoOperation(modelRoot);
            this.executeRedoOperation(modelRoot);
            this.executeUndoOperation(modelRoot);
            this.executeRedoOperation(modelRoot);

        } finally {
            disposeModel();
        }
    }

    @Override
    protected void executeOperation(final AbstractEMFOperation operation, final IWsdlModelRoot modelRoot) throws Throwable {
        final IStatus status = modelRoot.getEnv().execute(operation);
        assertPostExecuteState(status, modelRoot);
        operations.add(operation);
    }

    protected void executeUndoOperation(final IWsdlModelRoot modelRoot) throws Throwable {
        assertTrue(operationHistory.canUndo(undoContext));
        assertFalse(operationHistory.canRedo(undoContext));

        IStatus status = null;
        for (int i = operations.size() - 1; i >= 0; i--) {
            final IUndoableOperation operation = operationHistory.getUndoOperation(undoContext);
            assertEquals(operations.get(i).getLabel(), operation.getLabel());
            assertTrue(operation.canUndo());
            status = operationHistory.undo(undoContext, new NullProgressMonitor(), null);
            assertEquals(Status.OK_STATUS, status);
            assertPostOperationUndoState(operation, modelRoot);
        }

        assertEquals(0, operationHistory.getUndoHistory(undoContext).length);
        assertEquals(operations.size(), operationHistory.getRedoHistory(undoContext).length);
        assertPostUndoState(status, modelRoot);
    }

    protected void executeRedoOperation(final IWsdlModelRoot modelRoot) throws Throwable {
        assertTrue(operationHistory.canRedo(undoContext));
        assertFalse(operationHistory.canUndo(undoContext));

        IStatus status = null;
        for (int i = 0; i < operations.size(); i++) {
            final IUndoableOperation operation = operationHistory.getRedoOperation(undoContext);
            assertEquals(operations.get(i).getLabel(), operation.getLabel());
            assertTrue(operation.canRedo());
            status = operationHistory.redo(undoContext, new NullProgressMonitor(), null);
            assertEquals(Status.OK_STATUS, status);
            assertPostOperationRedoState(operation, modelRoot);
        }

        assertEquals(operations.size(), operationHistory.getUndoHistory(undoContext).length);
        assertEquals(0, operationHistory.getRedoHistory(undoContext).length);
        assertPostRedoState(status, modelRoot);
    }

}
