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

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandChainTest;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatus;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddStructureTypeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.RenameStructureTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;

public class AddGlobalElementAndRenameToExistingOneCommandTest extends AbstractCommandChainTest {

    private static final String SCHEMA_NAMESPACE = "http://www.example.org/NewWSDLFile/";
    private static final String EXISTING_NAME = "NewOperationResponse";

    private AddStructureTypeCommand addElementCommand;
    private RenameStructureTypeCommand renameCommand;

    private IWsdlModelRoot modelRoot;
    private ISchema schema;

    @Override
    protected String getWsdlFilename() {
        return "NewWSDLFile.wsdl";
    }

    @Override
    protected String getWsdlFoldername() {
        return "pub/simple/";
    }

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        final IType[] allTypes = schema.getAllTypes(EXISTING_NAME);
        assertThereAreValidationErrorsPresent(1);
        assertEquals(2, allTypes.length);
        assertEquals(4, schema.getComponent().getContents().size());
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        assertThereAreNoValidationErrors();
        assertEquals(1, schema.getAllTypes(EXISTING_NAME).length);
        assertEquals(3, schema.getComponent().getContents().size());
    }

    @Override
    protected AbstractNotificationOperation getNextOperation(final IWsdlModelRoot modelRoot) throws Exception {
        this.modelRoot = modelRoot;
        this.schema = modelRoot.getDescription().getSchema(SCHEMA_NAMESPACE)[0];

        if (addElementCommand == null) {
            assertEquals(1, schema.getAllTypes(EXISTING_NAME).length);
            assertEquals(3, schema.getComponent().getContents().size());

            addElementCommand = new AddStructureTypeCommand((IXSDModelRoot) schema.getModelRoot(), schema, "label", "Element1",
                    true, (AbstractType) Schema.getDefaultSimpleType());
            return addElementCommand;
        }
        if (renameCommand == null) {
            final IStructureType addedElement = addElementCommand.getStructureType();
            renameCommand = new RenameStructureTypeCommand(schema.getModelRoot(), addedElement, EXISTING_NAME);
            return renameCommand;
        }

        return null;
    }

    @Override
    protected void assertPostOperationRedoState(final IUndoableOperation operation, final IWsdlModelRoot modelRoot) {
        if (operation == renameCommand) {
            int errors = 0;
            final IType[] allTypes = schema.getAllTypes(EXISTING_NAME);
            for (final IType type : allTypes) {
                final List<IValidationStatus> statuses = editor.getValidationService().getValidationStatusProvider().getStatus(
                        type);
                for (final IValidationStatus status : statuses) {
                    if (status.getSeverity() == IStatus.ERROR) {
                        errors++;
                    }
                }
            }
            assertEquals(1, errors);
            assertEquals(2, schema.getAllTypes(EXISTING_NAME).length);
            assertEquals(3, schema.getComponent().getContents().size());
        }
    }

    @Override
    protected void assertPostOperationUndoState(final IUndoableOperation operation, final IWsdlModelRoot modelRoot) {
        if (operation == renameCommand) {
            int errors = 0;

            // we need to check the part of errors
            // prior to the fix, a
            // "the part 'parameters' has invalid value 'NewOperationResponse'..."
            // was shown
            final IParameter outputParameter = modelRoot.getDescription().getAllInterfaces().iterator().next().getAllOperations()
                    .iterator().next().getAllOutputParameters().iterator().next();
            final List<IValidationStatus> statuses = editor.getValidationService().getValidationStatusProvider().getStatus(
                    outputParameter);
            for (final IValidationStatus status : statuses) {
                if (status.getSeverity() == IStatus.ERROR) {
                    errors++;
                }
            }

            assertEquals(0, errors);
            assertEquals(1, schema.getAllTypes(EXISTING_NAME).length);
            assertEquals(3, schema.getComponent().getContents().size());
        }
    }

}
