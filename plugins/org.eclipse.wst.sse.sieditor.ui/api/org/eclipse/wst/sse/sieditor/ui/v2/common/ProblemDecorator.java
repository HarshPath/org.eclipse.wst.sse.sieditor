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
 *    Dinko Ivanov - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.ui.v2.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatus;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatusProvider;

/**
 * A default implementation for {@link IProblemDecorator}
 * 
 */
public class ProblemDecorator implements IProblemDecorator {

    /**
     * The instance, which validation results are queried for problems.
     */
    private IModelObject modelObject;

    private Map<EStructuralFeature, IProblemDecoratableControl> decorationBindings = new HashMap<EStructuralFeature, IProblemDecoratableControl>();

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.wst.sse.sieditor.ui.v2.common.IProblemDecorator#bind(java
     * .lang.Integer,
     * org.eclipse.wst.sse.sieditor.ui.v2.common.IProblemDecoratableControl)
     */
    public void bind(EStructuralFeature feature, IProblemDecoratableControl control) {
        decorationBindings.put(feature, control);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.wst.sse.sieditor.ui.v2.common.IProblemDecorator#setModelObject
     * (org.eclipse.wst.sse.sieditor.model.api.IModelObject)
     */
    public void setModelObject(IModelObject modelObject) {
        this.modelObject = modelObject;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.wst.sse.sieditor.ui.v2.common.IProblemDecorator#
     * updateDecorations()
     */
    public void updateDecorations() {

        clearDecorations();

        if (modelObject == null) {
            return;
        }

        IValidationStatusProvider provider = getValidationStatusProvider();

        if (provider != null && !provider.isValid(modelObject)) {

            List<IValidationStatus> statusList = provider.getStatus(modelObject);

            if (!statusList.isEmpty()) {
                for (IValidationStatus status : statusList) {
                    Set<EObject> resultLocus = status.getResultLocus();
                    for (Object object : resultLocus) {
                        if (object instanceof EStructuralFeature) {
                            IProblemDecoratableControl decoratableControl = decorationBindings.get(object);
                            if (decoratableControl != null) {
                                decoratableControl.decorate(status.getSeverity(), status.getMessage());
                            }
                        }
                    }
                }
            }
        }

    }
    
	@Override
	public boolean decorationNeedsUpdate() {
		IValidationStatusProvider provider = getValidationStatusProvider();
		
		if(provider == null) {
			return false;
		}
		List<IValidationStatus> statusList = provider.getStatus(modelObject);
		
        if (statusList.isEmpty() || provider.isValid(modelObject)) {
        	for(IProblemDecoratableControl control : decorationBindings.values()) {
        		if(control.getDecorateSeverity() != IStatus.OK) {
        			return true;
        		}
        	}
        }
        else {
            for (IValidationStatus status : statusList) {
                Set<EObject> resultLocus = status.getResultLocus();
                for (Object object : resultLocus) {
                    if (object instanceof EStructuralFeature) {
                        IProblemDecoratableControl decoratableControl = decorationBindings.get(object);
                        int statusSeverity = status.getSeverity();
                        if (decoratableControl != null && statusSeverity != decoratableControl.getDecorateSeverity()) {
                            return true;
                        }
                    }
                }
            }
        }
		return false;
	}

    /**
     * Return the {@link IValidationStatusProvider}. Method provided for
     * testability
     * 
     * @return the {@link IValidationStatusProvider}
     */
    protected IValidationStatusProvider getValidationStatusProvider() {
        IValidationStatusProvider provider = modelObject == null ? null : (IValidationStatusProvider) Platform.getAdapterManager().getAdapter(modelObject,
                IValidationStatusProvider.class);
        return provider;
    }

    /*
     * Clears all decorations
     */
    private void clearDecorations() {
        for (IProblemDecoratableControl control : decorationBindings.values()) {
            control.decorate(IStatus.OK, null);
        }
    }

}
