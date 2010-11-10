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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.wsdl.Message;
import org.eclipse.wst.wsdl.Types;
import org.eclipse.wst.wsdl.XSDSchemaExtensibilityElement;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

import org.eclipse.wst.sse.sieditor.model.reconcile.adapters.componentsource.IConcreteComponentSource;

/**
 * Subclass of the {@link AbstractModelReconcileAdapter}. This adapter is
 * responsible for the fixing of problems caused by DOM element changes -
 * removal, addition.
 * 
 * 
 * 
 */
public class ElementsReconcileAdapter extends AbstractModelReconcileAdapter {

    public ElementsReconcileAdapter(final IConcreteComponentSource concreteComponentSource) {
        super(concreteComponentSource);
    }

    @Override
    protected void processNotifyChanged(final INodeNotifier notifier, final int eventType, final Object changedFeature,
            final Object oldValue, final Object newValue, final int pos) {

        if (changedFeature instanceof Element && notifier instanceof Element) {
            final EObject containerObject = concreteComponentSource.getConcreteComponentFor((Element) notifier);
            processSchemaElementRemoved(changedFeature, containerObject);
            processMessagePartRemoved(changedFeature, newValue, containerObject);
        }
        if (changedFeature == null && notifier instanceof Element) {
            processAddSchemaNamespace(notifier, newValue);
            processRemoveSchemaNamespace(notifier);
        }
    }

    private void processSchemaElementRemoved(final Object changedFeature, final EObject containerObject) {
        if (containerObject instanceof XSDSchema || containerObject instanceof XSDSchemaExtensibilityElement) {

            final String nodeName = ((Element) changedFeature).getNodeName();
            if (XSDConstants.ELEMENT_ELEMENT_TAG.equals(nodeName.substring(nodeName.indexOf(':') + 1))) {
                final XSDSchema schema = containerObject instanceof XSDSchema ? (XSDSchema) containerObject
                        : ((XSDSchemaExtensibilityElement) containerObject).getSchema();
                getModelReconcileRegistry().setNeedsReconciling(true);
                getModelReconcileRegistry().addChangedSchema(schema);
            }
        }
    }

    private void processMessagePartRemoved(final Object changedFeature, final Object newValue, final EObject containerObject) {
        if (containerObject instanceof Message && newValue == null) {
            getModelReconcileRegistry().setNeedsReconciling(true);
        }
    }

    private void processAddSchemaNamespace(final INodeNotifier notifier, final Object schemaElement) {
        if (schemaElement == null || !(schemaElement instanceof Element)) {
            return;
        }
        final EObject eNotifier = concreteComponentSource.getConcreteComponentFor((Element) notifier);
        if (eNotifier instanceof Types) {
            final String nodeName = ((Element) schemaElement).getNodeName();
            if (XSDConstants.SCHEMA_ELEMENT_TAG.equals(nodeName.substring(nodeName.indexOf(':') + 1))) {
                getModelReconcileRegistry().setNeedsReconciling(true);
            }
        }
    }

    private void processRemoveSchemaNamespace(final INodeNotifier notifier) {
        final EObject eNotifier = concreteComponentSource.getConcreteComponentFor((Element) notifier);
        if (eNotifier instanceof Types) {
            getModelReconcileRegistry().setNeedsReconciling(true);
        }
    }

}
