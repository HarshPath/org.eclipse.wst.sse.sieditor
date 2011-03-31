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
package org.eclipse.wst.sse.sieditor.test.model.commands.common.settargetnamespace;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EList;
import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.tns.ChangeDefinitionTNSCompositeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.validation.ValidationService;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.Fault;
import org.eclipse.wst.wsdl.Message;
import org.eclipse.wst.wsdl.Operation;
import org.eclipse.wst.wsdl.PortType;

public abstract class AbstractSetDefinitionTNSCommandTest extends AbstractCommandTest {

    protected static final String NEW_TARGET_NAMESPACE = "newTargetNamespace_" + System.currentTimeMillis();
    protected String oldNamespace;
    protected ValidationService validationService;

    // =========================================================
    // assert state methods
    // =========================================================

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        final Definition definition = modelRoot.getDescription().getComponent();
        assertEquals(NEW_TARGET_NAMESPACE, definition.getTargetNamespace());
        assertThereAreNoValidationErrors();
        assertWsdlDocumentStateAsExpected(definition, modelRoot);
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        final Definition definition = modelRoot.getDescription().getComponent();
        assertEquals(oldNamespace, definition.getTargetNamespace());
        assertThereAreNoValidationErrors();
        assertWsdlDocumentStateAsExpected(definition, modelRoot);
    }

    protected void assertWsdlDocumentStateAsExpected(final Definition definition, final IWsdlModelRoot modelRoot) {
        final EList<Message> eMessages = definition.getEMessages();
        assertNotNull(eMessages);
        for (final Message message : eMessages) {
            assertNotNull(message.eContainer());
        }
        final EList<PortType> ePortTypes = definition.getEPortTypes();
        assertNotNull(ePortTypes);
        for (final PortType portType : ePortTypes) {
            final EList<Operation> eOperations = portType.getEOperations();
            assertNotNull(eOperations);
            for (final Operation operation : eOperations) {
                assertNotNull("input message should be resolved", operation.getEInput().getMessage());
                assertNotNull("output message should be resolved", operation.getEOutput().getMessage());
                final EList<Fault> eFaults = operation.getEFaults();
                for (final Fault fault : eFaults) {
                    assertNotNull("fault message should be resolved", fault.getMessage());
                }
            }
        }
    }

    // =========================================================
    // get operation main method
    // =========================================================

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        oldNamespace = modelRoot.getDescription().getComponent().getTargetNamespace();
        validationService = editor.getValidationService();

        assertWsdlDocumentStateAsExpected(modelRoot.getDescription().getComponent(), modelRoot);
        return new ChangeDefinitionTNSCompositeCommand(modelRoot, modelRoot.getDescription(), NEW_TARGET_NAMESPACE);
    }
}
