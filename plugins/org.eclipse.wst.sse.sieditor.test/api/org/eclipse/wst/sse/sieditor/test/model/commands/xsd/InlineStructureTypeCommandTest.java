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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.InlineStructureTypeContentsCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IElement;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IStructureType;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;
import org.eclipse.wst.sse.sieditor.model.validation.IValidationStatus;

public class InlineStructureTypeCommandTest extends AbstractCommandTest {

    private final List<String> contentsNames = new LinkedList<String>();

    private static final int EXPECTED_CONTENTS_NUMBER = 2;
    private static final String TARGET_NAMESPACE = "http://sap.com/xi/SAPGlobal20/Global";
    private static final String ELEMENT_NAME = "AppropriationRequestRejectConfirmation_sync";

    private IStructureType element;
    private IType oldType;

    @Override
    protected String getWsdlFilename() {
        return "ECC_IMAPPROPRIATIONREQREJRC.wsdl";
    }

    @Override
    protected String getWsdlFoldername() {
        return "pub/csns/";
    }

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        assertTrue(element.isAnonymous());
        final Collection<IElement> collection = element.getAllElements();

        assertTrue(element.isAnonymous());
        assertEquals(EXPECTED_CONTENTS_NUMBER, collection.size());

        final Iterator<IElement> iterator = collection.iterator();
        for (int i = 0; i < collection.size(); i++) {
            assertEquals(contentsNames.get(i), iterator.next().getName());
        }
        
        int errors = 0;
        final List<IValidationStatus> statuses = editor.getValidationService().getValidationStatusProvider().getStatus(element);
        for (final IValidationStatus status : statuses) {
            if (status.getSeverity() == IStatus.ERROR) {
                errors++;
            }
        }
        assertEquals(0, errors);
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        assertFalse(element.isAnonymous());
        assertEquals(oldType, element.getType());

        final ISchema[] schemas = modelRoot.getDescription().getSchema(TARGET_NAMESPACE);
        element = (IStructureType) schemas[0].getType(true, ELEMENT_NAME);
        assertNotNull(element);

        oldType = element.getType();
        assertFalse(element.isAnonymous());

        final Collection<IElement> allElements = ((IStructureType) element.getType()).getAllElements();
        assertEquals(EXPECTED_CONTENTS_NUMBER, allElements.size());
        
        int errors = 0;
        final List<IValidationStatus> statuses = editor.getValidationService().getValidationStatusProvider().getStatus(element);
        for (final IValidationStatus status : statuses) {
            if (status.getSeverity() == IStatus.ERROR) {
                errors++;
            }
        }
        assertEquals(0, errors);
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        final ISchema[] schemas = modelRoot.getDescription().getSchema(TARGET_NAMESPACE);
        element = (IStructureType) schemas[0].getType(true, ELEMENT_NAME);
        assertNotNull(element);

        oldType = element.getType();
        assertFalse(element.isAnonymous());

        final Collection<IElement> allElements = ((IStructureType) element.getType()).getAllElements();
        assertEquals(EXPECTED_CONTENTS_NUMBER, allElements.size());
        for (final IElement element : allElements) {
            contentsNames.add(element.getName());
        }

        return new InlineStructureTypeContentsCommand((IXSDModelRoot) element.getModelRoot(), element);
    }

}
