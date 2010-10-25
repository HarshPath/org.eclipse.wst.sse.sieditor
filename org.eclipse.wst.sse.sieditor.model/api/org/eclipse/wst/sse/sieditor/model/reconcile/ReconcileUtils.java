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
package org.eclipse.wst.sse.sieditor.model.reconcile;

import java.util.List;

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
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

import org.eclipse.wst.sse.sieditor.command.emf.common.namespace.UpdateNSPrefixUtils;
import org.eclipse.wst.sse.sieditor.model.utils.EmfXsdUtils;

/**
 * This is the default implementation of the {@link IReconcileUtils} interface.
 * 
 * 
 * 
 */
public class ReconcileUtils implements IReconcileUtils {

    private static final IReconcileUtils INSTANCE = new ReconcileUtils();

    private ReconcileUtils() {

    }

    public static IReconcileUtils instance() {
        return INSTANCE;
    }

    // =========================================================
    // reconcile schema elements references
    // =========================================================

    @Override
    public void reconcileSchemaContents(final XSDSchema schema, final List<XSDElementDeclaration> elementsForReferenceResolve,
            final List<XSDElementDeclaration> elementsForTypeResolve, final List<XSDAttributeDeclaration> attributesForResolve) {
        this.reconcileElementsReferences(schema, elementsForReferenceResolve);
        this.reconcileElementsTypes(schema, elementsForTypeResolve);
        this.reconcileAttributesReferences(schema, attributesForResolve);
    }

    private void reconcileElementsTypes(final XSDSchema changedSchema,
            final java.util.List<XSDElementDeclaration> elementsForTypeResolve) {
        for (final XSDElementDeclaration xsdElementDeclaration : elementsForTypeResolve) {
            if (xsdElementDeclaration.getTypeDefinition() == null) {
                continue;
            }
            final XSDTypeDefinition resolvedTypeDefinition = resolveUtils().resolveTypeDefinition(changedSchema,
                    changedSchema.getTargetNamespace(),
                    xsdElementDeclaration.getTypeDefinition().getName());

            if (resolvedTypeDefinition != null) {
                xsdElementDeclaration.setTypeDefinition(resolvedTypeDefinition);
            }
        }
    };

    private void reconcileElementsReferences(final XSDSchema changedSchema, final List<XSDElementDeclaration> elementsForResolve) {
        for (final XSDElementDeclaration xsdElementDeclaration : elementsForResolve) {
            final XSDElementDeclaration newResolveElementDeclaration = resolveUtils().resolveElementDeclaration(changedSchema,
                    changedSchema.getTargetNamespace(), xsdElementDeclaration.getResolvedElementDeclaration().getName());
            if (newResolveElementDeclaration != null) {
                // the element declaration was resolved, so we proceed with
                // the reference update

                final String prefix = prefixUtils().extractPrefixFromQName(
                        xsdElementDeclaration.getElement().getAttribute(XSDConstants.REF_ATTRIBUTE));
                final String tnsForPrefix = changedSchema.getQNamePrefixToNamespaceMap().get(prefix);
                if (isElementCorrectlyResolved(changedSchema, changedSchema.getElement(), newResolveElementDeclaration
                        .getTargetNamespace(), prefix, tnsForPrefix)) {
                    xsdElementDeclaration.setResolvedElementDeclaration(newResolveElementDeclaration);
                }
            }
        }
    }

    private void reconcileAttributesReferences(final XSDSchema changedSchema,
            final List<XSDAttributeDeclaration> attributesForResolve) {
        for (final XSDAttributeDeclaration xsdAttributeDeclaration : attributesForResolve) {
            final XSDAttributeDeclaration newResolveAttributeDeclaration = resolveUtils().resolveAttributeDeclaration(
                    changedSchema, changedSchema.getTargetNamespace(),
                    xsdAttributeDeclaration.getResolvedAttributeDeclaration().getName());
            if (newResolveAttributeDeclaration != null) {

                final String prefix = prefixUtils().extractPrefixFromQName(
                        xsdAttributeDeclaration.getElement().getAttribute(XSDConstants.REF_ATTRIBUTE));
                final String tnsForPrefix = changedSchema.getQNamePrefixToNamespaceMap().get(prefix);
                if (isElementCorrectlyResolved(changedSchema, changedSchema.getElement(), newResolveAttributeDeclaration
                        .getTargetNamespace(), prefix, tnsForPrefix)) {
                    xsdAttributeDeclaration.setResolvedAttributeDeclaration(newResolveAttributeDeclaration);
                }
            }
        }
    }

