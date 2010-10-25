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
 *    Keshav Veerapaneni - initial API and implementation.
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.model.xsd.api;

import java.util.Collection;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.xsd.XSDSchema;

import org.eclipse.wst.sse.sieditor.model.api.INamespacedObject;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.AbstractType;

/**
 * 
 * 
 * 
 */
public interface ISchema extends INamespacedObject {
    String getLocation();

    String getRawLocation();

    Collection<IType> getAllContainedTypes();

    Collection<ISchema> getAllReferredSchemas();

    IType[] getAllTypes(String name);

    IType getType(boolean isElement, String name);

    XSDSchema getComponent();

    /**
     * 
     * @param schema
     * @return The first imported ISchema that corresponds to the given
     *         {@link XSDSchema}
     */
    ISchema getReferredSchema(XSDSchema schema);

    /**
     * Resolves the type from the current or if not found, from imported schemas
     * 
     * @param type
     * @return the resolved type
     * @throws ExecutionException
     *             may be thrown while the model is updated with content from
     *             imported documents.
     */
    IType resolveType(final AbstractType type) throws ExecutionException;
}
