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
package org.eclipse.wst.sse.sieditor.model.validation.constraints.soap;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintStatus;
import org.eclipse.wst.wsdl.binding.soap.SOAPHeaderBase;
import org.eclipse.wst.wsdl.binding.soap.SOAPHeaderFault;

import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.validation.constraints.AbstractConstraint;

public class HeaderEncodedUseNamespaceURI extends AbstractConstraint{

	@Override
	protected IStatus doValidate(IValidationContext ctx) {
		SOAPHeaderBase header = (SOAPHeaderBase) ctx.getTarget();
		
		if ("encoded".equals(header.getUse())) { //$NON-NLS-1$
			String namespaceURI = header.getNamespaceURI();
			if (namespaceURI == null || namespaceURI.trim().length() == 0) {
				if (header instanceof SOAPHeaderFault) {
					return ConstraintStatus.createStatus(ctx, header, null, Messages.SOAPHeaderEncodedUseNamespaceURI_1);
				}
				return ConstraintStatus.createStatus(ctx, header, null, Messages.SOAPHeaderEncodedUseNamespaceURI_2);
			}
		}
		
		return ConstraintStatus.createSuccessStatus(ctx, header, null);
	}

	@Override
	protected boolean shouldExecute(IValidationContext ctx) {
		return true;
	}

}
