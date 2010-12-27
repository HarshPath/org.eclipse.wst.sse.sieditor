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
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintStatus;
import org.eclipse.wst.wsdl.BindingInput;
import org.eclipse.wst.wsdl.WSDLElement;

import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.validation.constraints.AbstractConstraint;

public class InputChildren extends AbstractConstraint{

	@Override
	protected IStatus doValidate(IValidationContext ctx) {
		EObject target = ctx.getTarget();
		
		if (target.eContainer() instanceof BindingInput) {
			BindingInput input = (BindingInput) target.eContainer();
			
			if (input.getEExtensibilityElements().size() > 1) {
				String elementName = ((WSDLElement) target).getElement().getLocalName();
				return ConstraintStatus.createStatus(ctx, target, null, Messages.HTTPInputChildren_0, elementName);
			}
		}
		
		return ConstraintStatus.createSuccessStatus(ctx, target, null);
	}

	@Override
	protected boolean shouldExecute(IValidationContext ctx) {
		return true;
	}
}
