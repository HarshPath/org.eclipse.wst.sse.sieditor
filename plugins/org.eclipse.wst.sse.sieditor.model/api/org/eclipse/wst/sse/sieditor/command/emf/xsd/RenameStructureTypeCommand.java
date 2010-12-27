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

import java.text.MessageFormat;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDTypeDefinition;

import org.eclipse.wst.sse.sieditor.model.Activator;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;

public class RenameStructureTypeCommand extends RenameNamedComponent {

    public RenameStructureTypeCommand(final IModelRoot root, final IStructureType namedObject, final String name) {
        super(root, namedObject, name);
    }

    @Override
    public IStatus run(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
        final EObject baseComponent = root.getModelObject().getComponent();
        final XSDNamedComponent component = (XSDNamedComponent) _namedObject.getComponent();

        final XSDElementDeclaration element = component instanceof XSDElementDeclaration ? (XSDElementDeclaration) component
                : null;
        if (element != null) {
            setNamedComponentNewName(element, _name);
            EmfXsdUtils.updateModelReferencers(baseComponent, element);
        } else {
            final XSDTypeDefinition type = (XSDTypeDefinition) component;
            if (type != null) {
                if (!((IStructureType) _namedObject).isAnonymous()) {
                    setNamedComponentNewName(type, _name);
                    EmfXsdUtils.updateModelReferencers(baseComponent, type);
                } else {
                    return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
                            Messages.RenameStructureTypeCommand_msg_can_not_rename_anonymous_type);
                }
            } else {
                return new Status(IStatus.ERROR, Activator.PLUGIN_ID, MessageFormat.format(
                        Messages.RenameStructureTypeCommand_msg_can_not_find_XSD_element_for_type_X, _namedObject.getName()));
            }
        }

        return Status.OK_STATUS;
    }

}
