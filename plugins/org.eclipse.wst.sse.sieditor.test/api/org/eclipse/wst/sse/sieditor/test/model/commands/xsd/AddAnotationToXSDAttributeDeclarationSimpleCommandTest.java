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
package org.eclipse.wst.sse.sieditor.test.model.commands.xsd;

import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDConcreteComponent;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddAnotationCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddAttributeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.impl.XSDFactory;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.StructureType;

public class AddAnotationToXSDAttributeDeclarationSimpleCommandTest extends AddAnotationToXSDAttributeDeclarationCommandTest {
    private static final String EXISTING_TYPE_NAME = "Address"; //$NON-NLS-1$
    private static final String TARGET_NAMESPACE = "http://sap.com/xi/Purchasing"; //$NON-NLS-1$

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final ISchema schema = modelRoot.getDescription().getSchema(TARGET_NAMESPACE)[0];
        final IXSDModelRoot xsdModelRoot = XSDFactory.getInstance().createXSDModelRoot(schema.getComponent());

        final IType existingType = schema.getType(false, EXISTING_TYPE_NAME);
        assertTrue(existingType instanceof StructureType);
        final AddAttributeCommand command = new AddAttributeCommand(xsdModelRoot, (StructureType) existingType, ATTRIBUTE_NAME) {
            @Override
            public boolean canUndo() {
                return false;
            }

            @Override
            public boolean canRedo() {
                return false;
            }
        };
        modelRoot.getEnv().execute(command);
        final XSDConcreteComponent attributeComponent = ((StructureType) existingType).getElements(ATTRIBUTE_NAME).iterator().next()
                .getComponent();
        assertTrue(attributeComponent instanceof XSDAttributeDeclaration);
        return new AddAnotationCommand(attributeComponent, xsdModelRoot, existingType);
    }

    @Override
    public String getExistingTypeName() {
        return EXISTING_TYPE_NAME;
    }
}
