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
package org.eclipse.wst.sse.sieditor.model.factory;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.wst.sse.core.internal.provisional.IModelStateListener;
import org.eclipse.wst.xsd.ui.internal.text.XSDModelAdapter;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.util.XSDResourceImpl;
import org.w3c.dom.Document;

import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.impl.XSDFactory;
import org.eclipse.wst.sse.sieditor.model.reconcile.adapters.AnnotationsReconcileAdapter;
import org.eclipse.wst.sse.sieditor.model.reconcile.adapters.AttributesReconcileAdapter;
import org.eclipse.wst.sse.sieditor.model.reconcile.adapters.ElementsReconcileAdapter;
import org.eclipse.wst.sse.sieditor.model.reconcile.adapters.componentsource.XsdSchemaConcreteComponentSource;

/**
 * This is the Data Types Editor model root factory. It builds the
 * {@link IXSDModelRoot} from the given document.
 * 
 * 
 */
public class DataTypesModelRootFactory extends AbstractModelRootFactory {

    protected static DataTypesModelRootFactory INSTANCE = new DataTypesModelRootFactory();

    protected DataTypesModelRootFactory() {

    }

    public static DataTypesModelRootFactory instance() {
        return INSTANCE;
    }

    @Override
    public IModelRoot createModelRootFromDocument(final Document document) {
        final XSDModelAdapter modelAdapter = XSDModelAdapter.lookupOrCreateModelAdapter(document);
        final XSDSchema schema = modelAdapter.createSchema(document);

        // update schemes, so that any QNames are properly resolved.
        final EList<Resource> resources = schema.eResource().getResourceSet().getResources();
        for (final Resource res : resources) {
            if (res instanceof XSDResourceImpl) {
                ((XSDResourceImpl) res).getSchema().update();
            }
        }
        return XSDFactory.getInstance().createXSDModelRoot(schema);
    }

    @Override
    protected List<IModelStateListener> getCustomModelStateListeners(final IModelRoot modelRoot) {
        final List<IModelStateListener> customModelStateListeners = new LinkedList<IModelStateListener>();

        final EObject component = modelRoot.getModelObject().getComponent();
        final XsdSchemaConcreteComponentSource concreteComponentSource = new XsdSchemaConcreteComponentSource(
                (XSDSchema) component);

        customModelStateListeners.add(new AttributesReconcileAdapter(concreteComponentSource));
        customModelStateListeners.add(new ElementsReconcileAdapter(concreteComponentSource));
        customModelStateListeners.add(new AnnotationsReconcileAdapter(concreteComponentSource));

        return customModelStateListeners;
    }
}
