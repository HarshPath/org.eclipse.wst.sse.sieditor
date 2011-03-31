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
package org.eclipse.wst.sse.sieditor.test.model.commands.xsd.base;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetAnonymousCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetElementTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.impl.XSDFactory;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Element;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.StructureType;

public abstract class SetElementAnonymousCommandAbstractTest extends ElementManipulationsAbstractCommandTest {
    protected Element element;
    private AbstractType initialType;

    protected abstract String getNewElementName();
    protected abstract AbstractType getInitialType() throws ExecutionException;

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        final IType type = element.getType();

        assertNotNull(type);
        assertTrue(type.isAnonymous());
        assertNotSame(initialType, type);
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        //final IType type = element.getType();
        final IType type = getNewType();

        assertNotNull(type);
        assertEquals(initialType, type);
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        element = createElement(modelRoot, getNewElementName());
        initialType = getInitialType();
        
        final SetElementTypeCommand setTypeCmd = new SetElementTypeCommand(modelRoot, element, initialType) {
            @Override
            public boolean canRedo() {
                return false;
            }
            @Override
            public boolean canUndo() {
                return false;
            }
        };

        modelRoot.getEnv().execute(setTypeCmd);
        final IXSDModelRoot xsdModelRoot = XSDFactory.getInstance().createXSDModelRoot(schema.getComponent());
        return new SetAnonymousCommand(xsdModelRoot, element, null, false);
        
    }

    private IType getNewType() {
        return ((StructureType) element.getParent()).getElements(element.getName()).iterator().next().getType();
    }
}