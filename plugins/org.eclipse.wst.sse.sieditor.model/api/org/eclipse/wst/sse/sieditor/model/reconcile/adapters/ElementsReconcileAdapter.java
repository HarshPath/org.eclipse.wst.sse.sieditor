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
package org.eclipse.wst.sse.sieditor.model.reconcile.adapters;

import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.wsdl.Message;
import org.eclipse.wst.wsdl.Types;
import org.eclipse.wst.wsdl.XSDSchemaExtensibilityElement;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.impl.XSDImportImpl;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

import org.eclipse.wst.sse.sieditor.model.reconcile.adapters.componentsource.IConcreteComponentSource;
import org.eclipse.wst.sse.sieditor.model.reconcile.adapters.utils.ConcreteComponentsCacheUtils;

/**
 * Subclass of the {@link AbstractModelReconcileAdapter}. This adapter is
 * responsible for the fixing of problems caused by DOM element changes -
 * removal, addition.
 * 
 */
public class ElementsReconcileAdapter extends AbstractModelReconcileAdapter {

    public ElementsReconcileAdapter(final IConcreteComponentSource concreteComponentSource) {
        super(concreteComponentSource);
    }

    @Override
    protected void processNotifyChanged(final INodeNotifier notifier, final int eventType, final Object changedFeature,
            final Object oldValue, final Object newValue, final int pos) {

        processChangeOfSchemaContent(notifier, changedFeature, eventType, INodeNotifier.REMOVE);
        processChangeOfSchemaContent(notifier, newValue, eventType, INodeNotifier.ADD);

        processMessagePartRemoved(notifier, changedFeature, newValue, eventType);

        processChangeOfTypesNamespace(notifier, newValue, eventType, INodeNotifier.ADD);
        processChangeOfTypesNamespace(notifier, oldValue, eventType, INodeNotifier.REMOVE);

        processChangeOfXsdInclude(notifier, newValue, eventType, INodeNotifier.ADD);
        processChangeOfXsdInclude(notifier, oldValue, eventType, INodeNotifier.REMOVE);

        processChangeOfXsdImport(notifier, newValue, eventType, INodeNotifier.ADD);
        processChangeOfXsdImport(notifier, oldValue, eventType, INodeNotifier.REMOVE);

        processChangeOfAttribute(notifier, newValue, eventType, INodeNotifier.ADD);
        processChangeOfAttribute(notifier, oldValue, eventType, INodeNotifier.REMOVE);

    }

    private void processChangeOfSchemaContent(final Object notifier, final Object changedFeature, final int eventType,
            final int expectedEventType) {
        if (!(changedFeature instanceof Element) || !(notifier instanceof Element) || eventType != expectedEventType) {
            return;
        }
        final EObject containerObject = concreteComponentSource.getConcreteComponentFor((Element) notifier);

        if (!(containerObject instanceof XSDSchema) && !(containerObject instanceof XSDSchemaExtensibilityElement)) {
            return;
        }

        final String nodeName = ((Element) changedFeature).getNodeName();
        final String nodeSimpleName = nodeName.substring(nodeName.indexOf(':') + 1);

        if (XSDConstants.ELEMENT_ELEMENT_TAG.equals(nodeSimpleName) || XSDConstants.SIMPLETYPE_ELEMENT_TAG.equals(nodeSimpleName)
                || XSDConstants.COMPLEXTYPE_ELEMENT_TAG.equals(nodeSimpleName)
                || XSDConstants.ATTRIBUTE_ELEMENT_TAG.equals(nodeSimpleName)
                || XSDConstants.ATTRIBUTEGROUP_ELEMENT_TAG.equals(nodeSimpleName)
                || XSDConstants.GROUP_ELEMENT_TAG.equals(nodeSimpleName)) {

            final XSDSchema schema = containerObject instanceof XSDSchema ? (XSDSchema) containerObject
                    : ((XSDSchemaExtensibilityElement) containerObject).getSchema();
            schema.elementChanged(schema.getElement());
            getModelReconcileRegistry().setNeedsReconciling(true);
            getModelReconcileRegistry().addChangedSchema(schema);
        }
    }

    private void processMessagePartRemoved(final Object notifier, final Object changedFeature, final Object newValue,
            final int eventType) {
        if (!(changedFeature instanceof Element) || !(notifier instanceof Element)) {
            return;
        }
        final EObject containerObject = concreteComponentSource.getConcreteComponentFor((Element) notifier);
        if (containerObject instanceof Message && newValue == null) {
            getModelReconcileRegistry().setNeedsReconciling(true);
        }
    }

