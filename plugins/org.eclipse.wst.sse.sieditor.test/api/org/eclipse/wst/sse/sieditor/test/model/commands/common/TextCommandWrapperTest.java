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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.command.AbstractCommand;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.transaction.Transaction;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;
import org.eclipse.wst.xml.core.internal.document.XMLModelNotifier;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Element;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.common.TextCommandWrapper;
import org.eclipse.wst.sse.sieditor.model.XMLModelNotifierWrapper;
import org.eclipse.wst.sse.sieditor.model.api.IChangeListener;
import org.eclipse.wst.sse.sieditor.model.api.IModelChangeEvent;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;

public class TextCommandWrapperTest extends AbstractCommandTest {

    private boolean wrappedCommandExecuted = false;

    private int changeListenerCalls = 0;

    // TextCommand.didCommit(...) MUST not notify model root listener
    // so setup lastChangeListenerCalls to have (0 = -1 + 1) value on first
    // assert in the
    // assertPostRedoState(...) method
    private int lastChangeListenerCalls = -1;

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {

        // On first call changeListenerCalls==0
        // because TextCommand.didCommit(...) MUST not notify model root
        // listener
        assertEquals(lastChangeListenerCalls + 1, changeListenerCalls);
        lastChangeListenerCalls = changeListenerCalls;
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {

        assertTrue(wrappedCommandExecuted);
        assertEquals(lastChangeListenerCalls + 1, changeListenerCalls);
        lastChangeListenerCalls = changeListenerCalls;
    }

    @SuppressWarnings("restriction")
    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {

        final Command wrappedCommand = new AbstractCommand() {
            @Override
            public boolean canUndo() {
                return true;
            }

            @Override
            public boolean canExecute() {
                return true;
            }

            @Override
            public void execute() {
                wrappedCommandExecuted = true;
            }

            @Override
            public void redo() {
            }

            @Override
            public void undo() {
            }
        };
        final Element aDomElement = modelRoot.getDescription().getComponent().getElement();
        final IDOMModel model = (IDOMModel) aDomElement.getOwnerDocument().getImplementation();
        final XMLModelNotifier originalModelNotifier = model.getModelNotifier();
        final XMLModelNotifierWrapper modelNotifier = new XMLModelNotifierWrapper(originalModelNotifier, modelRoot);
        (model).setModelNotifier(modelNotifier);

        final TextCommandWrapper textCommand = new TextCommandWrapper(modelRoot, modelRoot.getDescription(), wrappedCommand, modelNotifier);
        assertEquals(Boolean.TRUE, textCommand.getOptions().get(Transaction.OPTION_NO_VALIDATION));
        
        modelRoot.addChangeListener(new IChangeListener() {
            @Override
            public void componentChanged(final IModelChangeEvent event) {
                changeListenerCalls++;
            }
        });
        return textCommand;
    }

    @Override
    protected IWsdlModelRoot getWSDLModelRoot(final String fileName, final String targetFileName) throws IOException, CoreException {
        return super.getWsdlModelRootViaDocumentProvider(fileName, targetFileName);
    }

}
