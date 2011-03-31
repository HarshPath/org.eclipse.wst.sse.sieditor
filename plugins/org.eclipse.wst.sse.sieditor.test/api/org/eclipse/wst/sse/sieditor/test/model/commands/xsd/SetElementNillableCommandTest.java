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

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.SetElementNillableCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;

public class SetElementNillableCommandTest extends AbstractCommandTest {

    private static final String TARGET_NAMESPACE = "http://sap.com/xi/Purchasing";
    private static final String COMPLEX_TYPE_NAME = "BusinessTransactionDocumentShipToLocation";
    private static final String CHILD_ELEMENT_NAME = "Note";

    private IStructureType structureType;
    private IElement element;

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        assertTrue(element.getNillable());
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        assertFalse(element.getNillable());
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final ISchema[] schemas = modelRoot.getDescription().getSchema(TARGET_NAMESPACE);
        structureType = (IStructureType) schemas[0].getType(false, COMPLEX_TYPE_NAME);
        assertNotNull(structureType);
        
        final Collection<IElement> elements = structureType.getElements(CHILD_ELEMENT_NAME);
        assertFalse(elements.isEmpty());

        element = elements.iterator().next();
        return new SetElementNillableCommand(modelRoot, element, true);
    }
}
