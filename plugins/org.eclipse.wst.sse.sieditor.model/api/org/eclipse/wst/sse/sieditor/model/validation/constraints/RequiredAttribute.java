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
import org.eclipse.wst.wsdl.Input;
import org.eclipse.wst.wsdl.Operation;
import org.eclipse.wst.wsdl.Output;
import org.eclipse.wst.wsdl.WSDLElement;
import org.eclipse.wst.wsdl.WSDLPackage;
import org.eclipse.wst.wsdl.binding.http.HTTPPackage;
import org.eclipse.wst.wsdl.binding.soap.SOAPPackage;
import org.w3c.dom.Element;

import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;

public class RequiredAttribute extends AbstractConstraint {

    @Override
    protected IStatus doValidate(IValidationContext ctx) {
        EObject target = ctx.getTarget();

        EStructuralFeature[] features = getRequiredFeatures(target);
        List<IStatus> statusList = new ArrayList<IStatus>(features.length);

        for (EStructuralFeature feature : features) {
            Object value = target.eGet(feature);
            boolean exists = true;

            if (value == null || !target.eIsSet(feature)) {
                exists = false;
            } else if (value instanceof QName) {
                String localPart = ((QName) value).getLocalPart();
                exists = localPart != null && !localPart.isEmpty();
            }

            if (!exists) {
                if ((target instanceof Input || target instanceof Output)
                        && feature.equals(WSDLPackage.Literals.MESSAGE_REFERENCE__EMESSAGE)) {
                    WSDLElement container = ((WSDLElement) target).getContainer();
                    if (container instanceof Operation) {

                        List<EObject> resultLocus = new ArrayList<EObject>(1);
                        resultLocus.add(feature);

                        return ConstraintStatus.createStatus(ctx, container, resultLocus,
                                Messages.RequiredAttribute_MESSAGE_REFERENCE_NOT_FOUND, target instanceof Input ? Messages.RequiredAttribute_INPUT : Messages.RequiredAttribute_OUTPUT);
                    }
                }
                statusList.add(createErrorStatus(ctx, target, feature));
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

        return ConstraintStatus.createStatus(ctx, target, resultLocus, Messages.RequiredAttribute, buf.toString(),
                getFeatureXMLName(feature.getName()));
    }

    public EStructuralFeature[] getRequiredFeatures(EObject target) {
        EClass eClass = target.eClass();

        EStructuralFeature[] features;

        if (eClass == WSDLPackage.Literals.IMPORT) {
            features = getFeatures(target, WSDLPackage.IMPORT__LOCATION_URI, WSDLPackage.IMPORT__NAMESPACE_URI);
        } else if (eClass == WSDLPackage.Literals.MESSAGE) {
            features = getFeatures(target, WSDLPackage.MESSAGE__QNAME);
        } else if (eClass == WSDLPackage.Literals.PART) {
            features = getFeatures(target, WSDLPackage.PART__NAME);
        } else if (eClass == WSDLPackage.Literals.PORT_TYPE) {
            features = getFeatures(target, WSDLPackage.PORT_TYPE__QNAME);
        } else if (eClass == WSDLPackage.Literals.OPERATION) {
            features = getFeatures(target, WSDLPackage.OPERATION__NAME);
        } else if (eClass == WSDLPackage.Literals.INPUT) {
            features = getFeatures(target, WSDLPackage.INPUT__EMESSAGE);
        } else if (eClass == WSDLPackage.Literals.OUTPUT) {
            features = getFeatures(target, WSDLPackage.OUTPUT__EMESSAGE);
        } else if (eClass == WSDLPackage.Literals.FAULT) {
            features = getFeatures(target, WSDLPackage.FAULT__NAME, WSDLPackage.FAULT__EMESSAGE);
        } else if (eClass == WSDLPackage.Literals.BINDING) {
            features = getFeatures(target, WSDLPackage.BINDING__QNAME, WSDLPackage.BINDING__EPORT_TYPE);
        } else if (eClass == WSDLPackage.Literals.BINDING_FAULT) {
            features = getFeatures(target, WSDLPackage.BINDING_FAULT__NAME);
        } else if (eClass == WSDLPackage.Literals.BINDING_OPERATION) {
            features = getFeatures(target, WSDLPackage.BINDING_OPERATION__NAME);
        } else if (eClass == WSDLPackage.Literals.SERVICE) {
            features = getFeatures(target, WSDLPackage.SERVICE__QNAME);
        } else if (eClass == WSDLPackage.Literals.PORT) {
            features = getFeatures(target, WSDLPackage.PORT__NAME, WSDLPackage.PORT__EBINDING);
        } else if (eClass == SOAPPackage.Literals.SOAP_FAULT) {
            features = getFeatures(target, SOAPPackage.SOAP_FAULT__NAME);
        } else if (eClass == SOAPPackage.Literals.SOAP_HEADER) {
            features = getFeatures(target, SOAPPackage.SOAP_HEADER__EMESSAGE, SOAPPackage.SOAP_HEADER__EPART,
                    SOAPPackage.SOAP_HEADER__USE);
        } else if (eClass == SOAPPackage.Literals.SOAP_HEADER_FAULT) {
            features = getFeatures(target, SOAPPackage.SOAP_HEADER_FAULT__EMESSAGE, SOAPPackage.SOAP_HEADER_FAULT__EPART,
                    SOAPPackage.SOAP_HEADER_FAULT__USE);
        } else if (eClass == SOAPPackage.Literals.SOAP_ADDRESS) {
            features = getFeatures(target, SOAPPackage.SOAP_ADDRESS__LOCATION_URI);
        } else if (eClass == HTTPPackage.Literals.HTTP_ADDRESS) {
            features = getFeatures(target, HTTPPackage.HTTP_ADDRESS__LOCATION_URI);
        } else if (eClass == HTTPPackage.Literals.HTTP_BINDING) {
            features = getFeatures(target, HTTPPackage.HTTP_BINDING__VERB);
        } else if (eClass == HTTPPackage.Literals.HTTP_OPERATION) {
            features = getFeatures(target, HTTPPackage.HTTP_OPERATION__LOCATION_URI);
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
