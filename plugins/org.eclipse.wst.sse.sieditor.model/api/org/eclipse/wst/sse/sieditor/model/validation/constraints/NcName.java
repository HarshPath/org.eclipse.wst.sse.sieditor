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

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintStatus;
import org.eclipse.wst.wsdl.WSDLElement;
import org.eclipse.wst.wsdl.WSDLPackage;
import org.eclipse.wst.wsdl.binding.soap.SOAPPackage;
import org.w3c.dom.Element;

import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;

public class NcName extends AbstractConstraint {

    @Override
    protected IStatus doValidate(IValidationContext ctx) {
        EObject target = ctx.getTarget();

        EStructuralFeature[] features = getRequiredFeatures(target);
        List<IStatus> statusList = new ArrayList<IStatus>(features.length);

        for (EStructuralFeature feature : features) {
            Object value = target.eGet(feature);
            boolean valid = true;

            if (value instanceof String) {
                valid = EmfXsdUtils.isValidNCName((String) value);
            } else if (value instanceof QName) {
                String localPart = ((QName) value).getLocalPart();
                valid = EmfXsdUtils.isValidNCName(localPart);
            }

            if (!valid) {
                statusList.add(createErrorStatus(ctx, target, feature));
            } else {
                List<EObject> resultLocus = new ArrayList<EObject>(1);
                resultLocus.add(feature);
                statusList.add(ConstraintStatus.createSuccessStatus(ctx, target, resultLocus));
            }
        }

        return createStatus(ctx, statusList);
    }

    @Override
    protected boolean shouldExecute(IValidationContext ctx) {
        return true;
    }

    protected IStatus createErrorStatus(IValidationContext ctx, EObject target, EStructuralFeature feature) {
        Element targetElement = ((WSDLElement) target).getElement();

        StringBuilder buf = new StringBuilder();
        if (targetElement.getPrefix() != null) {
            buf.append(targetElement.getPrefix()).append(':');
        }
        buf.append(targetElement.getLocalName());

        List<EObject> resultLocus = new ArrayList<EObject>(1);
        resultLocus.add(feature);

        return ConstraintStatus.createStatus(ctx, target, resultLocus, Messages.NcName_0, buf
                .toString(), getFeatureXMLName(feature.getName()));
    }

    public EStructuralFeature[] getRequiredFeatures(EObject target) {
        EClass eClass = target.eClass();
        
        EStructuralFeature[] features;
        
        if (eClass == WSDLPackage.Literals.MESSAGE) {
            features = getFeatures(target, WSDLPackage.MESSAGE__QNAME);
        } else if (eClass == WSDLPackage.Literals.PART) {
            features = getFeatures(target, WSDLPackage.PART__NAME);
        } else if (eClass == WSDLPackage.Literals.PORT_TYPE) {
            features = getFeatures(target, WSDLPackage.PORT_TYPE__QNAME);
        } else if (eClass == WSDLPackage.Literals.OPERATION) {
            features = getFeatures(target, WSDLPackage.OPERATION__NAME);
        } else if (eClass == WSDLPackage.Literals.INPUT) {
            features = getFeatures(target, WSDLPackage.INPUT__NAME);
        } else if (eClass == WSDLPackage.Literals.OUTPUT) {
            features = getFeatures(target, WSDLPackage.OUTPUT__NAME);
        } else if (eClass == WSDLPackage.Literals.FAULT) {
            features = getFeatures(target, WSDLPackage.FAULT__NAME);
        } else if (eClass == WSDLPackage.Literals.BINDING) {
            features = getFeatures(target, WSDLPackage.BINDING__QNAME);
        } else if (eClass == WSDLPackage.Literals.BINDING_FAULT) {
            features = getFeatures(target, WSDLPackage.BINDING_FAULT__NAME);
        } else if (eClass == WSDLPackage.Literals.BINDING_OPERATION) {
            features = getFeatures(target, WSDLPackage.BINDING_OPERATION__NAME);
        } else if (eClass == WSDLPackage.Literals.SERVICE) {
            features = getFeatures(target, WSDLPackage.SERVICE__QNAME);
        } else if (eClass == WSDLPackage.Literals.PORT) {
            features = getFeatures(target, WSDLPackage.PORT__NAME);
        } else if (eClass == SOAPPackage.Literals.SOAP_FAULT) {
            features = getFeatures(target, SOAPPackage.SOAP_FAULT__NAME);
        } else if (eClass == WSDLPackage.Literals.DEFINITION) {
            features = getFeatures(target, WSDLPackage.DEFINITION__QNAME);
        } else {
            Logger.logError("Unsupported target class: " + eClass.getName()); //$NON-NLS-1$
            features = new EStructuralFeature[0];
        }
        
        return features;
    }

    private EStructuralFeature[] getFeatures(EObject target, int... featureIds) {
        EClass eClass = target.eClass();
        EStructuralFeature[] features = new EStructuralFeature[featureIds.length];

        for (int ndx = 0; ndx < features.length; ndx++) {
            features[ndx] = eClass.getEStructuralFeature(featureIds[ndx]);
        }
        return features;
    }
}
