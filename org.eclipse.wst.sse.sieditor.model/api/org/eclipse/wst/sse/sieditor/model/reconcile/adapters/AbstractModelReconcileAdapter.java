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

import org.eclipse.wst.sse.core.internal.provisional.IModelStateListener;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.eclipse.wst.sse.sieditor.core.common.IDisposable;
import org.eclipse.wst.sse.sieditor.core.common.IModelReconcileRegistry;
import org.eclipse.wst.sse.sieditor.model.reconcile.adapters.componentsource.IConcreteComponentSource;

/**
 * This adapter workarounds the following eclipse bug: The EMF model is not
 * correctly updated on DOM model change.
 * 
 * 
 * 
 */
public abstract class AbstractModelReconcileAdapter implements IModelStateListener, INodeAdapter, IDisposable {

    private IModelReconcileRegistry modelReconcileRegistry;

    protected final IConcreteComponentSource concreteComponentSource;

	private Document document;

    public AbstractModelReconcileAdapter(final IConcreteComponentSource concreteComponentSource) {
        this.concreteComponentSource = concreteComponentSource;
    }

    // =========================================================
    // adapt/unadapt methods
    // =========================================================

    public void adapt(final Document document) {
    	this.document = document;
        ((INodeNotifier) document).addAdapter(this);
        adaptChildElements(document);
    }

    /**
     * Registers this adapter to the given element and all it's children
     * 
     * @param element
     */
    protected void adapt(final Element element) {
        if (((INodeNotifier) element).getExistingAdapter(this) == null) {
            ((INodeNotifier) element).addAdapter(this);
            adaptChildElements(element);
        }
    }

    /**
     * Registers this adapter to all children of the given element node
     * 
     * @param element
     */
    private void adaptChildElements(final Node parentNode) {
        for (Node child = parentNode.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                adapt((Element) child);
            }
        }
    }

    /**
     * Unregisters this adapter to the given element and all it's children
     * 
     * @param element
     */
    private void unadapt(final Element element) {
        if (((INodeNotifier) element).getExistingAdapter(this) != null) {
            ((INodeNotifier) element).removeAdapter(this);
            unadaptChildElements(element);
        }

    }

    /**
     * Unregisters this adapter to all children of the given element node
     * 
     * @param element
     */
    private void unadaptChildElements(final Node parentNode) {
        for (Node child = parentNode.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                unadapt((Element) child);
            }
        }
    }

    @Override
    public boolean isAdapterForType(final Object type) {
        return type == this;
    }

    /**
     * Utility method. Custom implementation should be added here since the
     * {@link #notifyChanged(INodeNotifier, int, Object, Object, Object, int)}
     * method is final.
     */
    protected abstract void processNotifyChanged(final INodeNotifier notifier, final int eventType, final Object changedFeature,
            final Object oldValue, final Object newValue, final int pos);

    /**
     * Method is final in order to keep the methods call pattern.
     */
    @Override
    public final void notifyChanged(final INodeNotifier notifier, final int eventType, final Object changedFeature,
            final Object oldValue, final Object newValue, final int pos) {
        redistributeAdapter(eventType, oldValue, newValue);
        processNotifyChanged(notifier, eventType, changedFeature, oldValue, newValue, pos);
    }

    /**
     * Utility method. Returns the attribute prefix from the given attribute
     * name.
     * 
     * @param attr
     *            - the attribute that has changed
     * @return the prefix of the attribute name
     */
    protected String getPrefix(final Attr attr) {
        String prefix = null;
        if (attr.getName().indexOf(":") != -1) { //$NON-NLS-1$
            prefix = attr.getLocalName();
        }
        return prefix;
    }

    protected void redistributeAdapter(final int eventType, final Object oldValue, final Object newValue) {
        if (INodeNotifier.ADD == eventType && newValue instanceof Element) {
            adapt((Element) newValue);
        }
        if (INodeNotifier.REMOVE == eventType && oldValue instanceof Element) {
            unadapt((Element) oldValue);
        }
    }

    public void setModelReconcileRegistry(final IModelReconcileRegistry modelReconcileRegistry) {
        this.modelReconcileRegistry = modelReconcileRegistry;
    }

    public IModelReconcileRegistry getModelReconcileRegistry() {
        return modelReconcileRegistry;
    }
    
//    /**
//     * Override, so it has one instance per child class per concreteComponentSource
//     */
//    public abstract int hashCode();
//    
//    /**
//     * Override, so it has one instance per child class per concreteComponentSource
//     */
//    public abstract boolean equals(final Object obj);

    @Override
    public int hashCode() {
        return this.getClass().hashCode() & concreteComponentSource.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this.getClass().isInstance(obj)) {
            return concreteComponentSource.equals(((AbstractModelReconcileAdapter) obj).concreteComponentSource);
        }
        return false;
    }

    // =========================================================
    // dummy interface implementation
    // =========================================================

    @Override
    public void modelAboutToBeChanged(final IStructuredModel model) {
    }

    @Override
    public void modelAboutToBeReinitialized(final IStructuredModel structuredModel) {
    }

    @Override
    public void modelChanged(final IStructuredModel model) {
    }

    @Override
    public void modelDirtyStateChanged(final IStructuredModel model, final boolean isDirty) {
    }

    @Override
    public void modelReinitialized(final IStructuredModel structuredModel) {
    }

    @Override
    public void modelResourceDeleted(final IStructuredModel model) {
    }

    @Override
    public void modelResourceMoved(final IStructuredModel oldModel, final IStructuredModel newModel) {
    }
    
    @Override
	public void doDispose() {
		((INodeNotifier) document).removeAdapter(this);
		unadaptChildElements(document);
		final IStructuredModel structuredModel = ((IDOMDocument) document)
				.getModel();
		structuredModel.removeModelStateListener(this);
	}

}
