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
 *    Stanislav Nichev - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.model.reconcile.utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.xml.type.internal.QName;
import org.eclipse.wst.wsdl.Binding;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.Fault;
import org.eclipse.wst.wsdl.Message;
import org.eclipse.wst.wsdl.MessageReference;
import org.eclipse.wst.wsdl.Operation;
import org.eclipse.wst.wsdl.Part;
import org.eclipse.wst.wsdl.Port;
import org.eclipse.wst.wsdl.Service;
import org.eclipse.wst.wsdl.util.WSDLConstants;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.w3c.dom.Element;

import org.eclipse.wst.sse.sieditor.command.emf.common.utils.UpdateNSPrefixUtils;
import org.eclipse.wst.sse.sieditor.model.reconcile.IResolveUtils;
import org.eclipse.wst.sse.sieditor.model.reconcile.ResolveUtils;
import org.eclipse.wst.sse.sieditor.model.reconcile.utils.IXsdReconcileUtils.NamespaceResolverType;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;

/**
 * This is the default implementation of the {@link IWsdlReconcileUtils}
 * interface.
 * 
 */
public class WsdlReconcileUtils implements IWsdlReconcileUtils {

    private static final IWsdlReconcileUtils INSTANCE = new WsdlReconcileUtils();

    private WsdlReconcileUtils() {

    }

    public static IWsdlReconcileUtils instance() {
        return INSTANCE;
    }

    @Override
    public void reconcileMessages(final List<Message> messagesForResolve, final Definition definition) {
        for (final Message message : messagesForResolve) {
            String namespaceURI = definition.getNamespace(message.getQName().getPrefix());
            if ("".equals(message.getQName().getPrefix())) { //$NON-NLS-1$
                namespaceURI = definition.getTargetNamespace();
            }
            message.setQName(new QName(namespaceURI, message.getQName().getLocalPart(), message.getQName().getPrefix()));
        }
    }

    @Override
    public void reconcileMessagePartsReferences(final XSDSchema changedSchema, final List<Part> messagePartsForResolve,
            final Definition definition) {
        reconcileMessagePartsInternal(changedSchema, messagePartsForResolve, definition, NamespaceResolverType.ELEMENT);
        reconcileMessagePartsInternal(changedSchema, messagePartsForResolve, definition, NamespaceResolverType.SCHEMA);
    }

    private void reconcileMessagePartsInternal(final XSDSchema changedSchema, final List<Part> messagePartsForResolve,
            final Definition definition, final NamespaceResolverType namespaceResolverType) {
        final List<Part> reconciledMessageParts = new LinkedList<Part>();

        for (final Part part : messagePartsForResolve) {
            if (part.getElementDeclaration() != null) {
                final String targetNamespace = namespaceResolverType == NamespaceResolverType.ELEMENT ? part.getElementName()
                        .getNamespaceURI() : changedSchema.getTargetNamespace();

                final boolean messagePartReconciled = reconcileMessagePartReference(changedSchema, changedSchema
                        .getQNamePrefixToNamespaceMap(), definition, part, targetNamespace);

                if (messagePartReconciled) {
                    reconciledMessageParts.add(part);
                }
            }
        }

        messagePartsForResolve.removeAll(reconciledMessageParts);
    }

    private boolean reconcileMessagePartReference(final XSDSchema changedSchema, final Map<String, String> prefixesMap,
            final Definition definition, final Part part, final String targetNamespaceForResolve) {
        final XSDElementDeclaration newResolvedElementDeclaration = resolveUtils().resolveElementDeclaration(changedSchema,
                targetNamespaceForResolve, part.getElementName().getLocalPart());

        if (newResolvedElementDeclaration == null) {
            // the attribute declaration was not resolved. the message must be
            // in another schema
            return false;
        }

        final String prefix = prefixUtils().extractPrefixFromQName(
                part.getElement().getAttribute(WSDLConstants.ELEMENT_ATTRIBUTE));
        final String tnsForPrefix = changedSchema.getQNamePrefixToNamespaceMap().get(prefix);

        if (isElementCorrectlyResolved(changedSchema, prefixesMap, definition.getElement(), newResolvedElementDeclaration
                .getTargetNamespace(), prefix, tnsForPrefix)) {
            part.setElementName(new QName(tnsForPrefix, part.getElementName().getLocalPart(), part.getElementName().getPrefix()));
            part.setElementDeclaration(newResolvedElementDeclaration);
            return true;
        }

        return false;
    }

