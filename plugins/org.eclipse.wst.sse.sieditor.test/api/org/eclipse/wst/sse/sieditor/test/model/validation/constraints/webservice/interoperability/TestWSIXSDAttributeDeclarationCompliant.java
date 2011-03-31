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

import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.WSDL_ARRAYTYPE_IS_NOT_ALLOWED_IN_XSDATTRIBUTE_ELEMENT;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationService;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatus;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatusProvider;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.ui.ServiceInterfaceEditor;

public class TestWSIXSDAttributeDeclarationCompliant extends SIEditorBaseTest {
    private IDescription modelDescription;
    private IValidationService validationService;
    private IWsdlModelRoot modelRoot;
    private static final String WSDL_FOR_WSI_XSDATTRIBUTEDECLARATION_COMPLIANT_WSDL = "WSDLForXSDAttributeDeclaration.wsdl"; //$NON-NLS-1$
    private static final String LOCATION_OF_THE_WSDL = "validation/WSDLForXSDAttributeDeclaration.wsdl"; //$NON-NLS-1$

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        if (modelRoot == null) {
            modelRoot = (IWsdlModelRoot) getModelRoot(LOCATION_OF_THE_WSDL, WSDL_FOR_WSI_XSDATTRIBUTEDECLARATION_COMPLIANT_WSDL,
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
    public void testSchemaForUsedXsdAttributeWsdlArrayType() throws Exception {
        final ISchema schema = modelDescription.getSchema("http://www.example.org/WSDLForXSDAttributeDeclaration/")[0]; //$NON-NLS-1$    
        final IValidationStatusProvider validationStatusProvider = validationService.getValidationStatusProvider();
        final List<IValidationStatus> status = validationStatusProvider.getStatus(schema);
        assertEquals(1, status.size());
        IValidationStatus statusForWSDLArrayTypeAttribute = status.get(0);
        assertEquals(IStatus.WARNING, statusForWSDLArrayTypeAttribute.getSeverity());
        assertEquals(WSDL_ARRAYTYPE_IS_NOT_ALLOWED_IN_XSDATTRIBUTE_ELEMENT,
                statusForWSDLArrayTypeAttribute.getMessage());
    }

    @Override
    @After
    public void tearDown() throws Exception {
        disposeModel();
        super.tearDown();
    }
}
