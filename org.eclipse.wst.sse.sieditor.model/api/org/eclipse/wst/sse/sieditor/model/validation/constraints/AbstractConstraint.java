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
package org.eclipse.wst.sse.sieditor.model.validation.constraints;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.EMFEventType;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintStatus;

import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.model.validation.IModelAdapter;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationService;

public abstract class AbstractConstraint extends AbstractModelConstraint {
    protected static IStatus createStatus(IValidationContext ctx, List<IStatus> statusList) {
        if (statusList == null || statusList.isEmpty()) {
            return ctx.createSuccessStatus();
        } else {
            return ConstraintStatus.createMultiStatus(ctx, statusList);
        }
    }

    protected abstract IStatus doValidate(IValidationContext ctx);

    protected abstract boolean shouldExecute(IValidationContext ctx);

    @Override
    public IStatus validate(IValidationContext ctx) {
        try {
            if (shouldExecute(ctx)) {
                return doValidate(ctx);
            }
        } catch (Exception t) {
            // EMF turns off the validation if exception occurs. We are catching
            // all exceptions to keep the validation running
            Logger.logError("Constraint validation failed", t); //$NON-NLS-1$
        }
        ctx.skipCurrentConstraintFor(ctx.getTarget());
        return null;// ctx.createSuccessStatus();
    }

    protected boolean isLiveValidation(IValidationContext ctx) {
        return ctx.getEventType() != EMFEventType.NULL;
    }

    protected boolean isBatchValidation(IValidationContext ctx) {
        return !isLiveValidation(ctx);
    }

    @SuppressWarnings("unchecked")
    protected <T> T getModelObject(EObject target, Class<T> modelObjectClass) {
        IValidationService validationService = null;
        for (Adapter adapter : target.eAdapters()) {
            if (adapter instanceof IValidationService) {
                validationService = (IValidationService) adapter;
            }
        }

        for (IModelAdapter modelAdapter : validationService.getModelAdapters()) {
            Object modelObject = modelAdapter.adaptToModelObject(target);
            if (modelObject != null && modelObjectClass.isAssignableFrom(modelObject.getClass())) {
                return (T) modelObject;
            }
        }
        return null;
    }

    /**
     * Translates EMF feature names to XML feature name. "aFeature" shall be
     * converted to "feature"
     * 
     * @param featureName
     * @return
     */
    protected String getFeatureXMLName(String featureName) {
        if (featureName.length() > 2) {
            char c1 = featureName.charAt(0);
            char c2 = featureName.charAt(1);
            if (Character.isLowerCase(c1) && Character.isUpperCase(c2)) {
                return Character.toLowerCase(c2) + featureName.substring(2);
            }
        }
        return featureName;
    }

}