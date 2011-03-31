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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.IConstraintStatus;
import org.eclipse.emf.validation.model.IModelConstraint;
import org.eclipse.emf.validation.service.IConstraintDescriptor;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationService;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatus;
import org.eclipse.wst.sse.sieditor.model.validation.impl.ValidationStatusRegistry;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.test.util.SIEditorBaseTest;
import org.eclipse.wst.sse.sieditor.ui.ServiceInterfaceEditor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ValidationStatusRegistryDuplicatedMessagesTest extends SIEditorBaseTest {
    private final int[] actualLength = new int[] { 0 };

    private IDescription modelDescription;
    private IWsdlModelRoot modelRoot;
    private IValidationService validationService;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        modelRoot = (IWsdlModelRoot) getModelRoot("validation/validaitionServiceTest.wsdl", "validaitionServiceTest.wsdl",
                ServiceInterfaceEditor.EDITOR_ID);

        modelDescription = modelRoot.getDescription();
        validationService = editor.getValidationService();

    }

    @Test
    public void testValidationStatusRegistryWithEqualStatuses() {

        final List<IValidationStatus> statuses = new ArrayList<IValidationStatus>();

        class ValidationStatusRegistryTest extends ValidationStatusRegistry {
            @Override
            public Set<IModelObject> add(Collection<IValidationStatus> statuses) {
                Set<IModelObject> added = super.add(statuses);
                actualLength[0] = added.size();
                return added;
            }
        }

        ValidationStatusRegistryTest registry = new ValidationStatusRegistryTest();

        final String message = "Test Message";

        final IModelConstraint modelConstraint = new IModelConstraint() {

            @Override
            public IStatus validate(IValidationContext ctx) {
                return null;
            }

            @Override
            public IConstraintDescriptor getDescriptor() {
                return null;
            }
        };

        IValidationStatus status = new IValidationStatus() {

            @Override
            public boolean isOK() {
                return false;
            }

            @Override
            public IModelObject getTarget() {
                return modelDescription;
            }

            @Override
            public IConstraintStatus getSourceStatus() {
                return null;
            }

            @Override
            public int getSeverity() {
                return 0;
            }

            @Override
            public Set<EObject> getResultLocus() {
                return null;
            }

            @Override
            public String getMessage() {
                return message;
            }

            @Override
            public String getId() {
                return null;
            }

            @Override
            public EObject getConstraintStatusTarget() {
                return modelDescription.getComponent();
            }
        };

        statuses.add(status);
        statuses.add(status);
        statuses.add(status);
        registry.add(statuses);
        assertEquals(actualLength[0], 1);

    }

    @Override
    @After
    public void tearDown() throws Exception {
        disposeModel();
        super.tearDown();
    }

}
