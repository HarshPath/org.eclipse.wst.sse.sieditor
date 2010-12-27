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
 *    Keshav Veerapaneni - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.model.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.wsdl.Fault;

import org.eclipse.core.runtime.Assert;
import org.eclipse.wst.wsdl.Binding;
import org.eclipse.wst.wsdl.BindingFault;
import org.eclipse.wst.wsdl.BindingInput;
import org.eclipse.wst.wsdl.BindingOperation;
import org.eclipse.wst.wsdl.BindingOutput;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.ExtensibilityElement;
import org.eclipse.wst.wsdl.Input;
import org.eclipse.wst.wsdl.Message;
import org.eclipse.wst.wsdl.Operation;
import org.eclipse.wst.wsdl.Part;
import org.eclipse.wst.wsdl.Port;
import org.eclipse.wst.wsdl.PortType;
import org.eclipse.wst.wsdl.Service;
import org.eclipse.wst.wsdl.WSDLElement;
import org.eclipse.wst.wsdl.WSDLFactory;
import org.eclipse.wst.wsdl.binding.soap.SOAPBinding;
import org.eclipse.wst.wsdl.binding.soap.SOAPBody;
import org.eclipse.wst.wsdl.binding.soap.SOAPFault;
import org.eclipse.wst.wsdl.binding.soap.SOAPOperation;

import org.eclipse.wst.sse.sieditor.core.common.Nil;

/**
 * Utilities for checking some of the WSDL specific's e.g. Document or RPC Style
 * 
 */
public class WSDLAnalyzer {

    private static final String DOCUMENT_STYLE = "document"; //$NON-NLS-1$
    public static final String KEY_BINDING = "FAULTS"; //$NON-NLS-1$
    public static final String KEY_INPUT = "INPUT"; //$NON-NLS-1$
    public static final String KEY_OUTPUT = "OUTPUT"; //$NON-NLS-1$
    public static final String KEY_FAULTS = "FAULTS"; //$NON-NLS-1$

    private final Definition _definition;

    public WSDLAnalyzer(final Definition definition) {
        Nil.checkNil(definition, "definition"); //$NON-NLS-1$
        this._definition = definition;
    }

