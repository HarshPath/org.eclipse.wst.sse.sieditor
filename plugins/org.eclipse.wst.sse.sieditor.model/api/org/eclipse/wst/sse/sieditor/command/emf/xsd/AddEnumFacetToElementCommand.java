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
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDPackage;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDTypeDefinition;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.INamedObject;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.utils.StatusUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Element;

public class AddEnumFacetToElementCommand extends AbstractNotificationOperation {

    private XSDTypeDefinition oldTypeDefinition;
    private XSDTypeDefinition newTypeDefinitoin;
    private XSDElementDeclaration xsdElementDeclaration;
    private XSDAttributeDeclaration xsdAttributeDeclaration;
    private final INamedObject input;
    private final String value;

    private final IModelRoot model;

    public AddEnumFacetToElementCommand(final IModelRoot model, final INamedObject input, final String value) {
        super(input.getModelRoot(), input, Messages.ADD_FACET_LABEL);
        this.input = input;

        this.value = value;
        this.model = model;

    }

    @Override
    public IStatus run(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
        final AddFacetCommand addFacetCommand = FacetsCommandFactory.createAddFacetCommand(XSDPackage.XSD_ENUMERATION_FACET,
                model, null, value);

        SetAnonymousSimpleTypeCommand setAnonymousSimpleTypeCommand = null;
        if (input instanceof IElement) {
            final XSDConcreteComponent component = (XSDConcreteComponent) input.getComponent();
            if (component instanceof XSDAttributeDeclaration) {
                xsdAttributeDeclaration = (XSDAttributeDeclaration) component;
            } else {
                xsdElementDeclaration = (XSDElementDeclaration) ((XSDParticle) component).getContent();
            }

            setAnonymousSimpleTypeCommand = new SetAnonymousSimpleTypeCommand((IXSDModelRoot) input.getModelRoot(),
                    (IElement) input, ((Element) input).getSchema());
        } else if (input instanceof IStructureType) {
            xsdElementDeclaration = (XSDElementDeclaration) input.getComponent();
            setAnonymousSimpleTypeCommand = new SetAnonymousSimpleTypeCommand((IXSDModelRoot) input.getModelRoot(),
                    (IStructureType) input);
        }

        if (xsdAttributeDeclaration != null) {
            oldTypeDefinition = xsdAttributeDeclaration.getTypeDefinition();
        } else {
            oldTypeDefinition = xsdElementDeclaration.getTypeDefinition();
        }

        IStatus status = setAnonymousSimpleTypeCommand.run(monitor, info);
        if (!StatusUtils.canContinue(status)) {
            StatusUtils.showStatusDialog(Messages.SET_FACET_TITLE, MessageFormat.format(Messages.CANNOT_ADD_FACET_ERR, input
                    .getName()), status);
            return status;
        }

        final ISimpleType newType = setAnonymousSimpleTypeCommand.getType();
        newTypeDefinitoin = (XSDTypeDefinition) newType.getComponent();
        addFacetCommand.setType(newType);
        status = addFacetCommand.run(monitor, info);
        if (!StatusUtils.canContinue(status)) {
            StatusUtils.showStatusDialog(Messages.SET_FACET_TITLE, MessageFormat.format(Messages.CANNOT_ADD_FACET_ERR, input
                    .getName()), status);
            return status;
        }

        return Status.OK_STATUS;
    }
}
