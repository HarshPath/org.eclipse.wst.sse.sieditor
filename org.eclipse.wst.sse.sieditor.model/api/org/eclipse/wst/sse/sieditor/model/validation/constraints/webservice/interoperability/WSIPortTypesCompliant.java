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

import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.BINDINGS_ARE_NOT_SPECIFIED_MESSAGE;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.HTTP_TRANSPORT;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.INAPPROPRIATE_SOAP_BINDING;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.INAPPROPRIATE_TRANSPORT_ATTRIBUTE_IN_SOME_SOAP_BINDING;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.NO_SOAP_BINDING_MESSAGE;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIConstants.TRANSPORT_ATTRIBUTE_MISSING;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIUtils.getSOAPBinding;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIUtils.getSOAPBindings;
import static org.eclipse.wst.sse.sieditor.model.validation.constraints.webservice.interoperability.WSIUtils.hasBindingForSpecifiedPortType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.emf.validation.model.ConstraintStatus;
import org.eclipse.wst.wsdl.Binding;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.PortType;
import org.eclipse.wst.wsdl.binding.soap.SOAPBinding;

import org.eclipse.wst.sse.sieditor.model.validation.constraints.AbstractConstraint;

public class WSIPortTypesCompliant extends AbstractConstraint {
    private PortType portType;

    @SuppressWarnings("unchecked")
    @Override
    protected IStatus doValidate(IValidationContext validationContext) {
        this.portType = (PortType) validationContext.getTarget();
        Definition definition = (Definition) this.portType.getEnclosingDefinition();

        WSIManager.clearTheRegistry();

        final List<Binding> allBindings = (List<Binding>) definition.getEBindings();

        if (!hasBindingForSpecifiedPortType(portType, allBindings)) {
            WSIManager.setShouldExecuteContraintsOnOperation(false);
            return ConstraintStatus.createStatus(validationContext, this.portType, null, BINDINGS_ARE_NOT_SPECIFIED_MESSAGE,
                    BINDINGS_ARE_NOT_SPECIFIED_MESSAGE);
        }

        final List<Binding> soapBindings = getSOAPBindings(this.portType, allBindings);

        WSIManager.setShouldBeShownAMessageForInappropriateSOAPBindingStyle(this.portType, true);

        return checkSoapBindings(validationContext, soapBindings);

    }

    /**
     * R2401 A wsdl:binding element in a DESCRIPTION MUST use WSDL SOAP Binding
     * as defined in WSDL 1.1 Section 3.
     * 
     * R2701 The wsdl:binding element in a DESCRIPTION MUST be constructed so
     * that its soapbind:binding child element specifies the transport
     * attribute.
     * 
     * R2702 A wsdl:binding element in a DESCRIPTION MUST specify the HTTP
     * transport protocol with SOAP binding. Specifically, the transport
     * attribute of its soapbind:binding child MUST have the value
     * "http://schemas.xmlsoap.org/soap/http".
     * 
     * Note that this requirement does not prohibit the use of HTTPS; See
     * R5000,R5001.
     * 
     * @param validationContext
     *            is the parameter from the doValidate(..) method
     * @param soapBindings
     *            is not null list of soap bindings which are specified for
     *            "this.portType"
     * @return status for this doValidate(..) method
     */
    private IStatus checkSoapBindings(IValidationContext validationContext, final List<Binding> soapBindings) {
        if (soapBindings.isEmpty()) {
            WSIManager.setShouldExecuteContraintsOnOperation(false);
            return ConstraintStatus.createStatus(validationContext, this.portType, null, NO_SOAP_BINDING_MESSAGE,
                    NO_SOAP_BINDING_MESSAGE);
        }
        Collection<IStatus> statusesForTransportAttribute = new HashSet<IStatus>();
        WSIManager.setShouldExecuteContraintsOnOperation(true);
        WSIManager.putSoapBindings(this.portType, (ArrayList<Binding>) soapBindings);
        for (final Binding currentBinding : soapBindings) {
            final SOAPBinding currentSoapBinding = getSOAPBinding(currentBinding);

            if (currentSoapBinding == null) {
                statusesForTransportAttribute.add(ConstraintStatus.createStatus(validationContext, this.portType, null,
                        INAPPROPRIATE_SOAP_BINDING, INAPPROPRIATE_SOAP_BINDING));
            }

            if (currentSoapBinding.getTransportURI() == null) {
                statusesForTransportAttribute.add(ConstraintStatus.createStatus(validationContext, this.portType, null,
                        TRANSPORT_ATTRIBUTE_MISSING, TRANSPORT_ATTRIBUTE_MISSING));
            } else if (!(currentSoapBinding.getTransportURI().equalsIgnoreCase(HTTP_TRANSPORT))) {
                statusesForTransportAttribute.add(ConstraintStatus.createStatus(validationContext, this.portType, null,
                        INAPPROPRIATE_TRANSPORT_ATTRIBUTE_IN_SOME_SOAP_BINDING,
                        INAPPROPRIATE_TRANSPORT_ATTRIBUTE_IN_SOME_SOAP_BINDING));
            }
        }
        if (statusesForTransportAttribute.isEmpty()) {
            return ConstraintStatus.createSuccessStatus(validationContext, this.portType, null);
        } else {
            return ConstraintStatus.createMultiStatus(validationContext, statusesForTransportAttribute);
        }
    }

    @Override
    protected boolean shouldExecute(IValidationContext ctx) {
        return true;
    }

}
