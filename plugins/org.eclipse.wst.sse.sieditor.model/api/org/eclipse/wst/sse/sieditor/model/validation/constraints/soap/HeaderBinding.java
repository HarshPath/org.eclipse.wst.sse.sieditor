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
import org.eclipse.wst.wsdl.Binding;
import org.eclipse.wst.wsdl.binding.soap.SOAPBinding;
import org.eclipse.wst.wsdl.binding.soap.SOAPHeader;

import org.eclipse.wst.sse.sieditor.model.i18n.Messages;

public class HeaderBinding extends BodyBinding{

	@Override
	protected IStatus doValidate(IValidationContext ctx) {
		SOAPHeader header = (SOAPHeader) ctx.getTarget();
		
		Binding binding = getBinding(header);
		if (binding != null) {
			for (Object extensibilityElement : binding.getEExtensibilityElements()) {
				if (extensibilityElement instanceof SOAPBinding) {
					return ConstraintStatus.createSuccessStatus(ctx, header, null);
				}
			}
			
			return ConstraintStatus.createStatus(ctx, header, null, Messages.SOAPHeaderBinding_0, binding.getQName().getLocalPart());
		}
		
		return ConstraintStatus.createSuccessStatus(ctx, header, null);
	}

	@Override
	protected boolean shouldExecute(IValidationContext ctx) {
		return true;
	}

}
