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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.wst.wsdl.Binding;
import org.eclipse.wst.wsdl.ExtensibilityElement;
import org.eclipse.wst.wsdl.PortType;
import org.eclipse.wst.wsdl.binding.soap.SOAPBinding;

public class WSIUtils {

    /**
     * Get all SOAP Bindings for specified PortType and list of all bindings
     * which are contained in wsdl:definition
     * 
     * @param portType
     *            must be not null
     * @param bindings
     *            must not be null
     * @return list of SOAP bindings specified for the portType parameter
     */
    @SuppressWarnings("unchecked")
    public static List<Binding> getSOAPBindings(final PortType portType, List<Binding> bindings) {

        final List<Binding> result = new ArrayList<Binding>(1);
        for (Binding binding : bindings) {
            // Check if the binding is for current PortType
            if (!portType.equals(binding.getPortType()))
                continue;

            final List<ExtensibilityElement> extensions = binding.getEExtensibilityElements();
            // Check if this is an SOAP Binding
            for (ExtensibilityElement extensibilityElement : extensions) {
                if (extensibilityElement instanceof SOAPBinding) {
                    result.add(binding);
                    break;
                }
            }

        }
        return result.isEmpty() ? new ArrayList<Binding>() : result;
    }

    /**
     * Check whether the portType parameter has any bindings
     * 
     * @param portType
     * @param bindings
     * @return true if bindings exist for the portType, false in the other case
     */
    public static boolean hasBindingForSpecifiedPortType(final PortType portType, List<Binding> bindings) {
        if (bindings == null || portType == null)
            return false;
        for (Binding binding : bindings) {
            // Check if the binding is for current PortType
            if (portType.equals(binding.getPortType()))
                return true;
        }
        return false;
    }

    /**
     * 
     * @param binding
     *            must be not null
     * @return SOAPBinding if exist in the binding parameter, null in the other
     *         case
     */
    @SuppressWarnings("unchecked")
    public static SOAPBinding getSOAPBinding(final Binding binding) {
        final List<ExtensibilityElement> extensions = binding.getEExtensibilityElements();
        // Check if this is an SOAP Binding
        for (ExtensibilityElement extensibilityElement : extensions) {
            if (extensibilityElement instanceof SOAPBinding) {
                return (SOAPBinding) extensibilityElement;
            }
        }
        return null;
    }

}
