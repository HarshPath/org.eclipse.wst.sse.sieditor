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
package org.eclipse.wst.sse.sieditor.model.reconcile.adapters;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.transaction.RollbackException;
import org.eclipse.emf.transaction.Transaction;
import org.eclipse.emf.transaction.impl.TransactionalEditingDomainImpl;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.wst.sse.core.internal.provisional.IModelStateListener;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.ui.internal.text.WSDLModelAdapter;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xsd.ui.internal.util.ModelReconcileAdapter;
import org.w3c.dom.Document;

import org.eclipse.wst.sse.sieditor.core.common.IDisposable;

public class TransactionalWSDLModelStateListener implements IModelStateListener, IDisposable  {

    private final ModelReconcileAdapter wrappedModelStateListener;

    private final TransactionalEditingDomainImpl editingDomain;

    private final Map<String, Object> transactionOptions;

    private final Definition definition;
    
    private final Document document;

    public TransactionalWSDLModelStateListener(final Document document, final Definition definition) {
    	this.document = document;
        this.definition = definition;

        wrappedModelStateListener = WSDLModelAdapter.lookupOrCreateModelAdapter(document).getModelReconcileAdapter();

        final IStructuredModel structuredModel = ((IDOMDocument) document).getModel();
        structuredModel.removeModelStateListener(wrappedModelStateListener);

        final ResourceSet resourceSet = definition.eResource().getResourceSet();
        editingDomain = (TransactionalEditingDomainImpl) TransactionUtil.getEditingDomain(resourceSet);
        transactionOptions = new HashMap<String, Object>();
        transactionOptions.put(Transaction.OPTION_NO_VALIDATION, Boolean.TRUE);
        transactionOptions.put(Transaction.OPTION_NO_UNDO, Boolean.TRUE);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof TransactionalWSDLModelStateListener) {
            return wrappedModelStateListener.equals(((TransactionalWSDLModelStateListener) obj).wrappedModelStateListener);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return wrappedModelStateListener.hashCode();
    }

    @Override
    public void modelAboutToBeChanged(final IStructuredModel model) {
        wrappedModelStateListener.modelAboutToBeChanged(model);
    }

    @Override
    public void modelAboutToBeReinitialized(final IStructuredModel structuredModel) {
        wrappedModelStateListener.modelAboutToBeReinitialized(structuredModel);
    }

    @Override
    public void modelChanged(final IStructuredModel model) {
        wrappedModelStateListener.modelChanged(model);
    }

    @Override
    public void modelDirtyStateChanged(final IStructuredModel model, final boolean isDirty) {
        if (!isDirty) {
            return;
        }
        Transaction activeTransaction = editingDomain.getActiveTransaction();
        boolean handleTransaction = false;
        if (activeTransaction == null) {
            try {
                activeTransaction = editingDomain.startTransaction(false, transactionOptions);
            } catch (final InterruptedException e) {
                throw new IllegalStateException(e);
            }
            handleTransaction = true;
        }
        try {
            wrappedModelStateListener.modelDirtyStateChanged(model, isDirty);
        } catch (final Throwable th) {
            if (handleTransaction) {
                activeTransaction.rollback();
            }
            throw new RuntimeException(th);
        }
        if (handleTransaction) {
            try {
                activeTransaction.commit();
            } catch (final RollbackException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void modelReinitialized(final IStructuredModel structuredModel) {
        wrappedModelStateListener.modelReinitialized(structuredModel);
    }

    @Override
    public void modelResourceDeleted(final IStructuredModel model) {
        wrappedModelStateListener.modelResourceDeleted(model);
    }

    @Override
    public void modelResourceMoved(final IStructuredModel oldModel, final IStructuredModel newModel) {
        wrappedModelStateListener.modelResourceMoved(oldModel, newModel);
    }

	@Override
	public void doDispose() {
		final IStructuredModel structuredModel = ((IDOMDocument) document).getModel();
		structuredModel.removeModelStateListener(this);
		structuredModel.addModelStateListener(wrappedModelStateListener);
	}

}
