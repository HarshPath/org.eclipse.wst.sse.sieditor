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

import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.ELEM_XSD_SCHEMA;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.LOCATION_ATTRIBUTE_IN_WSDL_IMPORT_CAN_NOT_BE_EMPTY;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.NAMESPACE_ATTRIBUTE_IN_WSDL_IMPORT_MUST_BE_NOT_RELATIVE;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.NAMESPACE_IN_SOME_WSDLIMPORT_IS_NOT_EQUAL_LIKE_DEFINITION_TARGETNAMESPACE;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.THE_SCHEMAS_IN_THE_TYPES_ELEMENT_SHOULD_USE_THE_RECOMENDED_NAMESPACE;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.XML;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.XML_NAMESPACE_SHOULD_NOT_BE_CONTAINED_IN_DESCRIPTION;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;

import javax.wsdl.extensions.schema.Schema;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintStatus;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.ExtensibilityElement;
import org.eclipse.wst.wsdl.Import;
import org.eclipse.wst.wsdl.Types;
import org.eclipse.xsd.util.XSDConstants;

import org.eclipse.wst.sse.sieditor.model.validation.constraints.AbstractConstraint;

public class WSIDocumentStructureCompliant extends AbstractConstraint {

    private Definition definition;

    @Override
    protected boolean shouldExecute(IValidationContext ctx) {
        return true;
    }

    @Override
    protected IStatus doValidate(IValidationContext validationContext) {
        this.definition = (Definition) validationContext.getTarget();

        Collection<IStatus> statuses = new HashSet<IStatus>();

        checkTheContentOfTheTypesElement(validationContext, statuses);

        checkForXMLNamespace(validationContext, statuses);

        checkTheCorrectionOfTheWSDLImports(validationContext, statuses);

        if (!statuses.isEmpty()) {
            return ConstraintStatus.createMultiStatus(validationContext, statuses);
        } else {
            return ConstraintStatus.createSuccessStatus(validationContext, this.definition, null);
        }
    }

