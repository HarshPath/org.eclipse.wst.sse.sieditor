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
package org.eclipse.wst.sse.sieditor.model.validation;

import java.util.Collection;

import javax.wsdl.Message;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.wst.wsdl.Binding;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.Fault;
import org.eclipse.wst.wsdl.Import;
import org.eclipse.wst.wsdl.Operation;
import org.eclipse.wst.wsdl.Part;
import org.eclipse.wst.wsdl.Port;
import org.eclipse.wst.wsdl.PortType;
import org.eclipse.wst.wsdl.WSDLPackage;
import org.eclipse.wst.wsdl.binding.http.HTTPPackage;
import org.eclipse.wst.wsdl.binding.soap.SOAPPackage;
import org.eclipse.xsd.XSDDiagnostic;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IOperation;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IServiceInterface;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceInterface;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.ServiceOperation;

public class ESMModelAdapter implements IModelAdapter {

    private IWsdlModelRoot modelRoot;

    public ESMModelAdapter(IWsdlModelRoot modelRoot) {
        this.modelRoot = modelRoot;
    }

    public IModelObject adaptToModelObject(EObject source) {

        if (source == null) {
            return null;
        }

        if (source instanceof XSDDiagnostic) {
            source = ((XSDDiagnostic) source).getPrimaryComponent();
        }

        IModelObject adapted = null;

        EPackage ePackage = source.eClass().getEPackage();

        if (ePackage == WSDLPackage.eINSTANCE) {
            if (source instanceof PortType) {
                adapted = adaptPortType((PortType) source);
            } else {
                IDescription description = modelRoot.getDescription();
                if (source instanceof Port) {
                    adapted = description;
                } else if (source instanceof Operation) {
                    adapted = adaptOperation((Operation) source);
                } else if (source instanceof Fault) {
                    Operation parentOperation = (Operation) ((Fault) source).getContainer();
                    IOperation adaptOperation = (IOperation) adaptOperation(parentOperation);
                    if(adaptOperation==null){
                        return null;
                    }
                    for (IFault fault : adaptOperation.getAllFaults()) {
                        if (source == fault.getComponent()) {
                            adapted = fault;
                            break;
                        }
                    }
                } else if (source instanceof Binding) {
                    adapted = adaptBinding((Binding) source);
                } else if (source instanceof Part) {
                    adapted = adaptMessagePart((Part) source);
                } else if (source instanceof Import) {
                    return description.getReferencedDescription(((Import) source).getNamespaceURI());
                } else if (source instanceof Definition){
                    return description;
                }
            }

        } else if (ePackage == SOAPPackage.eINSTANCE || ePackage == HTTPPackage.eINSTANCE) {
            adapted = adaptBindingExtensionElement(source);
        }

        return adapted;
    }

    private IModelObject adaptMessagePart(Part part) {
        for (IServiceInterface si : modelRoot.getDescription().getAllInterfaces()) {
            Collection<IOperation> operations = si.getAllOperations();
            for (IOperation op : operations) {
                Collection<IParameter> inParams = op.getAllInputParameters();
                for (IParameter iParameter : inParams) {
                    if (iParameter.getComponent().equals(part)) {
                        return iParameter;
                    }
                }
                Collection<IParameter> outParams = op.getAllOutputParameters();
                for (IParameter iParameter : outParams) {
                    if (iParameter.getComponent().equals(part)) {
                        return iParameter;
                    }
                }
                for (IFault fault : op.getAllFaults()) {
                    Message message = fault.getComponent().getMessage();
                    if (message == null) {
                        continue;
                    }
                    if (message.getParts().containsValue(part)) {
                        return fault;
                    }
                }
            }
        }
        return null;
    }

    private IModelObject adaptOperation(Operation source) {
        for (IServiceInterface si : modelRoot.getDescription().getAllInterfaces()) {
            Collection<IOperation> operations = si.getAllOperations();
            for (IOperation op : operations) {
                if (source == ((ServiceOperation) op).getComponent()) {
                    return op;
                }
            }
        }
        return null;
    }

    public EObject adapatToEMF(Object source) {
        if (!(source instanceof IModelObject)) {
            return null;
        }

        IModelObject modelObject = (IModelObject) source;
        if (modelObject instanceof IDescription) {
            return ((Description) modelObject).getComponent();
        }
        return null;
    }

    private IServiceInterface adaptPortType(PortType portType) {
        for (IServiceInterface si : modelRoot.getDescription().getAllInterfaces()) {
            if (portType == ((ServiceInterface) si).getComponent()) {
                return si;
            }
        }
        return null;
    }

    private IServiceInterface adaptBinding(Binding binding) {
        PortType portType = binding.getEPortType();
        if (portType != null) {
            return adaptPortType(portType);
        }
        return null;
    }

    private IModelObject adaptPort(Port port) {
        Binding binding = port.getEBinding();
        if (binding != null) {
            return adaptBinding(binding);
        }
        return null;
    }

    private IModelObject adaptBindingExtensionElement(EObject source) {
        EObject container = source;

        while ((container = container.eContainer()) != null) {
            if (container instanceof Binding) {
                return adaptBinding((Binding) container);
            } else if (container instanceof Port) {
                return adaptPort((Port) container);
            }
        }

        return null;
    }

    public void setModelRoot(IWsdlModelRoot modelRoot) {
        this.modelRoot = modelRoot;
    }
}
