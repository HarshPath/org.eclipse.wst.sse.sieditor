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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintStatus;
import org.eclipse.wst.wsdl.Part;
import org.eclipse.wst.wsdl.binding.soap.SOAPBody;

import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.validation.constraints.AbstractConstraint;

public class BodyParts extends AbstractConstraint{

	@SuppressWarnings("unchecked")
	@Override
	protected IStatus doValidate(IValidationContext ctx) {
		SOAPBody body = (SOAPBody) ctx.getTarget();
		
		String partsAttribute = body.getElement().getAttribute("parts"); //$NON-NLS-1$
		if (partsAttribute != null) {
			partsAttribute = partsAttribute.trim();
			
			List<Part> parts = body.getParts();
			Set<String> existingPartsNames = new HashSet<String>();
			for (Part part : parts) {
				existingPartsNames.add(part.getName());
			}
			
			String[] partNames = partsAttribute.split("\\s+"); //$NON-NLS-1$
			List<IStatus> statusList = new ArrayList<IStatus>(partNames.length);
			for (String partName : partNames) {
				partName = partName.trim();
				if (!existingPartsNames.contains(partName)) {
					statusList.add(ConstraintStatus.createStatus(ctx, body, null, Messages.SOAPBodyParts_0, partName));
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
