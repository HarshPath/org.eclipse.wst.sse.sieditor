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

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.impl.XSDComplexTypeDefinitionImpl;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.utils.NameGenerator;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.StructureType;

/**
 * This command changes an anonymous global element to a global element with
 * global type. The child elements and attributes are "extracted" to new
 * structure type.<br>
 * <br>
 * 
 * 
 * 
 */
public class MakeAnonymousTypeGlobalCommand extends AbstractNotificationOperation {

    private IStructureType extractedStructureType;

    public MakeAnonymousTypeGlobalCommand(final IXSDModelRoot modelRoot, final IStructureType structureType) {
        super(modelRoot, structureType, Messages.MakeAnonymousStructureTypeGlobalCommand_operation_label);
    }

    @Override
    public IStatus run(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
        final ISchema schema = ((IXSDModelRoot) root).getSchema();

        final XSDElementDeclaration anonymousElement = (XSDElementDeclaration) ((IStructureType) modelObject).getComponent();
        final XSDComplexTypeDefinitionImpl anonymousElementComplexTypeDefinition = (XSDComplexTypeDefinitionImpl) anonymousElement
                .getAnonymousTypeDefinition();

        final XSDComplexTypeDefinitionImpl eComplexType = EmfXsdUtils.cloneWithAnnotation(anonymousElementComplexTypeDefinition,
                schema.getComponent());
        eComplexType.setName(NameGenerator.getNewStructureTypeDefaultName(schema));
        
        anonymousElement.setAnonymousTypeDefinition(null);
        
        schema.getComponent().getContents().add(eComplexType);
        anonymousElement.setTypeDefinition(eComplexType);
        
        extractedStructureType = new StructureType((IXSDModelRoot) root, (Schema) schema, (XSDNamedComponent) eComplexType);

        return Status.OK_STATUS;
    }

    public IStructureType getExtractedStructureType() {
        return extractedStructureType;
    }

    public IStructureType getStructureType() {
        return (IStructureType) modelObject;
    }

}
