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
import org.eclipse.wst.wsdl.BindingInput;
import org.eclipse.wst.wsdl.BindingOperation;
import org.eclipse.wst.wsdl.Port;
import org.eclipse.wst.wsdl.WSDLElement;
import org.eclipse.wst.wsdl.binding.http.HTTPAddress;
import org.eclipse.wst.wsdl.binding.http.HTTPBinding;
import org.eclipse.wst.wsdl.binding.http.HTTPOperation;
import org.eclipse.wst.wsdl.binding.http.HTTPUrlEncoded;
import org.eclipse.wst.wsdl.binding.http.HTTPUrlReplacement;

import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.validation.constraints.AbstractConstraint;

public class HTTPElementLocation extends AbstractConstraint{

	@Override
	protected IStatus doValidate(IValidationContext ctx) {
		WSDLElement target = (WSDLElement) ctx.getTarget();
		WSDLElement parent = target.getContainer();

		String errorMessage = null;
		
		if (parent instanceof Port && !(target instanceof HTTPAddress)) {
			errorMessage = Messages.HTTPElementLocation_0;
		} else if (parent instanceof Binding && !(target instanceof HTTPBinding)) {
			errorMessage = Messages.HTTPElementLocation_1;
		} else if (parent instanceof BindingOperation && !(target instanceof HTTPOperation)) {
			errorMessage = Messages.HTTPElementLocation_2;
		} else if (parent instanceof BindingInput && !(target instanceof HTTPUrlEncoded || target instanceof HTTPUrlReplacement)) {
			errorMessage = Messages.HTTPElementLocation_3;
		}

		if (errorMessage != null) {
			return ConstraintStatus.createStatus(ctx, target, null, errorMessage, target.getElement().getLocalName());
		}
		return ConstraintStatus.createSuccessStatus(ctx, target, null);
	}

	@Override
	protected boolean shouldExecute(IValidationContext ctx) {
		return true;
	}

}
