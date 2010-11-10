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
package org.eclipse.wst.sse.sieditor.model.reconcile;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.wst.wsdl.Binding;
import org.eclipse.wst.wsdl.Definition;
import org.eclipse.wst.wsdl.Message;
import org.eclipse.wst.wsdl.Operation;
import org.eclipse.wst.wsdl.Part;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;

/**
 * This is the interface for the reconcile utility methods.
 * 
 * 
 * 
 */
public interface IReconcileUtils {

    public abstract void reconcileSchemaContents(XSDSchema schema,
            List<XSDElementDeclaration> elementsForReferenceResolve, List<XSDElementDeclaration> elementsForTypeResolve,
            List<XSDAttributeDeclaration> attributesForResolve);
    
    public abstract void reconcileMessages(final List<Message> messagesForResolve, final Definition definition);

    public abstract void reconcileMessagePartsReferences(final XSDSchema resolverSchema, final List<Part> messagePartsForResolve,
            final Definition definition);

    /**
     * Method for reconciling the operations messages. It circles all the
     * messages in the WSDL document and checks which of them are referenced.
     * 
     */
    public abstract void reconcileOperationsMessagesReferences(final List<Operation> operationsForResolve,
            final Definition editedDefinition, final EList<Message> eMessages, final String prefix);

    /**
     * Method for reconciling the definition &lt;wsdl:binding type="..." ...>
     */
    public abstract void reconcileBindingsQNames(Definition definition);

    // public abstract void reconcileBindingsQNames(EList<Binding> eBindings,
    // final String targetNamespace, EList<PortType> ePortTypes);

    /**
     * Method for reconciling the &lt;wsdl:service>&lt;wsdl:port binding="..."
     * /> port bindings.
     */
    public abstract void reconcileServicePortBindings(Definition definition, final EList<Binding> eServices);


}