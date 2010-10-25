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

import org.eclipse.core.runtime.Status;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;
import org.eclipse.xsd.XSDTypeDefinition;

import org.eclipse.wst.sse.sieditor.command.common.AbstractXSDNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.StructureType;

/**
 * Command for adding a new Global ComplexTypeDefinition or ElementDeclaration
 * 
 * 
 * 
 */
public class AddStructureTypeCommand extends AbstractXSDNotificationOperation {
    private final String _name;
    private StructureType _structureType;
    private final boolean _element;
    private final AbstractType referencedType;

    /**
     * @deprecated use
     *             {@link #AddStructureTypeCommand(IXSDModelRoot, ISchema, String, String, boolean, AbstractType)}
     *             instead
     */
    @Deprecated
    public AddStructureTypeCommand(final IXSDModelRoot root, final ISchema schema, final String name, final boolean element,
            final AbstractType referencedType) {
        this(root, schema, Messages.AddStructureTypeCommand_create_new_structure_type_command_label, name, element,
                referencedType);
    }

    public AddStructureTypeCommand(final IXSDModelRoot root, final ISchema schema, final String operationLabel,
            final String name, final boolean element, final AbstractType referencedType) {
        super(root, schema, operationLabel);
        this._name = name;
        this._element = element;
        this.referencedType = referencedType;
    }

    @Override
    public org.eclipse.core.runtime.IStatus run(final org.eclipse.core.runtime.IProgressMonitor monitor,
            final org.eclipse.core.runtime.IAdaptable info) throws org.eclipse.core.commands.ExecutionException {
        final Schema schema = (Schema) modelObject;
        XSDNamedComponent component;
        final XSDComplexTypeDefinition eComplexType = getXSDFactory().createXSDComplexTypeDefinition();
        if (_element) {
            final XSDElementDeclaration element = getXSDFactory().createXSDElementDeclaration();
            element.setAnonymousTypeDefinition(eComplexType);
            if (referencedType != null) {
                addChildElement(eComplexType, referencedType.getName(), referencedType.getComponent());
            }
            component = element;
        } else {
            component = eComplexType;
        }
        component.setName(_name);
        final XSDSchema emfSchema = schema.getComponent();
        component.setTargetNamespace(emfSchema.getTargetNamespace());
        emfSchema.getContents().add((XSDSchemaContent) component);

        final StructureType structureType = new StructureType(getModelRoot(), schema, component);

        _structureType = structureType;
        return Status.OK_STATUS;
    }

    static void addChildElement(final XSDComplexTypeDefinition eComplexType, final String elementName,
            final XSDNamedComponent referenced) {
        final XSDModelGroup modelGroup = getXSDFactory().createXSDModelGroup();
        modelGroup.setCompositor(XSDCompositor.SEQUENCE_LITERAL);

        final XSDParticle particle = getXSDFactory().createXSDParticle();
        final XSDParticle elementParticle = getXSDFactory().createXSDParticle();
        elementParticle.setMinOccurs(1);
        elementParticle.setMaxOccurs(1);

        final XSDElementDeclaration newElement = getXSDFactory().createXSDElementDeclaration();
        newElement.setName(elementName);

        if (referenced instanceof XSDElementDeclaration) {
            newElement.setResolvedElementDeclaration((XSDElementDeclaration) referenced);
        } else {
            newElement.setTypeDefinition((XSDTypeDefinition) referenced);
        }

        eComplexType.setContent(particle);
        particle.setContent(modelGroup);
        modelGroup.getContents().add(elementParticle);
        elementParticle.setContent(newElement);
    }

    public StructureType getStructureType() {
        return _structureType;
    }

}