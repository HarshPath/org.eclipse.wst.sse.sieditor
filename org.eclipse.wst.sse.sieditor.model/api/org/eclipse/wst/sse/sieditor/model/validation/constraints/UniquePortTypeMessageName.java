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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintStatus;
import org.eclipse.wst.wsdl.Input;
import org.eclipse.wst.wsdl.MessageReference;
import org.eclipse.wst.wsdl.Operation;
import org.eclipse.wst.wsdl.Output;
import org.eclipse.wst.wsdl.PortType;

import org.eclipse.wst.sse.sieditor.model.i18n.Messages;

public class UniquePortTypeMessageName extends AbstractConstraint{
	private static final String REQUEST = "Request"; //$NON-NLS-1$
	private static final String RESPONSE = "Response"; //$NON-NLS-1$

	@SuppressWarnings("unchecked")
	@Override
	protected IStatus doValidate(IValidationContext ctx) {
		EObject target = ctx.getTarget();
		
		PortType portType = null;
		
		if (target instanceof PortType) {
			portType = (PortType) target;
		} else if (target instanceof MessageReference) {
			MessageReference messageReference = (MessageReference) target;
			portType = (PortType) messageReference.getContainer().getContainer();
		} else if (target instanceof Operation) {
			Operation operation = (Operation) target;
			portType = (PortType) operation.getContainer();
		} else {
			throw new IllegalArgumentException("Unsupported target: " + target.eClass().getName()); //$NON-NLS-1$
		}
		
		
		Set<String> names = new HashSet<String>();
		EList<Operation> operations = portType.getEOperations();
		List<IStatus> statusList = new ArrayList<IStatus>();
		
		
		for (Operation operation : operations) {
			MessageReference messageReference = operation.getEInput();
			if (messageReference != null) {
				String name = getMessageReferenceName(operation, messageReference);
				if (names.contains(name)) {
					statusList.add(
							ConstraintStatus.createStatus(ctx, messageReference, null, Messages.UniquePortTypeInputMessageName_0, name, portType.getQName().getLocalPart()));
				} else {
					names.add(name);
					ConstraintStatus.createSuccessStatus(ctx, messageReference, null);
				}
			}
			
			messageReference = operation.getEOutput();
			if (messageReference != null) {
				String name = getMessageReferenceName(operation, messageReference);
				if (names.contains(name)) {
					statusList.add(
							ConstraintStatus.createStatus(ctx, messageReference, null, Messages.UniquePortTypeOutputMsgName_0, name, portType.getQName().getLocalPart()));
				} else {
					names.add(name);
					ConstraintStatus.createSuccessStatus(ctx, messageReference, null);
				}
			}
		}
		
		return createStatus(ctx, statusList);
	}

	@Override
	protected boolean shouldExecute(IValidationContext ctx) {
		EObject target = ctx.getTarget();
		boolean result = false;
		
		if (isBatchValidation(ctx)) {
			result = target instanceof PortType;
		} else {
			result = target instanceof Operation || target instanceof MessageReference;
		}
		
		if (!result) {
			ctx.skipCurrentConstraintFor(target);
		}
		return result;
	}
	
	private String getMessageReferenceName(Operation operation, MessageReference messageReference) {
		String name = messageReference.getName();
		if (name == null) {
			if (messageReference instanceof Input) {
				name = operation.getName() + REQUEST;
			} else if (messageReference instanceof Output) {
				name = operation.getName() + RESPONSE;
			}
		}
		
		return name;
	}

}
