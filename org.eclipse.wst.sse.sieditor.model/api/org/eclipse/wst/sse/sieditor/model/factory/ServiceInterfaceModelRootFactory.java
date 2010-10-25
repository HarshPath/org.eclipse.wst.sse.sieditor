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

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.wst.sse.core.internal.provisional.IModelStateListener;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.ui.internal.asd.facade.IDescription;
import org.eclipse.wst.wsdl.ui.internal.text.WSDLModelAdapter;
import org.eclipse.wst.wsdl.ui.internal.util.WSDLAdapterFactoryHelper;
import org.w3c.dom.Document;

import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.impl.WSDLFactory;
import org.eclipse.wst.sse.sieditor.model.reconcile.adapters.AnnotationsReconcileAdapter;
import org.eclipse.wst.sse.sieditor.model.reconcile.adapters.AttributesReconcileAdapter;
import org.eclipse.wst.sse.sieditor.model.reconcile.adapters.ElementsReconcileAdapter;
import org.eclipse.wst.sse.sieditor.model.reconcile.adapters.TransactionalWSDLModelStateListener;
import org.eclipse.wst.sse.sieditor.model.reconcile.adapters.componentsource.DefinitionConcreteComponentSource;

/**
 * This is the Service Interface Editor model root factory. It builds the
 * {@link IWsdlModelRoot} from the given document.
 * 
 * 
 */
@SuppressWarnings("restriction")
public class ServiceInterfaceModelRootFactory extends AbstractModelRootFactory {

    protected static ServiceInterfaceModelRootFactory INSTANCE = new ServiceInterfaceModelRootFactory();

    protected ServiceInterfaceModelRootFactory() {

    }

    public static ServiceInterfaceModelRootFactory instance() {
        return INSTANCE;
    }

    @Override
    protected IModelRoot createModelRootFromDocument(final Document document) {
        final WSDLModelAdapter modelAdapter = WSDLModelAdapter.lookupOrCreateModelAdapter(document);

        final IDescription model = (IDescription) WSDLAdapterFactoryHelper.getInstance().adapt(
                modelAdapter.createDefinition(document));

        final Definition commonModelDefinition = (Definition) ((Adapter) model).getTarget();

        return WSDLFactory.getInstance().createWSDLModelRoot(commonModelDefinition);
    }

    @Override
    protected List<IModelStateListener> getCustomModelStateListeners(final IModelRoot modelRoot) {
        final List<IModelStateListener> customModelStateListeners = new LinkedList<IModelStateListener>();

        final Document document = ((Definition) modelRoot.getModelObject().getComponent()).getDocument();
        final Definition definition = (Definition) modelRoot.getModelObject().getComponent();
        final DefinitionConcreteComponentSource concreteComponentSource = new DefinitionConcreteComponentSource(definition);

        customModelStateListeners.add(new TransactionalWSDLModelStateListener(document, definition));
        customModelStateListeners.add(new AttributesReconcileAdapter(concreteComponentSource));
        customModelStateListeners.add(new ElementsReconcileAdapter(concreteComponentSource));
        customModelStateListeners.add(new AnnotationsReconcileAdapter(concreteComponentSource));

        return customModelStateListeners;
    }
}
