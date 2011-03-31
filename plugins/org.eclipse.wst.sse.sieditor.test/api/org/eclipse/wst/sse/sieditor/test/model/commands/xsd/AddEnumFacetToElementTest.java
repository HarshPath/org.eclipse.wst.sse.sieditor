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
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddEnumFacetToElementCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.AddStructureTypeCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.impl.XSDFactory;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IFacet;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.StructureType;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;

@SuppressWarnings("nls")
public class AddEnumFacetToElementTest extends AbstractCommandTest {

    private static final String ENUM_FACET_VALUE = "first";
    private static final String TARGET_NAMESPACE = "http://sap.com/xi/Purchasing";
    private static final String NEW_ELEMENT_NAME = "myElement";

    private IElement element;
    private IType type;

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        final IFacet[] enums = ((ISimpleType) element.getType()).getEnumerations();
        assertNotNull(enums);
        assertEquals(1, enums.length);
        assertEquals(ENUM_FACET_VALUE, enums[0].getValue());
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        assertEquals(type, element.getType());
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final ISchema[] schemas = modelRoot.getDescription().getSchema(TARGET_NAMESPACE);
        final IXSDModelRoot xsdModelRoot = XSDFactory.getInstance().createXSDModelRoot(schemas[0].getComponent());
        final AddStructureTypeCommand cmd = new AddStructureTypeCommand(xsdModelRoot, schemas[0], "add", "myStruct",
                false, null) {
            @Override
            public boolean canUndo() {

                return false;
            }

            @Override
            public boolean canRedo() {

                return false;
            }
        };

        modelRoot.getEnv().execute(cmd);
        final StructureType struct = cmd.getStructureType();

        final AddElementCommand addEl = new AddElementCommand(xsdModelRoot, struct, NEW_ELEMENT_NAME) {
            @Override
            public boolean canUndo() {

                return false;
            }

            @Override
            public boolean canRedo() {

                return false;
            }
        };
        modelRoot.getEnv().execute(addEl);

        element = addEl.getElement();
        type = element.getType();
        return new AddEnumFacetToElementCommand(modelRoot, element, ENUM_FACET_VALUE);
    }
}
