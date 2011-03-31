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

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.workspace.AbstractEMFOperation;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.After;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.model.XMLModelNotifierWrapper;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;

public abstract class AbstractBaseCommandTest<T extends IModelRoot> extends SIEditorBaseTest {

    protected IOperationHistory operationHistory;
    protected IUndoContext undoContext;
    protected XMLModelNotifierWrapper xmlModelNotifierWrapper;

    protected void assertPostExecuteState(final IStatus status, final T modelRoot) {
        assertStatusOK(status);
        assertPostRedoState(status, modelRoot);
    }

    protected abstract void assertPostUndoState(IStatus undoStatus, T modelRoot);

    protected abstract void assertPostRedoState(IStatus redoStatus, T modelRoot);

    protected abstract AbstractEMFOperation getOperation(T modelRoot) throws Exception;

    protected abstract String getFilename();

    protected abstract String getFolderName();

    protected abstract String getEditorId();

    @After
    @Override
    public void tearDown() throws Exception {
        xmlModelNotifierWrapper = null;
        super.tearDown();
    }

    @Test
    public void testCommandExecution() throws Throwable {
        try {
            final T modelRoot = getModelRoot();
            final AbstractEMFOperation operation = getOperation(modelRoot);

            executeOperation(operation, modelRoot);
            executeUndoOperation(operation, modelRoot);
            executeRedoOperation(operation, modelRoot);
            executeUndoOperation(operation, modelRoot);
            executeRedoOperation(operation, modelRoot);

        } finally {
            disposeModel();
        }
    }

    protected void executeOperation(final AbstractEMFOperation operation, final T modelRoot) throws Throwable {
        assertFalse(operationHistory.canUndo(undoContext));
        assertFalse(operationHistory.canRedo(undoContext));

        final IStatus status = modelRoot.getEnv().execute(operation);

        assertPostExecuteState(status, modelRoot);
    }

    protected void executeUndoOperation(final AbstractEMFOperation operation, final T modelRoot) throws Throwable {
        assertEquals(operation.getLabel(), operationHistory.getUndoOperation(undoContext).getLabel());
        assertTrue(operationHistory.canUndo(undoContext));
        assertFalse(operationHistory.canRedo(undoContext));

        final IStatus status = operationHistory.undo(undoContext, new NullProgressMonitor(), null);

        assertStatusOK(status);
        assertPostUndoState(status, modelRoot);
    }

    protected void executeRedoOperation(final AbstractEMFOperation operation, final T modelRoot) throws Throwable {
        assertEquals(operation.getLabel(), operationHistory.getRedoOperation(undoContext).getLabel());
        assertTrue(operationHistory.canRedo(undoContext));
        assertFalse(operationHistory.canUndo(undoContext));

        final IStatus status = operationHistory.redo(undoContext, new NullProgressMonitor(), null);

        assertStatusOK(status);
        assertPostRedoState(status, modelRoot);
    }

    private void assertStatusOK(final IStatus status) {
        if (!status.isOK()) {
            Logger.log(status);
            if (status.getException() != null) {
                status.getException().printStackTrace();
            }
        }
        assertEquals(Status.OK_STATUS, status);
    }

    @Override
    protected void setupEnvironment(final IModelRoot modelRoot) {
        operationHistory = modelRoot.getEnv().getOperationHistory();
    }

    @Override
    protected String getProjectName() {
        return "CommandsTestingProject";
    }

    protected T getModelRoot() throws Exception {
        final String wsdlFilename = getFilename();
        final String fullPath = getFolderName() + wsdlFilename;

        final T modelRoot = (T) getModelRoot(fullPath, wsdlFilename, getEditorId());

        operationHistory = editor.getOperationHistory();
        undoContext = editor.getUndoContext();
        xmlModelNotifierWrapper = editor.getModelNotifier();

        return modelRoot;
    }

}
