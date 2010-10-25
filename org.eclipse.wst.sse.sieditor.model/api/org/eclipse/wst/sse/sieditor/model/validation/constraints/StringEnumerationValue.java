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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintStatus;
import org.eclipse.wst.wsdl.WSDLElement;
import org.eclipse.wst.wsdl.binding.soap.SOAPBinding;
import org.eclipse.wst.wsdl.binding.soap.SOAPBody;
import org.eclipse.wst.wsdl.binding.soap.SOAPFault;
import org.eclipse.wst.wsdl.binding.soap.SOAPHeader;
import org.eclipse.wst.wsdl.binding.soap.SOAPOperation;
import org.eclipse.wst.wsdl.binding.soap.SOAPPackage;

import org.eclipse.wst.sse.sieditor.model.i18n.Messages;

public class StringEnumerationValue extends AbstractConstraint{
	
	private static final String SOAP_USE[] = {"literal", "encoded"};  //$NON-NLS-1$ //$NON-NLS-2$
	private static final String SOAP_STYLE[] = {"rpc", "document"}; //$NON-NLS-1$ //$NON-NLS-2$

	@Override
	protected IStatus doValidate(IValidationContext ctx) {
		WSDLElement target = (WSDLElement) ctx.getTarget();
		
		EStructuralFeature features[] = getEnumFeatures(target);
		for (EStructuralFeature feature : features) {
			String[] permitedValues = getPermitedFeatureValues(feature);
			Object value = target.eGet(feature);
			if (value != null) {
				for (String permittedValue : permitedValues) {
					if (permittedValue.equals(value)) {
						return ConstraintStatus.createSuccessStatus(ctx, target, null);
					}
				}
				
				List<EObject> resultLocus = new ArrayList<EObject>(1);
				resultLocus.add(feature);
				
				return ConstraintStatus.createStatus(ctx, target, null, Messages.InvalidEnumerationValue_0, value, permitedValues);
			}
		}

		return ConstraintStatus.createSuccessStatus(ctx, target, null);
	}

	private String[] getPermitedFeatureValues(EStructuralFeature feature) {
		if (feature == SOAPPackage.Literals.SOAP_BODY__USE) {
			return SOAP_USE;
		} else if (feature == SOAPPackage.Literals.SOAP_BINDING__STYLE) {
			return SOAP_STYLE;
		} else if (feature == SOAPPackage.Literals.SOAP_OPERATION__STYLE) {
			return SOAP_STYLE;
		} else if (feature == SOAPPackage.Literals.SOAP_HEADER_BASE__USE) {
			return SOAP_USE;
		} else if (feature == SOAPPackage.Literals.SOAP_FAULT__USE) {
			return SOAP_USE;
		}
		throw new IllegalArgumentException();
	}

	private EStructuralFeature[] getEnumFeatures(WSDLElement target) {
		if (target instanceof SOAPBody) {
			return new EStructuralFeature[]{SOAPPackage.Literals.SOAP_BODY__USE};
		} else if (target instanceof SOAPBinding) {
			return new EStructuralFeature[]{SOAPPackage.Literals.SOAP_BINDING__STYLE};
		} else if (target instanceof SOAPOperation) {
			return new EStructuralFeature[]{SOAPPackage.Literals.SOAP_OPERATION__STYLE};
		} else if (target instanceof SOAPHeader) {
			return new EStructuralFeature[]{SOAPPackage.Literals.SOAP_HEADER_BASE__USE};
		} else if (target instanceof SOAPFault) {
			return new EStructuralFeature[]{SOAPPackage.Literals.SOAP_FAULT__USE};
		}
		
		throw new IllegalArgumentException();
	}

	@Override
	protected boolean shouldExecute(IValidationContext ctx) {
		return true;
	}

}
