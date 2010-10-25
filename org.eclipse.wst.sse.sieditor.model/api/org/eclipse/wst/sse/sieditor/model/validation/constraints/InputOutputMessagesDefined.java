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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintStatus;
import org.eclipse.wst.wsdl.Fault;
import org.eclipse.wst.wsdl.Input;
import org.eclipse.wst.wsdl.Message;
import org.eclipse.wst.wsdl.MessageReference;
import org.eclipse.wst.wsdl.Output;

import org.eclipse.wst.sse.sieditor.model.i18n.Messages;

public class InputOutputMessagesDefined extends AbstractConstraint{

	@Override
	protected IStatus doValidate(IValidationContext ctx) {
		MessageReference messageReference = (MessageReference) ctx.getTarget();
		
		Message message = messageReference.getEMessage();
		
		if (message == null || message.isUndefined()) {
			String messageName = messageReference.getElement().getAttribute("message"); //$NON-NLS-1$
			String errorMessageName = getErrorMessageName(messageReference);
			return ConstraintStatus.createStatus(ctx, messageReference, null, errorMessageName, messageName);
		}
		
		return ConstraintStatus.createSuccessStatus(ctx, messageReference, null);
	}

	@Override
	protected boolean shouldExecute(IValidationContext ctx) {
		return true;
	}
	
	private String getErrorMessageName(MessageReference messageReference) {
		if (messageReference instanceof Input) {
			return Messages.InputOutputMessagesDefined_0;
		} else if (messageReference instanceof Output) {
			return Messages.InputOutputMessagesDefined_1;
		} else if (messageReference instanceof Fault) {
			return Messages.InputOutputMessagesDefined_2;
		}
		
		return null;
	}

}
