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
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDSchemaContent;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetNamespaceCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

/**
 * Test Case: Set target namespace of schema with existing prefix.
 * 
 * 
 * 
 */
public class SetSchemaTargetNamespaceTest1 extends AbstractSetDefinitionTNSCommandTest {

    private static final String SCHEMA_INITIAL_NAMESPACE = "http://www.example.org/NewWSDLFile/444";
    private static final String EXISTING_PREFIX = "ns1";

    private static final String SECOND_SCHEMA_NAMESPACE = "http://namespace1";

    private ISchema schema;

    @Override
    protected String getWsdlFilename() {
        return "DefinitionWithDefaultTNSPrefixWSDLAndSchemasWithExistingPrefixes.wsdl";
    }

    @Override
    protected String getWsdlFoldername() {
        return "pub/csns/setnamespace/";
    }

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        assertWsdlDocumentStateAsExpected(modelRoot.getDescription().getComponent(), modelRoot);

        final Definition definition = modelRoot.getDescription().getComponent();
        assertEquals(NEW_TARGET_NAMESPACE, schema.getComponent().getTargetNamespace());
        assertEquals(NEW_TARGET_NAMESPACE, definition.getNamespace(EXISTING_PREFIX));
        assertEquals(schema.getComponent().getQNamePrefixToNamespaceMap().get(EXISTING_PREFIX), NEW_TARGET_NAMESPACE);

        final XSDSchemaContent xsdSchemaContent = modelRoot.getDescription().getSchema(SECOND_SCHEMA_NAMESPACE)[0].getComponent()
                .getContents().get(0);
        assertEquals(NEW_TARGET_NAMESPACE, ((XSDImport) xsdSchemaContent).getNamespace());
        
        assertThereAreNoValidationErrors();
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        assertWsdlDocumentStateAsExpected(modelRoot.getDescription().getComponent(), modelRoot);

        final Definition definition = modelRoot.getDescription().getComponent();
        assertEquals(SCHEMA_INITIAL_NAMESPACE, schema.getComponent().getTargetNamespace());
        assertEquals(SCHEMA_INITIAL_NAMESPACE, definition.getNamespace(EXISTING_PREFIX));
        assertEquals(schema.getComponent().getQNamePrefixToNamespaceMap().get(EXISTING_PREFIX), SCHEMA_INITIAL_NAMESPACE);

        final XSDSchemaContent xsdSchemaContent = modelRoot.getDescription().getSchema(SECOND_SCHEMA_NAMESPACE)[0].getComponent()
                .getContents().get(0);
        assertEquals(SCHEMA_INITIAL_NAMESPACE, ((XSDImport) xsdSchemaContent).getNamespace());
        
        assertThereAreNoValidationErrors();
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        validationService = editor.getValidationService();
        schema = modelRoot.getDescription().getSchema(SCHEMA_INITIAL_NAMESPACE)[0];

        final Definition definition = modelRoot.getDescription().getComponent();
        assertEquals(SCHEMA_INITIAL_NAMESPACE, definition.getNamespace(EXISTING_PREFIX));
        assertEquals(SCHEMA_INITIAL_NAMESPACE, schema.getComponent().getQNamePrefixToNamespaceMap().get(EXISTING_PREFIX));

        final XSDSchemaContent xsdSchemaContent = modelRoot.getDescription().getSchema(SECOND_SCHEMA_NAMESPACE)[0].getComponent()
                .getContents().get(0);
        assertEquals(SCHEMA_INITIAL_NAMESPACE, ((XSDImport) xsdSchemaContent).getNamespace());

        return new SetNamespaceCommand(modelRoot, schema, NEW_TARGET_NAMESPACE);
    }

}