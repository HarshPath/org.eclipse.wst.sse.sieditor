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
import org.eclipse.wst.wsdl.util.WSDLConstants;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.wsdl.EnsureDefinitionCommand;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;

public class EnsureWsdlDefinitionCommandTest extends AbstractCommandTest {

    private static final String WSDL_PREFIX = "wsdl";
    private static final String TARGET_NAMESPACE = "http://www.example.org/empty/";
    private static final String TNS_PREFIX = "tns";
    private static final String XSD_PREFIX = "xsd";
    private static final String EMPTY_WSDL = "empty.wsdl";
    private static final String EMPTY_WSDLS_DIR = "pub/csns/badContent/";

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IWsdlModelRoot modelRoot) {
        assertNotNull(modelRoot.getDescription());
        final Definition definitions = modelRoot.getDescription().getComponent();
        assertNotNull(definitions);
        assertNotNull(definitions.getElement());
        // DOM checks
        final Document domDoc = definitions.getDocument();
        assertNotNull(domDoc);
        final Node firstDomElement = domDoc.getFirstChild();
        assertNotNull(firstDomElement);
        assertEquals(Node.PROCESSING_INSTRUCTION_NODE, firstDomElement.getNodeType());
        assertEquals(EmfXsdUtils.XML_TAG_NAME, ((ProcessingInstruction) firstDomElement).getTarget());

        // model checks
        assertEquals(TARGET_NAMESPACE, definitions.getTargetNamespace());
        assertEquals(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, definitions.getNamespace(XSD_PREFIX));
        assertEquals(TARGET_NAMESPACE, definitions.getNamespace(TNS_PREFIX));
        assertEquals(WSDLConstants.WSDL_NAMESPACE_URI, definitions.getNamespace(WSDL_PREFIX));
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IWsdlModelRoot modelRoot) {
        assertNotNull(modelRoot.getDescription());
        final Definition definitions = modelRoot.getDescription().getComponent();
        assertNotNull(definitions);
        assertNull(definitions.getElement());
        // DOM checks
        final Document domDoc = definitions.getDocument();
        assertNotNull(domDoc);
        assertEquals(0, domDoc.getChildNodes().getLength());
        // model checks
        assertNull(definitions.getTargetNamespace());
        assertNull(definitions.getNamespace(XSD_PREFIX));
        assertNull(definitions.getNamespace(TNS_PREFIX));
        assertNull(definitions.getNamespace(WSDL_PREFIX));
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IWsdlModelRoot modelRoot) throws Exception {
        return new EnsureDefinitionCommand(modelRoot, modelRoot.getDescription(), "testEnsureOperation");
    }

    @Override
    protected String getWsdlFilename() {
        return EMPTY_WSDL;
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

}
