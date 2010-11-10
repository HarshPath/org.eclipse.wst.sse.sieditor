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

import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.model.IConstraintStatus;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatus;

public class ValidationStatus implements IValidationStatus {
    protected final IConstraintStatus constraintStatus;
    protected final IModelObject modelObject;

    public ValidationStatus(IConstraintStatus constraintStatus, IModelObject modelObject) {
        this.constraintStatus = constraintStatus;
        this.modelObject = modelObject;
    }

    public String getMessage() {
        return constraintStatus.getMessage();
    }

    public int getSeverity() {
        return constraintStatus.getSeverity();
    }

    public IModelObject getTarget() {
        return modelObject;
    }

    public boolean isOK() {
        return getSeverity() == IStatus.OK;
    }

    public String getId() {
        return constraintStatus.getConstraint().getDescriptor().getId();
    }

    public EObject getConstraintStatusTarget() {
        return constraintStatus.getTarget();
    }

    public IConstraintStatus getSourceStatus() {
        return constraintStatus;
    }

    public Set<EObject> getResultLocus() {
        return constraintStatus.getResultLocus();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((constraintStatus == null) ? 0 : constraintStatusHashCode());
        return result;
    }

    private int constraintStatusHashCode() {
        return constraintStatus.getMessage().hashCode() + constraintStatus.getTarget().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ValidationStatus other = (ValidationStatus) obj;
        if (constraintStatus == null) {
            if (other.constraintStatus != null)
                return false;
        } else if (this.hashCode() != other.hashCode()) {
            return false;
        }
        return true;
    }
}
