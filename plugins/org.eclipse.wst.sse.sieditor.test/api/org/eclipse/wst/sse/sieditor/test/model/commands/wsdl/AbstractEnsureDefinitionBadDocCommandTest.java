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
package org.eclipse.wst.sse.sieditor.test.model.commands.wsdl;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractCommandTest;
import org.eclipse.wst.wsdl.Definition;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.eclipse.wst.sse.sieditor.command.emf.wsdl.AbstractCompositeEnsuringDefinitionNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;

public abstract class AbstractEnsureDefinitionBadDocCommandTest extends AbstractCommandTest {

    private static final String JIBERISH_WSDL = "jiberish.wsdl";//$NON-NLS-0$
    private static final String EMPTY_WSDLS_DIR = "pub/csns/badContent/";//$NON-NLS-0$

    public AbstractEnsureDefinitionBadDocCommandTest() {
        super();
    }

    /**
     * Add a call to the default implementation of this method in the beginning
     * of the one overriding it
     */
    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        final IDescription description = modelRoot.getDescription();
        final Definition component = description.getComponent();
        final Element elementFromComponent = component.getElement();
        assertNull(elementFromComponent);
        assertNull(component.getDocument().getDocumentElement());
        assertEquals(1, component.getDocument().getChildNodes().getLength());
        assertEquals(Node.TEXT_NODE, component.getDocument().getFirstChild().getNodeType());
    }

    /**
     * Add a call to the default implementation of this method in the beginning
     * of the one overriding it
     */
    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        final Element rootElement = modelRoot.getDescription().getComponent().getElement();
        assertEquals(Node.ELEMENT_NODE, rootElement.getNodeType());
        assertEquals("wsdl:definitions", rootElement.getTagName());////$NON-NLS-0$
    }

    @Override
    protected String getWsdlFilename() {
        return JIBERISH_WSDL;
    }

    @Override
    protected String getWsdlFoldername() {
        return EMPTY_WSDLS_DIR;
    }

    @SuppressWarnings("restriction")
    @Override
    protected IWsdlModelRoot getWSDLModelRoot(final String fileName, final String targetFileName) throws IOException,
            CoreException {
        return getWsdlModelRootViaDocumentProvider(fileName, targetFileName);
    }

    // predefine the abstract parent operation to return the specific type of
    // command
    @Override
    abstract protected AbstractCompositeEnsuringDefinitionNotificationOperation getOperation(IWsdlModelRoot modelRoot)
            throws Exception;
}