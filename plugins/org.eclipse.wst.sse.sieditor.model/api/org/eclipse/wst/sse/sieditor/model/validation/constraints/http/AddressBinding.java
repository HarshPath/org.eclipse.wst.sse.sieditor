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
package org.eclipse.wst.sse.sieditor.model.validation.constraints.http;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintStatus;
import org.eclipse.wst.wsdl.Binding;
import org.eclipse.wst.wsdl.Port;
import org.eclipse.wst.wsdl.binding.http.HTTPAddress;
import org.eclipse.wst.wsdl.binding.http.HTTPBinding;

import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.validation.constraints.AbstractConstraint;

public class AddressBinding extends AbstractConstraint{

	@Override
	protected IStatus doValidate(IValidationContext ctx) {
		HTTPAddress address = (HTTPAddress) ctx.getTarget();
		
		if (address.getContainer() instanceof Port) {
			Port port = (Port) address.getContainer();
			Binding binding = port.getEBinding();
			if (binding != null) {
				for (Object extensibilityElement : binding.getExtensibilityElements()) {
					if (extensibilityElement instanceof HTTPBinding) {
						return ConstraintStatus.createSuccessStatus(ctx, address, null);
					}
				}
				
				return ConstraintStatus.createStatus(ctx, address, null, Messages.HTTPAddressBinding_0, binding.getQName().getLocalPart(), port.getName());
			}
		}
		
		return ConstraintStatus.createSuccessStatus(ctx, address, null);
	}

	@Override
	protected boolean shouldExecute(IValidationContext ctx) {
		return true;
	}

}
