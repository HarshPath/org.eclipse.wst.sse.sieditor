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
package org.eclipse.wst.sse.sieditor.model.reconcile.utils.componentresolver;

import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;

import org.eclipse.wst.sse.sieditor.model.reconcile.utils.IXsdReconcileUtils.NamespaceResolverType;

/**
 * This is the helper interface for all the concrete component resolvers.
 * Implementors are responsible for the resolving of the given concrete
 * components.
 * 
 * @param <T>
 *            - the source component type
 * @param <Y>
 *            - the resolved component type
 */
public interface IConcreteComponentResolver<T extends XSDNamedComponent, Y extends XSDNamedComponent> {

    /**
     * This method returns whether the given source component is to be checked
     * for resolving by this concrete component resolver - e.g. whether it is
     * supported and whether it can be resolved.
     * 
     * @param sourceComponent
     *            - the source component to be resolved by this concrete
     *            component resolver.
     * @return <code>true</code> if the source component is supported by this
     *         concrete component resolver and if the component can be resolved.
     *         <code>false</code> otherwise
     */
    public boolean shouldProceedWithResolveOf(T sourceComponent);

    /**
     * The actual resolve method. It returns the resolved component.
     * 
     * @param xsdSchema
     *            - the XML Schema in which to search the component for
     * @return the resolved component or <code>null</code> if the component was
     *         not resolved
     */
    public Y resolveConcreteComponent(T sourceComponent, XSDSchema xsdSchema, NamespaceResolverType namespaceResolverType);

    /**
     * Method for notifying the source component that the resolved component was
     * actually resolved
     * 
     * @param sourceComponent
     * @param resolvedComponent
     */
    public void componentResolved(T sourceComponent, Y resolvedComponent);

    public String getResolveComponentAttributeValue(T sourceComponent);

}
