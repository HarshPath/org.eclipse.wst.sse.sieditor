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

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddElementCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.RenameElementCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Element;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.StructureType;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;

public class RenameLocalElementCommandTest extends AbstractCommandTest {
    private static final String TARGET_NAMESPACE = "http://sap.com/xi/Purchasing";
    private static final String INITIAL_ELEMENT_NAME = "myNewElementToBeRenamed";
    private static final String NEW_ELEMENT_NAME = "myNewElementAlreadyRenamed";
    private static final String COMPLEX_TYPE_NAME = "Address";
    private StructureType type;

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        assertEquals(0, type.getElements(INITIAL_ELEMENT_NAME).size());
        assertEquals(1, type.getElements(NEW_ELEMENT_NAME).size());
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        assertEquals(0, type.getElements(NEW_ELEMENT_NAME).size());
        assertEquals(1, type.getElements(INITIAL_ELEMENT_NAME).size());
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final ISchema[] schemas = modelRoot.getDescription().getSchema(TARGET_NAMESPACE);
        type = (StructureType) schemas[0].getType(false, COMPLEX_TYPE_NAME);

        final AddElementCommand addElementCommand = new AddElementCommand(modelRoot, type, INITIAL_ELEMENT_NAME) {
            @Override
            public boolean canRedo() {
                return false;
            }

            @Override
            public boolean canUndo() {
                return false;
            }
        };

        modelRoot.getEnv().execute(addElementCommand);

        final Element element = addElementCommand.getElement();

        return new RenameElementCommand(modelRoot, element, NEW_ELEMENT_NAME);
    }
}
