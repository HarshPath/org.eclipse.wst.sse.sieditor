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
package org.eclipse.wst.sse.sieditor.model.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.wst.wsdl.Definition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

public class EmfModelPatcher implements IEmfModelPatcher {

    private static final IEmfModelPatcher INSTANCE = new EmfModelPatcher();

    private EmfModelPatcher() {

    }

    public static IEmfModelPatcher instance() {
        return INSTANCE;
    }

    @Override
    public void patchEMFModelAfterDomChange(final IModelRoot modelRoot, final Set<Node> changedNodes) {
        if (modelRoot instanceof IXSDModelRoot) {
            this.patchEMFModelAfterDomChange((IXSDModelRoot) modelRoot, changedNodes);
        } else {
            this.patchEMFModelAfterDomChange((IWsdlModelRoot) modelRoot, changedNodes);
        }
    }

    private void patchEMFModelAfterDomChange(final IWsdlModelRoot wsdlRoot, final Set<Node> changedNodes) {
        final Definition definition = wsdlRoot.getDescription().getComponent();
        final Element element = definition.getElement();
        if (element == null || EmfWsdlUtils.isSchemaForSchemaMissingForAnySchema(wsdlRoot)) {
            return;
        }
        definition.elementChanged(element);
        // explicit refresh of the targetNamespace in case of
        // missing such attribute. Due to Bug in EMF model -
        // TNS is not updated if the attribute is missing, though the change
        // might be the sole attribute's deletion.
        if (!element.hasAttribute(XSDConstants.TARGETNAMESPACE_ATTRIBUTE)) {
            definition.setTargetNamespace(null);
        }
        // update changed elements from source page in XSD schemas
        final List<ISchema> containedSchemas = wsdlRoot.getDescription().getContainedSchemas();
        // check the state of targetNamespace attribute and
        // explicit refresh of the targetNamespace in case of
        // missing such attribute. Due to the bug described above
        this.checkTargetNamespaceAttribute(containedSchemas);

        this.updateChangedNodes(changedNodes, definition, containedSchemas);
    }

    @Override
    public void updateChangedNodes(final Set<Node> changedNodes, final Definition definition, final List<ISchema> containedSchemas) {
        final Set<XSDConcreteComponent> updatedComponents = new HashSet<XSDConcreteComponent>();
        for (final Node node : changedNodes) {
            for (final ISchema schema : containedSchemas) {
                final XSDSchema xsdSchema = schema.getComponent();
                final XSDConcreteComponent component = xsdSchema.getCorrespondingComponent(node);
                if (component != null && !updatedComponents.contains(component) &&
                // case when component is not found into xsdSchema
                        xsdSchema != component) {
                    EmfXsdUtils.updateModelReferencers(definition, component);
                    updatedComponents.add(component);
                    break;
                }
            }
        }
        // Do always patch schemas, even on empty 'changedNodes'
        // There are cases when something is editted into source, and is not in changedNodes
        // e.g.: change 'targetNamespace' attribute name of schema to 'targetspace'
        for (final ISchema schema : containedSchemas) {
            this.patchEMFModelAfterDomChange((IXSDModelRoot) schema.getModelRoot(), new HashSet<Node>(changedNodes));
        }
        changedNodes.clear();
    }

    private void patchEMFModelAfterDomChange(final IXSDModelRoot ixsdModelRoot, final Set<Node> changedNodes) {
        final XSDSchema xsdSchema = ixsdModelRoot.getSchema().getComponent();
        // if there is NO shcema element - there is nothing to be patched
        if (xsdSchema.getElement() == null || EmfXsdUtils.isSchemaForSchemaMissing(ixsdModelRoot.getSchema())) {
            return;
        }
        xsdSchema.elementChanged(xsdSchema.getElement());

        this.updateChangedNodes(changedNodes, xsdSchema);
    }

    @Override
    public void updateChangedNodes(final Set<Node> changedNodes, final XSDSchema xsdSchema) {
        // update changed elements from source page
        final Set<XSDConcreteComponent> updatedComponents = new HashSet<XSDConcreteComponent>();

        for (final Node node : changedNodes) {
            final XSDConcreteComponent component = xsdSchema.getCorrespondingComponent(node);
            if (component != null && !updatedComponents.contains(component) &&
            // case when component is not found into xsdSchema
                    xsdSchema != component) {
                EmfXsdUtils.updateModelReferencers(xsdSchema, component);
                updatedComponents.add(component);
            }
        }
        changedNodes.clear();
    }

    // =========================================================
    // helpers
    // =========================================================

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

}