    @SuppressWarnings("unchecked")
    private void processChangeOfTypesNamespace(final INodeNotifier notifier, final Object schemaElementObject,
            final int eventType, final int expectedEventType) {
        if (!(notifier instanceof Element) || !(schemaElementObject instanceof Element) || eventType != expectedEventType) {
            return;
        }
        final Element schemaElement = (Element) schemaElementObject;
        if (!(XSDConstants.SCHEMA_ELEMENT_TAG.equals(schemaElement.getLocalName().substring(
                schemaElement.getLocalName().indexOf(':') + 1)))) {
            return;
        }

        final EObject eNotifier = concreteComponentSource.getConcreteComponentFor((Element) notifier);

        if (!(eNotifier instanceof Types)) {
            return;
        }
        final Types eTypes = (Types) eNotifier;
        eTypes.elementChanged(eTypes.getElement());

        final List<XSDSchema> xsdSchemas = eTypes.getSchemas();

        for (final XSDSchema xsdSchema : xsdSchemas) {
            cacheUtils().clearConcreteComponentsCacheForSchema(xsdSchema, eTypes);
            updateReferences(xsdSchema, eTypes, schemaElement);
            xsdSchema.elementChanged(xsdSchema.getElement());
        }

        getModelReconcileRegistry().setNeedsReconciling(true);
    }

    private void processChangeOfXsdInclude(final INodeNotifier notifier, final Object includeValue, final int eventType,
            final int expectedEventType) {
        final String schemaDirectiveTag = XSDConstants.INCLUDE_ELEMENT_TAG;
        processChangeOfXsdSchemaDirective(notifier, includeValue, eventType, expectedEventType, schemaDirectiveTag);
    }

    private void processChangeOfXsdImport(final INodeNotifier notifier, final Object importValue, final int eventType,
            final int expectedEventType) {
        final String schemaDirectiveTag = XSDConstants.IMPORT_ELEMENT_TAG;
        final XSDSchema xsdSchema = processChangeOfXsdSchemaDirective(notifier, importValue, eventType, expectedEventType,
                schemaDirectiveTag);
        if (xsdSchema == null) {
            return;
        }
        if (eventType == INodeNotifier.REMOVE) {
            cacheUtils().clearConcreteComponentsCacheForSchema(xsdSchema, null);
        }
    }

    private XSDSchema processChangeOfXsdSchemaDirective(final INodeNotifier notifier, final Object includeValue,
            final int eventType, final int expectedEventType, final String schemaDirectiveTag) {
        if (!(notifier instanceof Element) || !(includeValue instanceof Element) || eventType != expectedEventType) {
            return null;
        }
        final EObject eContainerObject = concreteComponentSource.getConcreteComponentFor((Element) notifier);

        XSDSchema xsdSchema = null;
        if (eContainerObject instanceof XSDSchema) {
            xsdSchema = (XSDSchema) eContainerObject;
        } else if (eContainerObject instanceof XSDSchemaExtensibilityElement) {
            xsdSchema = ((XSDSchemaExtensibilityElement) eContainerObject).getSchema();
        } else {
            return null;
        }

        final String nodeName = ((Element) includeValue).getNodeName();
        final String simpleNodeName = nodeName.substring(nodeName.indexOf(':') + 1);
        if (!schemaDirectiveTag.equals(simpleNodeName)) {
            return null;
        }

        xsdSchema.elementChanged(xsdSchema.getElement());

        getModelReconcileRegistry().setNeedsReconciling(true);
        getModelReconcileRegistry().addChangedSchema(xsdSchema);

        return xsdSchema;
    }

    private void processChangeOfAttribute(final INodeNotifier notifier, final Object changedElement, final int eventType,
            final int expectedEvent) {
        if (eventType != expectedEvent
                || !(notifier instanceof Element)
                || !(changedElement instanceof Element)
                || !XSDConstants.ATTRIBUTE_ELEMENT_TAG.equals(((Element) changedElement).getNodeName().substring(
                        ((Element) changedElement).getNodeName().indexOf(':') + 1))) {
            return;
        }
        final EObject eObject = concreteComponentSource.getConcreteComponentFor((Element) notifier);

        if (eObject instanceof XSDTypeDefinition) {
            final XSDTypeDefinition xsdTypeDefinition = (XSDTypeDefinition) eObject;
            if (xsdTypeDefinition.getName() == null) {
                final XSDConcreteComponent container = (XSDConcreteComponent) xsdTypeDefinition.eContainer();
                container.elementChanged(container.getElement());
            }
        }

        getModelReconcileRegistry().setNeedsReconciling(true);
    }

    private void updateReferences(final XSDSchema xsdSchema, final Types eTypes, final Element oldValue) {
        for (final XSDSchemaContent schemaContent : xsdSchema.getContents()) {
            if (!(schemaContent instanceof XSDImportImpl)) {
                continue;
            }

            final XSDImportImpl xsdImport = (XSDImportImpl) schemaContent;
            xsdImport.elementChanged(xsdImport.getElement());

            XSDSchema resolvedSchema = xsdImport.getResolvedSchema();
            if (resolvedSchema == null || resolvedSchema.eContainer() instanceof XSDSchemaExtensibilityElement
                    && resolvedSchema.eContainer().eContainer() == null) {
                xsdImport.reset();
                resolvedSchema = xsdImport.importSchema();
            }

            if (xsdImport.getSchemaLocation() == null
                    && (!eTypes.getSchemas().contains(resolvedSchema) || resolvedSchema.getElement() == oldValue)) {
                xsdImport.setResolvedSchema(null);
            }
        }
    }

    // =========================================================
    // helpers
    // =========================================================

    private ConcreteComponentsCacheUtils cacheUtils() {
        return ConcreteComponentsCacheUtils.instance();
    }
}
