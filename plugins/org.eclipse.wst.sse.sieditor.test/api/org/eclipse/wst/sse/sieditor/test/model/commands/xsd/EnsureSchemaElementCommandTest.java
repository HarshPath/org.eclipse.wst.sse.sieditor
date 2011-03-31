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

import java.io.IOException;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.command.emf.xsd.EnsureSchemaElementCommand;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.test.model.commands.AbstractXSDCommandTest;

public class EnsureSchemaElementCommandTest extends AbstractXSDCommandTest {

    private static final String TARGET_NAMESPACE = "http://www.example.org/empty/";
    private static final String TNS_PREFIX = "tns";
    private static final String EMPTY_XSD = "empty.xsd";
    private static final String EMPTY_WSDLS_DIR = "pub/csns/badContent/";

    @Override
    protected void assertPostRedoState(IStatus redoStatus, IXSDModelRoot modelRoot) {
        assertNotNull(modelRoot.getSchema());
        XSDSchema xsdSchema = modelRoot.getSchema().getComponent();
        assertNotNull(xsdSchema);
        assertNotNull(xsdSchema.getElement());
        // DOM checks
        Document domDoc = xsdSchema.getDocument();
        assertNotNull(domDoc);
        Node firstDomElement = domDoc.getFirstChild();
        assertNotNull(firstDomElement);
        assertEquals(Node.PROCESSING_INSTRUCTION_NODE, firstDomElement.getNodeType());
        assertEquals(EmfXsdUtils.XML_TAG_NAME, ((ProcessingInstruction) firstDomElement).getTarget());

        // model checks
        assertEquals(TARGET_NAMESPACE, xsdSchema.getTargetNamespace());
        assertEquals(EmfXsdUtils.getSchemaForSchemaNS(), xsdSchema.getSchemaForSchemaNamespace());

        Map<String, String> namespaces = xsdSchema.getQNamePrefixToNamespaceMap();
        assertEquals(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, namespaces.get(null));
        assertEquals(TARGET_NAMESPACE, namespaces.get(TNS_PREFIX));
    }

    @Override
    protected void assertPostUndoState(IStatus undoStatus, IXSDModelRoot modelRoot) {
        assertNotNull(modelRoot.getSchema());
        XSDSchema xsdSchema = modelRoot.getSchema().getComponent();
        assertNotNull(xsdSchema);
        assertNull(xsdSchema.getElement());
        // DOM checks
        Document domDoc = xsdSchema.getDocument();
        assertNotNull(domDoc);
        assertEquals(0, domDoc.getChildNodes().getLength());
        // model checks
        assertNull(xsdSchema.getTargetNamespace());
        // these checks probably expose bugs in the Eclipse EMF api -
        // even after the DOM model does not contain a <schema> element there
        // are
        // cashes for it's namespace attributes available
        // assertNull(xsdSchema.getSchemaForSchemaNamespace());
        // assertNull(xsdSchema.getSchemaForSchemaQNamePrefix());
        // Map<String, String> namespaces =
        // xsdSchema.getQNamePrefixToNamespaceMap();
        // assertNull(namespaces.get(null));
        // assertNull(namespaces.get(TNS_PREFIX));
    }

    @Override
    protected AbstractNotificationOperation getOperation(IXSDModelRoot modelRoot) throws Exception {
        return new EnsureSchemaElementCommand(modelRoot.getSchema(), "testEnsureOperation");
    }

    @Override
    protected String getXSDFilename() {
        return EMPTY_XSD;
    }

    @Override
    protected String getXSDFoldername() {
        return EMPTY_WSDLS_DIR;
    }

    @Override
    protected IXSDModelRoot getXSDModelRoot(final String fileName, final String targetFileName) throws IOException,
            CoreException {
        return getXSDModelRootViaDocumentProvider(fileName, targetFileName);
    }

}
