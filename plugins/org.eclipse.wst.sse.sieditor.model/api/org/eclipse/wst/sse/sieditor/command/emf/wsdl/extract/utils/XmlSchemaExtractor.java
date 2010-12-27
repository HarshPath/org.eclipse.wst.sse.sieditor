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
package org.eclipse.wst.sse.sieditor.command.emf.wsdl.extract.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.xsd.util.XSDConstants;
import org.eclipse.xsd.util.XSDResourceImpl;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.eclipse.wst.sse.sieditor.model.utils.ElementAttributeUtils;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

public class XmlSchemaExtractor implements IXmlSchemaExtractor {

    private static final XmlSchemaExtractor INSTANCE = new XmlSchemaExtractor();

    protected XmlSchemaExtractor() {

    }

    public static XmlSchemaExtractor instance() {
        return INSTANCE;
    }

    @Override
    public void extractSchema(final IFile schemaFile, final ISchema schema, final Map<String, String> filenamesMap,
            final IPath wsdlLocationPath, final String wsdlEncoding) throws IOException, CoreException {

        final IPath wsdlPathSimple = locationUtils().getLocationRelativeToWorkspace(wsdlLocationPath);
        final IPath schemaPathSimple = locationUtils().getLocationRelativeToWorkspace(schemaFile.getFullPath());

        final String relativeLocation = getWsdlToSchemaRelativeLocation(wsdlPathSimple, schemaPathSimple);

        final Element extractedSchemaElement = cloneSchemaElement(schema);
        updateSchemaContents(extractedSchemaElement, filenamesMap, relativeLocation);
        updateSchemaPrefixNamespaces(extractedSchemaElement, getPrefixToNamespaceMap(schema));
        serializeExtractedSchema(extractedSchemaElement, schemaFile, wsdlEncoding);
    }

    protected String getWsdlToSchemaRelativeLocation(final IPath schemaLocation, final IPath wsdlLocationPath) {
        return locationUtils().getSchemaToWsdlRelativeLocation(schemaLocation, wsdlLocationPath);
    }

    protected Map<String, String> getPrefixToNamespaceMap(final ISchema schema) {
        return schema.getComponent().getQNamePrefixToNamespaceMap();
    }

    protected Element cloneSchemaElement(final ISchema schema) {
        return (Element) schema.getComponent().getElement().cloneNode(true);
    }

    protected void updateSchemaContents(final Element schemaElement, final Map<String, String> filenamesMap,
            final String relativeLocation) {
        final NodeList childNodes = schemaElement.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            final Node child = childNodes.item(i);

            if (child instanceof Element) {
                final Element element = (Element) child;
                processSchemaImport(filenamesMap, element, relativeLocation);
                processSchemaInclude(element, relativeLocation);
            }
        }
    }

    protected void processSchemaImport(final Map<String, String> filenamesMap, final Element element,
            final String relativeLocation) {
        final String elementSimpleName = element.getTagName().substring(element.getTagName().indexOf(':') + 1);
        if (XSDConstants.IMPORT_ELEMENT_TAG.equals(elementSimpleName)
                && ElementAttributeUtils.hasAttributeValue(element, XSDConstants.NAMESPACE_ATTRIBUTE)) {

            final String schemaLocation = element.getAttribute(XSDConstants.SCHEMALOCATION_ATTRIBUTE);
            if (schemaLocation == null) {
                // we are looking at a dependent import
                element.setAttribute(XSDConstants.SCHEMALOCATION_ATTRIBUTE, filenamesMap.get(element
                        .getAttribute(XSDConstants.NAMESPACE_ATTRIBUTE)));
            } else {
                element.setAttribute(XSDConstants.SCHEMALOCATION_ATTRIBUTE, new Path(relativeLocation).append(schemaLocation)
                        .toString());
            }
        }
    }

    protected void processSchemaInclude(final Element element, final String relativeLocation) {
        final String elementSimpleName = element.getTagName().substring(element.getTagName().indexOf(':') + 1);
        if (XSDConstants.INCLUDE_ELEMENT_TAG.equals(elementSimpleName)) {
            final String schemaLocation = element.getAttribute(XSDConstants.SCHEMALOCATION_ATTRIBUTE);
            element.setAttribute(XSDConstants.SCHEMALOCATION_ATTRIBUTE, new Path(relativeLocation).append(schemaLocation)
                    .toString());
        }
    }

    protected void updateSchemaPrefixNamespaces(final Element schemaElement, final Map<String, String> prefixesMap) {
        for (final String prefix : prefixesMap.keySet()) {
            String attributeName = EmfXsdUtils.XMLNS_PREFIX;
            if (prefix != null) {
                attributeName = attributeName + ":" + prefix; //$NON-NLS-1$
            }
            if (!ElementAttributeUtils.hasAttributeValue(schemaElement, attributeName)) {
                schemaElement.setAttribute(attributeName, prefixesMap.get(prefix));
            }
        }
    }

    /**
     * Workaround method. Method for serializing the given schema to file. <br>
     * This method serializes the schema to string and removes the "&amp;#13;"
     * leftover symbols.
     * 
     * @param schemaElement
     * @param iFile
     * @param wsdlEncoding
     * @throws IOException
     * @throws FileNotFoundException
     * @throws CoreException
     */
    protected void serializeExtractedSchema(final Element schemaElement, final IFile iFile, final String wsdlEncoding)
            throws IOException, FileNotFoundException, CoreException {

        String extractedSchemaString = ""; //$NON-NLS-1$
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            XSDResourceImpl.serialize(byteArrayOutputStream, schemaElement, wsdlEncoding);
            extractedSchemaString = (byteArrayOutputStream).toString("UTF-8").replaceAll("&#13;", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        } finally {
            if (byteArrayOutputStream != null) {
                byteArrayOutputStream.close();
            }
        }

        FileOutputStream fileOutputStream = null;
        try {
            final File file = new File(iFile.getRawLocation().toString());
            file.getParentFile().mkdirs();
            file.createNewFile();
            fileOutputStream = new FileOutputStream(file);

            final byte[] bytes = extractedSchemaString.getBytes("UTF-8"); //$NON-NLS-1$
            fileOutputStream.write(bytes);

        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }

        iFile.refreshLocal(IFile.DEPTH_INFINITE, null);
    }

    // =========================================================
    // helpers
    // =========================================================

    protected SchemaLocationUtils locationUtils() {
        return SchemaLocationUtils.instance();
    }
}
