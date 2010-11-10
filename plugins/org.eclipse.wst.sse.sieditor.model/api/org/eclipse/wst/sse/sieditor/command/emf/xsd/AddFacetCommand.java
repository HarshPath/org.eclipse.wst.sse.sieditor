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
import org.eclipse.xsd.XSDSimpleTypeDefinition;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;

/**
 * Command for adding a facet to a SimpleTypeDefintion
 * 
 * 
 * 
 */
public class AddFacetCommand extends AbstractNotificationOperation {
    private final EClass _facetClass;
    private XSDConstrainingFacet _facet;
    private XSDConstrainingFacet _newFacet;
    private final String _value;

    public AddFacetCommand(final IModelRoot root, final ISimpleType type, final EClass facetClass,
            final XSDConstrainingFacet oldFacet, final String value) {
        super(root, type, Messages.AddFacetCommand_add_faceted_command_label);
        this._facetClass = facetClass;
        this._value = value;
        _facet = oldFacet;
    }
    
    public void setType(ISimpleType type) {
    	modelObject = type;
    }

    @Override
    public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        if (_facet == null) {
            _facet = (XSDConstrainingFacet) EmfXsdUtils.getXSDFactory().create(_facetClass);
            _facet.setLexicalValue(_value);
            ((XSDSimpleTypeDefinition)  modelObject.getComponent()).getFacetContents().add(_facet);
            _newFacet = _facet;
        }
        _facet.setLexicalValue(_value);

        return Status.OK_STATUS;
    }

    @Override
    public boolean canExecute() {
        return !(modelObject == null || _facetClass == null);
    }

    public XSDConstrainingFacet getNewFacet() {
        return _newFacet;
    }

}
