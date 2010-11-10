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

import java.text.MessageFormat;

import javax.xml.namespace.QName;

import org.eclipse.wst.wsdl.util.WSDLConstants;
import org.eclipse.xsd.util.XSDConstants;

import org.eclipse.wst.sse.sieditor.model.i18n.Messages;

public class WSIConstants {
    public static final String STRING = "\""; //$NON-NLS-1$
    public static final String LESS_THAN = "<"; //$NON-NLS-1$
    public static final String GREATER_THAN = ">"; //$NON-NLS-1$
    public static final String TYPE = "type"; //$NON-NLS-1$
    public static final String RPC_LITERAL = "rpc-literal"; //$NON-NLS-1$
    public static final String DOCUMENT_LITERAL = "document-literal"; //$NON-NLS-1$
    public static final String WSDL_BINDING = "<wsdl:binding>"; //$NON-NLS-1$
    public static final String DESCRIPTION = "description"; //$NON-NLS-1$
    public static final String ELEMENT = "element"; //$NON-NLS-1$
    public static final String SOAP_FAULT = "<soap:fault>";//$NON-NLS-1$
    public static final String PARTS_ATTRIBUTE = "parts"; //$NON-NLS-1$

    public static final String HTTP_TRANSPORT = "http://schemas.xmlsoap.org/soap/http"; //$NON-NLS-1$
    public static final String XSD_PREFIX = "xsd:"; //$NON-NLS-1$
    public static final String UTF_8 = "UTF-8"; //$NON-NLS-1$
    public static final String UTF_16 = "UTF-16"; //$NON-NLS-1$
    public static final String VERSION = "1.0"; //$NON-NLS-1$
    public static final String RPC_STYLE = "rpc"; //$NON-NLS-1$
    public static final String DOCUMENT_STYLE = "document"; //$NON-NLS-1$ 
    public static final String LITERAL = "literal"; //$NON-NLS-1$
    public static final String XML = "xml"; //$NON-NLS-1$
    public static final String versionAttribute = "version"; //$NON-NLS-1$
    public final static QName ELEM_XSD_SCHEMA = new QName(XSDConstants.SCHEMA_FOR_SCHEMA_URI_2001, "schema"); //$NON-NLS-1$

    private WSIConstants() {
    }

    public static final String THE_PART_DOESNOT_HAVE_ELEMENT_ATTRIBUTE_IN_SOME_INPUT_MESSAGE = MessageFormat.format(
            Messages.WSIConstants_0, STRING + ELEMENT + STRING);

    public static final String THE_PART_DOESNOT_HAVE_ELEMENT_ATTRIBUTE_IN_SOME_OUTPUT_MESSAGE = MessageFormat.format(
            Messages.WSIConstants_1, STRING + ELEMENT + STRING);

    public static final String NO_SOAP_BINDING_MESSAGE = MessageFormat.format(Messages.WSIConstants_2, WSDL_BINDING, DESCRIPTION);

    public static final String BINDINGS_ARE_NOT_SPECIFIED_MESSAGE = MessageFormat.format(Messages.WSIConstants_33, LESS_THAN
            + WSDLConstants.TYPE_ATTRIBUTE + GREATER_THAN, WSDL_BINDING);

    public static final String INAPPROPRIATE_SOAP_BINDING = Messages.WSIConstants_3;

    public static final String INAPPROPRIATE_SOAP_BINDING_STYLE = MessageFormat.format(Messages.WSIConstants_4, WSDL_BINDING,
            DESCRIPTION, STRING + RPC_LITERAL + STRING, STRING + DOCUMENT_LITERAL + STRING);

    public static final String THE_VALUE_OF_USE_ATTRIBUTE_IN_SOAP_BODY_IS_NOT_LITERAL = MessageFormat.format(
            Messages.WSIConstants_6,
            "the <soapbind:body>, <soapbind:fault>, <soapbind:header>,", "<soapbind:headerfault>", "\"use\"", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            "\"literal\""); //$NON-NLS-1$

    public static final String THE_OPERATION_HAS_MORE_THAN_ONE_INPUT_PARAMETER = MessageFormat.format(Messages.WSIConstants_10,
            "\"Input\""); //$NON-NLS-1$

    public static final String THE_OPERATION_HAS_MORE_THAN_ONE_OUTPUT_PARAMETER = MessageFormat.format(Messages.WSIConstants_7,
            "\"Output\""); //$NON-NLS-1$

    public static final String MISSING_NAMESPACE_ATTRIBUTE_IN_SOAP_BODY = MessageFormat.format(Messages.WSIConstants_8,
            "\"namespace\"", "<soapbind:body>"); //$NON-NLS-1$ //$NON-NLS-2$

    public static final String THE_PART_DOESNOT_HAVE_TYPE_ATTRIBUTE_IN_SOME_INPUT_MESSAGE = MessageFormat.format(
            Messages.WSIConstants_11, STRING + TYPE + STRING);

    public static final String THE_PART_DOESNOT_HAVE_TYPE_ATTRIBUTE_IN_SOME_OUTPUT_MESSAGE = MessageFormat.format(
            Messages.WSIConstants_12, STRING + TYPE + STRING);

