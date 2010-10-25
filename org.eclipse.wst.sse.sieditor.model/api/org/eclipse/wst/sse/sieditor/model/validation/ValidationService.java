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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.transaction.TransactionalEditingDomain.Lifecycle;
import org.eclipse.emf.transaction.util.Adaptable;
import org.eclipse.emf.validation.model.EvaluationMode;
import org.eclipse.emf.validation.model.IConstraintStatus;
import org.eclipse.emf.validation.service.IBatchValidator;
import org.eclipse.emf.validation.service.IConstraintFilter;
import org.eclipse.emf.validation.service.ILiveValidator;
import org.eclipse.emf.validation.service.ModelValidationService;
import org.eclipse.emf.workspace.WorkspaceEditingDomainFactory;
import org.eclipse.swt.widgets.Display;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.util.WSDLResourceImpl;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDDiagnostic;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDResourceImpl;

import org.eclipse.wst.sse.sieditor.core.common.IDisposable;
import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.model.Activator;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.validation.impl.ValidationEvent;
import org.eclipse.wst.sse.sieditor.model.validation.impl.ValidationStatus;
import org.eclipse.wst.sse.sieditor.model.validation.impl.ValidationStatusRegistry;
import org.eclipse.wst.sse.sieditor.model.validation.impl.XSDDiagnosticValidationStatus;

public class ValidationService extends EContentAdapter implements IValidationService, IDisposable {

    public static final String OPTION_BATCH_VALIDTION = "batch_validation"; //$NON-NLS-1$
    private final ValidationStatusRegistry registry;
    private final List<IModelAdapter> adapters;
    private final List<IValidationListener> listeners;

    protected ILiveValidator liveValidator;
    protected IBatchValidator batchValidator;
    
    private EditingDomainListener editingDomainListener;
    
    private ResourceSet resourceSet;

    private static final ILog logger = Activator.getDefault().getLog();

    private final boolean logEnabled;

    public ValidationService(final ResourceSet resourceSet, final IModelRoot modelRoot) {
        super();

        registry = new ValidationStatusRegistry();
        adapters = new ArrayList<IModelAdapter>();
        listeners = new ArrayList<IValidationListener>();

        liveValidator = ModelValidationService.getInstance().newValidator(EvaluationMode.LIVE);
        liveValidator.setReportSuccesses(true);

        batchValidator = ModelValidationService.getInstance().newValidator(EvaluationMode.BATCH);
        batchValidator.setReportSuccesses(true);
        batchValidator.setIncludeLiveConstraints(true);

        update(resourceSet, modelRoot);

        logEnabled = System.getProperty("validationLogging") != null; //$NON-NLS-1$
    }

    public void update(final ResourceSet resourceSet, final IModelRoot modelRoot) {
    	detachFromCurrentResourceSet();
    	final Adaptable editingDomainAdaptable = (Adaptable) WorkspaceEditingDomainFactory.INSTANCE.getEditingDomain(resourceSet);
		final Lifecycle lifecycle = editingDomainAdaptable.getAdapter(Lifecycle.class);
        
		editingDomainListener = new EditingDomainListener(this);
		lifecycle.addTransactionalEditingDomainListener(editingDomainListener);

        resourceSet.eAdapters().add(this);
        this.resourceSet = resourceSet;
    }
    
    private void detachFromCurrentResourceSet() {
    	if(resourceSet == null) {
    		return;
    	}
    	final Adaptable editingDomainAdaptable = (Adaptable) WorkspaceEditingDomainFactory.INSTANCE.getEditingDomain(resourceSet);
    	
    	if(editingDomainAdaptable != null) {
	    	final Lifecycle lifecycle = editingDomainAdaptable.getAdapter(Lifecycle.class);
	    	
	    	lifecycle.removeTransactionalEditingDomainListener(editingDomainListener);
	        resourceSet.eAdapters().remove(this);
    	}
    }

