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
import org.eclipse.xsd.XSDAttributeUse;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Element;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.StructureType;

public class AddAttributeCommand extends AbstractNotificationOperation {

    private final String name;

    private IElement attribute;

    public AddAttributeCommand(final IModelRoot root, final IStructureType type, final String name) {
        super(root, type, Messages.AddAttributeCommand_0);
        this.name = name;
    }

    @Override
    public IStatus run(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
        final XSDAttributeDeclaration attributeDeclaration = createAttributeDeclaration(name);
        final XSDComplexTypeDefinition typeDefinition = getComplexTypeDefinition((StructureType) modelObject);
        typeDefinition.getAttributeContents().add(createAttributeUse(attributeDeclaration));
        attribute = new Element((IXSDModelRoot) getModelRoot(), attributeDeclaration, null, (StructureType) modelObject,
                ((StructureType) modelObject).getSchema());
        return Status.OK_STATUS;
    }

    // ===========================================================
    // command execution helpers
    // ===========================================================

    /**
     * utility method. creates attribute declaration element with the given
     * attribute name
     * 
     * @param attributeName
     *            - the name of the attribute to be created
     * @return the created attribute declaration
     */
    private XSDAttributeDeclaration createAttributeDeclaration(final String attributeName) {
        final XSDFactory xsdFactory = EmfXsdUtils.getXSDFactory();
        final XSDAttributeDeclaration attributeDeclaration = xsdFactory.createXSDAttributeDeclaration();
        attributeDeclaration.setName(attributeName);
        attributeDeclaration.setTypeDefinition(getSimpleTypeDefinitionForDefaultType());
        return attributeDeclaration;
    }

    /**
     * utility method. creates type definition for the default XSD type -
     * {@link Schema#getDefaultSimpleType()}
     * 
     * @return the created type definition
     */
    private XSDSimpleTypeDefinition getSimpleTypeDefinitionForDefaultType() {
        final IType defaultType = Schema.getDefaultSimpleType();
        final XSDTypeDefinition defaultXsdType = (XSDTypeDefinition) defaultType.getComponent();
        final XSDSimpleTypeDefinition defaultXsdTypeSimpleDefinition = defaultXsdType.getSimpleType();
        return defaultXsdTypeSimpleDefinition;
    }

    /**
     * utility method. creates attribute use object for the given attribute
     * declaration
     * 
     * @param attributeDeclaration
     *            - the attribute declaration to create the attribute use object
     *            for
     * @return the created attribute use object
     */
    private XSDAttributeUse createAttributeUse(final XSDAttributeDeclaration attributeDeclaration) {
        final XSDFactory xsdFactory = EmfXsdUtils.getXSDFactory();
        final XSDAttributeUse attributeUse = xsdFactory.createXSDAttributeUse();
        attributeUse.setContent(attributeDeclaration);
        return attributeUse;
    }

    /**
     * utility method. returns the {@link XSDComplexTypeDefinition} of the given
     * {@link StructureType}
     * 
     * @param structureType
     *            - the structure type to get the
     *            {@link XSDComplexTypeDefinition} for
     * @return the {@link XSDComplexTypeDefinition} of the structureType or
     *         <code>null</code> if such definition cannot be extracted
     */
    private XSDComplexTypeDefinition getComplexTypeDefinition(final StructureType structureType) {
        final XSDNamedComponent schemaComponent = structureType.getComponent();
        if (schemaComponent instanceof XSDComplexTypeDefinition) {
            return (XSDComplexTypeDefinition) schemaComponent;
        } else if (schemaComponent instanceof XSDElementDeclaration) {
            return (XSDComplexTypeDefinition) ((XSDElementDeclaration) schemaComponent).getType();
        }
        return null;
    }

    // ===========================================================
    // getters
    // ===========================================================

    public IElement getAttribute() {
        return attribute;
    }

}
