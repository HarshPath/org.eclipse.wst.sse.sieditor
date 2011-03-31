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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.wsdl.Message;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.xml.type.internal.QName;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.Input;
import org.eclipse.wst.wsdl.Operation;
import org.eclipse.wst.wsdl.Part;
import org.eclipse.wst.wsdl.PortType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddInputCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddMessageCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.utils.EmfModelPatcher;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;

public class AddMessageNoNSCommandTest extends AbstractCommandTest {

    private static final String NEW_INPUT_NAME = "NewInputName";
    private static final String EMPTY_WSDL = "NoTnsNsPref.wsdl";
    private static final String EMPTY_WSDLS_DIR = "pub/csns/";

    private static final String NEW_MESSAGE_NAME = "NewMessageName";
    private static final String NEW_PART_NAME = "NewPartName";

    private Input inputMessageReference;
    private QName messageQname;

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        final IDescription description = modelRoot.getDescription();
        final Definition definition = description.getComponent();

        final Message message = definition.getMessage(messageQname);
        assertNotNull(message);

        final Map partsMap = message.getParts();
        final Collection parts = partsMap.values();
        assertNotNull(parts);
        assertEquals(1, parts.size());
        final Part part = (Part) parts.iterator().next();
        assertNull(part.getTypeDefinition());
        assertNull(part.getTypeName());
        assertNull(part.getElementDeclaration());
        assertNull(part.getElementName());

        assertNotNull(definition.getPrefix(definition.getTargetNamespace()));
        assertEquals(message, inputMessageReference.getEMessage());

        refreshModelFromDom(modelRoot, description, definition);

        assertNotNull(definition.getPrefix(definition.getTargetNamespace()));
        // if this call fails - the DOM model is not ok, and when refreshed from
        // the EMF - the EMF gets corrupted
        assertEquals(message, inputMessageReference.getEMessage());
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        final IDescription description = modelRoot.getDescription();
        final Definition definition = description.getComponent();

        final Message message = definition.getMessage(messageQname);
        assertNull(message);

        assertNull(definition.getPrefix(definition.getTargetNamespace()));
        assertEquals(message, inputMessageReference.getEMessage());

        refreshModelFromDom(modelRoot, description, definition);

        assertNull(definition.getPrefix(definition.getTargetNamespace()));
        // if this call fails - the DOM model is not ok, and when refreshed from
        // the EMF - the EMF gets corrupted
        assertEquals(message, inputMessageReference.getEMessage());
    }

    private void refreshModelFromDom(final IWsdlModelRoot modelRoot, final IDescription description, final Definition definition) {
        final Element definitionElement = definition.getElement();
        final Element inputElement = inputMessageReference.getElement();

        final AbstractNotificationOperation refresh = new AbstractNotificationOperation(modelRoot, description, "") {
            @Override
            public IStatus run(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
                definition.elementChanged(definitionElement);
                inputMessageReference.elementChanged(inputElement);

                final Set<Node> changedNodes = new HashSet<Node>();
                changedNodes.add(definitionElement);
                changedNodes.add(inputElement);
                EmfModelPatcher.instance().patchEMFModelAfterDomChange((IWsdlModelRoot) description.getModelRoot(), changedNodes);
                return Status.OK_STATUS;
            }

        };
        try {
            refresh.execute(new NullProgressMonitor(), null);
        } catch (final ExecutionException e) {
            fail(e.toString());
        }
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final IDescription description = modelRoot.getDescription();
        final Definition definition = description.getComponent();

        final PortType portType = (PortType) definition.getEPortTypes().get(0);
        final Operation operation = (Operation) portType.getEOperations().get(0);

        final AddInputCommand addInputCommand = new AddInputCommand(modelRoot, operation, NEW_INPUT_NAME,
                org.eclipse.wst.sse.sieditor.model.wsdl.api.OperationType.ASYNCHRONOUS) {
            @Override
            public boolean canUndo() {
                return false;
            }

            @Override
            public boolean canRedo() {
                return false;
            }
        };
        assertTrue(modelRoot.getEnv().execute(addInputCommand).isOK());

        messageQname = new QName(definition.getTargetNamespace(), NEW_MESSAGE_NAME, "");
        assertNull(definition.getMessage(messageQname));

        inputMessageReference = operation.getEInput();
        return new AddMessageCommand(description, inputMessageReference, NEW_MESSAGE_NAME, NEW_PART_NAME);
    }

    @Override
    protected String getWsdlFilename() {
        return EMPTY_WSDL;
    }

    @Override
    protected String getWsdlFoldername() {
        return EMPTY_WSDLS_DIR;
    }

}
