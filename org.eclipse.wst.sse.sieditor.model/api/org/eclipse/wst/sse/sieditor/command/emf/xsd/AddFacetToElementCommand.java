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
 *    Richard Birenheide - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.command.emf.xsd;

import java.util.List;

import org.eclipse.wst.sse.sieditor.command.common.AbstractCompositeNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Element;

public class AddFacetToElementCommand extends AbstractCompositeNotificationOperation {

    final private IModelObject input;
    final AddFacetCommand addFacetCommand;

    public AddFacetToElementCommand(final IModelRoot model, final IModelObject input, final int facetId, final String value) {
        super(model, input, Messages.AddFacetCommand_add_faceted_command_label);
        this.input = input;
        addFacetCommand = FacetsCommandFactory.createAddFacetCommand(facetId, model, null, value);

        setTransactionPolicy(AbstractCompositeNotificationOperation.TransactionPolicy.MULTI);
    }

    @Override
    public AbstractNotificationOperation getNextOperation(final List<AbstractNotificationOperation> subOperations) {
        if (subOperations.isEmpty()) {
            if (input instanceof IElement) {
                final SetAnonymousSimpleTypeCommand operation = new SetAnonymousSimpleTypeCommand((IXSDModelRoot) input
                        .getModelRoot(), (IElement) input, ((Element) input).getSchema());
                return operation;
            } else if (input instanceof IStructureType) {
                return new SetAnonymousSimpleTypeCommand((IXSDModelRoot) input.getModelRoot(), (IStructureType) input);
            }
        } else if (subOperations.size() == 1) {
            final SetAnonymousSimpleTypeCommand previous = (SetAnonymousSimpleTypeCommand) subOperations.get(0);
            addFacetCommand.setType(previous.getType());
            return addFacetCommand;
        }
        return null;
    }

}
