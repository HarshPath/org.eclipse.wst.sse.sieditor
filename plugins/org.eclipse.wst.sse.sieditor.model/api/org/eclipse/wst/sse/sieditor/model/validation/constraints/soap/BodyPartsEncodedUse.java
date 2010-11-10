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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintStatus;
import org.eclipse.wst.wsdl.Part;
import org.eclipse.wst.wsdl.binding.soap.SOAPBody;

import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.validation.constraints.AbstractConstraint;

public class BodyPartsEncodedUse extends AbstractConstraint{

	@SuppressWarnings("unchecked")
	@Override
	protected IStatus doValidate(IValidationContext ctx) {
		SOAPBody body = (SOAPBody) ctx.getTarget();
		
		if ("encoded".equals(body.getUse())) { //$NON-NLS-1$
			List<Part> parts = body.getParts();
			List<IStatus> statusList = new ArrayList<IStatus>(parts.size());
			for (Part part : parts) {
				if (part.getTypeName() == null) {
					statusList.add(ConstraintStatus.createStatus(ctx, body, null, Messages.SOAPBodyPartsEncodedUse_1, part.getName()));
				}
			}
			
			return createStatus(ctx, statusList);
		}
		
		return ConstraintStatus.createSuccessStatus(ctx, body, null);
	}

	@Override
	protected boolean shouldExecute(IValidationContext ctx) {
		return true;
	}

}
