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
package org.eclipse.wst.sse.sieditor.model.validation.impl;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.wst.sse.sieditor.model.Activator;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationService;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatusProvider;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.OperationFault;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.OperationParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceInterface;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceOperation;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IFacet;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

@SuppressWarnings("unchecked")
public class ValidationAdapterFactory implements IAdapterFactory {

    private static final ILog logger = Activator.getDefault().getLog();

    private final Class[] adapterList;

    public ValidationAdapterFactory() {
        adapterList = new Class[] { IValidationStatusProvider.class, IModelObject.class };
    }

    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (adapterType == IValidationStatusProvider.class) {
            EObject eObject = null;

            if (adaptableObject instanceof IModelObject) {
                eObject = getEObject((IModelObject) adaptableObject);
            } else if (adaptableObject instanceof EObject) {
                eObject = (EObject) adaptableObject;
            }

            if (eObject != null) {
                for (Adapter adapter : eObject.eAdapters()) {
                    if (adapter instanceof IValidationService) {
                        return ((IValidationService) adapter).getValidationStatusProvider();
                    }
                }
            }
        }

        return null;
    }

    public Class[] getAdapterList() {
        return adapterList;
    }

    private EObject getEObject(IModelObject modelObject) {
        if (modelObject instanceof Description || modelObject instanceof OperationFault
                || modelObject instanceof OperationParameter || modelObject instanceof ServiceInterface
                || modelObject instanceof ServiceOperation || modelObject instanceof ISchema || modelObject instanceof IType
                || modelObject instanceof IElement || modelObject instanceof IFacet) {
            return modelObject.getComponent();
        }
        logger.log(new Status(IStatus.WARNING, Activator.PLUGIN_ID, "EObject not found for " + modelObject)); //$NON-NLS-1$
        return null;
    }
}
