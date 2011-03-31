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

import java.util.Iterator;

import org.eclipse.core.runtime.IStatus;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.MakeAnonymousTypeGlobalCommand;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.MakeGlobalTypeAnonymousCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandChainTest;

public class ToggleBetweenMakeTypeAnonymousAndMakeTypeGlobalCommandTest extends AbstractCommandChainTest {

    private static final String EXPECTED_EXTRACTED_STRUCTURE_TYPE = "StructureType1";
    private static final String TARGET_NAMESPACE = "http://sap.com/xi/SAPGlobal20/Global";
    private static final String ELEMENT_NAME = "StandardMessageFault";
    private static final String EXPECTED_CHILD_ELEMENT_NAME = "Element1";

    private IStructureType element;
    private IType extractedType;

    private MakeGlobalTypeAnonymousCommand makeTypeAnonymousCommand;
    private MakeAnonymousTypeGlobalCommand makeTypeGlobalCommand;

    @Override
    protected String getWsdlFilename() {
        return "ECC_IMAPPROPRIATIONREQREJRC_VALID.wsdl";
    }

    @Override
    protected String getWsdlFoldername() {
        return "pub/csns/";
    }

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        assertTrue(element.isAnonymous());
        final Iterator<IElement> iterator = element.getElements(EXPECTED_CHILD_ELEMENT_NAME).iterator();
        assertTrue(iterator.hasNext());
        final IElement childElement = iterator.next();
        assertNotNull(childElement);
        assertEquals(EXPECTED_EXTRACTED_STRUCTURE_TYPE, childElement.getType().getName());
        assertThereAreNoValidationErrors();
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        assertTrue(element.isAnonymous());
        assertEquals(1, element.getElements("standard").size());
        assertThereAreNoValidationErrors();
    }

    @Override
    protected AbstractNotificationOperation getNextOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final ISchema[] schemas = modelRoot.getDescription().getSchema(TARGET_NAMESPACE);
        element = (IStructureType) schemas[0].getType(true, ELEMENT_NAME);
        assertNotNull(element);

        if (makeTypeGlobalCommand == null) {
            assertTrue(element.isAnonymous());
            makeTypeGlobalCommand = new MakeAnonymousTypeGlobalCommand((IXSDModelRoot) element.getModelRoot(), element);
            return makeTypeGlobalCommand;
        }

        if (makeTypeAnonymousCommand == null) {
            extractedType = makeTypeGlobalCommand.getExtractedStructureType();
            assertFalse(element.isAnonymous());
            makeTypeAnonymousCommand = new MakeGlobalTypeAnonymousCommand((IXSDModelRoot) element.getModelRoot(), element, true);
            return makeTypeAnonymousCommand;
        }

        return null;
    }
}
