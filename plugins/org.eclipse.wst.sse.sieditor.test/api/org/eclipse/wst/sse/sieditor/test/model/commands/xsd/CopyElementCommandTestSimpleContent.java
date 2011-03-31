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
import org.eclipse.wst.sse.sieditor.command.emf.xsd.CopyElementCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.impl.XSDFactory;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;

public class CopyElementCommandTestSimpleContent extends AbstractCommandTest {
    private static final String TARGET_NAMESPACE = "http://sap.com/xi/Purchasing";
    private static final String SCHEMA2_TARGET_NAMESPACE = "http://sap.com/xi/SRM/Basis/Global";

    private Schema schema;
    private Schema targetSchema;
    private IStructureType type;

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        final IStructureType amount = (IStructureType) schema.getType(false, "Amount");
        assertEquals(2, amount.getElements("currencyCode").size());
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        final IStructureType amount = (IStructureType) schema.getType(false, "Amount");
        assertEquals(1, amount.getElements("currencyCode").size());
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final ISchema[] schemas = modelRoot.getDescription().getSchema(TARGET_NAMESPACE);
        final ISchema[] targetSchemas = modelRoot.getDescription().getSchema(SCHEMA2_TARGET_NAMESPACE);
        schema = (Schema) schemas[0];
        targetSchema = (Schema) targetSchemas[0];
        type = (IStructureType) schema.getType(false, "Amount");
        final Collection<IElement> elements = type.getElements("currencyCode");

        assertEquals(1, elements.size());

        final IElement elementToCopy = elements.iterator().next();
        final IXSDModelRoot xsdModelRoot = XSDFactory.getInstance().createXSDModelRoot(schema.getComponent());
        return new CopyElementCommand(xsdModelRoot, type, elementToCopy, targetSchema);
    }
}
