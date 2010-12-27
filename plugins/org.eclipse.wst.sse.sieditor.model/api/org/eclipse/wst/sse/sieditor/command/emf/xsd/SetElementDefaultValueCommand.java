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
package org.eclipse.wst.sse.sieditor.command.emf.xsd;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFeature;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;

public class SetElementDefaultValueCommand extends AbstractNotificationOperation {

    private final String defaultValue;

    public SetElementDefaultValueCommand(final IModelRoot root, final IElement element, final String defaultValue) {
        super(root, element, element.isAttribute() ? Messages.SetElementDefaultValueCommand_0 : Messages.SetElementDefaultValueCommand_1);
        this.defaultValue = defaultValue;
    }

    @Override
    public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        EObject element = this.modelObject.getComponent();
    	XSDFeature xsdFeature = null;
    	
        if(element instanceof XSDParticle) {
        	XSDParticleContent content = ((XSDParticle)element).getContent();
			if(content instanceof XSDElementDeclaration) {
				xsdFeature = (XSDFeature)content;
        	}
        }
        else if(element instanceof XSDAttributeDeclaration){
        	xsdFeature = ((XSDFeature)element);
        }
        
        if(xsdFeature != null) {
        	xsdFeature.setLexicalValue(defaultValue);
        }
    	
        return Status.OK_STATUS;
    }

}
