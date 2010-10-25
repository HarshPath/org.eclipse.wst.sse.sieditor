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

import java.text.MessageFormat;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;

import org.eclipse.wst.sse.sieditor.model.Activator;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;

public class RenameElementCommand extends RenameNamedComponent{

	public RenameElementCommand(IModelRoot root, IElement element, String name) {
		super(root, element, name);
	}
	
	@Override
	public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		EObject baseComponent = root.getModelObject().getComponent();
		XSDConcreteComponent component =  (XSDConcreteComponent) modelObject.getComponent();
		IStatus status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 
				MessageFormat.format(Messages.RenameElementCommand_rename_element_command_label, component.getClass()));
		
		
        if (component instanceof XSDAttributeDeclaration) {
            if (!((XSDAttributeDeclaration) component).isAttributeDeclarationReference()) {
            	((XSDNamedComponent) component).setName(_name);
            	EmfXsdUtils.updateModelReferencers(baseComponent, (XSDNamedComponent)component);
            	status = Status.OK_STATUS;
            }
        } else if (component instanceof XSDParticle) {
            final XSDParticleContent content = ((XSDParticle) component).getContent();
            if (content instanceof XSDElementDeclaration) {
                if (!((XSDElementDeclaration) content).isElementDeclarationReference()) {
                	((XSDNamedComponent) content).setName(_name);
                	EmfXsdUtils.updateModelReferencers(baseComponent, (XSDNamedComponent)content);
                	status = Status.OK_STATUS;
                }
            }
        }
        
        return status;
	}
	
    public boolean canExecute() {
    	if (modelObject == null || _namedObject == null || _name == null) {
    		return false;
    	}
    	XSDConcreteComponent component =  (XSDConcreteComponent) modelObject.getComponent();
    	if (!(component instanceof XSDAttributeDeclaration) && !(component instanceof XSDParticle)) {
    		return false;
    	}
    	
        return true;
    }

}
