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
package org.eclipse.wst.sse.sieditor.model.wsdl.impl;

import java.util.Collection;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.Message;
import org.eclipse.wst.wsdl.Part;
import org.eclipse.xsd.XSDNamedComponent;

import org.eclipse.wst.sse.sieditor.command.emf.wsdl.RenameParameterCommand;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.SetParameterTypeCommand;
import org.eclipse.wst.sse.sieditor.core.common.Nil;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.generic.DuplicateException;
import org.eclipse.wst.sse.sieditor.model.generic.IllegalInputException;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;

/**
 * 
 * 
 * 
 */
public class OperationParameter extends AbstractWSDLComponent implements IParameter,
        org.eclipse.wst.sse.sieditor.model.write.wsdl.api.IParameter {
    private final Message _message;
    private byte parameterType;

    public OperationParameter(final IWsdlModelRoot root, final Part part, final IModelObject parent, final Message message,
            byte parameterType) {
        super(part, root, parent);
        this._message = message;
        this.parameterType = parameterType;
    }

    public IType getType() {
        final Part part = (Part) component;
        XSDNamedComponent namedComponent = part.getElementDeclaration();
        if (namedComponent == null) {
            namedComponent = part.getTypeDefinition();
        }

        if (null == namedComponent)
            return null;

        Description containingDesc = null;
        if (canEdit())
            containingDesc = (Description) getModelRoot().getDescription();
        else {
            final Definition definition = part.getEnclosingDefinition();
            IDescription description = getModelRoot().getDescription();
            final Collection<IDescription> services = description.getReferencedServices();
            for (IDescription desc : services) {
                if (definition == ((Description) desc).getComponent()) {
                    containingDesc = (Description) desc;
                    break;
                }
            }
        }

        return null == containingDesc ? null : containingDesc.resolveType(namedComponent);
    }

    public String getName() {
        return ((Part) component).getName();
    }

    public void setType(final IType type, final boolean wrapType) throws ExecutionException {
        Nil.checkNil(type, "type"); //$NON-NLS-1$

        if (!canEdit())
            return;
        /*
         * final Description desc = (Description)
         * getModelRoot().getDescription();
         * 
         * IType resolvedType = null; if (type instanceof AbstractType) { if
         * (type.getRoot() instanceof Description || !wrapType) { resolvedType =
         * desc.resolveType((AbstractType) type); if (null == resolvedType)
         * return; } else if (type.getRoot() instanceof Schema) { resolvedType =
         * type; }
         * 
         * final SetParameterTypeCommand command = new
         * SetParameterTypeCommand(getModelRoot(), this, (AbstractType)
         * resolvedType, wrapType); getModelRoot().getEnv().execute(command); }
         */
        final SetParameterTypeCommand command = new SetParameterTypeCommand(this, (AbstractType) type);
        getModelRoot().getEnv().execute(command);
    }

    public void setName(String name) throws IllegalInputException, DuplicateException, ExecutionException {
        Nil.checkNil(name, "name"); //$NON-NLS-1$
        final RenameParameterCommand command;
        command = new RenameParameterCommand(getModelRoot(), this, name);
        getModelRoot().getEnv().execute(command);
    }

    @Override
    public Part getComponent() {
        return (Part) super.getComponent();
    }

    public boolean canEdit() {
        return _message.eContainer() == ((Description) getModelRoot().getDescription()).getComponent() ? true : false;
    }

    public byte getParameterType() {

        return parameterType;
    }

}