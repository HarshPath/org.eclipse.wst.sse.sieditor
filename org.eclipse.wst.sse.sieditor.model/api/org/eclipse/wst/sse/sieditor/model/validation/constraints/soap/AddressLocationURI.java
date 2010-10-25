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
package org.eclipse.wst.sse.sieditor.model.validation.constraints.soap;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintStatus;
import org.eclipse.wst.wsdl.binding.soap.SOAPAddress;

import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.validation.constraints.AbstractConstraint;

public class AddressLocationURI extends AbstractConstraint{

	@Override
	protected IStatus doValidate(IValidationContext ctx) {
		SOAPAddress address = (SOAPAddress) ctx.getTarget();
		String locationURI = address.getLocationURI();
		
		if (locationURI == null || locationURI.trim().length() == 0) {
			return ConstraintStatus.createStatus(ctx, address, null, Messages.SOAPAddressLocationURI_0);
		}
		
		return ConstraintStatus.createSuccessStatus(ctx, address, null);
	}

	@Override
	protected boolean shouldExecute(IValidationContext ctx) {
		return true;
	}

}