    @Override
    public void reconcileOperationsMessagesReferences(final List<Operation> operationsForResolve,
            final Definition editedDefinition, final EList<Message> eMessages, final String prefix) {
        for (final Operation operation : operationsForResolve) {
            for (final Message currentMessage : eMessages) {
                processMessageReference(operation.getEInput(), currentMessage, prefix);
                processMessageReference(operation.getEOutput(), currentMessage, prefix);
                processFaults(operation, currentMessage, prefix);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void processFaults(final Operation operation, final Message message, final String prefix) {
        final EList<Fault> eFaults = operation.getEFaults();
        for (final Fault fault : eFaults) {
            if (fault.getMessage() == null) {
                processMessageReference(fault, message, prefix);
            }
        }
    }

    private void processMessageReference(final MessageReference messageReference, final Message message, final String prefix) {
        if (messageReference == null || messageReference.getEMessage() != null) {
            return;
        }

        final String messageDOMqName = messageReference.getElement().getAttribute(WSDLConstants.MESSAGE_ATTRIBUTE);
        if (messageDOMqName == null || messageDOMqName.isEmpty()) {
            return;
        }
        final String expectedPrefix = prefix != null ? prefix : getPrefixForDefinition(messageDOMqName);
        final String prefixNamespace = message.getEnclosingDefinition().getNamespace(expectedPrefix);

        final javax.xml.namespace.QName messageEMFqName = message.getQName();

        if (prefixNamespace != null && !prefixNamespace.equals(messageEMFqName.getNamespaceURI())) {
            return;
        }

        final String messageDOMname = messageDOMqName.substring(messageDOMqName.indexOf(':') + 1);
        if (messageDOMname.equals(messageEMFqName.getLocalPart())
                && (messageEMFqName.getPrefix().equals(expectedPrefix) || messageEMFqName.getPrefix().equals(""))) { //$NON-NLS-1$
            messageReference.setEMessage(message);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void reconcileBindingsQNames(final Definition definition) {
        final EList<Binding> eBindings = definition.getEBindings();

        if (eBindings != null) {
            for (final Binding binding : eBindings) {
                if (binding.getEnclosingDefinition() != definition) {
                    continue;
                }
                binding.setQName(new QName(definition.getTargetNamespace(), binding.getQName().getLocalPart(), binding.getQName()
                        .getPrefix().equals(EmfXsdUtils.XMLNS_PREFIX) ? "" : binding.getQName().getPrefix())); //$NON-NLS-1$
            }
        }
    }

    // @Override
    // public void reconcileBindingsQNames(final EList<Binding> eBindings, final
    // String targetNamespace,
    // final EList<PortType> ePortTypesToCheck) {
    // if (eBindings != null) {
    // for (final Binding binding : eBindings) {
    // if (binding.getEPortType() == null) {
    // final String portTypeQName =
    // binding.getElement().getAttribute(WSDLConstants.TYPE_ATTRIBUTE);
    // final String portTypeName =
    // portTypeQName.substring(portTypeQName.indexOf(':') + 1);
    // final String prefix =
    // prefixUtils().extractPrefixFromQName(portTypeQName);
    //
    // for (final PortType portType : ePortTypesToCheck) {
    // if (portTypeName.equals(portType.getQName().getLocalPart())) {
    // portType.setQName(new QName(portType.getQName().getNamespaceURI(),
    // portType.getQName().getLocalPart(), prefix));
    // binding.setEPortType(portType);
    // binding.setQName(new QName(targetNamespace,
    // binding.getQName().getLocalPart(), binding.getQName()
    //                                    .getPrefix().equals(EmfXsdUtils.XMLNS_PREFIX) ? "" : binding.getQName().getPrefix())); //$NON-NLS-1$                            
    // }
    // }
    // }
    // }
    // }
    // }

    @Override
    @SuppressWarnings("unchecked")
    public void reconcileServicePortBindings(final Definition definition, final EList<Binding> eBindings) {
        final EList<Service> eServices = definition.getEServices();
        if (eServices != null) {
            for (final Service service : eServices) {
                final EList<Port> ePorts = service.getEPorts();
                if (ePorts == null) {
                    continue;
                }
                resolvePorts(definition, ePorts, eBindings);
            }
        }
    }

    /**
     * Utility method for resolving the port bindings. For each port it searches
     * all the bindings in the given definition and determines which one is the
     * correct one to be set to the port binding.
     */
    private void resolvePorts(final Definition definition, final EList<Port> ePorts, final EList<Binding> eBindings) {
        for (final Port port : ePorts) {
            if (port.getEBinding() != null || port.getEnclosingDefinition() != definition) {
                continue;
            }
            final String portBindingQName = port.getElement().getAttribute(WSDLConstants.BINDING_ATTRIBUTE);
            final String portBindingName = portBindingQName.substring(portBindingQName.indexOf(':') + 1);
            final String prefix = getPrefixForDefinition(portBindingQName);

            // we need to search all the bindings in the definition, so that we
            // can find the correct binding to set to the portBinding
            for (final Binding binding : eBindings) {
                if (binding.getQName().getLocalPart().equals(portBindingName)
                        && binding.getQName().getNamespaceURI().equals(definition.getNamespace(prefix))) {
                    port.setEBinding(binding);
                    break;
                }
            }
        }
    }

    // =========================================================
    // helpers
    // =========================================================

    protected IResolveUtils resolveUtils() {
        return ResolveUtils.instance();
    }

    private String getPrefixForDefinition(final String qName) {
        String prefix = prefixUtils().extractPrefixFromQName(qName);
        if (prefix == null) {
            prefix = ""; //$NON-NLS-1$
        }
        return prefix;
    }

    /**
     * Utility method. Checks if the prefix is an actual and valid one, so that
     * we can safely set the new reference.
     */
    private boolean isElementCorrectlyResolved(final XSDSchema schema, final Map<String, String> prefixesMap,
            final Element element, final String resolvedTargetNamespace, final String prefix, final String tnsForPrefix) {

        final boolean targetNamespacesAreTheSame = (tnsForPrefix != null && tnsForPrefix.equals(resolvedTargetNamespace));

        // this is used to resolve referred elements from importing schemas
        final boolean prefixValueMatchesResolvedTargetNamespace = (prefixesMap.get(prefix) != null && prefixesMap.get(prefix)
                .equals(resolvedTargetNamespace));

        return targetNamespacesAreTheSame && (/*
                                               * element.getAttribute(tnsPrefixAttribute
                                               * (prefix)) != null ||
                                               */prefixValueMatchesResolvedTargetNamespace);
    }

    private UpdateNSPrefixUtils prefixUtils() {
        return UpdateNSPrefixUtils.instance();
    }

}
