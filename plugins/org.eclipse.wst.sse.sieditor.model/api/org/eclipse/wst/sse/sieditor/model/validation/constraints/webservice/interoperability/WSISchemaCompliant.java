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
 *    Richard Birenheide - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability;

import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.NAMESPACE_FOR_IMPORTED_SCHEMA_MUST_NOT_BE_RELATIVE;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.THE_DOCUMENT_DOESNOT_HAVE_APPROPRIATE_VERSION_AND_ENCODING;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.UTF_16;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.UTF_8;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.VERSION;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.XML;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.XML_NAMESPACE_SHOULD_NOT_BE_CONTAINED_IN_DESCRIPTION;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.versionAttribute;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintStatus;
import org.eclipse.wst.wsdl.util.WSDLConstants;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDImport;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;
import org.eclipse.xsd.impl.XSDImportImpl;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.eclipse.wst.sse.sieditor.model.validation.constraints.AbstractConstraint;

public class WSISchemaCompliant extends AbstractConstraint {

    private XSDSchema schema;

    @Override
    protected IStatus doValidate(IValidationContext validationContext) {
        this.schema = (XSDSchema) validationContext.getTarget();

        final Collection<IStatus> statuses = new HashSet<IStatus>();

        checkWhetherXMLNamespaceAppear(validationContext, statuses);

        Collection<XSDSchema> schemasForChecking = getImportedSchemasAndCheckForAbsolutePath(this.schema.getContents(), statuses,
                validationContext);

        checkEncodingAndVersionOfAllUsedSchemas(validationContext, statuses, schemasForChecking);

        if (statuses.isEmpty()) {
            return ConstraintStatus.createSuccessStatus(validationContext, this.schema, null);
        } else {
            return ConstraintStatus.createMultiStatus(validationContext, statuses);
        }
    }

    /**
     * R1034 A DESCRIPTION SHOULD NOT contain the namespace declaration
     * xmlns:xml="http://www.w3.org/XML/1998/namespace".
     * 
     * @param validationContext
     *            is the parameter from the doValidate(..) method
     * @param statuses
     *            is the list of statuses which will be returned from the
     *            doValidate(..) method
     */
    private void checkWhetherXMLNamespaceAppear(IValidationContext validationContext, final Collection<IStatus> statuses) {
        String xmlnsAttributeValue = this.schema.getElement().getAttributeNS(XSDConstants.XMLNS_URI_2000, XML);
        if (XSDConstants.XML_NAMESPACE_URI_1998.equals(xmlnsAttributeValue)) {
            statuses.add(ConstraintStatus.createStatus(validationContext, this.schema, null,
                    XML_NAMESPACE_SHOULD_NOT_BE_CONTAINED_IN_DESCRIPTION, XML_NAMESPACE_SHOULD_NOT_BE_CONTAINED_IN_DESCRIPTION));
        }
    }

    /**
     * R2010 An XML Schema directly or indirectly imported by a DESCRIPTION MUST
     * use either UTF-8 or UTF-16 encoding.
     * 
     * R2011 An XML Schema directly or indirectly imported by a DESCRIPTION MUST
     * use version 1.0 of the eXtensible Markup Language W3C Recommendation.
     * 
     * R4004 A DESCRIPTION MUST use version 1.0 of the eXtensible Markup
     * Language W3C Recommendation.
     * 
     * @param validationContext
     *            is the parameter from the doValidate(..) method
     * @param statuses
     *            is the list of statuses which will be returned from the
     *            doValidate(..) method
     * 
     * @param schemasForChecking
     *            is a Collection of all imported schemas in the this.schema
     */
    private void checkEncodingAndVersionOfAllUsedSchemas(IValidationContext validationContext,
            final Collection<IStatus> statuses, Collection<XSDSchema> schemasForChecking) {
        for (XSDSchema currentXsdSchema : schemasForChecking) {
            if (currentXsdSchema == null) {
                continue;
            }
            Document document = currentXsdSchema.getDocument();
            if (document == null) {
                continue;
            }
            Node node = document.getFirstChild();
            if (node != null) {
                statuses.add(getStatusAccordingToXMLEncodingAndVersion(validationContext, currentXsdSchema, node
                        .getOwnerDocument()));
            } else {
                statuses.add(createStatusForInappropriateXMLVersionOrEncoding(validationContext));
            }
        }

    }

