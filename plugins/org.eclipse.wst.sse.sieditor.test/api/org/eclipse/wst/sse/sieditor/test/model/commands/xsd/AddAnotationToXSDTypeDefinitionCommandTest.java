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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDTypeDefinition;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddAnotationCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddSimpleTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.impl.XSDFactory;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;

/**
 * This test fails due to a bug or wrong usage of classes from org.eclipse.xsd
 * When the issue is fixed, the test should perform fine
 * 
 *
 * 
 */
public class AddAnotationToXSDTypeDefinitionCommandTest extends AbstractCommandTest {
    private static final String TYPE_NAME = "SimpleTypeWithDoc"; //$NON-NLS-1$
    private static final String TARGET_NAMESPACE = "http://sap.com/xi/Purchasing"; //$NON-NLS-1$

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        final XSDNamedComponent component = modelRoot.getDescription().getSchema(TARGET_NAMESPACE)[0].getType(false, TYPE_NAME)
                .getComponent();
        assertTrue(component instanceof XSDTypeDefinition);
        assertNotNull(((XSDTypeDefinition) component).getAnnotation());
        assertTrue(((XSDTypeDefinition) component).getAnnotations().size() == 1);
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        final XSDNamedComponent component = modelRoot.getDescription().getSchema(TARGET_NAMESPACE)[0].getType(false, TYPE_NAME)
                .getComponent();
        assertTrue(component instanceof XSDTypeDefinition);
        assertNull(((XSDTypeDefinition) component).getAnnotation());
        assertTrue(((XSDTypeDefinition) component).getAnnotations().size() == 0);
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final ISchema schema = modelRoot.getDescription().getSchema(TARGET_NAMESPACE)[0];
        final IXSDModelRoot xsdModelRoot = XSDFactory.getInstance().createXSDModelRoot(schema.getComponent());
        final AddSimpleTypeCommand command = new AddSimpleTypeCommand(xsdModelRoot, schema, TYPE_NAME) {
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
        final IType type = schema.getType(false, TYPE_NAME);
        final XSDNamedComponent component = type.getComponent();
        assertTrue(component instanceof XSDTypeDefinition);
        final XSDTypeDefinition typeDef = (XSDTypeDefinition) component;
        assertNull(typeDef.getAnnotation());
        return new AddAnotationCommand(typeDef, xsdModelRoot, type);
    }

}