    /**
     * Checks if an operation is document style compliant by looking at the
     * bindings if present else based on the parts
     * 
     * @param operation
     * @return
     */
    @SuppressWarnings("unchecked")
    public boolean isDocStyleCompliant(final Operation operation) {
        Assert.isTrue(operation.getEnclosingDefinition() == _definition,
                "Definition for operation is not the same as the definition for WSDL"); //$NON-NLS-1$

        final BindingOperation bindingOperation = getSOAPBindingOperation(operation);

        if (null != bindingOperation) {
            final Binding binding = (Binding) bindingOperation.getContainer();
            final String style;
            final String operationStyle = getBindingOperationStyle(bindingOperation);
            style = null != operationStyle ? operationStyle : getBindingStyle(binding);

            if (null != style)
                return DOCUMENT_STYLE.equals(style);
        } else {
            final List<Binding> bindings = getSOAPBindings((PortType) operation.getContainer());
            if (bindings.size() > 0)
                return true;
        }

        final Input input = operation.getEInput();
        if (null != input) {
            final Message message = input.getEMessage();
            if (null != message) {
                for (Part part : (List<Part>) message.getEParts()) {
                    if (null != part.getElementName())
                        return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns the first SOAP Binding operation for the given operation
     * 
     * @param operation
     * @return {@link BindingOperation}
     */
    private BindingOperation getSOAPBindingOperation(final Operation operation) {
        Nil.checkNil(operation, "operation"); //$NON-NLS-1$

        final WSDLElement parent = operation.getContainer();
        if (parent instanceof PortType) {
            // First ensure that the portType lies in current WSDL
            final PortType portType = (PortType) parent;
            if (isContained(portType)) {
                final List<Binding> bindings = getSOAPBindings(portType);
                final Collection<BindingOperation> bindingOperations = getBindingOperations(bindings, operation);

                if (bindingOperations.size() > 0) {
                    return bindingOperations.iterator().next();
                }
            }
        }
        return null;
    }

    private boolean isContained(final WSDLElement portType) {
        return _definition.equals(portType.getEnclosingDefinition());
    }

    @SuppressWarnings("unchecked")
    private List<Binding> getSOAPBindings(final PortType portType) {
        final List<Binding> bindings = _definition.getEBindings();
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
        return result.size() == 0 ? Collections.EMPTY_LIST : result;
    }

    @SuppressWarnings("unchecked")
    private SOAPBinding getSOAPBinding(final Binding binding) {
        final List<ExtensibilityElement> extensions = binding.getEExtensibilityElements();
        // Check if this is an SOAP Binding
        for (ExtensibilityElement extensibilityElement : extensions) {
            if (extensibilityElement instanceof SOAPBinding) {
                return (SOAPBinding) extensibilityElement;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private SOAPOperation getSOAPOperation(final BindingOperation bindingOperation) {
        final List<ExtensibilityElement> extensions = bindingOperation.getEExtensibilityElements();
        // Check if this is an SOAP Binding
        for (ExtensibilityElement extensibilityElement : extensions) {
            if (extensibilityElement instanceof SOAPOperation) {
                return (SOAPOperation) extensibilityElement;
            }
        }
        return null;
    }

    private String getBindingStyle(final Binding binding) {
        final SOAPBinding soapBinding = getSOAPBinding(binding);
        return null == soapBinding ? null : soapBinding.getStyle();
    }

    private String getBindingOperationStyle(final BindingOperation bindingOperation) {
        final SOAPOperation soapOperation = getSOAPOperation(bindingOperation);
        return null == soapOperation ? null : soapOperation.getStyle();
    }

    @SuppressWarnings("unchecked")
    private Collection<Binding> getBindings(final javax.wsdl.PortType portType) {
        if (portType == null)
            return Collections.EMPTY_LIST;

        final List<Binding> bindings = _definition.getEBindings();
        if (bindings == null || bindings.isEmpty())
            return Collections.EMPTY_LIST;

        final List<Binding> result = new ArrayList<Binding>(1);
        for (Binding binding : bindings) {
            if (portType.equals(binding.getPortType()))
                result.add(binding);
        }
        return result.size() == 0 ? Collections.EMPTY_LIST : result;
    }

    @SuppressWarnings("unchecked")
    private Collection<BindingOperation> getBindingOperations(final Collection<Binding> wsdlBindings,
            final javax.wsdl.Operation wsdlOperation) {

        if (wsdlOperation == null || wsdlBindings == null || wsdlBindings.isEmpty())
            return Collections.EMPTY_LIST;

        final List<BindingOperation> result = new ArrayList<BindingOperation>(1);
        for (final Binding wsdlBinding : wsdlBindings) {
            for (BindingOperation wsdlBindingOperation : (List<BindingOperation>) wsdlBinding.getEBindingOperations()) {
                if (wsdlOperation.equals(wsdlBindingOperation.getOperation())) {
                    result.add(wsdlBindingOperation);
                }
            }
        }
        return result.size() == 0 ? Collections.EMPTY_LIST : result;
    }

    @SuppressWarnings("unchecked")
    private Collection<BindingFault> getBindingFaults(final Collection<BindingOperation> bindingOperations, final Fault fault) {

        if (fault == null || bindingOperations == null || bindingOperations.isEmpty())
            return Collections.EMPTY_LIST;

        final List<BindingFault> result = new ArrayList<BindingFault>();
        for (final BindingOperation bindingOperation : bindingOperations) {
            for (BindingFault bindingFault : (List<BindingFault>) bindingOperation.getEBindingFaults()) {
                if (fault.equals(bindingFault.getFault())) {
                    result.add(bindingFault);
                }
            }
        }
        return result.size() == 0 ? Collections.EMPTY_LIST : result;
    }

    @SuppressWarnings( { "unchecked", "unused" })
    private SOAPBody getInSOAPBodyExtension(BindingOperation operation) {
        final BindingInput input = operation.getEBindingInput();
        if (null != input) {
            final List<ExtensibilityElement> extensions = input.getEExtensibilityElements();
            for (ExtensibilityElement extension : extensions) {
                if (extension instanceof SOAPBody) {
                    return (SOAPBody) extension;
                }
            }
        }
        return null;
    }

    @SuppressWarnings( { "unchecked", "unused" })
    private SOAPBody getOutSOAPBodyExtension(BindingOperation operation) {
        final BindingOutput input = operation.getEBindingOutput();
        if (null != input) {
            final List<ExtensibilityElement> extensions = input.getEExtensibilityElements();
            for (ExtensibilityElement extension : extensions) {
                if (extension instanceof SOAPBody) {
                    return (SOAPBody) extension;
                }
            }
        }
        return null;
    }

    @SuppressWarnings( { "unchecked", "unused" })
    private Map<String, SOAPFault> getFaultExtensions(final BindingOperation bindingOperation) {
        final List<BindingFault> faults = bindingOperation.getEBindingFaults();
        final Map<String, SOAPFault> result = new HashMap<String, SOAPFault>(1);

        for (BindingFault bindingFault : faults) {
            final String name = bindingFault.getName();
            if (null != name) {
                final List<ExtensibilityElement> extensions = bindingFault.getEExtensibilityElements();
                for (ExtensibilityElement extension : extensions) {
                    if (extension instanceof SOAPFault && !result.containsKey(name))
                        result.put(name, (SOAPFault) extension);
                }
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public void removeBindings(final javax.wsdl.PortType portType) {

        if (portType == null)
            return;

        Collection<Binding> bindings = getBindings(portType);
        if (bindings.isEmpty()) {
            return;
        }

        _definition.getEBindings().removeAll(bindings);

        final List<Service> services = _definition.getEServices();
        final List<Service> matchedServices = new ArrayList<Service>();
        for (Service service : services) {
            final List<Port> ports = service.getEPorts();
            final ArrayList<Port> matchedBindings = new ArrayList<Port>(1);
            for (Port port : ports) {
                if (bindings.contains(port.getBinding()))
                    matchedBindings.add(port);
            }
            service.getEPorts().removeAll(matchedBindings);
            if (service.getEPorts().size() == 0)
                matchedServices.add(service);
        }
        services.removeAll(matchedServices);
    }

    public void removeOperationBindings(final PortType portType, final javax.wsdl.Operation operation) {

        if(!checkRemovalOfBindingsPossible(portType, operation)){
            return;
        }
        
        Collection<Binding> bindings = getBindings(portType);
        for (Binding binding : bindings) {
            for (BindingOperation bindingOperation : getBindingOperations(bindings, operation)) {
                if (binding.getEBindingOperations().contains(bindingOperation))
                    binding.getEBindingOperations().remove(bindingOperation);
            }
        }
    }

    public void removeFaultBindings(final javax.wsdl.PortType portType, final javax.wsdl.Operation operation,
            final javax.wsdl.Fault wsdlFault) {

        if (wsdlFault == null || !checkRemovalOfBindingsPossible(portType, operation)){
            return;
        }

        Collection<Binding> bindings = getBindings(portType);
        Collection<BindingOperation> bindingOperations = getBindingOperations(bindings, operation);
        Collection<BindingFault> bindingFaults = getBindingFaults(bindingOperations, wsdlFault);

        if (bindingFaults == null || bindingFaults.isEmpty()){
            return;
        }
        
        for (final BindingOperation bindingOperation : bindingOperations) {
            for (BindingFault bindingFault : bindingFaults) {
                if (bindingOperation.getEBindingFaults().contains(bindingFault))
                    bindingOperation.getEBindingFaults().remove(bindingFault);
            }
        }
    }

    public void removeOutputAndFaultBindings(final javax.wsdl.PortType portType, final javax.wsdl.Operation operation) {
        if(!checkRemovalOfBindingsPossible(portType, operation)){
            return;
        }
        Collection<BindingOperation> bindingOperations = getBindingOperations( getBindings(portType), operation);
        for (final BindingOperation bindingOperation : bindingOperations) {
            bindingOperation.eUnset(WSDLFactory.eINSTANCE.getWSDLPackage().getBindingOperation_EBindingOutput());
            bindingOperation.eUnset(WSDLFactory.eINSTANCE.getWSDLPackage().getBindingOperation_EBindingFaults());
        }
    }
    
    public void removeAllOutputBindings(final javax.wsdl.PortType portType, final javax.wsdl.Operation operation){
        if(!checkRemovalOfBindingsPossible(portType, operation)){
            return;
        }
        Collection<BindingOperation> bindingOperations = getBindingOperations( getBindings(portType), operation);
        for (final BindingOperation bindingOperation : bindingOperations) {
            bindingOperation.eUnset(WSDLFactory.eINSTANCE.getWSDLPackage().getBindingOperation_EBindingOutput());
        }
    }
    
    private boolean checkRemovalOfBindingsPossible(javax.wsdl.PortType portType, javax.wsdl.Operation operation){
        if (portType == null || operation == null){
            return false;
        }

        Collection<Binding> bindings = getBindings(portType);
        if (bindings == null || bindings.isEmpty()){
            return false;
        }

        Collection<BindingOperation> bindingOperations = getBindingOperations(bindings, operation);
        if (bindingOperations == null || bindingOperations.isEmpty()){
            return false;
        }
        return true;
    }

    public void updateBindingFaults(final org.eclipse.wst.wsdl.Fault fault) {
        final WSDLElement container = fault.getContainer();
        if (container instanceof Operation) {
            final Operation operation = (Operation) container;
            final Collection<Binding> bindings = getBindings((javax.wsdl.PortType) operation.getContainer());
            if (bindings.size() > 0) {
                final Collection<BindingOperation> bindingOperations = getBindingOperations(bindings, operation);
                if (bindingOperations.size() > 0) {
                    final Collection<BindingFault> faults = getBindingFaults(bindingOperations, fault);
                    for (BindingFault bindingFault : faults)
                        bindingFault.setName(fault.getName());
                }
            }
        }
    }

}