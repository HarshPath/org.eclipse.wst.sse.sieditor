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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.wst.wsdl.Binding;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.PortType;

public class WSIManager {
    private static Map<PortType, List<Binding>> soapBindings = new HashMap<PortType, List<Binding>>();
    private static Map<PortType, Boolean> shouldBeShownAMessageOnAGivenPortType = new HashMap<PortType, Boolean>();
    private static boolean shouldExecute = true;

    private WSIManager() {
    }

    /**
     * Clear all contained mappings between PortType and Bindings
     */
    public static void clearTheRegistry() {
        if (!soapBindings.isEmpty()) {
            soapBindings.clear();
        }

        if (!shouldBeShownAMessageOnAGivenPortType.isEmpty()) {
            shouldBeShownAMessageOnAGivenPortType.clear();
        }
    }

    /**
     * @return whether INAPPROPRIATE_SOAP_BINDING_STYLE message should be shown
     *         on portType
     */
    public static boolean isShouldBeShownAMessageForInappropriateSOAPBindingStyle(PortType portType) {
        if (!WSIManager.shouldBeShownAMessageOnAGivenPortType.containsKey(portType))
            return true;
        return WSIManager.shouldBeShownAMessageOnAGivenPortType.get(portType);
    }

    /**
     * Define whether INAPPROPRIATE_SOAP_BINDING_STYLE message would be shown on
     * portType
     * 
     * @param shouldExecute
     *            is the new value of "shouldExecute" field
     */
    public static void setShouldBeShownAMessageForInappropriateSOAPBindingStyle(PortType portType,
            boolean shouldBeShownAMessageForInappropriateSOAPBindingStyle) {
        WSIManager.shouldBeShownAMessageOnAGivenPortType.put(portType, shouldBeShownAMessageForInappropriateSOAPBindingStyle);
    }

    public static boolean shouldExecuteContraintsOnOperation() {
        return shouldExecute;
    }

    /**
     * Define which "operation" constraint must be validate
     * 
     * @param shouldExecuteContraintsOnOperation
     *            is the new value of "shouldExecute" field
     */
    public static void setShouldExecuteContraintsOnOperation(boolean shouldExecute) {
        WSIManager.shouldExecute = shouldExecute;
    }

    /**
     * Get all specified bindings for existed in the WSDL PortType
     * 
     * @param port
     *            is a existed PortType
     * @return ArrayList of all SOAP bindings in opened WSDL
     */
    @SuppressWarnings("unchecked")
    public static List<Binding> getSoapBindings(PortType port) {
        if (!soapBindings.containsKey(port)) {
            Definition definition = (Definition) port.getEnclosingDefinition();
            if (definition != null) {
                List<Binding> allBindings = (List<Binding>) definition.getEBindings();
                final List<Binding> soapBindingsForTheGivenPort = WSIUtils.getSOAPBindings(port, allBindings);
                soapBindings.put(port, soapBindingsForTheGivenPort);
            }
        }
        return soapBindings.get(port);
    }

    /**
     * Add bindings to specified PortType
     * 
     * @param port
     *            must be not null and should represent an service in the WSDL
     * @param bindings
     *            are all SOAP bindings which are specified for the "port"
     *            parameter in the current WSDL
     */
    public static void putSoapBindings(PortType port, ArrayList<Binding> bindings) {
        soapBindings.put(port, bindings);
    }

}