    /**
     * R2801 A DESCRIPTION MUST use XML Schema 1.0
     * (URI=\"http://www.w3.org/2001/XMLSchema\""). Recommendation as the basis
     * of user defined data types and structures.
     * 
     * @param validationContext
     *            is the parameter from the doValidate(..) method
     * @param statuses
     *            is the list of statuses which will be returned from the
     *            doValidate(..) method
     */
    @SuppressWarnings("unchecked")
    private void checkTheContentOfTheTypesElement(IValidationContext validationContext, Collection<IStatus> statuses) {
        Types typesElement = this.definition.getETypes();
        if (typesElement != null) {
            EList theChildsOfTheTypesElement = typesElement.getEExtensibilityElements();
            if (theChildsOfTheTypesElement != null) {
                for (ExtensibilityElement element : (Collection<ExtensibilityElement>) theChildsOfTheTypesElement) {
                    if (!(element instanceof Schema)) {
                        continue;
                    }
                    if (element.getElementType().equals(ELEM_XSD_SCHEMA)) {
                        continue;
                    }
                    statuses.add(ConstraintStatus.createStatus(validationContext, element, null,
                            THE_SCHEMAS_IN_THE_TYPES_ELEMENT_SHOULD_USE_THE_RECOMENDED_NAMESPACE,
                            THE_SCHEMAS_IN_THE_TYPES_ELEMENT_SHOULD_USE_THE_RECOMENDED_NAMESPACE));
                }
            }
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
    private void checkForXMLNamespace(IValidationContext validationContext, Collection<IStatus> statuses) {
        String xmlNs = (String) this.definition.getNamespaces().get(XML);
        // If it exists and equals to "http://www.w3.org/XML/1998/namespace"
        if (xmlNs != null && xmlNs.equals(XSDConstants.XML_NAMESPACE_URI_1998)) {
            statuses.add(ConstraintStatus.createStatus(validationContext, this.definition, null,
                    XML_NAMESPACE_SHOULD_NOT_BE_CONTAINED_IN_DESCRIPTION, XML_NAMESPACE_SHOULD_NOT_BE_CONTAINED_IN_DESCRIPTION));
        }
    }

    @SuppressWarnings("unchecked")
    private void checkTheCorrectionOfTheWSDLImports(IValidationContext validationContext, Collection<IStatus> statuses) {
        String targetNamespace = this.definition.getTargetNamespace();
        if (targetNamespace != null) {
            Collection<Import> imports = (Collection<Import>) this.definition.getEImports();
            if (imports != null) {
                for (Import currentImport : imports) {
                    if (currentImport == null) {
                        continue;
                    }

                    checkForNullAndEmptyLocationAttributeInWSDLImport(validationContext, statuses, currentImport);
                    checkForRelatveNamespaceInWSDLImport(validationContext, statuses, currentImport);
                    checkTheConsistentOfTheDefinitionTargetNamespaceAndWSDLImportNamespace(validationContext, statuses,
                            targetNamespace, currentImport);
                }
            }
        }
    }

    /**
     * R2005 The targetNamespace attribute on the wsdl:definitions element of a
     * description that is being imported MUST have same the value as the
     * namespace attribute on the wsdl:import element in the importing
     * DESCRIPTION.
     * 
     * @param validationContext
     *            is the parameter from the doValidate(..) method
     * @param statuses
     *            is the list of statuses which will be returned from the
     *            doValidate(..) method
     * @param targetNamespace
     *            is the targetNamespace of the WSDL
     * @param currentImport
     *            is the wsdl:import
     */
    private void checkTheConsistentOfTheDefinitionTargetNamespaceAndWSDLImportNamespace(IValidationContext validationContext,
            Collection<IStatus> statuses, String targetNamespace, Import currentImport) {
        if (targetNamespace.equals(currentImport.getNamespaceURI())) {
            return;
        }

        statuses.add(ConstraintStatus.createStatus(validationContext, currentImport, null,
                NAMESPACE_IN_SOME_WSDLIMPORT_IS_NOT_EQUAL_LIKE_DEFINITION_TARGETNAMESPACE,
                NAMESPACE_IN_SOME_WSDLIMPORT_IS_NOT_EQUAL_LIKE_DEFINITION_TARGETNAMESPACE));
    }

    /**
     * R2007 A DESCRIPTION MUST specify a non-empty location attribute on the
     * wsdl:import element.
     * 
     * @param validationContext
     *            is the parameter from the doValidate(..) method
     * @param statuses
     *            is the list of statuses which will be returned from the
     *            doValidate(..) method
     * @param currentImport
     *            is the wsdl:import
     */
    private void checkForNullAndEmptyLocationAttributeInWSDLImport(IValidationContext validationContext,
            Collection<IStatus> statuses, Import currentImport) {
        String locationURIForCurrentImport = currentImport.getLocationURI();
        if (locationURIForCurrentImport == null || "".equals(locationURIForCurrentImport)) { //$NON-NLS-1$
            statuses.add(ConstraintStatus.createStatus(validationContext, currentImport, null,
                    LOCATION_ATTRIBUTE_IN_WSDL_IMPORT_CAN_NOT_BE_EMPTY, LOCATION_ATTRIBUTE_IN_WSDL_IMPORT_CAN_NOT_BE_EMPTY));
        }
    }

    /**
     * R2803 In a DESCRIPTION, the namespace attribute of the wsdl:import MUST
     * NOT be a relative URI.
     * 
     * @param validationContext
     *            is the parameter from the doValidate(..) method
     * @param statuses
     *            is the list of statuses which will be returned from the
     *            doValidate(..) method
     * @param currentImport
     *            is the wsdl:import
     */
    private void checkForRelatveNamespaceInWSDLImport(IValidationContext validationContext, Collection<IStatus> statuses,
            Import currentImport) {
        String namespaceOfWSDLImport = currentImport.getNamespaceURI();
        if (namespaceOfWSDLImport != null) {
            try {
                URI locationUri = new URI(namespaceOfWSDLImport);
                if (!locationUri.isAbsolute()) {
                    addWarningStatusForNotAbsolutePath(validationContext, currentImport, statuses);
                }
            } catch (URISyntaxException e) {
                addWarningStatusForNotAbsolutePath(validationContext, currentImport, statuses);
            }
        }
    }

    private void addWarningStatusForNotAbsolutePath(IValidationContext validationContext, Import importElement,
            Collection<IStatus> statuses) {
        statuses
                .add(ConstraintStatus.createStatus(validationContext, importElement, null,
                        NAMESPACE_ATTRIBUTE_IN_WSDL_IMPORT_MUST_BE_NOT_RELATIVE,
                        NAMESPACE_ATTRIBUTE_IN_WSDL_IMPORT_MUST_BE_NOT_RELATIVE));
    }
}
