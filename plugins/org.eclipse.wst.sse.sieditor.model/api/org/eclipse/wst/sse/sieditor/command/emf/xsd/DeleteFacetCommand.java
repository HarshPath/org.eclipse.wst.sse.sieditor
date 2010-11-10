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
import org.eclipse.emf.ecore.EClass;
import org.eclipse.xsd.XSDConstrainingFacet;
import org.eclipse.xsd.XSDFacet;
import org.eclipse.xsd.XSDSimpleTypeDefinition;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;

/**
 * Command for removing a facet in SimpleTypeDefinition
 * 
 * 
 * 
 */
public class DeleteFacetCommand extends AbstractNotificationOperation {
	private EClass _facetEClass;
	private XSDFacet _facet;

    public DeleteFacetCommand(final IModelRoot root, final ISimpleType object, XSDFacet facet) {
        super(root, object, Messages.DeleteFacetCommand_delete_facet_command_label);
		this._facet = facet;
    }
	
    public DeleteFacetCommand(final IModelRoot root, final ISimpleType object, EClass _facetEClass) {
        super(root, object, Messages.DeleteFacetCommand_delete_facet_command_label);
		this._facetEClass = _facetEClass;
    }

    @Override
    public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        final XSDSimpleTypeDefinition simpleType = (XSDSimpleTypeDefinition) modelObject.getComponent();

        if (_facetEClass != null) {
	    	for (XSDConstrainingFacet facet : simpleType.getFacetContents()) {
	    		if (facet.eClass().equals(_facetEClass)) {
	    			_facet = facet;
	    			break;
	    		}
	    	}
        }
    	
    	if (_facet != null) {
    		simpleType.getFacetContents().remove(_facet);
    	}
        return Status.OK_STATUS;
    }

    public boolean canExecute() {
        return modelObject != null;
    }

}