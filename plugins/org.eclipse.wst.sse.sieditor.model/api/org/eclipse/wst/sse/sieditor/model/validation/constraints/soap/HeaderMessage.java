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
import org.eclipse.wst.wsdl.Message;
import org.eclipse.wst.wsdl.binding.soap.SOAPHeaderBase;
import org.eclipse.wst.wsdl.binding.soap.SOAPHeaderFault;

import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.validation.constraints.AbstractConstraint;

public class HeaderMessage extends AbstractConstraint{

	@Override
	protected IStatus doValidate(IValidationContext ctx) {
		SOAPHeaderBase header = (SOAPHeaderBase) ctx.getTarget();
		
		Message message = header.getEMessage();
		if (message == null) {
			String messageAttribute = header.getElement().getAttribute("message"); //$NON-NLS-1$
			if (messageAttribute != null) {
				if (header instanceof SOAPHeaderFault) {
					return ConstraintStatus.createStatus(ctx, header, null, Messages.SOAPHeaderMessage_1, messageAttribute);
				}
				return ConstraintStatus.createStatus(ctx, header, null, Messages.SOAPHeaderMessage_2, messageAttribute);
			}
		}

		return ConstraintStatus.createSuccessStatus(ctx, header, null);
	}

	@Override
	protected boolean shouldExecute(IValidationContext ctx) {
		return true;
	}

}
