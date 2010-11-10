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
package org.eclipse.wst.sse.sieditor.command.emf.common.namespace;

import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.query.conditions.eobjects.EObjectCondition;
import org.eclipse.emf.query.conditions.eobjects.structuralfeatures.EObjectReferencerCondition;
import org.eclipse.emf.query.statements.FROM;
import org.eclipse.emf.query.statements.IQueryResult;
import org.eclipse.emf.query.statements.SELECT;
import org.eclipse.emf.query.statements.WHERE;
import org.eclipse.wst.wsdl.Binding;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.Message;
import org.eclipse.wst.wsdl.Part;
import org.eclipse.wst.wsdl.Port;
import org.eclipse.wst.wsdl.PortType;
import org.eclipse.wst.wsdl.Service;
import org.eclipse.wst.wsdl.util.WSDLConstants;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDAttributeGroupDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroupDefinition;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;
import org.eclipse.xsd.XSDTypeDefinition;

import org.eclipse.wst.sse.sieditor.model.reconcile.ReconcileUtils;

/**
 * This is the element references updater class. This class has methods for
 * remapping definition message parts and schema element references.
 * 
 * 
 * 
 */
public class RemapReferencesUtils {

    private static final RemapReferencesUtils INSTANCE = new RemapReferencesUtils();

    private RemapReferencesUtils() {

    }

    public static RemapReferencesUtils instance() {
        return INSTANCE;
    }

    // =========================================================
    // element schema references remap methods
    // =========================================================

    /**
     * Method for remapping schema references
     * 
     * @param baseComponent
     * @param changedSchema
     * @param namespacePrefix
     */
    public void remapSchemaReferences(final EObject baseComponent, final XSDSchema changedSchema, final String namespacePrefix) {
        final EList<XSDSchemaContent> contents = changedSchema.getContents();
        for (final XSDSchemaContent xsdSchemaContent : contents) {
            if (needsToRemapReferencesForContent(xsdSchemaContent)) {
                final EObjectCondition refCondition = new EObjectReferencerCondition(xsdSchemaContent);
                final IQueryResult result = new SELECT(new FROM(baseComponent), new WHERE(refCondition)).execute();
                processQueryResult(changedSchema, namespacePrefix, xsdSchemaContent, result);
            }
        }
    }

    /**
     * Utility method. Checks if we need to start the remapping of references
     * for the given schema content.
     * 
     * @param xsdSchemaContent
     * @return
     */
    private boolean needsToRemapReferencesForContent(final XSDSchemaContent xsdSchemaContent) {
        return xsdSchemaContent instanceof XSDElementDeclaration || xsdSchemaContent instanceof XSDTypeDefinition
                || xsdSchemaContent instanceof XSDAttributeDeclaration || xsdSchemaContent instanceof XSDAttributeGroupDefinition
                || xsdSchemaContent instanceof XSDModelGroupDefinition;
    }

    private void processQueryResult(final XSDSchema changedSchema, final String namespacePrefix,
            final XSDSchemaContent xsdSchemaContent, final IQueryResult result) {
        for (final Object next : result) {
            if (next instanceof XSDSchema || next instanceof Definition) {
                continue; // we do not need to check root elements
            }
            if (next != xsdSchemaContent) {
                if (next instanceof XSDParticle) {
                    processParticle(changedSchema, namespacePrefix, next);
                } else if (next instanceof XSDElementDeclaration) {
                    processElementDeclaration(changedSchema, namespacePrefix, next);
                } else if (next instanceof XSDAttributeDeclaration) {
                    processAttributeDeclaration(changedSchema, namespacePrefix, next);
                } else if (next instanceof XSDAttributeGroupDefinition) {
                    processAttributeGroupDefinition(changedSchema, namespacePrefix, next);
                } else if (next instanceof XSDModelGroupDefinition) {
                    processModelGroupDefinition(changedSchema, namespacePrefix, next);
                }
            }
        }
    }

