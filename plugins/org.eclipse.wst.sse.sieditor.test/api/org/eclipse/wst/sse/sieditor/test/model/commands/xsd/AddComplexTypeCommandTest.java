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
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddStructureTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.impl.XSDFactory;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;

public class AddComplexTypeCommandTest extends AbstractCommandTest {
    private static final String TARGET_NAMESPACE = "http://sap.com/xi/Purchasing";
    private static final String NEW_TYPE_NAME = "newGlobalComplexType";

    private Schema schema;
    private AbstractType type;

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        final IType[] types = schema.getAllTypes(NEW_TYPE_NAME);
        assertEquals(1, types.length);
        final IType iType = types[0];
        assertNotNull(iType);
        assertTrue(iType instanceof IStructureType);

        final IStructureType newType = (IStructureType) iType;

        assertFalse(newType.isElement());
        assertNull(newType.getType());
        assertEquals(0, newType.getAllElements().size());
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        assertNull(schema.getAllTypes(NEW_TYPE_NAME));
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final ISchema[] schemas = modelRoot.getDescription().getSchema(TARGET_NAMESPACE);
        schema = (Schema) schemas[0];
        type = (AbstractType) schema.getType(false, "Address");
        final IXSDModelRoot xsdModelRoot = XSDFactory.getInstance().createXSDModelRoot(schema.getComponent());
        return new AddStructureTypeCommand(xsdModelRoot, schema, NEW_TYPE_NAME, false, type);
    }
}
