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

import java.text.MessageFormat;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.XSDTypeDefinition;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.Activator;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.StructureType;

/**
 * Command for setting type for Global ElementDeclaration (
 * {@link XSDElementDeclaration})
 * 
 * 
 */
public class SetStructureTypeCommand extends AbstractNotificationOperation {
    private IType _type;
    private final IStructureType structure;

    public SetStructureTypeCommand(final IModelRoot root, final IStructureType structure, final IType type) {
        super(root, structure, Messages.SetStructureTypeCommand_set_structure_type_command_label);
        this.structure = structure;
        this._type = type;
    }

    @Override
    public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        IType elementType = structure.getType();

        // Check if facets can be reused and in such case execute
        // SetBaseTypeCommand
        IStatus iStatus = SetBaseTypeCommand.setBaseTypeIfFacetsCanBeReused(elementType, _type, monitor, info, getModelRoot());
        if (iStatus != null) {
            // SetBaseTypeCommand is executed
            return iStatus;
        }
        if (_type.getComponent().getTargetNamespace() == null) {
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.SetStructureTypeCommand_cannot_refer_type_with_no_namespace);
        }

        IType resolvedType = ((StructureType) structure).getSchema().resolveType((AbstractType) _type);

        if (resolvedType == null) {
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, MessageFormat.format(
                    Messages.SetStructureTypeCommand_msg_can_not_resolve_type_X, _type.getName()));
        }

        _type = resolvedType;
        // DOIT Import if needed
        if (resolvedType.getComponent() instanceof XSDTypeDefinition) {
            final XSDNamedComponent component = _type.getComponent();
            if (component instanceof XSDTypeDefinition) {
                final XSDTypeDefinition xsdType = (XSDTypeDefinition) component;
                XSDElementDeclaration element = ((StructureType) structure).getElement();
                ((EObject) element).eUnset(XSDPackage.eINSTANCE.getXSDElementDeclaration_AnonymousTypeDefinition());
                ((EObject) element).eUnset(XSDPackage.eINSTANCE.getXSDElementDeclaration_TypeDefinition());
                element.setTypeDefinition(xsdType);
                element.updateElement(true);
                return Status.OK_STATUS;
            }
        }

        return new Status(IStatus.ERROR, Activator.PLUGIN_ID, MessageFormat
                .format(Messages.SetStructureTypeCommand_msg_can_not_resolve_type_X_for_structure_Y, _type.getName(), structure
                        .getName()));
    }

    public boolean canExecute() {
        return structure != null && _type != null && _type instanceof AbstractType;
    }

}