    private void processParticle(final XSDSchema changedSchema, final String namespacePrefix, final Object next) {
        final XSDParticle particle = (XSDParticle) next;
        if (particle.getContent() instanceof XSDElementDeclaration) {
            final XSDElementDeclaration xsdElementDeclaration = (XSDElementDeclaration) particle.getContent();
            elementPrefixUtils().updateXsdElementDeclaration(changedSchema, namespacePrefix, xsdElementDeclaration);
        }
    }

    private void processElementDeclaration(final XSDSchema changedSchema, final String namespacePrefix, final Object next) {
        final XSDElementDeclaration xsdElementDeclaration = (XSDElementDeclaration) next;
        elementPrefixUtils().updateXsdElementDeclaration(changedSchema, namespacePrefix, xsdElementDeclaration);
    }

    private void processAttributeDeclaration(final XSDSchema changedSchema, final String namespacePrefix, final Object next) {
        final XSDAttributeDeclaration xsdAttributeDeclaration = (XSDAttributeDeclaration) next;
        elementPrefixUtils().updateXsdAttributeDeclaration(changedSchema, namespacePrefix, xsdAttributeDeclaration);
    }

    private void processAttributeGroupDefinition(final XSDSchema changedSchema, final String namespacePrefix, final Object next) {
        final XSDAttributeGroupDefinition attributeGroup = (XSDAttributeGroupDefinition) next;
        elementPrefixUtils().updateXsdAttributeGroupDeclaration(changedSchema, namespacePrefix, attributeGroup);
    }

    private void processModelGroupDefinition(final XSDSchema changedSchema, final String namespacePrefix, final Object next) {
        final XSDModelGroupDefinition xsdModelGroupDefinition = (XSDModelGroupDefinition) next;
        elementPrefixUtils().updateXsdModelGroupDeclaration(changedSchema, namespacePrefix, xsdModelGroupDefinition);
    }

    // =========================================================
    // message parts remap methods
    // =========================================================

    /**
     * Method for remapping the message parts to the new namespace. If the given
     * schema is <code>null</code>, we are changing definition target namespace
     */
    @SuppressWarnings("unchecked")
    public void remapMessageParts(final Definition editedDefinition, final XSDSchema schema, final String oldNamespaceValue,
            final String newNamespaceValue, final String namespacePrefix) {
        final EList<Message> messages = editedDefinition.getEMessages();
        if (messages == null) {
            return;
        }
        for (final Message message : messages) {
            processMessageParts(editedDefinition, schema, oldNamespaceValue, newNamespaceValue, namespacePrefix, message);
        }
    }

    @SuppressWarnings("unchecked")
    private void processMessageParts(final Definition editedDefinition, final XSDSchema schema, final String oldNamespaceValue,
            final String newNamespaceValue, final String namespacePrefix, final Message message) {
        final List<Part> eParts = message.getEParts();
        if (eParts == null) {
            return;
        }
        for (final Part part : eParts) {
            if (part.getEMessage() != null && part.getEMessage().getEnclosingDefinition() != editedDefinition) {
                continue;
            }
            
            if (schema == null) {
                if (part.getElementDeclaration() != null
                        && part.getElementDeclaration().getTargetNamespace().equals(newNamespaceValue)) {
                    // we are changing the targetNamespace of the definition
                    part.setElementName(new QName(newNamespaceValue, part.getElementName().getLocalPart(), namespacePrefix));
                }
            } else if (part.getElementDeclaration() != null
                    && part.getElementDeclaration().getTargetNamespace().equals(oldNamespaceValue)) {

                if (part.getElementDeclaration().eContainer() == null) {
                    // we need to resolve the element references
                    final XSDElementDeclaration resolvedElementDeclaration = schema.resolveElementDeclaration(newNamespaceValue,
                            part.getElementDeclaration().getName());
                    part.setElementDeclaration(resolvedElementDeclaration);
                }

                if (part.getElementDeclaration().getContainer() == schema) {
                    // update message part QName
                    part.setElementName(new QName(schema.getQNamePrefixToNamespaceMap().get(namespacePrefix), part
                            .getElementName().getLocalPart(), namespacePrefix));
                }
            }
        }
    }

