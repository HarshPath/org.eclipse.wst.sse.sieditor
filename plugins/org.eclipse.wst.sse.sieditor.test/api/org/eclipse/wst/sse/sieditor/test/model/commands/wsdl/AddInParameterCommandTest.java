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

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatus;
import org.eclipse.wst.wsdl.Part;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDTypeDefinition;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AddInParameterCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceOperation;

public class AddInParameterCommandTest extends AbstractCommandTest {
    private static final String MY_PARAMETER_NAME = "myParameter";
    private int initialNumberOfParts;

    @Override
    protected String getWsdlFilename() {
        return "NewWSDLFile.wsdl";
    }

    @Override
    protected String getWsdlFoldername() {
        return "pub/import/services/wsdl/";
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        final IDescription description = modelRoot.getDescription();
        final IServiceInterface intf = description.getAllInterfaces().iterator().next();
        final ServiceOperation operation = (ServiceOperation) intf.getAllOperations().iterator().next();
        assertEquals(initialNumberOfParts + 1, operation.getAllInputParameters().size());
        assertFalse(operation.getInputParameter(MY_PARAMETER_NAME).isEmpty());
        assertNotNull(operation.getInputParameter(MY_PARAMETER_NAME).get(0));

        final List<Part> parts = operation.getComponent().getEInput().getEMessage().getEParts();
        assertEquals(initialNumberOfParts + 1, parts.size());

        boolean newPartExists = false;
        Part newPart = null;
        for (final Part part : parts) {
            if (MY_PARAMETER_NAME.equals(part.getName())) {
                newPartExists = true;
                newPart = part;
                break;
            }
        }

        assertTrue(newPartExists);
        final XSDElementDeclaration elementDeclaration = newPart.getElementDeclaration();
        final XSDTypeDefinition typeDefinition = newPart.getTypeDefinition();
        assertNotNull(elementDeclaration);
        assertNull(typeDefinition);

        assertValidationStatus(operation, 0, 1);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        final IDescription description = modelRoot.getDescription();
        final IServiceInterface intf = description.getAllInterfaces().iterator().next();
        final ServiceOperation operation = (ServiceOperation) intf.getAllOperations().iterator().next();
        assertEquals(initialNumberOfParts, operation.getAllInputParameters().size());
        assertTrue(operation.getFault(MY_PARAMETER_NAME).isEmpty());

        final List<Part> parts = operation.getComponent().getEInput().getEMessage().getEParts();
        assertEquals(initialNumberOfParts, parts.size());

        boolean newPartExists = false;
        for (final Part part : parts) {
            if (MY_PARAMETER_NAME.equals(part.getName())) {
                newPartExists = true;
                break;
            }
        }

        assertFalse(newPartExists);
        assertValidationStatus(operation, 0, 0);
    }

    private void assertValidationStatus(final ServiceOperation operation, final int expectedErrors, final int expectedWarnings) {
        final List<IValidationStatus> statuses = editor.getValidationService().getValidationStatusProvider().getStatus(operation);
        int warnings = 0;
        int errors = 0;
        for (final IValidationStatus status : statuses) {
            if (status.getSeverity() == IStatus.ERROR) {
                errors++;
            } else if (status.getSeverity() == IStatus.WARNING) {
                warnings++;
            }
        }
        assertEquals(expectedErrors, errors);
        assertEquals(expectedWarnings, warnings);
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final IDescription description = modelRoot.getDescription();
        final IServiceInterface intf = description.getAllInterfaces().iterator().next();
        final ServiceOperation operation = (ServiceOperation) intf.getAllOperations().iterator().next();
        initialNumberOfParts = operation.getAllInputParameters().size();

        return new AddInParameterCommand(modelRoot, operation, MY_PARAMETER_NAME);
    }

}
