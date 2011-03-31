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
package org.eclipse.wst.sse.sieditor.test.ui.v2.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.easymock.EasyMock.createMock;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatus;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatusProvider;
import org.eclipse.wst.sse.sieditor.ui.v2.common.IProblemDecoratableControl;
import org.eclipse.wst.sse.sieditor.ui.v2.common.IProblemDecorator;
import org.eclipse.wst.sse.sieditor.ui.v2.common.ProblemDecorator;
import org.junit.Test;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;

/**
 * A test for the {@link ProblemDecorator}
 * 
 *
 * 
 */
public class ProblemDecoratorTest {

    /**
     * Ensures that {@link ProblemDecorator#updateDecorations()} method will
     * call the decorate methods of the bound UI controls.
     */
    @Test
    public void testUpdateDecoration() {

        // ///////////// define data and mock objects ///////////////
        int severity = 111;
        String message = "test message";

        IModelObject modelObject = createMock(IModelObject.class);
        IProblemDecoratableControl controlForInvalidFeature = createMock(IProblemDecoratableControl.class);
        IProblemDecoratableControl controlForValidFeature = createMock(IProblemDecoratableControl.class);
        final IValidationStatusProvider validationStatusProvider = createMock(IValidationStatusProvider.class);
        IValidationStatus validationStatus = createMock(IValidationStatus.class);
        EStructuralFeature validFeature = createMock(EStructuralFeature.class);
        EStructuralFeature invalidFeature = createMock(EStructuralFeature.class);        

        List<IValidationStatus> statusList = new ArrayList<IValidationStatus>();
        statusList.add(validationStatus);

        Set<EObject> resultLocus = new HashSet<EObject>();
        resultLocus.add(invalidFeature);

        // ///////////// define behavior ///////////////
        expect(validationStatusProvider.isValid(modelObject)).andReturn(false).once();
        expect(validationStatusProvider.getStatus(modelObject)).andReturn(statusList).once();

        expect(validationStatus.getSeverity()).andReturn(severity).once();
        expect(validationStatus.getMessage()).andReturn(message).once();
        expect(validationStatus.getResultLocus()).andReturn(resultLocus).once();

        controlForInvalidFeature.decorate(IStatus.OK, null);
        controlForInvalidFeature.decorate(severity, message);
        controlForValidFeature.decorate(IStatus.OK, null);

        // ///////////// create the instance to be tested ///////////////
        IProblemDecorator decorator = new ProblemDecorator() {
            @Override
            protected IValidationStatusProvider getValidationStatusProvider() {
                return validationStatusProvider;
            }
        };
        decorator.setModelObject(modelObject);
        decorator.bind(invalidFeature, controlForInvalidFeature);
        decorator.bind(validFeature, controlForValidFeature);

        replay(modelObject, controlForInvalidFeature, controlForValidFeature, validationStatusProvider, validationStatus, validFeature);

        // ///////////invoke the method being tested ///////////////
        decorator.updateDecorations();

        verify(modelObject, controlForInvalidFeature, controlForValidFeature, validationStatusProvider, validationStatus, validFeature);
    }
}
