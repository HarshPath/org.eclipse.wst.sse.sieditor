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
package org.eclipse.wst.sse.sieditor.model.reconcile.utils;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.wst.wsdl.Message;
import org.eclipse.wst.wsdl.Operation;
import org.eclipse.wst.wsdl.Part;
import org.eclipse.xsd.XSDNamedComponent;

/**
 * Helper class used to store the document elements which need to be reconciled.<br>
 * <br>
 * This container helper class is used in the recursive search method
 * {@link AbstractModelReconcileCommand#findObjectsForResolve(EObject)}.
 * 
 */
public class ObjectsForResolveContainer {

    private final List<XSDNamedComponent> elementsForReferenceResolve = new LinkedList<XSDNamedComponent>();
    private final List<XSDNamedComponent> elementsForTypeResolve = new LinkedList<XSDNamedComponent>();
    private final List<XSDNamedComponent> attributesForReferenceResolve = new LinkedList<XSDNamedComponent>();
    private final List<XSDNamedComponent> attributesForTypeResolve = new LinkedList<XSDNamedComponent>();
    private final List<XSDNamedComponent> complexTypesForExtensionResolve = new LinkedList<XSDNamedComponent>();
    private final List<XSDNamedComponent> complexTypesForRestrictionResolve = new LinkedList<XSDNamedComponent>();

    private final List<Message> messagesForResolve = new LinkedList<Message>();
    private final List<Operation> operationsForResolve = new LinkedList<Operation>();
    private final List<Part> messagePartsForResolve = new LinkedList<Part>();

    public List<XSDNamedComponent> getElementsForReferenceResolve() {
        return elementsForReferenceResolve;
    }

    public List<XSDNamedComponent> getElementsForTypeResolve() {
        return elementsForTypeResolve;
    }

    public List<XSDNamedComponent> getAttributesForReferenceResolve() {
        return attributesForReferenceResolve;
    }

    public List<XSDNamedComponent> getAttributesForTypeResolve() {
        return attributesForTypeResolve;
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

    public List<XSDNamedComponent> getComplexTypesForExtensionResolve() {
        return complexTypesForExtensionResolve;
    }

    public List<XSDNamedComponent> getComplexTypesForRestrictionResolve() {
        return complexTypesForRestrictionResolve;
    }

    /**
     * Utility method. Transfers all the data from the give container to the
     * current one.
     */
    public void addAll(final ObjectsForResolveContainer otherContainer) {
        this.getElementsForReferenceResolve().addAll(otherContainer.getElementsForReferenceResolve());
        this.getElementsForTypeResolve().addAll(otherContainer.getElementsForTypeResolve());
        this.getAttributesForReferenceResolve().addAll(otherContainer.getAttributesForReferenceResolve());
        this.getAttributesForTypeResolve().addAll(otherContainer.getAttributesForTypeResolve());
        this.getMessagesForResolve().addAll(otherContainer.getMessagesForResolve());
        this.getMessagePartsForResolve().addAll(otherContainer.getMessagePartsForResolve());
        this.getOperationsForResolve().addAll(otherContainer.getOperationsForResolve());
        this.getComplexTypesForExtensionResolve().addAll(otherContainer.getComplexTypesForExtensionResolve());
        this.getComplexTypesForRestrictionResolve().addAll(otherContainer.getComplexTypesForRestrictionResolve());
    }

    public boolean areSchemaContentsCollectionsEmpty() {
        return this.getElementsForReferenceResolve().isEmpty() && this.getElementsForTypeResolve().isEmpty()
                && this.getAttributesForReferenceResolve().isEmpty() && this.getAttributesForTypeResolve().isEmpty()
                && this.getComplexTypesForExtensionResolve().isEmpty() && this.getComplexTypesForRestrictionResolve().isEmpty();
    }
}