    /**
     * Utility method. Remaps the definition messages to the new prefix and
     * namespace.<br>
     * <br>
     * <i>NOTE: if we are not processing an imported definition, the
     * importedDefinition parameter should be the same as the
     * importingDefinition one.</i>
     */
    @SuppressWarnings("unchecked")
    public void remapOperationsMessagesReferences(final Definition editedDefinition, final Definition importedDefinition,
            final String namespaceValue, final String prefix) {
        final EList<PortType> portTypes = editedDefinition.getEPortTypes();
        if (portTypes == null) {
            return;
        }
        for (final PortType portType : portTypes) {
            ReconcileUtils.instance().reconcileOperationsMessagesReferences(portType.getEOperations(), editedDefinition,
                    importedDefinition.getEMessages(), prefix);
        }
    }

    /**
     * Utility method. Remaps the difinition bindings to the new prefix and
     * given namespace.</br> This method ramaps the <code>&lt;wsdl:binding
     * name="NewWSDLFileSOAP2" type="..."></code> "type" references. <br>
     * <br>
     * <i>NOTE: if we are not processing an imported definition, the
     * importedDefinition parameter should be the same as the
     * importingDefinition one.</i>
     */
    @SuppressWarnings("unchecked")
    public void remapBindingsPortTypes(final Definition editedDefinition, final Definition importedDefinition,
            final String namespaceValue) {
        final EList<Binding> bindings = editedDefinition.getEBindings();
        if (bindings == null) {
            return;
        }
        for (final Binding binding : bindings) {
            remapBindingPortType(importedDefinition, namespaceValue, binding);
        }
    }

    /**
     * Utility method. Searches the imported definition for suitable port type
     * to set.
     */
    @SuppressWarnings("unchecked")
    private void remapBindingPortType(final Definition importedDefinition, final String namespaceValue, final Binding binding) {
        if (binding.getEPortType() == null) {
            final String portTypeQName = binding.getElement().getAttribute(WSDLConstants.TYPE_ATTRIBUTE);
            final String portTypeName = portTypeQName.substring(portTypeQName.indexOf(':') + 1);

            final EList<PortType> ePortTypes = importedDefinition.getEPortTypes();

            for (final PortType portType : ePortTypes) {
                if (portType.getQName().getLocalPart().equals(portTypeName)
                        && portType.getQName().getNamespaceURI().equals(namespaceValue)) {
                    binding.setEPortType(portType);
                    return;
                }
            }
        }
    }

    /**
     * Method for updating the &lt;wsdl:service>&lt;wsdl:port binding="..." />
     * port bindings references.
     */
    @SuppressWarnings("unchecked")
    public void remapServicePortBindings(final Definition editedDefinition, final Definition importedDefinition,
            final String namespaceValue) {
        final EList<Service> eServices = editedDefinition.getEServices();
        if (eServices != null) {
            for (final Service service : eServices) {
                final EList<Port> ePorts = service.getEPorts();
                for (final Port port : ePorts) {
                    remapDefinitionPortBinding(importedDefinition, namespaceValue, port);
                }
            }
        }
    }

    /**
     * This method searches for the binding to set based on the &lt;wsdl:port
     * binding="..." /> attribute value.
     * 
     */
    @SuppressWarnings("unchecked")
    private void remapDefinitionPortBinding(final Definition importedDefinition, final String namespaceValue, final Port port) {
        if (port.getEBinding() == null) {
            // the port binding is not resolved. we need to check all the
            // bindings from the imported definition to find a matching binding
            // (we are using the DOMs attribute value for "binding")

            final String portBindingQName = port.getElement().getAttribute(WSDLConstants.BINDING_ATTRIBUTE);
            final String bindingName = portBindingQName.substring(portBindingQName.indexOf(':') + 1);

            final EList<Binding> importedDefinitionBindings = importedDefinition.getEBindings();
            for (final Binding importedBinding : importedDefinitionBindings) {
                if (importedBinding.getQName().getLocalPart().equals(bindingName)
                        && importedBinding.getQName().getNamespaceURI().equals(namespaceValue)) {
                    port.setEBinding(importedBinding);
                    break;
                }
            }
        }
    }

    // =========================================================
    // helpers
    // =========================================================

    protected UpdateNSPrefixUtils elementPrefixUtils() {
        return UpdateNSPrefixUtils.instance();
    }

}
