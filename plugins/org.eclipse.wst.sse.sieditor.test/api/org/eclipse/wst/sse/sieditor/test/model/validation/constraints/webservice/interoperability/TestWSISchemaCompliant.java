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
package org.eclipse.wst.sse.sieditor.test.model.validation.constraints.webservice.interoperability;

import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.NAMESPACE_FOR_IMPORTED_SCHEMA_MUST_NOT_BE_RELATIVE;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.THE_DOCUMENT_DOESNOT_HAVE_APPROPRIATE_VERSION_AND_ENCODING;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationService;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatus;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatusProvider;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.ui.ServiceInterfaceEditor;

public class TestWSISchemaCompliant extends SIEditorBaseTest {
    private static final String WSDL_FOR_WSI_SCHEMA_COMPLIANT_WSDL = "WSDLForWSISchemaCompliant.wsdl"; //$NON-NLS-1$
    private static final String LOCATION_OF_THE_WSDL = "validation/WSDLForWSISchemaCompliant.wsdl"; //$NON-NLS-1$
    private static final String MY_SCHEMA_XSD = "MySchema.xsd"; //$NON-NLS-1$
    private static final String SCHEMA_XSD = "schema.xsd"; //$NON-NLS-1$
    private static final String SCHEMA_LOCATION = "validation/schema.xsd"; //$NON-NLS-1$
    private static final String MY_SCHEMA_LOCATION = "validation/MySchema.xsd"; //$NON-NLS-1$
    private IDescription modelDescription;
    private IValidationService validationService;
    private IWsdlModelRoot modelRoot;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        if (modelRoot == null) {
            ResourceUtils.copyFileIntoTestProject(MY_SCHEMA_LOCATION, Document_FOLDER_NAME, this.getProject(), MY_SCHEMA_XSD);
            ResourceUtils.copyFileIntoTestProject(SCHEMA_LOCATION, Document_FOLDER_NAME, this.getProject(), SCHEMA_XSD);
            modelRoot = (IWsdlModelRoot) getModelRoot(LOCATION_OF_THE_WSDL, WSDL_FOR_WSI_SCHEMA_COMPLIANT_WSDL,
                    ServiceInterfaceEditor.EDITOR_ID);

            modelDescription = modelRoot.getDescription();

            validationService = editor.getValidationService();

            modelRoot.getEnv().getEditingDomain().getCommandStack().execute(
                    new RecordingCommand(modelRoot.getEnv().getEditingDomain()) {
                        @Override
                        protected void doExecute() {
                            // validationService.validate(schema);
                        }
                    });
        }
    }

    @Test
    public void testSchemaWithRelativeNamespaceAttributeInImportedSchema() throws Exception {
        final ISchema schema = modelDescription.getSchema("http://www.example.org/WSDLForWSISchemaCompliant/")[0]; //$NON-NLS-1$
        final IValidationStatusProvider validationStatusProvider = validationService.getValidationStatusProvider();
        final List<IValidationStatus> status = validationStatusProvider.getStatus(schema);
        assertEquals(0, status.size());

        final Collection<ISchema> importedSchemas = schema.getAllReferredSchemas();
        importedSchemas.remove(schema);

        boolean schemaForValidationFound = false;
        for (final ISchema importedSchema : importedSchemas) {
            if (!"targetnamespacMyschema".equals(importedSchema.getNamespace())) {
                continue;
            }
            final List<IValidationStatus> statuses = validationStatusProvider.getStatus(importedSchema);
            assertEquals(1, statuses.size());
            final IValidationStatus statusForImportedSchemaWithRelativeNamespace = statuses.get(0);
            assertEquals(IStatus.WARNING, statusForImportedSchemaWithRelativeNamespace.getSeverity());
            assertEquals(NAMESPACE_FOR_IMPORTED_SCHEMA_MUST_NOT_BE_RELATIVE, statusForImportedSchemaWithRelativeNamespace
                    .getMessage());
            schemaForValidationFound = true;
        }
        assertTrue(
                "the schema for validation was not found in the imported schemas for schema \"" + schema.getNamespace() + "\"",
                schemaForValidationFound);

    }

    @Test
    public void testSchemaForAppropriateVersionAndEncodingAndRelativeNamespaceOfImportedSchema() throws Exception {
        final ISchema schema = modelDescription.getSchema("http://namespace1")[0]; //$NON-NLS-1$    
        final IValidationStatusProvider validationStatusProvider = validationService.getValidationStatusProvider();
        final List<IValidationStatus> status = validationStatusProvider.getStatus(schema);
        assertEquals(1, status.size());
        final IValidationStatus someOfTheimportedSchemasHasInappropriateEncodingOrXMLVersion = status.get(0);
        assertEquals(IStatus.WARNING, someOfTheimportedSchemasHasInappropriateEncodingOrXMLVersion.getSeverity());
        assertEquals(THE_DOCUMENT_DOESNOT_HAVE_APPROPRIATE_VERSION_AND_ENCODING,
                someOfTheimportedSchemasHasInappropriateEncodingOrXMLVersion.getMessage());

        final Collection<ISchema> importedSchemas = schema.getAllReferredSchemas();
        importedSchemas.remove(schema);

        boolean schemaForValidationFound = false;
        for (final ISchema importedSchema : importedSchemas) {
            if (!"cecomaster".equals(importedSchema.getNamespace())) {
                continue;
            }
            final List<IValidationStatus> statuses = validationStatusProvider.getStatus(importedSchema);
            final IValidationStatus statusForImportedSchemaWithRelativeNamespace = statuses.get(0);
            assertEquals(1, statuses.size());
            assertEquals(IStatus.WARNING, statusForImportedSchemaWithRelativeNamespace.getSeverity());
            assertEquals(NAMESPACE_FOR_IMPORTED_SCHEMA_MUST_NOT_BE_RELATIVE, statusForImportedSchemaWithRelativeNamespace
                    .getMessage());
            schemaForValidationFound = true;
        }
        assertTrue("schema for validation was not found", schemaForValidationFound);
    }

    @Override
    @After
    public void tearDown() throws Exception {
        disposeModel();
        super.tearDown();
    }
}
