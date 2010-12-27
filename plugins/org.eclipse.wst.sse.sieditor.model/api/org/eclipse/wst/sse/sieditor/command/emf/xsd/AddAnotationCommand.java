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
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.impl.XSDSchemaImpl;

import org.eclipse.wst.sse.sieditor.command.common.AbstractXSDNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.Activator;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;

public class AddAnotationCommand extends AbstractXSDNotificationOperation {

    private final Object targetObject;
    private XSDAnnotation annotation;

    public AddAnotationCommand(final Object targetObject, final IXSDModelRoot root, final IModelObject object) {
        super(root, object, Messages.AddAnotationCommand_add_anotation_command_label); 
        this.targetObject = targetObject;
    }

    @Override
    public IStatus run(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
        annotation = EmfXsdUtils.createXSDAnnotation();
        final IStatus addAnotationStatus = addAnotation();
        if (addAnotationStatus.isOK()) {
            annotation.updateElement();
        }
        return addAnotationStatus;
    }

    protected IStatus addAnotation() {
        if (targetObject instanceof XSDTypeDefinition) {
            ((XSDTypeDefinition) targetObject).setAnnotation(annotation);
        } else if (targetObject instanceof XSDElementDeclaration) {
            ((XSDElementDeclaration) targetObject).setAnnotation(annotation);
        } else if (targetObject instanceof XSDAttributeDeclaration) {
            ((XSDAttributeDeclaration) targetObject).setAnnotation(annotation);
        } else if (targetObject instanceof XSDSchemaImpl) {
            ((XSDSchemaImpl) targetObject).getContents().add(annotation);
        } else {
            return new Status(IStatus.CANCEL, Activator.PLUGIN_ID, 
                    MessageFormat.format(
                    Messages.AddAnotationCommand_msg_can_not_add_annotation_to_elements_of_type_X, targetObject.getClass()));
        }
        return Status.OK_STATUS;
    }

    @Override
    public boolean canExecute() {
        return targetObject != null
                && (targetObject instanceof XSDTypeDefinition || targetObject instanceof XSDElementDeclaration
                        || targetObject instanceof XSDAttributeDeclaration || targetObject instanceof XSDSchemaImpl);
    }

    public XSDAnnotation getAnnotation() {
        return annotation;
    }
}

