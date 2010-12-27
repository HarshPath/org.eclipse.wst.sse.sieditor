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
 *    Keshav Veerapaneni - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.command.emf.xsd;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;

/**
 * Command for setting the Nillable attribute for Local ElementDeclarations
 * 
 * 
 */
public class SetElementNillableCommand extends AbstractNotificationOperation {
    private final boolean _nillable;
	private final IElement element;

    public SetElementNillableCommand(final IModelRoot root, final IElement element, boolean nillable) {
        super(root, element, Messages.SetElementNillableCommand_set_element_nillable_command_label);
		this.element = element;
        this._nillable = nillable;
    }

    @Override
    public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        // set the nillable property for the element
    	XSDParticle particle = (XSDParticle) element.getComponent();
        final XSDParticleContent content = particle.getContent();
        ((XSDElementDeclaration) content).setNillable(_nillable);
        return Status.OK_STATUS;
    }

    public boolean canExecute() {
    	if (element == null) {
    		return false;
    	}
    	
    	EObject component =  element.getComponent();
    	if (!(component instanceof XSDParticle)) {
    		return false;
    	}
    	
    	if (!(((XSDParticle) component).getContent() instanceof XSDElementDeclaration)) {
    		return false;
    	}
        
    	return true;
    }
}
