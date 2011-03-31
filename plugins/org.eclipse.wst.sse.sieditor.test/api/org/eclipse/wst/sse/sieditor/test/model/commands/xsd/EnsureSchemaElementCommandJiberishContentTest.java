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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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

public class EnsureSchemaElementCommandJiberishContentTest extends AbstractXSDCommandTest {

    private static final String TARGET_NAMESPACE = "http://www.example.org/jiberish/";
    private static final String TNS_PREFIX = "tns";
    private static final String JIBERISH_XSD = "jiberish.xsd";
    private static final String EMPTY_XSD_DIR = "pub/csns/badContent/";
    private String initialContent;

    @Override
    protected void assertPostRedoState(final IStatus redoStatus, final IXSDModelRoot modelRoot) {
        assertNotNull(modelRoot.getSchema());
        final XSDSchema xsdSchema = modelRoot.getSchema().getComponent();
        assertNotNull(xsdSchema);
        assertNotNull(xsdSchema.getElement());
        // DOM checks
        final Document domDoc = xsdSchema.getDocument();
        assertNotNull(domDoc);
        final Node firstDomElement = domDoc.getFirstChild();
        assertNotNull(firstDomElement);
        assertEquals(Node.PROCESSING_INSTRUCTION_NODE, firstDomElement.getNodeType());
        assertEquals(EmfXsdUtils.XML_TAG_NAME, ((ProcessingInstruction) firstDomElement).getTarget());

        // skip all text nodes after t he first child - since the dom could
        // fragment text block in a couple of elements
        Node thirdElement = firstDomElement.getNextSibling();
        while (Node.TEXT_NODE == ((thirdElement = thirdElement.getNextSibling()).getNodeType())) {
            assertTrue(initialContent.contains(thirdElement.getNodeValue()));
        }
        assertNotSame(thirdElement, firstDomElement.getNextSibling());

        assertNotNull(thirdElement);
        assertEquals(Node.ELEMENT_NODE, thirdElement.getNodeType());
        assertEquals("schema", thirdElement.getNodeName());

        // model checks

        assertEquals(TARGET_NAMESPACE, xsdSchema.getTargetNamespace());
        assertEquals(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, xsdSchema.getSchemaForSchemaNamespace());

        final Map<String, String> namespaces = xsdSchema.getQNamePrefixToNamespaceMap();
        assertEquals(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, namespaces.get(null));
        assertEquals(TARGET_NAMESPACE, namespaces.get(TNS_PREFIX));
    }

    @Override
    protected void assertPostUndoState(final IStatus undoStatus, final IXSDModelRoot modelRoot) {
        assertNotNull(modelRoot.getSchema());
        final XSDSchema xsdSchema = modelRoot.getSchema().getComponent();
        assertNotNull(xsdSchema);
        assertNull(xsdSchema.getElement());
        // DOM checks
        final Document domDoc = xsdSchema.getDocument();
        assertNotNull(domDoc);
        assertEquals(1, domDoc.getChildNodes().getLength());
        assertEquals(Node.TEXT_NODE, domDoc.getFirstChild().getNodeType());
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
        assertEquals(initialContent, extractFileContent());
    }

    @Override
    protected AbstractNotificationOperation getOperation(final IXSDModelRoot modelRoot) throws Exception {
        // extract the initial content from the file
        initialContent = extractFileContent();
        return new EnsureSchemaElementCommand(modelRoot.getSchema(), "testEnsureOperation");
    }

    private String extractFileContent() {
        final StringBuffer initContent = new StringBuffer();
        InputStreamReader reader;
        try {
            reader = new InputStreamReader(new FileInputStream(getProject().getFolder(Document_FOLDER_NAME).getFile(JIBERISH_XSD)
                    .getRawLocation().toOSString()));

            char ch;
            while ((ch = (char) reader.read()) != ((char) -1)) {
                initContent.append(ch);
            }
        } catch (final IOException e) {
            fail(e.toString());
        }
        return initContent.toString();
    }

    @Override
    protected String getXSDFilename() {
        return JIBERISH_XSD;
    }

    @Override
    protected String getXSDFoldername() {
        return EMPTY_XSD_DIR;
    }

    @SuppressWarnings("restriction")
    @Override
    protected IXSDModelRoot getXSDModelRoot(final String fileName, final String targetFileName) throws IOException, CoreException {
        return getXSDModelRootViaDocumentProvider(fileName, targetFileName);
    }
}
