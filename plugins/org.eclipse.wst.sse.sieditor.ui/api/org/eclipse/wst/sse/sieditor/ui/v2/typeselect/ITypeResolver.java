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
 *    Dinko Ivanov - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.ui.v2.typeselect;

import org.eclipse.core.resources.IFile;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;

import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.ui.v2.AbstractFormPageController;

/**
 * Helper interface used by the Service Interface Editor Controller (
 * {@link AbstractFormPageController}) for the Select Type functionality.
 * Instances are created by the {@link TypeResolverFactory}.
 * <p>
 * The main purpose of the resolver is to convert an EMF XSD type to an
 * {@link IType}. Typical usage will first call 
 * {@link ITypeResolver#resolveType(XSDNamedComponent)} to check if the type is
 * local (contained in the local schemas). If the type cannot be resolved
 * (method returned <code>null</code>), then try to resolve it by its name,
 * namespace and defining workspace file.
 * 
 */
public interface ITypeResolver {

    /**
     * Returns the XSD schemas, which are local to this resolver.
     * 
     * @return
     */
    XSDSchema[] getLocalSchemas();

    /**
     * Tries to resolve an XSD type in local schemas. Will return
     * <code>null</code> if the type is not from the local XSD schemas.
     * 
     * @param xsdType
     *            the XSD type to resolve
     * @return the resolved {@link IType}
     */
    IType resolveType(XSDNamedComponent xsdType);

    /**
     * Tries to resolve and XSD type in the workspace scope.
     * 
     * @param name
     *            the type name
     * @param namespace
     *            the namespace
     * @param file
     *            the workspace file containing the definition
     * @return the resolved {@link IType}
     */
    IType resolveType(String name, String namespace, IFile file);
}
