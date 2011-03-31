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

import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.wst.sse.sieditor.core.common.CollectionTypeUtils;
import org.eclipse.wst.sse.sieditor.core.common.Condition;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationService;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatus;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatusProvider;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.wst.sse.sieditor.ui.DataTypesEditor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ImportsValidationTest extends SIEditorBaseTest {

    private IXSDModelRoot modelRoot;
    private IValidationService validationService;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        ResourceUtils.copyFileIntoTestProject("validation/imports/Schema2.xsd", Document_FOLDER_NAME, this.getProject(),
                "Schema2.xsd");

        modelRoot = (IXSDModelRoot) getModelRoot("validation/imports/Schema1.xsd", "Schema1.xsd", DataTypesEditor.EDITOR_ID);
        validationService = editor.getValidationService();
        // final ValidationService validationService = new
        // ValidationService(resourceSet);
        // validationService.addModelAdapter(new EsmXsdModelAdapter());
    }

    @Test
    public void testValidationService() throws Throwable {
        final ISchema modelsSchema = modelRoot.getSchema();
        // forcefully resolve imports
        final Collection<ISchema> allReferredSchemas = modelsSchema.getAllReferredSchemas();

        modelRoot.getEnv().getEditingDomain().getCommandStack().execute(
                new RecordingCommand(modelRoot.getEnv().getEditingDomain()) {
                    @Override
                    protected void doExecute() {
                        // validationService.validate(modelsSchema);
                    }
                });
        final IValidationStatusProvider validationStatusProvider = validationService.getValidationStatusProvider();

        final Condition<? super ISchema> criteria = new Condition<ISchema>() {
            @Override
            public boolean isSatisfied(final ISchema x) {
                return "http://www.example.org/Schema2".equals(x.getNamespace());
            }
        };

        final List<ISchema> refSchemas = CollectionTypeUtils.find(allReferredSchemas, criteria);
        assertFalse(refSchemas.isEmpty());
        final List<IValidationStatus> status = validationStatusProvider.getStatus(refSchemas.get(0));
        assertEquals(2, status.size());
        assertEquals(IStatus.ERROR, status.get(0).getSeverity());
        assertEquals(IStatus.ERROR, status.get(1).getSeverity());
    }

    @Override
    @After
    public void tearDown() throws Exception {
        disposeModel();
        super.tearDown();
    }
}
