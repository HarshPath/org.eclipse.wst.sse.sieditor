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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintStatus;
import org.eclipse.wst.wsdl.Binding;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.PortType;

import org.eclipse.wst.sse.sieditor.model.i18n.Messages;

public class BindingPortType extends AbstractConstraint{

	@SuppressWarnings("unchecked")
	@Override
	protected IStatus doValidate(IValidationContext ctx) {
		EObject target = ctx.getTarget();
		
		if (target instanceof Definition) {
			//validate all bindings
			Definition definition = (Definition) target;
			EList<Binding> bindings = definition.getEBindings();
			List<IStatus> statusList = new ArrayList<IStatus>(bindings.size());
			for (Binding binding : bindings) {
				statusList.add(validateBinding(binding, ctx));
			}
			return createStatus(ctx, statusList);
			
		} else {
			//validate just this binding
			return validateBinding((Binding) target, ctx);
		}

	}
	
	private IStatus validateBinding(Binding binding, IValidationContext ctx) {
		PortType portType = binding.getEPortType();
		if (portType == null || portType.isUndefined()) {
			String portTypeName = binding.getElement().getAttribute("type"); //$NON-NLS-1$
			return ConstraintStatus.createStatus(
					ctx, binding, null, Messages.BindingPortType_0, portTypeName, binding.getQName().getLocalPart());
		}
		
		return ConstraintStatus.createSuccessStatus(ctx, binding, null);
	}

	@Override
	protected boolean shouldExecute(IValidationContext ctx) {
		EObject target = ctx.getTarget();
		boolean result = true;
		
		if (isBatchValidation(ctx)) {
			result = target instanceof Definition;
		}
		
		if (!result) {
			ctx.skipCurrentConstraintFor(target);
		}
		
		return result;
	}

}
