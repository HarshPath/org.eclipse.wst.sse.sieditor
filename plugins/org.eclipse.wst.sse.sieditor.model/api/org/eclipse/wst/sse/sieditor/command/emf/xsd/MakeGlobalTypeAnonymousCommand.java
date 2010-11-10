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
 *    Stanislav Nichev - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.command.emf.xsd;

import static org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils.getXSDFactory;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDTypeDefinition;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.utils.NameGenerator;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;

/**
 * This command changes a global element with global type to a global element
 * with anonymous type. A child element is added with the same type as the old
 * element's one.<br>
 * <br>
 * 
 * 
 * 
 */
public class MakeGlobalTypeAnonymousCommand extends AbstractNotificationOperation {

    private final boolean addChild;

    public MakeGlobalTypeAnonymousCommand(final IXSDModelRoot modelRoot, final IStructureType structureType,
            final boolean addChild) {
        super(modelRoot, structureType, Messages.MakeGlobalStructureTypeAnonymousCommand_operation_label);
        this.addChild = addChild;
    }

    @Override
    public IStatus run(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
        final IStructureType structureType = (IStructureType) modelObject;
        final XSDNamedComponent component = structureType.getComponent();
        if (component instanceof XSDElementDeclaration) {
            final XSDElementDeclaration elementDeclaration = (XSDElementDeclaration) component;
            final XSDComplexTypeDefinition eComplexType = getXSDFactory().createXSDComplexTypeDefinition();

            final XSDTypeDefinition typeDefinition = elementDeclaration.getTypeDefinition();

            if (typeDefinition != null && addChild) {
                AddStructureTypeCommand.addChildElement(eComplexType, NameGenerator.getNewElementDefaultName(modelObject),
                        typeDefinition);
            }

            elementDeclaration.setTypeDefinition(null);
            elementDeclaration.setAnonymousTypeDefinition(eComplexType);
        }
        return Status.OK_STATUS;
    }

    public IModelObject getStructureType() {
        return modelObject;
    }

    public boolean isAddChild() {
        return addChild;
    }
}
