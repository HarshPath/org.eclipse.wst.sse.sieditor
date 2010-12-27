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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.impl.AbstractModelObject;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatus;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatusProvider;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatusRegistry;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

public class ValidationStatusRegistry implements IValidationStatusRegistry, IValidationStatusProvider {

    private Map<EObject, Map<Object, List<IValidationStatus>>> registry;

    private HashMap<Object, Integer> objectsWithErrors = new HashMap<Object, Integer>();
    private HashMap<IModelObject, Integer> updatedModelObjects = new HashMap<IModelObject, Integer>();

    public ValidationStatusRegistry() {
        registry = new HashMap<EObject, Map<Object, List<IValidationStatus>>>();

    }

    public Set<IModelObject> add(Collection<IValidationStatus> statuses) {

        for (IValidationStatus status : statuses) {

            if (status.isOK()) {
                continue;
            }

            Map<Object, List<IValidationStatus>> localRegistry = null;

            IModelObject target = (IModelObject) status.getTarget();

            EObject targetContainer = target.getModelRoot().getModelObject().getComponent();
            localRegistry = registry.get(targetContainer);

            if (localRegistry == null) {
                localRegistry = new HashMap<Object, List<IValidationStatus>>();
                EObject key = targetContainer;

                registry.put(key, localRegistry);
            }

            List<IValidationStatus> statusList = localRegistry.get(target);

            if (statusList == null) {
                statusList = new ArrayList<IValidationStatus>();
                localRegistry.put(status.getTarget(), statusList);
            }

            if(!statusList.contains(status)) {
                statusList.add(status);
            }
            updatedModelObjects.put(status.getTarget(), status.getSeverity());

        }

        HashMap<IModelObject, Integer> allUpdatedObjects = findAllModelObjectsWithErrors(updatedModelObjects.keySet());

        for (Entry<IModelObject, Integer> updatedObj : allUpdatedObjects.entrySet()) {
            if (updatedObj.getValue()==IStatus.OK) {
                this.objectsWithErrors.remove(updatedObj.getKey());
                this.updatedModelObjects.remove(updatedObj.getKey());
            } else {
                this.objectsWithErrors.put(updatedObj.getKey(),updatedObj.getValue());
            }
        }

        return allUpdatedObjects.keySet();
    }

    private HashMap<IModelObject, Integer> findAllModelObjectsWithErrors(Collection<IModelObject> updatedModelObjects) {
        HashMap<IModelObject, Integer> updatedParents = new HashMap<IModelObject, Integer>();

        for (IModelObject updated : updatedModelObjects) {

            Integer statusType = getStatusType(updated);
            IModelObject root = updated.getRoot();
            if (root == null) {
                root = updated;
            }
            IModelRoot rootModelObject = root.getModelRoot();
            boolean isWSDLRoot = rootModelObject instanceof IWsdlModelRoot;

            do {
                //need changes
                boolean exists = updated.getComponent().eResource() != null;
                Integer parentStatus = updatedParents.get(updated);
                if (exists && (parentStatus == null || (statusType > parentStatus))) {

                    updatedParents.put(updated, statusType);
                }
                if (updated instanceof AbstractModelObject) {
                    updated = ((AbstractModelObject) updated).getDirectParent();
                } else {
                    updated = null;
                }

            } while ((updated != null) && (isWSDLRoot ? (!(updated instanceof IDescription)) : (!(updated instanceof ISchema))));
        }

        return updatedParents;

    }

    /**
     * 
     * Clears the statuses for XSDSchema or Definition (and their children) from
     * the registry.
     * 
     * @param object
     *            XSDSchema or Definition
     */
    public void clearSchemaOrDefinition(EObject object) {
        registry.remove(object);
    }

    public List<IValidationStatus> get(IModelObject target) {

        IModelRoot modelRoot = target.getModelRoot();

        Map<Object, List<IValidationStatus>> localRegistry = null;
        if (modelRoot instanceof IXSDModelRoot) {
            localRegistry = registry.get(((IXSDModelRoot) modelRoot).getSchema().getComponent());
        } else if (modelRoot instanceof IWsdlModelRoot) {
            localRegistry = registry.get(((IWsdlModelRoot) modelRoot).getDescription().getComponent());
        }

        if (localRegistry == null) {
            return Collections.emptyList();
        }

        List<IValidationStatus> statuses = localRegistry.get(target);
        if (statuses == null) {
            return Collections.emptyList();
        } else {
            return statuses;
        }
    }

    public List<IValidationStatus> getStatus(IModelObject modelObject) {
        return get(modelObject);
    }

    public Integer getStatusType(IModelObject modelObject) {
        final List<IValidationStatus> list = get(modelObject);
        if(list==null)
            return IStatus.OK;
        for(IValidationStatus currentStatus : list){
            if(currentStatus.getSeverity() == IStatus.ERROR){
                return IStatus.ERROR;
            }
        }
        return !list.isEmpty() ? IStatus.WARNING : IStatus.OK;
    }

    public boolean isValid(IModelObject modelObject) {
        final List<IValidationStatus> list = get(modelObject);

        return list == null || list.isEmpty();
    }

    public Integer getStatusMarker(IModelObject modelObject) {
        Integer result = this.objectsWithErrors.get(modelObject);
        if(result==null)
            return IStatus.OK;
        return result;
    }

}
