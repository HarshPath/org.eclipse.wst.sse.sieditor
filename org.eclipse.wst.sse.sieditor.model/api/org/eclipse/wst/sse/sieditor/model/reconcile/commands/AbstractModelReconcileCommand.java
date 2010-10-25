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
package org.eclipse.wst.sse.sieditor.model.reconcile.commands;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.wst.wsdl.Message;
import org.eclipse.wst.wsdl.Operation;
import org.eclipse.wst.wsdl.Part;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDTypeDefinition;

import org.eclipse.wst.sse.sieditor.command.common.AbstractNotificationOperation;
import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.reconcile.IReconcileUtils;
import org.eclipse.wst.sse.sieditor.model.reconcile.ReconcileUtils;

/**
 * This is the base command for all the model reconcile commands. It has utility
 * methods for searching for elements for reconcile.
 * 
 * 
 * 
 */
public abstract class AbstractModelReconcileCommand extends AbstractNotificationOperation {

    public AbstractModelReconcileCommand(final IModelRoot modelRoot, final IModelObject modelObject, final String operationLabel) {
        super(modelRoot, modelObject, operationLabel);
    }

    // =========================================================
    // objects for resolve container
    // =========================================================

    /**
     * Helper class used to store the document elements which need to be
     * reconciled.<br>
     * <br>
     * This container helper class is used in the recursive search method
     * {@link AbstractModelReconcileCommand#findObjectsForResolve(EObject)}.
     */
    protected class ObjectsForResolveContainer {

        private final List<XSDElementDeclaration> elementsForReferenceResolve = new LinkedList<XSDElementDeclaration>();
        private final List<XSDElementDeclaration> elementsForTypeResolve = new LinkedList<XSDElementDeclaration>();
        private final List<XSDAttributeDeclaration> attributesForResolve = new LinkedList<XSDAttributeDeclaration>();
        private final List<Message> messagesForResolve = new LinkedList<Message>();
        private final List<Operation> operationsForResolve = new LinkedList<Operation>();
        private final List<Part> messagePartsForResolve = new LinkedList<Part>();

        public List<XSDElementDeclaration> getElementsForReferenceResolve() {
            return elementsForReferenceResolve;
        }

        public List<XSDElementDeclaration> getElementsForTypeResolve() {
            return elementsForTypeResolve;
        }
        
        public List<XSDAttributeDeclaration> getAttributesForResolve() {
            return attributesForResolve;
        }

        public List<Message> getMessagesForResolve() {
            return messagesForResolve;
        }

        public List<Part> getMessagePartsForResolve() {
            return messagePartsForResolve;
        }

        public List<Operation> getOperationsForResolve() {
            return operationsForResolve;
        }

        /**
         * Utility method. Transfers all the data from the give container to the
         * current one.
         */
        public void addAll(final ObjectsForResolveContainer otherContainer) {
            this.getElementsForReferenceResolve().addAll(otherContainer.getElementsForReferenceResolve());
            this.getElementsForTypeResolve().addAll(otherContainer.getElementsForTypeResolve());
            this.getAttributesForResolve().addAll(otherContainer.getAttributesForResolve());
            this.getMessagesForResolve().addAll(otherContainer.getMessagesForResolve());
            this.getMessagePartsForResolve().addAll(otherContainer.getMessagePartsForResolve());
            this.getOperationsForResolve().addAll(otherContainer.getOperationsForResolve());
        }

    }

    // =========================================================
    // helpers
    // =========================================================

    protected IReconcileUtils reconcileUtils() {
        return ReconcileUtils.instance();
    }

    // =========================================================
    // unresolved elements search helper
    // =========================================================

    protected ObjectsForResolveContainer findObjectsForResolve(final EObject eObject) {
        final List<EObject> visited = new LinkedList<EObject>();
        return findObjectsForResolveInternal(eObject, visited);
    }

    /**
     * Utility internal method. Returns container with all the elements, which
     * need reconciling.
     */
    private ObjectsForResolveContainer findObjectsForResolveInternal(final EObject eObject, final List<EObject> visited) {
        final ObjectsForResolveContainer container = new ObjectsForResolveContainer();

        final Iterator<EObject> eAllContents = eObject.eContents().iterator();
        while (eAllContents.hasNext()) {
            final EObject next = eAllContents.next();

            if (!visited.contains(next)) {

                if (next instanceof XSDElementDeclaration) {
                    processXsdElementDeclaration(container, next);

                } else if (next instanceof XSDAttributeDeclaration) {
                    processXsdAttributeDeclaration(container, next);

                } else if (next instanceof Operation) {
                    processWsdlOperation(container, next);

                } else if (next instanceof Message) {
                    processWsdlMessage(container, next);

                } else if (next instanceof Part) {
                    processWsdlMessagePart(container, next);
                }
                // TODO: needs to resolve XsdAttributeGroupDefinition &&
                // XsdModelGroupDefinition

                visited.add(next);
                final ObjectsForResolveContainer childsContainer = findObjectsForResolveInternal(next, visited);
                container.addAll(childsContainer);
            }
        }
        return container;
    }

    // =========================================================
    // findObjectsForResolve helpers
    // =========================================================

    private void processXsdElementDeclaration(final ObjectsForResolveContainer container, final EObject next) {
        final XSDElementDeclaration xsdElementDeclaration = (XSDElementDeclaration) next;
        if (xsdElementDeclaration.isElementDeclarationReference()) {
            final XSDElementDeclaration resolvedElementDeclaration = xsdElementDeclaration.getResolvedElementDeclaration();
            if (resolvedElementDeclaration.eContainer() == null) {
                container.getElementsForReferenceResolve().add(xsdElementDeclaration);
            }
        } else {
            final XSDTypeDefinition typeDefinition = xsdElementDeclaration.getTypeDefinition();
            if (typeDefinition != null && typeDefinition.eContainer() == null) {
                container.getElementsForTypeResolve().add(xsdElementDeclaration);
            }
        }
    }

    private void processXsdAttributeDeclaration(final ObjectsForResolveContainer container, final EObject next) {
        final XSDAttributeDeclaration xsdAttributeDeclaration = (XSDAttributeDeclaration) next;
        if (xsdAttributeDeclaration.isAttributeDeclarationReference()) {
            final XSDAttributeDeclaration resolvedAttributeDeclaration = xsdAttributeDeclaration
                    .getResolvedAttributeDeclaration();
            if (resolvedAttributeDeclaration.eContainer() == null) {
                container.getAttributesForResolve().add(xsdAttributeDeclaration);
            }
        }
    }

    private void processWsdlOperation(final ObjectsForResolveContainer container, final EObject next) {
        final Operation operation = (Operation) next;
        container.getOperationsForResolve().add(operation);
    }

    private void processWsdlMessage(final ObjectsForResolveContainer container, final EObject next) {
        final Message message = (Message) next;
        container.getMessagesForResolve().add(message);
    }

    private void processWsdlMessagePart(final ObjectsForResolveContainer container, final EObject next) {
        final Part part = (Part) next;
        if ((part.getElementDeclaration() != null && part.getElementDeclaration().eContainer() == null)
                || (part.getTypeDefinition() != null && part.getTypeDefinition().eContainer() == null)) {
            container.getMessagePartsForResolve().add(part);
        }
    }

}