    public static final String NAMESPACE_ATTRIBUTE_ISNOT_ALLOWED_IN_SOAPBIND_ELEMENTS_WHEN_DOCUMENT_STYLE_IS_SPECIFIED = MessageFormat
            .format(Messages.WSIConstants_13,
                    "\"namespace\"", "<soapbind:body>, <soapbind:header>, <soapbind:headerfault>, <soapbind:fault>", //$NON-NLS-1$ //$NON-NLS-2$
                    "\"document\""); //$NON-NLS-1$

    public static final String INAPPROPRIATE_TRANSPORT_ATTRIBUTE_IN_SOME_SOAP_BINDING = MessageFormat.format(
            Messages.WSIConstants_15, "\"transport\"", "<soapbind:binding>", "HTTP"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    public static final String OPERATION_DOESNOT_HAVE_SIMILAR_BINDING_OPERATION = MessageFormat.format(Messages.WSIConstants_16,
            WSDL_BINDING);

    public static final String TRANSPORT_ATTRIBUTE_MISSING = MessageFormat.format(Messages.WSIConstants_14, "\"transport\"", //$NON-NLS-1$
            "<soapbind:binding>"); //$NON-NLS-1$

    public static final String WSDL_ARRAYTYPE_IS_NOT_ALLOWED_IN_XSDATTRIBUTE_ELEMENT = MessageFormat.format(
            Messages.WSIConstants_17, DESCRIPTION, "\"wsdl:arrayType\""); //$NON-NLS-1$

    public static final String NAMESPACE_IN_SOME_WSDLIMPORT_IS_NOT_EQUAL_LIKE_DEFINITION_TARGETNAMESPACE = MessageFormat.format(
            Messages.WSIConstants_18, "\"targetNamespace\"", "<wsdl:definitions>", "\"namespace\"", "<wsdl:import>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ 

    public static final String NAMESPACE_ATTRIBUTE_MUST_NOT_BE_SPECIFIED_IN_SOAP_FAULT_HEADER_FAULT_AND_HEADER = MessageFormat
            .format(Messages.WSIConstants_22, STRING + RPC_LITERAL + STRING, DESCRIPTION, "\"namespace\"", //$NON-NLS-1$ 
                    "<soapbind:header>, <soapbind:headerfault>", "<soapbind:fault>"); //$NON-NLS-1$ //$NON-NLS-2$

    public static final String THE_SCHEMAS_IN_THE_TYPES_ELEMENT_SHOULD_USE_THE_RECOMENDED_NAMESPACE = MessageFormat.format(
            Messages.WSIConstants_23, DESCRIPTION, "XML Schema 1.0");//$NON-NLS-1$

    public static final String LOCATION_ATTRIBUTE_IN_WSDL_IMPORT_CAN_NOT_BE_EMPTY = MessageFormat.format(
            Messages.WSIConstants_21, DESCRIPTION, "\"location\"", "<wsdl:import>"); //$NON-NLS-1$ //$NON-NLS-2$ 

    public static final String NAMESPACE_FOR_IMPORTED_SCHEMA_MUST_NOT_BE_RELATIVE = MessageFormat.format(
            Messages.WSIConstants_20, "\"namespace\"", "<xsd:import>", "URI"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    public static final String INCOMPATIBILITY_BETWEEN_THE_INPUT_OUTPUT_IN_OPERATION_AND_INPUT_OUTPUT_IN_BINDING_OPERATION = MessageFormat
            .format(Messages.WSIConstants_24, "Input/Output", "operation", "BindingInput/BindingOutput", "binding operation", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    "<wsdl:input>", "<wsdl:output>", WSDL_BINDING); //$NON-NLS-1$ //$NON-NLS-2$

    public static final String XML_NAMESPACE_SHOULD_NOT_BE_CONTAINED_IN_DESCRIPTION = MessageFormat.format(
            Messages.WSIConstants_29, DESCRIPTION, "namespace", "xmlns:xml=\"http://www.w3.org/XML/1998/namespace\""); //$NON-NLS-1$ //$NON-NLS-2$ 

    public static final String THE_DOCUMENT_DOESNOT_HAVE_APPROPRIATE_VERSION_AND_ENCODING = MessageFormat.format(
            Messages.WSIConstants_25, UTF_8, UTF_16, STRING + versionAttribute + STRING, STRING + VERSION + STRING);

    public static final String ELEMENT_ATTRIBUTE_HAS_INVALID_VALUE = MessageFormat.format(Messages.WSIConstants_27,
            "<wsdl:message>", DESCRIPTION, "<wsdl:part>", STRING + ELEMENT + STRING); //$NON-NLS-1$ //$NON-NLS-2$

    public static final String NAMESPACE_ATTRIBUTE_IN_WSDL_IMPORT_MUST_BE_NOT_RELATIVE = MessageFormat.format(
            Messages.WSIConstants_28, DESCRIPTION, "\"namespace\"", "<wsdl:import>", "URI"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 

    public static final String HAS_AT_MOST_ONE_PART_LISTED_IN_PARTS_ATTRIBUTE = MessageFormat.format(Messages.WSIConstants_30,
            STRING + PARTS_ATTRIBUTE + STRING,
            "<wsdl:part>", "<soapbind:body>", "<soapbind:header>", STRING + PARTS_ATTRIBUTE + STRING); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

}
