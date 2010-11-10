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
package org.eclipse.wst.sse.sieditor.model.validation;

import java.util.Collection;
import java.util.Set;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.validation.service.IConstraintFilter;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;

public interface IValidationService {
    public static final int BATCH_MODE = 0;
    public static final int LIVE_MODE = 1;

    /**
     * @param source
     *            object to validate
     * @return validated objects
     * 
     * 
     */
    Set<IModelObject> validate(Object source);

    Set<IModelObject> validateAll(Collection<?> sources);

    void liveValidate(Collection<Notification> notification);

    void addModelAdapter(IModelAdapter modelAdapter);

    void removeModelAdapter(IModelAdapter modelAdapter);

    Collection<IModelAdapter> getModelAdapters();

    void addConstraintFilter(IConstraintFilter constraintFilter);

    void removeConstraintFilter(IConstraintFilter constraintFilter);

    void addValidationListener(IValidationListener listener);

    void removeValidationListener(IValidationListener listener);

    IValidationStatusProvider getValidationStatusProvider();

}
