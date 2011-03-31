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
package org.eclipse.wst.sse.sieditor.test.model.validation;

import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.wst.sse.sieditor.ui.ServiceInterfaceEditor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestValidationOnImportedWsdl extends SIEditorBaseTest {
    private static final String NEW_WSDL_FILE_WSDL = "NewWSDLFile.wsdl"; //$NON-NLS-1$
    private static final String NEW_WSDL_FILE2_WSDL = "NewWSDLFile2.wsdl"; //$NON-NLS-1$
    private static final String DIR_PATH_PUB_CSNS_TYPE_PROPERTY_EDITOR_TEST_DATA = "pub/csns/validationErrorOnExpand/"; //$NON-NLS-1$
    private IDescription modelDescription;
    private IWsdlModelRoot modelRoot;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        ResourceUtils.copyFileIntoTestProject(DIR_PATH_PUB_CSNS_TYPE_PROPERTY_EDITOR_TEST_DATA + NEW_WSDL_FILE_WSDL,
                Document_FOLDER_NAME, this.getProject(), NEW_WSDL_FILE_WSDL);

        modelRoot = (IWsdlModelRoot) getModelRoot(DIR_PATH_PUB_CSNS_TYPE_PROPERTY_EDITOR_TEST_DATA + NEW_WSDL_FILE2_WSDL,
                NEW_WSDL_FILE2_WSDL, ServiceInterfaceEditor.EDITOR_ID);

        modelDescription = modelRoot.getDescription();

    }

    @Test
    public void testValidateThatErrorFromImportedWsdlIsShown() throws Exception {
        super.assertThereAreValidationErrorsPresent(1);

    }

    @Override
    @After
    public void tearDown() throws Exception {
        disposeModel();
        super.tearDown();
    }

}
