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
 *******************************************************************************/
package org.eclipse.wst.sse.sieditor.ui.v2.typeselect;

import org.eclipse.core.resources.IFile;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;

import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.Schema;

public class SchemaTypeResolver implements ITypeResolver {
    private ISchema schema;

    public SchemaTypeResolver(ISchema schema) {
        this.schema = schema;
    }

    public XSDSchema[] getLocalSchemas() {
        return new XSDSchema[] { schema.getComponent() };
    }

    public IType resolveType(XSDNamedComponent xsdComponent) {
        return (IType) ((Schema) schema).resolveComponent(xsdComponent, true);
    }

    public IType resolveType(String name, String namespace, IFile file) {
        return TypeResolverHelper.resolveExternalType(schema.getModelRoot().getEnv(), name, namespace, file);
    }
}