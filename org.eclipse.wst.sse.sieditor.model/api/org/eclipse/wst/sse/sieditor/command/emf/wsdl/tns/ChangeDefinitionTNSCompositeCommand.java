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
package org.eclipse.wst.sse.sieditor.command.emf.wsdl.tns;

import java.util.List;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AbstractCompositeEnsuringDefinitionNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;

/**
 * Command for changing the definition targetNamespace
 * 
 * CAUTION - this command does not support setting the new targetNamespace
 * attribute to null thus removing it. For now there is no such use case, and
 * totally different changes must be made in order to do so, without
 * invalidating the document. If such a use case is present, I suggest another
 * command to be implemented.<br>
 * 
 * 
 * 
 */
public class ChangeDefinitionTNSCompositeCommand extends AbstractCompositeEnsuringDefinitionNotificationOperation {

    private final ChangeDefinitionTNSInternalCommand chanteNSInternalCommand;

    public ChangeDefinitionTNSCompositeCommand(final IWsdlModelRoot root, final IDescription desc, final String newNamespace) {
        super(root, desc, Messages.ChangeTargetNamespaceCommand_change_target_namespace_command_label);
        chanteNSInternalCommand = new ChangeDefinitionTNSInternalCommand(root, desc, newNamespace);
    }

    @Override
    public AbstractNotificationOperation getNextOperation(final List<AbstractNotificationOperation> subOperations) {
        final AbstractNotificationOperation ensureDefinition = super.getNextOperation(subOperations);
        if (ensureDefinition != null) {
            return ensureDefinition;
        }
        if (subOperations.isEmpty() || (subOperations.size() == 1 && isDefinitionEnsured())) {
            return chanteNSInternalCommand;
        }
        return null;
    }

}