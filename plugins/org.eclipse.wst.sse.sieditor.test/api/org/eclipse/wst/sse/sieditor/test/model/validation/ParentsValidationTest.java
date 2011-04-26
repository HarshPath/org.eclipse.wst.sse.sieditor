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

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.wst.sse.sieditor.core.common.IEnvironment;
import org.eclipse.wst.sse.sieditor.core.editorfwk.ModelHandler;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.validation.ESMModelAdapter;
import org.eclipse.wst.sse.sieditor.model.validation.EsmXsdModelAdapter;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationService;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatus;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatusProvider;
import org.eclipse.wst.sse.sieditor.model.validation.ValidationService;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.StructureType;
import org.eclipse.wst.sse.sieditor.test.util.ResourceUtils;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.junit.Before;
import org.junit.Test;
//A Transaction Rollback exception is thrown in the end ot the test execution. This is perfectly normal and out of the test scope!
@SuppressWarnings("nls")
public class ParentsValidationTest extends SIEditorBaseTest {

    private ResourceSetImpl resourceSet;
    private IDescription modelDescription;
    private TransactionalEditingDomain editingDomain;
    private IWsdlModelRoot modelRoot;
    private IValidationService validationService;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        final IFile file = ResourceUtils.copyFileIntoTestProject("validation/ParentsErrorMarkers.wsdl", Document_FOLDER_NAME,
                this.getProject(), "One.wsdl");
        resourceSet = new ResourceSetImpl();
        modelRoot = (IWsdlModelRoot) ModelHandler.retrieveModelObject(resourceSet, URI.createFileURI(file.getLocation().toFile()
                .getAbsolutePath()), false);
        editingDomain = modelRoot.getEnv().getEditingDomain();

        modelDescription = modelRoot.getDescription();
        validationService = new ValidationService(resourceSet, modelRoot);
        validationService.addModelAdapter(new EsmXsdModelAdapter());
        validationService.addModelAdapter(new ESMModelAdapter(modelRoot));

        final ObjectUndoContext ctx = new ObjectUndoContext(this);
        final IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();
        operationHistory.setLimit(ctx, 100);

        final IEnvironment env = modelRoot.getEnv();
        env.setOperationHistory(operationHistory);
        env.setUndoContext(ctx);
    }
  //A Transaction Rollback exception is thrown in the end ot the test execution. This is perfectly normal and out of the test scope!
    @Test
    public void testParentsAreInvalid() throws Throwable {

        editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {

            @Override
            protected void doExecute() {

                ISchema modelsSchema = modelDescription
                        .getSchema("http://webservices.amazon.com/AWSECommerceService/2010-04-01_simplified")[0];
                validationService.validate(modelsSchema);
                IValidationStatusProvider validationStatusProvider = validationService.getValidationStatusProvider();
                assertNotNull(validationStatusProvider.getStatus(modelsSchema));
                assertTrue(validationStatusProvider.getStatusMarker(modelsSchema)==IStatus.ERROR);

                StructureType type = (StructureType) modelsSchema.getType(true, "Item");
                assertTrue(validationStatusProvider.getStatusMarker(type)==IStatus.ERROR);
                Collection<IElement> elements = type.getElements("Tracks");
                assertNotNull(elements);
                assertEquals(1, elements.size());
                List<IValidationStatus> status = validationStatusProvider.getStatus(elements.iterator().next());

                assertEquals(1, status.size());
                assertEquals(IStatus.ERROR, status.get(0).getSeverity());
            }
        });
    }

}
