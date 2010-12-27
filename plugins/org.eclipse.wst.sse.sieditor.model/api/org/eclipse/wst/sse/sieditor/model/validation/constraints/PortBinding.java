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
package org.eclipse.wst.sse.sieditor.model.validation.constraints;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintStatus;
import org.eclipse.wst.wsdl.Binding;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.Port;
import org.eclipse.wst.wsdl.Service;

import org.eclipse.wst.sse.sieditor.model.i18n.Messages;

public class PortBinding extends AbstractConstraint{

	@SuppressWarnings("unchecked")
	@Override
	protected IStatus doValidate(IValidationContext ctx) {
		EObject target = ctx.getTarget();
		
		if (target instanceof Definition) {
			//validate all ports
			Definition definition = (Definition) target;
			List<IStatus> statusList = new ArrayList<IStatus>();
			
			EList<Service> services = definition.getEServices();
			for (Service service : services) {
				EList<Port> ports = service.getEPorts();
				for (Port port : ports) {
					statusList.add(validatePort(ctx, port));
				}
			}
			return createStatus(ctx, statusList);
		} else if (target instanceof Port) {
			//validate just this one
			return validatePort(ctx, (Port) target);
		}
		
		throw new IllegalArgumentException(target.eClass().getName());
	}
	
	private IStatus validatePort(IValidationContext ctx, Port port) {
		Binding binding = port.getEBinding();
		
		if (binding == null || binding.isUndefined()) {
			//String bindingName = binding == null ? "" : binding.getQName().getLocalPart();
			String bindingName = port.getElement().getAttribute("binding"); //$NON-NLS-1$
			//TODO: substract the the local port from the bindingName
			return ConstraintStatus.createStatus(ctx, port, null, Messages.PortBinding_0, port.getName(), bindingName);
		}
		
		return ConstraintStatus.createSuccessStatus(ctx, port, null);
	}

	@Override
	protected boolean shouldExecute(IValidationContext ctx) {
		EObject target = ctx.getTarget();
		boolean result = true;
		
		if (isBatchValidation(ctx)) {
			result = target instanceof Port;
		}
		
		if (!result) {
			ctx.skipCurrentConstraintFor(target);
		}
		return result;
	}

}
