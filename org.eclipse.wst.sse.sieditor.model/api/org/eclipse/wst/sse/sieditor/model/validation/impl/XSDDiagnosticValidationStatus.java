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
 *    Richard Birenheide - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.model.validation.impl;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.model.IConstraintStatus;
import org.eclipse.xsd.XSDDiagnostic;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;

public class XSDDiagnosticValidationStatus extends ValidationStatus {
    private static final String SEPARATOR = "::"; //$NON-NLS-1$

    public XSDDiagnosticValidationStatus(IConstraintStatus constraintStatus, IModelObject modelObject) {
        super(constraintStatus, modelObject);
    }

    @Override
    public String getId() {
        final StringBuilder id;

        id = new StringBuilder(super.getId());
        id.append(SEPARATOR);
        id.append(((XSDDiagnostic) constraintStatus.getTarget()).getKey());

        return id.toString();
    }

    @Override
    public EObject getConstraintStatusTarget() {
        return ((XSDDiagnostic) constraintStatus.getTarget()).getPrimaryComponent();
    }

    @Override
    public Set<EObject> getResultLocus() {
        final Set<EObject> resultLocus = super.getResultLocus();
        if (resultLocus == null) {
            return null;
        }

        for (Iterator<EObject> iter = resultLocus.iterator(); iter.hasNext();) {
            final EObject current = iter.next();
            if (current instanceof XSDDiagnostic) {
                iter.remove();
            }
        }
        return resultLocus;
    }
}
