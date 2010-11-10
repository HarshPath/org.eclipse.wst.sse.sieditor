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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;

import org.eclipse.wst.sse.sieditor.command.common.AbstractXSDNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.SimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.StructureType;

public class SetAnonymousSimpleTypeCommand extends AbstractXSDNotificationOperation {

    private final ISchema schema;
    private ISimpleType type;

    public SetAnonymousSimpleTypeCommand(final IXSDModelRoot root, final IStructureType element) {
        this(root, element, ((StructureType) element).getSchema());
    }

    public SetAnonymousSimpleTypeCommand(final IXSDModelRoot root, final IModelObject element, final ISchema schema) {
        super(root, element, Messages.SetAnonymousSimpleTypeCommand_0);
        this.schema = schema;
    }

    @Override
    public IStatus run(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
        if (modelObject instanceof IStructureType) {
            setGlobalElementAnonymous();
        } else {
            if (modelObject.getComponent() instanceof XSDAttributeDeclaration) {
                setLocalAttributeAnonymous();
            } else {
                setLocalElementAnonymous();
            }
        }

        return Status.OK_STATUS;
    }

    public ISimpleType getType() {
        return type;
    }

    @Override
    public boolean canExecute() {
        if (modelObject == null || schema == null) {
            return false;
        }

        return true;
    }

    private void setGlobalElementAnonymous() {
        final XSDElementDeclaration xsdElementDeclaration = ((StructureType) modelObject).getElement();
        final XSDTypeDefinition xsdTypeDefinition = xsdElementDeclaration.getTypeDefinition();

        final XSDSimpleTypeDefinition anonymousTypeDefinition = XSDFactory.eINSTANCE.createXSDSimpleTypeDefinition();
        if (xsdTypeDefinition instanceof XSDSimpleTypeDefinition) {
            anonymousTypeDefinition.setBaseTypeDefinition((XSDSimpleTypeDefinition) xsdTypeDefinition);
        } else {
            anonymousTypeDefinition.setBaseTypeDefinition((XSDSimpleTypeDefinition) Schema.getDefaultSimpleType().getComponent());
        }

        xsdElementDeclaration.setResolvedElementDeclaration(xsdElementDeclaration);
        xsdElementDeclaration.setAnonymousTypeDefinition(anonymousTypeDefinition);
        xsdElementDeclaration.updateElement();
        type = new SimpleType(getModelRoot(), schema, modelObject, anonymousTypeDefinition);
    }

    private void setLocalElementAnonymous() {
        final XSDConcreteComponent _component = (XSDConcreteComponent) modelObject.getComponent();
        final XSDElementDeclaration element = (XSDElementDeclaration) ((XSDParticle) _component).getContent();

        final XSDSimpleTypeDefinition anonymousTypeDefinition = XSDFactory.eINSTANCE.createXSDSimpleTypeDefinition();

        if (!element.isElementDeclarationReference()) {
            // case 1 - not a reference, e.g. type="something"
            final XSDSimpleTypeDefinition xsdTypeDefinition = (XSDSimpleTypeDefinition) element.getTypeDefinition();

            anonymousTypeDefinition.setBaseTypeDefinition(xsdTypeDefinition);
            element.setAnonymousTypeDefinition(anonymousTypeDefinition);
            type = new SimpleType(getModelRoot(), schema, modelObject, anonymousTypeDefinition);
        } else {
            // case 2 - reference, e.g. ref="something"
            final XSDElementDeclaration referedElementDeclaration = element.getResolvedElementDeclaration();
            final XSDSimpleTypeDefinition xsdTypeDefinition = (XSDSimpleTypeDefinition) referedElementDeclaration
                    .getTypeDefinition();

            anonymousTypeDefinition.setBaseTypeDefinition(xsdTypeDefinition);
            element.setResolvedElementDeclaration(element);
            element.setName(referedElementDeclaration.getName());
            element.setAnonymousTypeDefinition(anonymousTypeDefinition);
            element.updateElement();
        }

        type = new SimpleType(getModelRoot(), schema, modelObject, anonymousTypeDefinition);
    }

    private void setLocalAttributeAnonymous() {
        final XSDAttributeDeclaration attribute = (XSDAttributeDeclaration) modelObject.getComponent();

        final XSDSimpleTypeDefinition anonymousTypeDefinition = XSDFactory.eINSTANCE.createXSDSimpleTypeDefinition();

        if (!attribute.isAttributeDeclarationReference()) {
            // case 1 - not a reference, e.g. type="something"
            final XSDSimpleTypeDefinition xsdTypeDefinition = attribute.getTypeDefinition();

            anonymousTypeDefinition.setBaseTypeDefinition(xsdTypeDefinition);
            attribute.setAnonymousTypeDefinition(anonymousTypeDefinition);
            type = new SimpleType(getModelRoot(), schema, modelObject, anonymousTypeDefinition);
        } else {
            // case 2 - reference, e.g. ref="something"
            final XSDAttributeDeclaration referedAttributeDeclaration = attribute.getResolvedAttributeDeclaration();
            final XSDSimpleTypeDefinition xsdTypeDefinition = referedAttributeDeclaration.getTypeDefinition();

            anonymousTypeDefinition.setBaseTypeDefinition(xsdTypeDefinition);
            attribute.setResolvedAttributeDeclaration(attribute);
            attribute.setName(referedAttributeDeclaration.getName());
            attribute.setAnonymousTypeDefinition(anonymousTypeDefinition);
            attribute.updateElement();
        }

        type = new SimpleType(getModelRoot(), schema, modelObject, anonymousTypeDefinition);
    }

    // =========================================================
    // helpers
    // =========================================================

    public IModelObject getModelObject() {
        return modelObject;
    }

}
