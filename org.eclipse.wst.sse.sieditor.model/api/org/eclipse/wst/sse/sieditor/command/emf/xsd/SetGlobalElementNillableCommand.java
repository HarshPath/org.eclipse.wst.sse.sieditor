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
package org.eclipse.wst.sse.sieditor.command.emf.xsd;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.xsd.XSDElementDeclaration;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.StructureType;

public class SetGlobalElementNillableCommand extends AbstractNotificationOperation{

	private final IStructureType element;
	private final boolean nillable;

	public SetGlobalElementNillableCommand(IModelRoot model, IStructureType element, boolean nillable) {
		super(model, element, Messages.SetGlobalElementNillableCommand_set_global_element_nillable_command_label);
		this.element = element;
		this.nillable = nillable;
	}

	@Override
	public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		XSDElementDeclaration xsdElementDeclaration = ((StructureType) element).getElement();
		xsdElementDeclaration.setNillable(nillable);
		return Status.OK_STATUS;
	}
	
	@Override
	public boolean canExecute() {
		if (element == null) {
			return false;
		}
		
		return element.isElement();
	}

}
