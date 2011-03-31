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
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDSchemaContent;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetNamespaceCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

public class SetSchemaTargetNamespaceTest5 extends AbstractSetDefinitionTNSCommandTest {

    private static final String FIRST_SCHEMA_NAMESPACE = "http://sap.com/xi/EA-APPL/SE/Global";

    private ISchema schema;

    private static final String SECOND_SCHEMA_NAMESPACE = "http://sap.com/xi/SAPGlobal20/Global";

    @Override
    protected String getWsdlFilename() {
        return "ECC_IMAPPROPRIATIONREQREJRC_VALID.wsdl";
    }

    @Override
    protected String getWsdlFoldername() {
        return "pub/csns/";
    }

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        assertThereAreNoValidationErrors();
        assertWsdlDocumentStateAsExpected(modelRoot.getDescription().getComponent(), modelRoot);

        assertEquals(NEW_TARGET_NAMESPACE, schema.getComponent().getTargetNamespace());
        final XSDSchemaContent xsdSchemaContent = modelRoot.getDescription().getSchema(SECOND_SCHEMA_NAMESPACE)[0].getComponent()
                .getContents().get(0);
        assertEquals(NEW_TARGET_NAMESPACE, ((XSDImport) xsdSchemaContent).getNamespace());
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        assertThereAreNoValidationErrors();
        assertWsdlDocumentStateAsExpected(modelRoot.getDescription().getComponent(), modelRoot);

        assertEquals(FIRST_SCHEMA_NAMESPACE, schema.getComponent().getTargetNamespace());

        final XSDSchemaContent xsdSchemaContent = modelRoot.getDescription().getSchema(SECOND_SCHEMA_NAMESPACE)[0].getComponent()
                .getContents().get(0);
        assertEquals(FIRST_SCHEMA_NAMESPACE, ((XSDImport) xsdSchemaContent).getNamespace());
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        validationService = editor.getValidationService();
        schema = modelRoot.getDescription().getSchema(FIRST_SCHEMA_NAMESPACE)[0];

        final XSDSchemaContent xsdSchemaContent = modelRoot.getDescription().getSchema(SECOND_SCHEMA_NAMESPACE)[0].getComponent()
                .getContents().get(0);
        assertEquals(FIRST_SCHEMA_NAMESPACE, ((XSDImport) xsdSchemaContent).getNamespace());

        return new SetNamespaceCommand(modelRoot, schema, NEW_TARGET_NAMESPACE);
    }

}