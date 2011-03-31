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
package org.eclipse.wst.sse.sieditor.test.ui;

import java.io.IOException;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.transaction.impl.InternalTransaction;
import org.eclipse.emf.transaction.impl.InternalTransactionalEditingDomain;
import org.eclipse.emf.workspace.AbstractEMFOperation;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.wst.xml.core.internal.document.XMLModelNotifier;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.eclipse.wst.sse.sieditor.model.XMLModelNotifierWrapper;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.ui.i18n.Messages;

/**
 *
 * 
 */
@SuppressWarnings("restriction")
public class XMLModelNotifierWrapperSingleTransactionTest extends SIEditorBaseTest {

    private static IXSDModelRoot modelRoot;
    private static InternalTransaction originalTransaction;

    /**
     * Test method for
     * {@link org.eclipse.wst.sse.sieditor.model.XMLModelNotifierWrapper#endChanging()}
     * .
     * 
     * @throws CoreException
     * @throws IOException
     * @throws ExecutionException
     */
    @Test
    public void testEndChanging() throws IOException, CoreException, ExecutionException {
        modelRoot = getXSDModelRoot("pub/xsd/example.xsd", "example.xsd"); //$NON-NLS-1$//$NON-NLS-2$
        XMLModelNotifierMock notifierMock = new XMLModelNotifierMock();
        final XMLModelNotifierWrapper xmlModelNotifierWrapper = new XMLModelNotifierWrapper(notifierMock, modelRoot);

        AbstractEMFOperation command = new AbstractEMFOperation(modelRoot.getEnv().getEditingDomain(),
                Messages.XMLModelNotifierWrapper_0, null) {

            @Override
            protected IStatus doExecute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
                // save a reference to the current transaction in order to
                // compare it later in the child command
                originalTransaction = ((InternalTransactionalEditingDomain) getEditingDomain()).getActiveTransaction();
                // call in order to create and execute the child command
                xmlModelNotifierWrapper.endChanging();
                return Status.OK_STATUS;
            }
        };

        assertTrue(modelRoot.getEnv().execute(command).isOK());
    }

    private static class XMLModelNotifierMock implements XMLModelNotifier {

        public void attrReplaced(Element element, Attr newAttr, Attr oldAttr) {
        }

        public void beginChanging() {
        }

        public void beginChanging(boolean newModel) {
        }

        public void cancelPending() {
        }

        public void childReplaced(Node parentNode, Node newChild, Node oldChild) {
        }

        public void editableChanged(Node node) {
        }

        public void endChanging() {
            InternalTransaction activeTransaction = ((InternalTransactionalEditingDomain) modelRoot.getEnv().getEditingDomain())
                    .getActiveTransaction();
            assertEquals("the current transactionChangeDescription differs from the parent transaction's one",
                    originalTransaction.getChangeDescription(), activeTransaction.getChangeDescription());
        }

        public void endTagChanged(Element element) {
        }

        public boolean hasChanged() {
            return false;
        }

        public boolean isChanging() {
            return false;
        }

        public void propertyChanged(Node node) {
        }

        public void startTagChanged(Element element) {
        }

        public void structureChanged(Node node) {
        }

        public void valueChanged(Node node) {
        }
    }
}