    @Override
    public Set<IModelObject> validate(final Object source) {

        final EObject obj;
        if (source instanceof EObject) {
            obj = (EObject) source;
        } else {
            obj = adaptToEMFObject(source);
        }

        if (obj == null) {
            return Collections.emptySet();
        }

        try {
            registry.clearSchemaOrDefinition(obj);
            final IStatus result = batchValidator.validate(obj);
            return processValidationResults(result, EvaluationMode.BATCH);
        } catch (final Exception e) {
            Logger.log(Activator.PLUGIN_ID, IStatus.WARNING, "Can not validate: " + obj.toString(), e); //$NON-NLS-1$
        }
        return Collections.emptySet();
    }

    public Set<IModelObject> validateAll(final Collection<?> sources) {

        final Set<IStatus> statuses = new HashSet<IStatus>();

        for (final Object source : sources) {
            final EObject obj;
            if (source instanceof EObject) {
                obj = (EObject) source;
            } else {
                obj = adaptToEMFObject(source);
            }

            if (obj == null) {
                return Collections.emptySet();
            }
            try {
                registry.clearSchemaOrDefinition(obj);
                statuses.add(batchValidator.validate(obj));
            } catch (final Exception e) {
                Logger.log(Activator.PLUGIN_ID, IStatus.WARNING, "Can not validate: " + obj.toString(), e); //$NON-NLS-1$
            }
        }

        return processValidationResults(new MultiStatus(Activator.PLUGIN_ID, IStatus.INFO, statuses.toArray(new IStatus[statuses
                .size()]), "This is a multistatus. Check children", null), EvaluationMode.BATCH); //$NON-NLS-1$

    }

    protected boolean validateDescription(final EObject eObject) {
        final Resource resource = eObject.eResource();
        if (resource != null && isWSDLResource(resource)) {
            validate(getDefinitionFromResource(resource));
            return true;
        }
        return false;
    }

    protected boolean isWSDLResource(final Resource resource) {
        return resource instanceof WSDLResourceImpl;
    }

    protected Definition getDefinitionFromResource(final Resource resource) {
        return ((WSDLResourceImpl) resource).getDefinition();
    }

    public void liveValidate(final Collection<Notification> notifications) {
        final Set<XSDSchema> schemas = new HashSet<XSDSchema>();
        boolean descriptionValidated = false;
        boolean descriptionNeedsValidation = false;

        for (final Notification notification : notifications) {

            Object notifier = notification.getNotifier();
            if (notifier instanceof WSDLResourceImpl) {

                notifier = ((WSDLResourceImpl) notifier).getDefinition();
            } else if (notifier instanceof XSDResourceImpl) {
                notifier = ((XSDResourceImpl) notifier).getSchema();
            }
            if (notifier instanceof XSDConcreteComponent) {

                final XSDSchema schema = ((XSDConcreteComponent) notifier).getSchema();

                if (schemas.contains(schema) || schema == null) {
                    continue;
                }
                if (schema != null) {
                    registry.clearSchemaOrDefinition(schema);
                    schemas.add(schema);
                }
                liveValidate(notification);
                descriptionNeedsValidation = true;

            } else if (!(notifier instanceof EObject)) {
                continue;
            } else {
                descriptionNeedsValidation = true;
            }

            if (!descriptionValidated && descriptionNeedsValidation) {
                descriptionValidated = validateDescription((EObject) notifier);
            }
        }
    }

    protected void liveValidate(final Notification notification) {
        processValidationResults(liveValidator.validate(notification), EvaluationMode.LIVE);
    }

    @Override
    public void notifyChanged(final Notification notification) {
        super.notifyChanged(notification);
    }

    public void addConstraintFilter(final IConstraintFilter constraintFilter) {
        liveValidator.addConstraintFilter(constraintFilter);
        batchValidator.addConstraintFilter(constraintFilter);
    }

    public void addModelAdapter(final IModelAdapter modelAdapter) {
        if (!adapters.contains(modelAdapter)) {
            adapters.add(modelAdapter);
        }
    }

    public void removeConstraintFilter(final IConstraintFilter constraintFilter) {
        liveValidator.removeConstraintFilter(constraintFilter);
        batchValidator.removeConstraintFilter(constraintFilter);
    }

