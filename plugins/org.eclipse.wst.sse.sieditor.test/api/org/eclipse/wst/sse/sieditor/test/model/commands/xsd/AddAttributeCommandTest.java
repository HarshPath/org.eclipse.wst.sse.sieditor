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

import java.util.Collection;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.xsd.XSDElementDeclaration;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddAttributeCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddStructureTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.impl.XSDFactory;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.SimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.StructureType;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;

public class AddAttributeCommandTest extends AbstractCommandTest {

    private static final String TARGET_NAMESPACE = "http://sap.com/xi/Purchasing";//$NON-NLS-1$
    private static final String NEW_ELEMENT_NAME = "myNewElement";//$NON-NLS-1$
    private static final String NEW_TYPE_NAME = "newSimpleTypeForAttribute"; //$NON-NLS-1$
    
    private StructureType type;

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        final Collection<IElement> elements = type.getElements(NEW_ELEMENT_NAME);
        assertEquals(1, elements.size());
        final IElement element = elements.iterator().next();
        assertEquals(NEW_ELEMENT_NAME, element.getName());
        assertEquals(Schema.getDefaultSimpleType().getName(), element.getType().getName());
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        assertEquals(0, type.getElements(NEW_ELEMENT_NAME).size());
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final ISchema schema = modelRoot.getDescription().getSchema(TARGET_NAMESPACE)[0];
        final IXSDModelRoot xsdModelRoot = XSDFactory.getInstance().createXSDModelRoot(schema.getComponent());
        final IType existingType = Schema.getDefaultSimpleType();
        
        assertTrue(existingType instanceof SimpleType);
        final AddStructureTypeCommand command = new AddStructureTypeCommand(xsdModelRoot, schema, NEW_TYPE_NAME, true,
                (AbstractType) existingType) {
            @Override
            public boolean canUndo() {
                return false;
            }

            @Override
            public boolean canRedo() {
                return false;
            }
        };
        assertTrue(xsdModelRoot.getEnv().execute(command).isOK());
        type = command.getStructureType();
        assertTrue(type.getComponent() instanceof XSDElementDeclaration);

        return new AddAttributeCommand(xsdModelRoot, type, NEW_ELEMENT_NAME);
    }

}
