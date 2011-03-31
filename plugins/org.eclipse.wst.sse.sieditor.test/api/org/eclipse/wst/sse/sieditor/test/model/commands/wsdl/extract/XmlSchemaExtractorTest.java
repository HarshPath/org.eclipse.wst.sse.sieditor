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
package org.eclipse.wst.sse.sieditor.test.model.commands.wsdl.extract;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.xml.core.internal.document.NodeListImpl;
import org.eclipse.xsd.util.XSDConstants;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.utils.XmlSchemaExtractor;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

public class XmlSchemaExtractorTest {

    private static final IPath TEST_WSDL_LOCATION = new Path("/test/wsdlLocation/TestWSDL.wsdl");

    // =========================================================
    // test case 1: test methods execution
    // =========================================================

    @Test
    public void extractSchema() throws CoreException, IOException {

        final boolean called[] = { false, false, false, false };

        final Element elementMock = createNiceMock(Element.class);

        final IFile iFileMock = createMock(IFile.class);
        final IPath schemaLocationMock = createNiceMock(IPath.class);
        expect(iFileMock.getFullPath()).andReturn(schemaLocationMock);
        expect(schemaLocationMock.getDevice()).andReturn(null);

        replay(iFileMock, schemaLocationMock);

        final Map<String, String> mapMock = new HashMap<String, String>();
        final ISchema schemaMock = createMock(ISchema.class);
        final Map<String, String> prefixesMapMock = new HashMap<String, String>();

        final XmlSchemaExtractor extractor = new XmlSchemaExtractor() {

            @Override
            protected Element cloneSchemaElement(final ISchema schema) {
                called[0] = true;
                return elementMock;
            }

            @Override
            protected Map<String, String> getPrefixToNamespaceMap(final ISchema schema) {
                return prefixesMapMock;
            }

            @Override
            protected void updateSchemaContents(final Element schemaElement, final Map<String, String> filenamesMap,
                    final String relativeLocation) {
                assertSame(elementMock, schemaElement);
                assertSame(mapMock, filenamesMap);
                called[1] = true;
            }

            @Override
            protected String getWsdlToSchemaRelativeLocation(final IPath schemaLocation, final IPath wsdlLocationPath) {
                return "";
            }

            @Override
            protected void updateSchemaPrefixNamespaces(final Element schemaElement, final Map<String, String> prefixesMap) {
                assertSame(elementMock, schemaElement);
                assertSame(prefixesMapMock, prefixesMap);
                called[2] = true;
            }

            @Override
            protected void serializeExtractedSchema(final Element schemaElement, final IFile iFile, final String wsdlEncoding)
                    throws IOException, FileNotFoundException, CoreException {
                assertSame(elementMock, schemaElement);
                assertSame(iFileMock, iFile);
                called[3] = true;
            }
        };
        extractor.extractSchema(iFileMock, schemaMock, mapMock, TEST_WSDL_LOCATION, null);

        assertTrue("cloneSchemaElement was not called", called[0]);
        assertTrue("updateSchemaImports was not called", called[1]);
        assertTrue("updateSchemaPrefixNamespaces was not called", called[2]);
        assertTrue("serializeExtractedSchema was not called", called[3]);
    }

    // =========================================================
    // test case 2: test update schema imports
    // =========================================================

    private static final String LOCATION2 = "location2";
    private static final String HTTP_LOCAL_IMPORT2 = "http://localImport2";
    private static final String XSD_IMPORT = "xsd:import";
    private static final String HTTP_LOCAL_IMPORT1 = "http://localImport1";
    private static final String LOCATION1 = "location1";
    private static final String EXTERNAL_XSD = "external.xsd";
    private static final String EXTERNAL_INCLUDE_XSD = "external_include.xsd";
    private static final String RELATIVE_LOCATION = "..";

    @Test
    public void updateSchemaContents() {
        final Element elementMock = createMock(Element.class);
        final Map<String, String> mapMock = new HashMap<String, String>();

        final TestNodeListImpl nodeList = new TestNodeListImpl();
        expect(elementMock.getChildNodes()).andReturn(nodeList);

        final Element localImport1 = createMock(Element.class);
        expect(localImport1.getTagName()).andReturn(XSD_IMPORT).anyTimes();
        expect(localImport1.getAttribute(XSDConstants.NAMESPACE_ATTRIBUTE)).andReturn(HTTP_LOCAL_IMPORT1).anyTimes();
        localImport1.setAttribute(XSDConstants.SCHEMALOCATION_ATTRIBUTE, LOCATION1);
        expect(localImport1.getAttribute(XSDConstants.SCHEMALOCATION_ATTRIBUTE)).andReturn(null).anyTimes();

        final Element localImport2 = createMock(Element.class);
        expect(localImport2.getTagName()).andReturn(XSD_IMPORT).anyTimes();
        expect(localImport2.getAttribute(XSDConstants.NAMESPACE_ATTRIBUTE)).andReturn(HTTP_LOCAL_IMPORT2).anyTimes();
        localImport2.setAttribute(XSDConstants.SCHEMALOCATION_ATTRIBUTE, LOCATION2);
        expect(localImport2.getAttribute(XSDConstants.SCHEMALOCATION_ATTRIBUTE)).andReturn(null).anyTimes();

        final Element externalImport1 = createMock(Element.class);
        expect(externalImport1.getTagName()).andReturn(XSD_IMPORT).anyTimes();
        expect(externalImport1.getAttribute(XSDConstants.NAMESPACE_ATTRIBUTE)).andReturn("http://exteralNamespace1").anyTimes();
        expect(externalImport1.getAttribute(XSDConstants.SCHEMALOCATION_ATTRIBUTE)).andReturn(EXTERNAL_XSD).anyTimes();
        externalImport1.setAttribute(XSDConstants.SCHEMALOCATION_ATTRIBUTE, RELATIVE_LOCATION + "/" + EXTERNAL_XSD);

        final Element nonImportElement = createMock(Element.class);
        expect(nonImportElement.getTagName()).andReturn("xsd:boom").anyTimes();

        final Element schemaIncludeElement = createMock(Element.class);
        expect(schemaIncludeElement.getTagName()).andReturn(XSDConstants.INCLUDE_ELEMENT_TAG).anyTimes();
        expect(schemaIncludeElement.getAttribute(XSDConstants.SCHEMALOCATION_ATTRIBUTE)).andReturn(EXTERNAL_INCLUDE_XSD).anyTimes();
        schemaIncludeElement.setAttribute(XSDConstants.SCHEMALOCATION_ATTRIBUTE, RELATIVE_LOCATION + "/" + EXTERNAL_INCLUDE_XSD);

        nodeList.appendNode(localImport1);
        nodeList.appendNode(localImport2);
        nodeList.appendNode(externalImport1);
        nodeList.appendNode(schemaIncludeElement);
        nodeList.appendNode(nonImportElement);

        mapMock.put(HTTP_LOCAL_IMPORT1, LOCATION1);
        mapMock.put(HTTP_LOCAL_IMPORT2, LOCATION2);
        mapMock.put("http://localImport4", "location4");

        replay(elementMock, localImport1, localImport2, externalImport1, nonImportElement, schemaIncludeElement);

        new XmlSchemaExtractor() {
            @Override
            public void updateSchemaContents(final Element schemaElement, final Map<String, String> filenamesMap,
                    final String relativeLocation) {
                super.updateSchemaContents(schemaElement, filenamesMap, relativeLocation);
            }
        }.updateSchemaContents(elementMock, mapMock, RELATIVE_LOCATION);

        verify(localImport1, localImport2, externalImport1, schemaIncludeElement);
    }

