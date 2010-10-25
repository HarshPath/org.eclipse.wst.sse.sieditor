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

import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.WSDL_ARRAYTYPE_IS_NOT_ALLOWED_IN_XSDATTRIBUTE_ELEMENT;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintStatus;
import org.eclipse.wst.wsdl.util.WSDLConstants;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.eclipse.wst.sse.sieditor.model.validation.constraints.AbstractConstraint;

public class WSIXSDAttributeCompliant extends AbstractConstraint {
    private static final String ARRAY_TYPE = "arrayType"; //$NON-NLS-1$
    private XSDAttributeDeclaration attributeElement;

    /**
     * R2110 In a DESCRIPTION, declarations MUST NOT extend or restrict the
     * soapenc:Array type.
     * 
     * R2111 In a DESCRIPTION, declarations MUST NOT use wsdl:arrayType
     * attribute in the type declaration.
     * 
     */
    @Override
    protected IStatus doValidate(IValidationContext validationContext) {
        this.attributeElement = (XSDAttributeDeclaration) validationContext.getTarget();
        Element domAttributeElement = this.attributeElement.getElement();
        NamedNodeMap attributes = domAttributeElement.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);
            if (ARRAY_TYPE.equals(attribute.getLocalName()) && WSDLConstants.WSDL_NAMESPACE_URI.equals(attribute.getNamespaceURI())) {
                return ConstraintStatus.createStatus(validationContext, this.attributeElement.getSchema(), null,
                        WSDL_ARRAYTYPE_IS_NOT_ALLOWED_IN_XSDATTRIBUTE_ELEMENT,
                        WSDL_ARRAYTYPE_IS_NOT_ALLOWED_IN_XSDATTRIBUTE_ELEMENT);
            }
        }
        
        return ConstraintStatus.createSuccessStatus(validationContext, this.attributeElement.getSchema(), null);

    }

    @Override
    protected boolean shouldExecute(IValidationContext ctx) {
        return true;
    }
}
