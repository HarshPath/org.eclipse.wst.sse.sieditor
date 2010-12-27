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
package org.eclipse.wst.sse.sieditor.model.validation.constraints;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintStatus;
import org.eclipse.wst.wsdl.ExtensibilityElement;
import org.eclipse.wst.wsdl.Port;
import org.eclipse.wst.wsdl.binding.http.HTTPAddress;
import org.eclipse.wst.wsdl.binding.soap.SOAPAddress;

import org.eclipse.wst.sse.sieditor.model.i18n.Messages;

public class PortAddress extends AbstractConstraint{

	@SuppressWarnings("unchecked")
	@Override
	protected IStatus doValidate(IValidationContext ctx) {
		Port port = (Port) ctx.getTarget();
		EList<ExtensibilityElement> extensibilityElements = port.getEExtensibilityElements();
		
		boolean addressFound = false;
		
		for (ExtensibilityElement extensibilityElement : extensibilityElements) {
			if (isAddress(extensibilityElement)) {
				if (addressFound) {
					return ConstraintStatus.createStatus(ctx, port, null, Messages.PortAddress_1, port.getName());
				} else {
					addressFound = true;
				}
			}
		}
		
		if (!addressFound) {
			return ConstraintStatus.createStatus(ctx, port, null, Messages.PortAddress_0, port.getName());
		}
		
		return ConstraintStatus.createSuccessStatus(ctx, port, null);
	}

	@Override
	protected boolean shouldExecute(IValidationContext ctx) {
		return true;
	}
	
	public boolean isAddress(ExtensibilityElement element) {
		return element instanceof SOAPAddress || element instanceof HTTPAddress;
	}

}
