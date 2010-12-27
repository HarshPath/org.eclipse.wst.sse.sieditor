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

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.wst.wsdl.Binding;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.Message;
import org.eclipse.wst.wsdl.Operation;
import org.eclipse.wst.wsdl.Part;
import org.eclipse.xsd.XSDSchema;

/**
 * This is the interface for the WSDL reconcile utility methods.
 * 
 */
public interface IWsdlReconcileUtils {

    public void reconcileMessages(final List<Message> messagesForResolve, final Definition definition);

    public void reconcileMessagePartsReferences(final XSDSchema resolverSchema, final List<Part> messagePartsForResolve,
            final Definition definition);

    public void reconcileOperationsMessagesReferences(final List<Operation> operationsForResolve,
            final Definition editedDefinition, final EList<Message> eMessages, final String prefix);

    /**
     * Method for reconciling the definition &lt;wsdl:binding type="..." ...>
     */
    public void reconcileBindingsQNames(Definition definition);

    // public abstract void reconcileBindingsQNames(EList<Binding> eBindings,
    // final String targetNamespace, EList<PortType> ePortTypes);

    /**
     * Method for reconciling the &lt;wsdl:service>&lt;wsdl:port binding="..."
     * /> port bindings.
     */
    public void reconcileServicePortBindings(Definition definition, final EList<Binding> eServices);

}