    // =========================================================
    // reconcile WSDL element references
    // =========================================================

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
        for (final Part part : messagePartsForResolve) {
            if (part.getElementDeclaration() != null) {
                reconcileMessagePartReference(changedSchema, definition, part);
            }
        }
    }

    private void reconcileMessagePartReference(final XSDSchema changedSchema, final Definition definition, final Part part) {
        // try and resolve the message using the current resolverScheam
        final XSDElementDeclaration newResolvedElementDeclaration = resolveUtils().resolveElementDeclaration(changedSchema, null,
                part.getElementName().getLocalPart());
        if (newResolvedElementDeclaration == null) {
            // the attribute declaration was not resolved. the message must be
            // in another schema
            return;
        }

        final String prefix = prefixUtils().extractPrefixFromQName(
                part.getElement().getAttribute(WSDLConstants.ELEMENT_ATTRIBUTE));
        final String tnsForPrefix = changedSchema.getQNamePrefixToNamespaceMap().get(prefix);

        if (isElementCorrectlyResolved(changedSchema, definition.getElement(),
                newResolvedElementDeclaration.getTargetNamespace(), prefix, tnsForPrefix)) {
            part.setElementName(new QName(tnsForPrefix, part.getElementName().getLocalPart(), part.getElementName().getPrefix()));
            part.setElementDeclaration(newResolvedElementDeclaration);
        }
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

    private void processMessageReference(final MessageReference messageReference, final Message message, String prefix) {
        if (messageReference != null && (messageReference.getEMessage() == null
        /*
         * || ( messageReference . getEMessage ( ) . getEnclosingDefinition ( )
         * . getTargetNamespace ( ) != null && ! messageReference . getEMessage
         * ( ) . getEnclosingDefinition ( ) . getTargetNamespace ( ) . equals (
         * message . getQName ( ) . getNamespaceURI ( ) ) )
         */)) {

            final String messageQName = messageReference.getElement().getAttribute(WSDLConstants.MESSAGE_ATTRIBUTE);
            if (messageQName == null) {
                return;
            }
            final String messageName = messageQName.substring(messageQName.indexOf(':') + 1);
            if (prefix == null) {
                prefix = prefixUtils().extractPrefixFromQName(messageQName);
            }

            // check if this is the correct message to be resolved
            if (message.getQName().getLocalPart().equals(messageName)
                    && (message.getQName().getPrefix().equals(prefix == null ? "" : prefix) || message.getQName().getPrefix() //$NON-NLS-1$
                            .equals(""))) { //$NON-NLS-1$
                messageReference.setEMessage(message);
            }
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
            String prefix = prefixUtils().extractPrefixFromQName(portBindingQName);
            if (prefix == null) {
                prefix = ""; //$NON-NLS-1$
            }

            // we need to search all the bindings in the definition, so that we
            // can find the correct binding to set to the portBinding
            for (final Binding binding : eBindings) {
                if (binding.getQName().getLocalPart().equals(portBindingName)
                        && binding.getQName().getNamespaceURI().equals(definition.getNamespaces().get(prefix))) {
                    port.setEBinding(binding);
                    break;
                }
            }
        }
    }

    // =========================================================
    // resolve helpers
    // =========================================================

    protected IResolveUtils resolveUtils() {
        return ResolveUtils.instance();
    }

    // =========================================================
    // helpers
    // =========================================================

    /**
     * Utility method. Checks if the prefix is an actual and valid one, so that
     * we can safely set the new reference.
     */
    private boolean isElementCorrectlyResolved(final XSDSchema schema, final Element element,
            final String resolvedTargetNamespace, final String prefix, final String tnsForPrefix) {

        final boolean targetNamespacesAreTheSame = (tnsForPrefix != null && tnsForPrefix.equals(resolvedTargetNamespace));

        // this is used to resolve referred elements from importing schemas
        final boolean prefixValueMatchesResolvedTargetNamespace = (schema.getQNamePrefixToNamespaceMap().get(prefix) != null && schema
                .getQNamePrefixToNamespaceMap().get(prefix).equals(resolvedTargetNamespace));

        return targetNamespacesAreTheSame
                && (element.getAttribute(tnsPrefixAttribute(prefix)) != null || prefixValueMatchesResolvedTargetNamespace);
    }

    /**
     * Utility method. It builds the tns prefix attribute for the given prefix.
     * If the prefix is some custom prefix (for e.g. "ns1"), it returns "xmlns:"
     * + "ns1". <br>
     * <br>
     * If the prefix is the default one, it simply returns it.
     */
    private String tnsPrefixAttribute(final String prefix) {
        String prefixAttribute = null;
        if (EmfXsdUtils.XMLNS_PREFIX.equals(prefix)) {
            prefixAttribute = EmfXsdUtils.XMLNS_PREFIX;
        } else {
            prefixAttribute = EmfXsdUtils.XMLNS_PREFIX + ':' + prefix;
        }
        return prefixAttribute;
    }

    private UpdateNSPrefixUtils prefixUtils() {
        return UpdateNSPrefixUtils.instance();
    }

}
