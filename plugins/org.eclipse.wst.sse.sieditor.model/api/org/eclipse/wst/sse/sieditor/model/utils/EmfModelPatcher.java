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
package org.eclipse.wst.sse.sieditor.model.utils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.wst.wsdl.Definition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

public class EmfModelPatcher implements IEmfModelPatcher {

    private static final int MAX_PARENTS_TO_REFRESH = 10;
    private static final int MAX_SEARCH_DEPTH = 15;

    private static final IEmfModelPatcher INSTANCE = new EmfModelPatcher();

    private EmfModelPatcher() {

    }

    public static IEmfModelPatcher instance() {
        return INSTANCE;
    }

    @Override
    public void patchEMFModelAfterDomChange(final IModelRoot modelRoot, final Set<Node> changedNodes) {
        if (modelRoot instanceof IXSDModelRoot) {
            this.patchXsdModelAfterDomChange((IXSDModelRoot) modelRoot, changedNodes);
        } else {
            this.patchWsdlModelAfterDomChange((IWsdlModelRoot) modelRoot, changedNodes);
        }
        changedNodes.clear();
    }

    private void patchWsdlModelAfterDomChange(final IWsdlModelRoot wsdlRoot, final Set<Node> changedNodes) {
        final Definition definition = wsdlRoot.getDescription().getComponent();

        final Element element = definition.getElement();
        if (element == null) {
            return;
        }

        definition.elementChanged(element);
        refreshDefinitionTargetNamespace(definition, element);

        if (EmfWsdlUtils.isSchemaForSchemaMissingForAnySchema(wsdlRoot)) {
            return;
        }

        final List<ISchema> containedSchemas = wsdlRoot.getDescription().getContainedSchemas();
        checkTargetNamespaceAttribute(containedSchemas);
        updateChangedNodes(changedNodes, definition, containedSchemas);

        for (final ISchema schema : containedSchemas) {
            this.patchXsdModelAfterDomChange((IXSDModelRoot) schema.getModelRoot(), (HashSet<Node>) changedNodes);
        }
    }

    /**
     * Explicit refresh of the targetNamespace in case of missing such
     * attribute. Due to Bug in EMF model - TNS is not updated if the attribute
     * is missing, though the change might be the sole attribute's deletion.
     */
    private void refreshDefinitionTargetNamespace(final Definition definition, final Element element) {
        if (!element.hasAttribute(XSDConstants.TARGETNAMESPACE_ATTRIBUTE)) {
            definition.setTargetNamespace(null);
        }
    }

    private void patchXsdModelAfterDomChange(final IXSDModelRoot iXsdModelRoot, final Set<Node> changedNodes) {
        final ISchema iSchema = iXsdModelRoot.getSchema();
        final XSDSchema xsdSchema = iSchema.getComponent();
        if (xsdSchema.getElement() == null) {
            return;
        }

        xsdSchema.elementChanged(xsdSchema.getElement());
        if (EmfXsdUtils.isSchemaForSchemaMissing(iSchema)) {
            return;
        }
        this.updateChangedNodes(changedNodes, iSchema);
    }

    private void updateChangedNodes(final Set<Node> changedNodes, final Definition definition,
            final List<ISchema> containedSchemas) {
        final Set<Node> updatedNodes = updateChangedNodes(changedNodes, containedSchemas);
        changedNodes.removeAll(updatedNodes);
    }

    private void updateChangedNodes(final Set<Node> changedNodes, final ISchema schema) {
        final List<ISchema> schemas = new LinkedList<ISchema>();
        schemas.add(schema);
        final Set<Node> updatedNodes = updateChangedNodes(changedNodes, schemas);
        changedNodes.removeAll(updatedNodes);
    }

    private Set<Node> updateChangedNodes(final Set<Node> changedNodes, final List<ISchema> containedSchemas) {
        final Set<XSDConcreteComponent> updatedComponents = new HashSet<XSDConcreteComponent>();
        final Set<Node> updatedNodes = new HashSet<Node>();
        
        for (final Node node : changedNodes) {
            for (final ISchema containedSchema : containedSchemas) {
                final XSDSchema xsdSchema = containedSchema.getComponent();

                final XSDConcreteComponent component = findCorrespondingRootComponent(node, xsdSchema);
                if (component != null && !updatedComponents.contains(component) && xsdSchema != component) {
                    updatedComponents.add(component);
                    updatedNodes.add(node);
                    EmfXsdUtils.updateModelReferencers(xsdSchema, component);
                    break;
                }

            }
        }
        return updatedNodes;
    }

    private XSDConcreteComponent findCorrespondingRootComponent(final Node initialNode, final XSDSchema containerSchema) {
        XSDConcreteComponent component = null;
        Node currentNode = initialNode;

        int currentDepth = 0;
        do {
            if (currentNode instanceof Attr) {
                currentNode = ((Attr) currentNode).getOwnerElement();
            }
            if (currentNode == null) {
                break;
            }
            if (component != null && component.eContainer() instanceof XSDConcreteComponent) {
                component = (XSDConcreteComponent) component.eContainer();
            } else {
                component = containerSchema.getCorrespondingComponent(currentNode);
            }
            refreshComponents(component);

            if (component instanceof XSDNamedComponent && ((XSDNamedComponent) component).getName() == null) {
                component = containerSchema;
            }
            currentNode = currentNode.getParentNode();
            currentDepth++;
        } while (component instanceof XSDSchema && currentNode != null
                && !currentNode.getNodeName().endsWith(XSDConstants.SCHEMA_ELEMENT_TAG) && currentDepth < MAX_SEARCH_DEPTH);

        return component;
    }

    // =========================================================
    // helpers
    // =========================================================

    /**
     * check the state of targetNamespace attribute and explicit refresh of the
     * targetNamespace in case of missing such attribute due to the bug: <br>
     * TNS is not updated if the attribute is missing, though the change might
     * be the sole attribute's deletion
     * 
     */
    private void checkTargetNamespaceAttribute(final List<ISchema> containedSchemas) {
        for (final ISchema schema : containedSchemas) {
            if (schema == null) {
                continue;
            }
            final XSDSchema currerntSchema = schema.getComponent();
            final Element element = currerntSchema.getElement();
            if (element == null) {
                continue;
            }
            if (!element.hasAttribute(XSDConstants.TARGETNAMESPACE_ATTRIBUTE)) {
                currerntSchema.setTargetNamespace(null);
            }
        }
    }

    private void refreshComponents(final XSDConcreteComponent component) {
        refreshComponentsInternal(component, 0);
    }

    private void refreshComponentsInternal(final XSDConcreteComponent component, final int currentRefreshLevel) {
        if (component == null) {
            return;
        }
        component.elementChanged(component.getElement());

        // if (component instanceof XSDNamedComponent && ((XSDNamedComponent)
        // component).getName() != null) {
        // return;
        // }

        if (currentRefreshLevel < MAX_PARENTS_TO_REFRESH && component.eContainer() instanceof XSDConcreteComponent) {
            refreshComponentsInternal((XSDConcreteComponent) component.eContainer(), currentRefreshLevel + 1);
        }
    }

}
