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
import org.eclipse.xsd.XSDNamedComponent;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetElementTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Element;

/**
 * Caution, the test won't give consistent results if the new set type is from a
 * different document, but has the same Name and Namespace (QName)
 * 
 */
public abstract class SetElementTypeAbstractCommandTest extends ElementManipulationsAbstractCommandTest {
    protected AbstractType newType;
    protected Element element;
    protected AbstractType initialType;

    protected abstract AbstractType getNewType() throws ExecutionException;
    protected abstract String getNewElementName();

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        assertTypeEqualInStateToInitial(newType, element.getType());
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        assertTypeEqualInStateToInitial(initialType, element.getType());
    }

    /**
     * Asserts equality of state of two objects. They are not directly compared
     * with equals, because in the tests the two types are extracted from two
     * different models, therefore they are different objects and emf model
     * equals check asserts with "==".
     * 
     * @param typeToCompareWith
     *            original object
     * @param typeToAssert
     *            object to assert with.
     */
    protected void assertTypeEqualInStateToInitial(final AbstractType typeToCompareWith, final IType typeToAssert) {
        final XSDNamedComponent originalTypeComponent = typeToCompareWith.getComponent();

        final XSDNamedComponent assertTypeComponent = element.getType().getComponent();

        // assert that the type name and namespace match
        assertEquals(originalTypeComponent.getQName(), assertTypeComponent.getQName());
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        element = createElement(modelRoot, getNewElementName());

        initialType = (AbstractType) element.getType();
        newType = getNewType();

        return createSetTypeCommand();
    }

    protected AbstractNotificationOperation createSetTypeCommand() {
        return new SetElementTypeCommand(modelRoot, element, newType);
    }
}
