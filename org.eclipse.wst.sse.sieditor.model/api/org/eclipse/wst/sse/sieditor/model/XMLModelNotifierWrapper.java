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
package org.eclipse.wst.sse.sieditor.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.transaction.Transaction;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.impl.InternalTransaction;
import org.eclipse.emf.transaction.impl.TransactionalEditingDomainImpl;
import org.eclipse.emf.workspace.AbstractEMFOperation;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.document.XMLModelNotifier;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.eclipse.wst.sse.sieditor.core.common.IDisposable;
import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.i18n.Messages;

public class XMLModelNotifierWrapper implements XMLModelNotifier, IDisposable {

    private XMLModelNotifier internalNotifier = null;

    private IModelRoot modelRoot = null;

    private final Set<Node> changedNodes = new HashSet<Node>();

    private boolean interceptChanges = false;

    private List<Notification> collectedNotifications;
    
    private IStructuredModel structuredModel;

    public List<Notification> getCollectedNotifications() {
        if (collectedNotifications != null) {
            return collectedNotifications;
        }
        return Collections.emptyList();
    }

    public void clearCollectedNotifications() {
        if (collectedNotifications != null) {
            collectedNotifications.clear();
        }
    }

    public XMLModelNotifierWrapper(final XMLModelNotifier notifier, final IModelRoot modelRoot) {
        this.internalNotifier = notifier;
        this.modelRoot = modelRoot;
    }

    /**
     * True - will suppress calls to inner XML notifier.
     * 
     * @param interceptChanges
     * @see #endChanging()
     */
    public void setInterceptChanges(final boolean interceptChanges) {
        this.interceptChanges = interceptChanges;
    }

    /**
     * Recorded changed DOM nodes during source edit.
     */
    public Set<Node> getChangedNodes() {
        return changedNodes;
    }

    public void attrReplaced(final Element element, final Attr newAttr, final Attr oldAttr) {
        internalNotifier.attrReplaced(element, newAttr, oldAttr);

    }

    public void beginChanging() {
        internalNotifier.beginChanging();

    }

    public void beginChanging(final boolean newModel) {
        internalNotifier.beginChanging(newModel);

    }

    public void cancelPending() {
        internalNotifier.cancelPending();

    }

    public void childReplaced(final Node parentNode, final Node newChild, final Node oldChild) {
        internalNotifier.childReplaced(parentNode, newChild, oldChild);

    }

    public void editableChanged(final Node node) {
        internalNotifier.editableChanged(node);

    }

    @Override
    public void endChanging() {
        if (interceptChanges) {
            return;
        }

        final TransactionalEditingDomain editingDomain = modelRoot.getEnv().getEditingDomain();
        // happens when close editor, and do not save contents
        if (editingDomain == null) {
            internalNotifier.endChanging();
            return;
        }

        final Map<String, Boolean> options = new HashMap<String, Boolean>();
        options.put(Transaction.OPTION_NO_VALIDATION, Boolean.TRUE);

        // this command should not notify SIE Model
        final AbstractEMFOperation command = new AbstractEMFOperation(editingDomain, Messages.XMLModelNotifierWrapper_0, options) {

            private Set<Notification> newNotifications;

            @Override
            protected IStatus doExecute(final IProgressMonitor monitor, final IAdaptable info) throws ExecutionException {
                final InternalTransaction activeTransaction = ((TransactionalEditingDomainImpl) getEditingDomain())
                        .getActiveTransaction();

                if (activeTransaction != null) {
                    final Set<Notification> oldNotifications = new HashSet<Notification>(activeTransaction.getNotifications());
                    internalNotifier.endChanging();
                    newNotifications = new HashSet<Notification>(activeTransaction.getNotifications());
                    newNotifications.removeAll(oldNotifications);
                } else {
                    Logger.logWarning("We are notifying the wrapped XMLModelNotifier but we do not have active transaction"); //$NON-NLS-1$
                    internalNotifier.endChanging();
                }
                return Status.OK_STATUS;
            }

            @Override
            protected void didCommit(final Transaction transaction) {
                if (transaction.getParent() != null) {
                    for (final Notification notification : newNotifications) {
                        ((InternalTransaction) transaction.getParent()).add(notification);
                    }
                    newNotifications.clear();
                }
                super.didCommit(transaction);
            }
        };

        try {
            command.setReuseParentTransaction(true);
            command.execute(null, null);
        } catch (final ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void endTagChanged(final Element element) {
        internalNotifier.endTagChanged(element);

    }

    public boolean hasChanged() {
        return internalNotifier.hasChanged();
    }

    public boolean isChanging() {
        return internalNotifier.isChanging();
    }

    public void propertyChanged(final Node node) {
        internalNotifier.propertyChanged(node);

    }

    public void startTagChanged(final Element element) {
        internalNotifier.startTagChanged(element);

    }

    public void structureChanged(final Node node) {
        internalNotifier.structureChanged(node);

    }

    public void valueChanged(final Node node) {
        internalNotifier.valueChanged(node);
        changedNodes.add(node);
    }
    
	@Override
	public void doDispose() {
		((IDOMModel) structuredModel).setModelNotifier(internalNotifier);
	}

	public void registerTo(IStructuredModel structuredModel) {
		this.structuredModel = structuredModel;
        ((IDOMModel) structuredModel).setModelNotifier(this);
	}

}
