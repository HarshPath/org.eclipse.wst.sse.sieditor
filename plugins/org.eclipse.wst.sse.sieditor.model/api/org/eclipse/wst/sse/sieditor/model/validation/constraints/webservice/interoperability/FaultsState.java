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
package org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability;

import java.text.MessageFormat;

import org.eclipse.wst.sse.sieditor.model.i18n.Messages;

public enum FaultsState {
    SOAPFAULT_NAME_AND_WSDLBINDING_FAULT_NAME_DONOT_MATCH(MessageFormat.format(Messages.FaultsState_0, "\"name\"", //$NON-NLS-1$
            WSIConstants.SOAP_FAULT, "<wsdl:fault>")), //$NON-NLS-1$
    MISSING_NAME_ATTRIBUTE_IN_SOME_SOAPFAULT(MessageFormat.format(Messages.FaultsState_1, WSIConstants.WSDL_BINDING,
            WSIConstants.DESCRIPTION, "\"name\"", "<soapbind:fault>")), //$NON-NLS-1$ //$NON-NLS-2$ 
    FOR_EVERY_WSDL_FAULT_MUST_HAVE_CORRESPONDING_SOAP_FAULT_IN_OPERATION_BINDING(MessageFormat.format(Messages.FaultsState_2,
            WSIConstants.WSDL_BINDING, WSIConstants.DESCRIPTION, WSIConstants.SOAP_FAULT)), THE_SOAPFAULT_HAS_SPECIFIED_USE_ATTTRIBUTE_WITH_NOT_LITERAL_VALUE(
            MessageFormat.format(Messages.FaultsState_3, WSIConstants.WSDL_BINDING, WSIConstants.DESCRIPTION,
                    WSIConstants.SOAP_FAULT, WSIConstants.STRING + WSIConstants.LITERAL + WSIConstants.STRING)), FAULT_STATE_IS_OK(
            null);

    private String message;

    public String getMessage() {
        return message;
    }

    private FaultsState(String message) {
        this.message = message;
    }
}
