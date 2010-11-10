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
import org.eclipse.wst.wsdl.BindingOperation;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.Operation;
import org.eclipse.wst.wsdl.PortType;

import org.eclipse.wst.sse.sieditor.model.i18n.Messages;

public class BindingOperationHasOperation extends AbstractConstraint{

	@SuppressWarnings("unchecked")
	@Override
	protected IStatus doValidate(IValidationContext ctx) {
		EObject target = ctx.getTarget();
		
		if (target instanceof BindingOperation) {
			return validateBindingOperation((BindingOperation) target, ctx);
		} else if (target instanceof PortType) {
			Definition definition = ((PortType) target).getEnclosingDefinition();
			List<IStatus> statusList = new ArrayList<IStatus>();
			EList<Binding> bindings = definition.getEBindings();
			for (Binding binding : bindings) {
				EList<BindingOperation> bindingOperations = binding.getEBindingOperations();
				for (BindingOperation bindingOperation : bindingOperations) {
					statusList.add(validateBindingOperation(bindingOperation, ctx));
				}
			}
			
			return createStatus(ctx, statusList);
		}

		throw new IllegalArgumentException("Unsupported target type: " + target.eClass().getName()); //$NON-NLS-1$
	}
	
	private IStatus validateBindingOperation(BindingOperation bindingOperation, IValidationContext ctx) {
		Operation operation = bindingOperation.getEOperation();
		if (operation == null || operation.isUndefined()) {
			Binding binding = (Binding) bindingOperation.getContainer();
			PortType portType = binding.getEPortType();
			String portTypeName = portType != null ? portType.getQName().getLocalPart() : null;
			return ConstraintStatus.createStatus(
					ctx, binding, null, Messages.BindingOperationHasOperation_0, binding.getQName().getLocalPart(), portTypeName);
		}
		
		return ConstraintStatus.createSuccessStatus(ctx, bindingOperation, null);
	}

	@Override
	protected boolean shouldExecute(IValidationContext ctx) {
		EObject target = ctx.getTarget();
		boolean result = true;
		
		if (isBatchValidation(ctx)) {
			result = target instanceof BindingOperation;
		}
		
		if (!result) {
			ctx.skipCurrentConstraintFor(target);
		}
		
		return result;
	}

}
