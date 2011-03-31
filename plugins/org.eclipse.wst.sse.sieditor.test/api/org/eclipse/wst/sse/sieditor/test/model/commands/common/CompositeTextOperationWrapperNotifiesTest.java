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

import java.io.IOException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.workspace.AbstractEMFOperation;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.common.CompositeTextOperationWrapper;
import org.eclipse.wst.sse.sieditor.model.api.IChangeListener;
import org.eclipse.wst.sse.sieditor.model.api.IModelChangeEvent;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;

public class CompositeTextOperationWrapperNotifiesTest extends AbstractCommandTest {

    private int changeListenerCalls = 0;

    private int lastChangeListenerCalls = 0;

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        assertEquals("notifyListeners() must be called", lastChangeListenerCalls + 1, changeListenerCalls);
        lastChangeListenerCalls = changeListenerCalls;
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {

        assertEquals("notifyListeners() must be called 2 time", lastChangeListenerCalls + 1, changeListenerCalls);
        lastChangeListenerCalls = changeListenerCalls;
    }

    @SuppressWarnings("restriction")
    @Override
    protected AbstractEMFOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {

        final AbstractNotificationOperation wrappedCommand = new AbstractNotificationOperation(modelRoot, modelRoot
                .getModelObject(), "Empty Test Operation") {

            @Override
            public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
                return Status.OK_STATUS;
            }

        };

        CompositeTextOperationWrapper operation = new CompositeTextOperationWrapper(wrappedCommand);
        modelRoot.addChangeListener(new IChangeListener() {
            @Override
            public void componentChanged(final IModelChangeEvent event) {
                changeListenerCalls++;
            }
        });

        return operation;
    }

    @Override
    protected IWsdlModelRoot getWSDLModelRoot(final String fileName, final String targetFileName) throws IOException,
            CoreException {
        return super.getWsdlModelRootViaDocumentProvider(fileName, targetFileName);
    }

}