    public void removeModelAdapter(final IModelAdapter modelAdapter) {
        adapters.remove(modelAdapter);
    }

    public void addValidationListener(final IValidationListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public List<IValidationListener> getValidationListeners() {
        return Collections.unmodifiableList(listeners);
    }
    
    public Collection<IModelAdapter> getModelAdapters() {
        return adapters;
    }

    public void removeValidationListener(final IValidationListener listener) {
        listeners.remove(listener);
    }

    public IValidationStatusProvider getValidationStatusProvider() {
        return registry;
    }

    private Set<IModelObject> processValidationResults(final IStatus status, final EvaluationMode<?> mode) {
        final List<IValidationStatus> validationStatusList = new ArrayList<IValidationStatus>();
        processStatus(status, validationStatusList);

        if (validationStatusList != null && !validationStatusList.isEmpty()) {
            final Set<IModelObject> updatedModelObjects = registry.add(validationStatusList);

            notifyListeners(updatedModelObjects, mode);
            return updatedModelObjects;
        }
        return Collections.emptySet();
    }

    private void processStatus(final IStatus status, final List<IValidationStatus> validationStatusList) {
        if (status.isMultiStatus()) {
            final IStatus[] children = status.getChildren();
            for (final IStatus subStatus : children) {
                if (subStatus.isMultiStatus()) {
                    processStatus(subStatus, validationStatusList);
                    continue;
                }
                processSingleValidationStatus(subStatus, validationStatusList);
            }
        } else {
            processSingleValidationStatus(status, validationStatusList);
        }
    }

    private void processSingleValidationStatus(final IStatus status, final List<IValidationStatus> validationStatusList) {
        if (logEnabled && !status.isOK()) {
            logger.log(status);
        }
        if (!(status instanceof IConstraintStatus)) {
            return;
        }

        final IConstraintStatus constraintStatus = (IConstraintStatus) status;
        final EObject constraintStatusTarget = constraintStatus.getTarget();
        final Collection<IModelObject> modelObjects = adaptToModelObject(constraintStatusTarget);

        for (final IModelObject modelObject : modelObjects) {
            if (constraintStatusTarget instanceof XSDDiagnostic && !constraintStatus.isOK()) {
                validationStatusList.add(new XSDDiagnosticValidationStatus(constraintStatus, modelObject));
            } else {
                validationStatusList.add(new ValidationStatus(constraintStatus, modelObject));
            }
        }
    }

    private EObject adaptToEMFObject(final Object source) {
        for (final IModelAdapter modelAdapter : adapters) {
            final EObject eObject = modelAdapter.adapatToEMF(source);
            if (eObject != null) {
                return eObject;
            }
        }
        return null;
    }

    public Set<IModelObject> adaptToModelObject(final EObject source) {
        final Set<IModelObject> modelObjects = new HashSet<IModelObject>();

        for (final IModelAdapter modelAdapter : adapters) {
            final IModelObject modelObject = (IModelObject) modelAdapter.adaptToModelObject(source);
            if (modelObject != null) {
                modelObjects.add(modelObject);
            }
        }
        return modelObjects;
    }

    private void notifyListeners(final Set<IModelObject> modelObjects, final EvaluationMode<?> mode) {

        final int intMode = (mode == EvaluationMode.LIVE) ? IValidationEvent.LIVE_MODE : IValidationEvent.BATCH_MODE;

        for (final IValidationListener listener : listeners) {
            final Set<Object> supportedObjects = new HashSet<Object>();
            for (final Object modelObject : modelObjects) {
                if (listener.isSupportedModelObject(modelObject)) {
                    supportedObjects.add(modelObject);
                }
            }
            final ValidationEvent event = new ValidationEvent(registry, supportedObjects, intMode);
            if (Display.getCurrent() != null) {
                listener.validationPerformed(event);
            } else {
                Display.getDefault().asyncExec(new Runnable() {

                    public void run() {

                        listener.validationPerformed(event);

                    }
                });
            }
        }
    }

	@Override
	public void doDispose() {
		detachFromCurrentResourceSet();
		
	}

}