    private IStatus getStatusAccordingToXMLEncodingAndVersion(IValidationContext validationContext, XSDSchema currentXsdSchema,
            Document ownerDocument) {
        if (ownerDocument == null) {
            return createStatusForInappropriateXMLVersionOrEncoding(validationContext);
        }
        String encoding = null;
        try {
            encoding = ownerDocument.getXmlEncoding();
        } catch (DOMException exception) {
            IStatus status = handleDomException(validationContext, ownerDocument);
            return status;
        }
        if (encoding == null || !(encoding.equalsIgnoreCase(UTF_8) || encoding.equalsIgnoreCase(UTF_16))) {
            return createStatusForInappropriateXMLVersionOrEncoding(validationContext);
        }

        String actualXMLVersion = null;
        try {
            actualXMLVersion = ownerDocument.getXmlVersion();
        } catch (DOMException exception) {
            IStatus status = handleDomException(validationContext, ownerDocument);
            return status;
        }
        if (actualXMLVersion == null || !actualXMLVersion.equalsIgnoreCase(VERSION)) {
            return createStatusForInappropriateXMLVersionOrEncoding(validationContext);
        }
        return ConstraintStatus.createSuccessStatus(validationContext, this.schema, null);
    }

    private IStatus handleDomException(IValidationContext validationContext, Document ownerDocument) {
        Node node = ownerDocument.getFirstChild();
        if (node != null) {
            return statusForEncodingAndVersion(node.getNodeValue(), validationContext);
        } else {
            return ConstraintStatus.createSuccessStatus(validationContext, this.schema, null);
        }
    }

    private IStatus statusForEncodingAndVersion(String aValueOfXMLProcessInstruction, IValidationContext validationContext) {
        if (aValueOfXMLProcessInstruction == null) {
            return createStatusForInappropriateXMLVersionOrEncoding(validationContext);
        }
        if (!((aValueOfXMLProcessInstruction.contains(WSDLConstants.ENCODING_ATTRIBUTE) && (aValueOfXMLProcessInstruction
                .contains(UTF_8) || aValueOfXMLProcessInstruction.contains(UTF_16))))) {
            return createStatusForInappropriateXMLVersionOrEncoding(validationContext);
        }
        if (!(aValueOfXMLProcessInstruction.contains(versionAttribute) && aValueOfXMLProcessInstruction.contains(VERSION))) {
            return createStatusForInappropriateXMLVersionOrEncoding(validationContext);
        }

        return ConstraintStatus.createSuccessStatus(validationContext, this.schema, null);

    }

    private IStatus createStatusForInappropriateXMLVersionOrEncoding(IValidationContext validationContext) {
        return ConstraintStatus.createStatus(validationContext, this.schema, null,
                THE_DOCUMENT_DOESNOT_HAVE_APPROPRIATE_VERSION_AND_ENCODING,
                THE_DOCUMENT_DOESNOT_HAVE_APPROPRIATE_VERSION_AND_ENCODING);
    }

    public static final Collection<XSDSchema> getImportedSchemasAndCheckForAbsolutePath(
            final Collection<? extends XSDSchemaContent> components, final Collection<IStatus> statuses,
            IValidationContext validationContext) {
        final Collection<XSDSchema> importedSchemas = new HashSet<XSDSchema>();
        for (final XSDConcreteComponent component : components) {
            if (!(component instanceof XSDImportImpl)) {
                continue;
            }
            XSDImportImpl xsdImport = (XSDImportImpl) component;

            checkWhetherTheNamespaceAttributeHasAbsolutePath(statuses, validationContext, xsdImport);

            if (xsdImport.getSchemaLocation() != null) {
                importedSchemas.add(xsdImport.importSchema());
            }

        }

        return importedSchemas;
    }

    /**
     * The namespace attribute of the xsd:import MUST NOT be a relative URI
     * 
     * @param validationContext
     *            is the parameter from the doValidate(..) method
     * @param statuses
     *            is the list of statuses which will be returned from the
     *            doValidate(..) method
     * @param xsdImport
     *            is a not null xsd:import EMF object
     */
    private static void checkWhetherTheNamespaceAttributeHasAbsolutePath(final Collection<IStatus> statuses,
            IValidationContext validationContext, XSDImport xsdImport) {
        try {
            String namespace = xsdImport.getNamespace();
            if (namespace == null) {
                return;
            }
            URI uri = new URI(namespace);
            if (!uri.isAbsolute()) {
                createStatusForNonAbsoluteNamespaceAttribute(statuses, validationContext, xsdImport);
            }
        } catch (URISyntaxException e) {
            createStatusForNonAbsoluteNamespaceAttribute(statuses, validationContext, xsdImport);
        }
    }

    private static void createStatusForNonAbsoluteNamespaceAttribute(final Collection<IStatus> statuses,
            IValidationContext validationContext, XSDImport xsdImport) {
        statuses.add(ConstraintStatus.createStatus(validationContext, xsdImport, null,
                NAMESPACE_FOR_IMPORTED_SCHEMA_MUST_NOT_BE_RELATIVE, NAMESPACE_FOR_IMPORTED_SCHEMA_MUST_NOT_BE_RELATIVE));
    }

    @Override
    protected boolean shouldExecute(IValidationContext ctx) {
        return true;
    }

}
