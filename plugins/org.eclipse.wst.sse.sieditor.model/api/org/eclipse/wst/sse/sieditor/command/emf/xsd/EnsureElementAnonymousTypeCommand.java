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

import static org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils.getXSDFactory;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;

import org.eclipse.wst.sse.sieditor.command.common.AbstractXSDNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;

/**
 * Command to ensure that Global {@link XSDElementDeclaration} has an anonymous
 * {@link XSDComplexTypeDefinition} so that we can add child elements
 * 
 * 
 * 
 */
public class EnsureElementAnonymousTypeCommand extends AbstractXSDNotificationOperation {
    private final XSDElementDeclaration _element;
    private XSDComplexTypeDefinition _type;

    public EnsureElementAnonymousTypeCommand(IXSDModelRoot root, final XSDElementDeclaration element) {
        super(root, root.getSchema(), Messages.EnsureElementAnonymousTypeCommand_ensure_element_anonymous_type_command_label);
        this._element = element;
    }

    public boolean canExecute() {
        return null != _element;
    }

    @Override
    public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        if (_element.isElementDeclarationReference())
            ((EObject) _element).eUnset(XSDPackage.eINSTANCE.getXSDElementDeclaration_ResolvedElementDeclaration());

        final XSDTypeDefinition elementAnonymousType = _element.getAnonymousTypeDefinition();
        if (null == elementAnonymousType || elementAnonymousType instanceof XSDSimpleTypeDefinition) {
            ((EObject) _element).eUnset(XSDPackage.eINSTANCE.getXSDElementDeclaration_TypeDefinition());
            _type = getXSDFactory().createXSDComplexTypeDefinition();
            _element.setAnonymousTypeDefinition(_type);
        } else {
            _type = (XSDComplexTypeDefinition) elementAnonymousType;
        }

        return Status.OK_STATUS;
    }

    public XSDComplexTypeDefinition getAnonymousType() {
        return _type;
    }
}
