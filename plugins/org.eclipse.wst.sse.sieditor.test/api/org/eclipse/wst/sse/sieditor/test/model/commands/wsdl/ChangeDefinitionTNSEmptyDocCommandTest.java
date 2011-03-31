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

import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AbstractCompositeEnsuringDefinitionNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.tns.ChangeDefinitionTNSCompositeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;

public class ChangeDefinitionTNSEmptyDocCommandTest extends AbstractEnsureDefinitionCommandTest {

    private static final String NEW_NAMESPACE = "http://new_namespace_" + System.currentTimeMillis();

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        super.assertPostUndoState(undoStatus, modelRoot);
        final IDescription description = modelRoot.getDescription();
        assertNull(description.getNamespace());
        assertNull(description.getComponent().getTargetNamespace());
    }

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        super.assertPostRedoState(redoStatus, modelRoot);
        final IDescription description = modelRoot.getDescription();
        final String namespace = description.getNamespace();
        assertNotNull(namespace);
        assertEquals(NEW_NAMESPACE, namespace);
        final String emfTNS = description.getComponent().getTargetNamespace();
        assertNotNull(emfTNS);
        assertEquals(NEW_NAMESPACE, emfTNS);
    }

    static final void assertUndo(final IWsdlModelRoot modelRoot) {
        final IDescription description = modelRoot.getDescription();
        assertNull(description.getNamespace());
        assertNull(description.getComponent().getTargetNamespace());
    }

    static final void assertRedo(final IWsdlModelRoot modelRoot) {
        final IDescription description = modelRoot.getDescription();
        final String namespace = description.getNamespace();
        assertNotNull(namespace);
        assertEquals(NEW_NAMESPACE, namespace);
        final String emfTNS = description.getComponent().getTargetNamespace();
        assertNotNull(emfTNS);
        assertEquals(NEW_NAMESPACE, emfTNS);
    }

    @Override
    protected AbstractCompositeEnsuringDefinitionNotificationOperation getOperation(final IWsdlModelRoot modelRoot)
            throws Exception {
        return new ChangeDefinitionTNSCompositeCommand(modelRoot, modelRoot.getDescription(), NEW_NAMESPACE);
    }

}