    // =========================================================
    // test case 3: test schema prefix namespaces update
    // =========================================================

    private static final String NS3 = "ns3";
    private static final String NS2 = "ns2";
    private static final String NS1 = "ns1";
    private static final String HTTP_NULL_PREFIX_VALUE = "http://null_prefix_value";
    private static final String HTTP_NS3_VALUE = "http://ns3_value";
    private static final String HTTP_NS2_VALUE = "http://ns2_value";
    private static final String HTTP_NS1_VALUE = "http://ns1_value";

    @Test
    public void updateSchemaPrefixNamespaces() {
        final Element elementMock = createMock(Element.class);
        final Map<String, String> prefixMapMock = new HashMap<String, String>();

        prefixMapMock.put(NS1, HTTP_NS1_VALUE);
        prefixMapMock.put(NS2, HTTP_NS2_VALUE);
        prefixMapMock.put(NS3, HTTP_NS3_VALUE);
        prefixMapMock.put(null, HTTP_NULL_PREFIX_VALUE);

        expect(elementMock.getAttribute(EmfXsdUtils.XMLNS_PREFIX + ":" + NS1)).andReturn(HTTP_NS1_VALUE).anyTimes();
        expect(elementMock.getAttribute(EmfXsdUtils.XMLNS_PREFIX + ":" + NS2)).andReturn(null).anyTimes();
        elementMock.setAttribute(EmfXsdUtils.XMLNS_PREFIX + ":" + NS2, HTTP_NS2_VALUE);
        expect(elementMock.getAttribute(EmfXsdUtils.XMLNS_PREFIX + ":" + NS3)).andReturn(null).anyTimes();
        elementMock.setAttribute(EmfXsdUtils.XMLNS_PREFIX + ":" + NS3, HTTP_NS3_VALUE);
        expect(elementMock.getAttribute(EmfXsdUtils.XMLNS_PREFIX)).andReturn(null).anyTimes();
        elementMock.setAttribute(EmfXsdUtils.XMLNS_PREFIX, HTTP_NULL_PREFIX_VALUE);

        replay(elementMock);

        new XmlSchemaExtractor() {
            @Override
            public void updateSchemaPrefixNamespaces(final Element schemaElement, final Map<String, String> filenamesMap) {
                super.updateSchemaPrefixNamespaces(schemaElement, filenamesMap);
            }
        }.updateSchemaPrefixNamespaces(elementMock, prefixMapMock);

        verify(elementMock);
    }

    // =========================================================
    // test case 4: test make relative location
    // =========================================================

    @Test
    public void getRelativeLocation() {
        String relativeLocation = new XmlSchemaExtractor() {
            @Override
            public String getWsdlToSchemaRelativeLocation(final IPath schemaLocation, final IPath wsdlLocationPath) {
                return super.getWsdlToSchemaRelativeLocation(schemaLocation, wsdlLocationPath);
            }
        }.getWsdlToSchemaRelativeLocation(new Path("platform://resource/test/a/b/test.xsd"), new Path(
                "platform://resource/test/a/b/c/test.wsdl"));
        assertEquals("..", relativeLocation);

        relativeLocation = new XmlSchemaExtractor() {
            @Override
            public String getWsdlToSchemaRelativeLocation(final IPath schemaLocation, final IPath wsdlLocationPath) {
                return super.getWsdlToSchemaRelativeLocation(schemaLocation, wsdlLocationPath);
            }
        }.getWsdlToSchemaRelativeLocation(new Path("platform://resource/test/a/b/c/test.wsdl"), new Path(
                "platform://resource/test/a/b/test.xsd"));
        assertEquals("c", relativeLocation);
    }

    // =========================================================
    // mocks
    // =========================================================

    private class TestNodeListImpl extends NodeListImpl {
        @Override
        public Node appendNode(final Node node) {
            return super.appendNode(node);
        }
    }

}
