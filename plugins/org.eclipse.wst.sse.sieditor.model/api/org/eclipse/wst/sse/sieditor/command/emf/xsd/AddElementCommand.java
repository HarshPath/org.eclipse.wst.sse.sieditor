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
import org.eclipse.xsd.XSDComplexTypeContent;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDParticleContent;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.Activator;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.impl.XSDFactory;
import org.eclipse.wst.sse.sieditor.model.utils.StatusUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Element;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.StructureType;

/**
 * Command for add a new Local Element in a StructureType it also ensured that
 * the structure type is in a condition for adding a new element to it
 * 
 * 
 * 
 */
public class AddElementCommand extends AbstractNotificationOperation {
    private static final int DEFAULT_MIN_OCCURS = 1;
    private static final int DEFAULT_MAX_OCCURS = 1;

    private final String _name;
    private Element _element;

    public AddElementCommand(final IModelRoot root, final IStructureType type, final String name) {
        super(root, type, Messages.AddElementCommand_add_element_command_label);
        this._name = name;
    }

    @Override
    public IStatus run(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
        final StructureType structureType = (StructureType) modelObject;
        final XSDNamedComponent schemaComponent = structureType.getComponent();
        final XSDElementDeclaration element;
        final XSDTypeDefinition xsdType;

        element = schemaComponent instanceof XSDElementDeclaration ? (XSDElementDeclaration) schemaComponent : null;
        xsdType = element == null ? (XSDTypeDefinition) schemaComponent : element.getType();

        XSDComplexTypeDefinition type = null;
        if (null != element) {
            final XSDTypeDefinition elementType = element.getAnonymousTypeDefinition();
            if (element.isElementDeclarationReference() || null == elementType || elementType instanceof XSDSimpleTypeDefinition) {

                IXSDModelRoot xsdModelRoot = XSDFactory.getInstance().createXSDModelRoot(element.getSchema());
                final EnsureElementAnonymousTypeCommand command = new EnsureElementAnonymousTypeCommand(xsdModelRoot, element);
                IStatus status = xsdModelRoot.getEnv().execute(command);
                if (!StatusUtils.canContinue(status)) {
                    return status;
                }
                type = command.getAnonymousType();
            } else {
                type = (XSDComplexTypeDefinition) elementType;
            }
        } else {
            type = (XSDComplexTypeDefinition) xsdType;
        }

        final Schema schema = (Schema) structureType.getParent();
        if (null == type) {
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, Messages.AddElementCommand_msg_can_not_create_type_definition);
        }

        XSDComplexTypeContent content = type.getContent();
        if (null == content || content instanceof XSDSimpleTypeDefinition) {
            content = getXSDFactory().createXSDParticle();
            XSDModelGroup particleContent = getXSDFactory().createXSDModelGroup();
            ((XSDModelGroup) particleContent).setCompositor(XSDCompositor.SEQUENCE_LITERAL);
            ((XSDParticle) content).setContent(particleContent);
            type.eUnset(getXSDFactory().getXSDPackage().getXSDComplexTypeDefinition_Content());
            type.eUnset(getXSDFactory().getXSDPackage().getXSDComplexTypeDefinition_ContentType());
            type.eUnset(getXSDFactory().getXSDPackage().getXSDComplexTypeDefinition_BaseTypeDefinition());
            type.eUnset(getXSDFactory().getXSDPackage().getXSDComplexTypeDefinition_Block());
            type.eUnset(getXSDFactory().getXSDPackage().getXSDComplexTypeDefinition_DerivationMethod());
            type.setContent(content);
            type.setContentType(content);
        }

        final XSDParticle particle = (XSDParticle) content;
        XSDParticleContent particleContent = particle.getContent();
        if (null == particleContent) {
            particleContent = getXSDFactory().createXSDModelGroup();
            ((XSDModelGroup) particleContent).setCompositor(XSDCompositor.SEQUENCE_LITERAL);
            particle.setContent(particleContent);
        }
        final XSDModelGroup modelGroup = (XSDModelGroup) particleContent;
        final XSDParticle elementParticle = getXSDFactory().createXSDParticle();
        elementParticle.setMinOccurs(DEFAULT_MIN_OCCURS);
        elementParticle.setMaxOccurs(DEFAULT_MAX_OCCURS);

        final XSDElementDeclaration newElement = getXSDFactory().createXSDElementDeclaration();
        newElement.setName(_name);

        final IType defaultType = Schema.getDefaultSimpleType();
        final XSDTypeDefinition defaultXsdType = (XSDTypeDefinition) defaultType.getComponent();
        newElement.setTypeDefinition(defaultXsdType);

        elementParticle.setContent(newElement);
        // particle.setContent(particleContent);
        modelGroup.getContents().add(elementParticle);

        IXSDModelRoot xsdModelRoot = XSDFactory.getInstance().createXSDModelRoot(schema.getComponent());
        _element = new Element(xsdModelRoot, elementParticle, modelGroup, structureType, schema);

        return Status.OK_STATUS;
    }

    public boolean canExecute() {
        return !(null == modelObject || null == _name);
    }

    public Element getElement() {
        return _element;
    }

}
