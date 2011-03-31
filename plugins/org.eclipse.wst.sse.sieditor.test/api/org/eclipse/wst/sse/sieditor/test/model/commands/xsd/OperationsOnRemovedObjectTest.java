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
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.xsd.XSDPackage;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddElementCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddFacetToElementCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddStructureTypeCommand;
import org.eclipse.wst.sse.sieditor.core.common.IEnvironment;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.impl.XSDFactory;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.StructureType;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;

/**
 * This test checks if any exceptions will be thrown if an operation is executed
 * over a deleted component; in particular it tries to add a facet to an element
 * which was already deleted
 * 
 *
 * 
 */
@SuppressWarnings("nls")
public class OperationsOnRemovedObjectTest extends AbstractCommandTest {

    private static final String TARGET_NAMESPACE = "http://sap.com/xi/Purchasing";
    private static final String NEW_TYPE_NAME = "newGlobalComplexType";

    private Schema schema;
    private AbstractType type;

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        assertTrue(redoStatus.isOK());

    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        assertTrue(undoStatus.isOK());
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final ISchema[] schemas = modelRoot.getDescription().getSchema(TARGET_NAMESPACE);
        schema = (Schema) schemas[0];
        type = (AbstractType) schema.getType(false, "Address");
        final IXSDModelRoot xsdModelRoot = XSDFactory.getInstance().createXSDModelRoot(schema.getComponent());
        final AddStructureTypeCommand add = new AddStructureTypeCommand(xsdModelRoot, schema, NEW_TYPE_NAME, false, type) {
            @Override
            public boolean canUndo() {
                return false;
            }

            @Override
            public boolean canRedo() {
                return false;
            }
        };
        final IEnvironment env = modelRoot.getEnv();

        assertTrue(env.execute(add).isOK());
        final StructureType structureType = add.getStructureType();

        final AddElementCommand addEl = new AddElementCommand(modelRoot, structureType, "element") {
            boolean undoableFirstTime = true;

            @Override
            public boolean canUndo() {
                final boolean result = undoableFirstTime;
                undoableFirstTime = false;
                return result;
            }

            @Override
            public boolean canRedo() {
                return false;
            }
        };

        assertTrue(env.execute(addEl).isOK());
        final IElement element = addEl.getElement();
        env.getOperationHistory().undoOperation(addEl, new NullProgressMonitor(), null);
        return new AddFacetToElementCommand(modelRoot, element, XSDPackage.XSD_MIN_LENGTH_FACET, "12");

    }
}
