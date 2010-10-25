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
/**
 * 
 */
package org.eclipse.wst.sse.sieditor.ui.v2.typeselect;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;

import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.wsdl.impl.Description;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

public class DescriptionTypeResolver implements ITypeResolver {

    private IDescription description;

    public DescriptionTypeResolver(IDescription description) {
        this.description = description;
    }

    public XSDSchema[] getLocalSchemas() {

        List<ISchema> containedSchemas = description.getContainedSchemas();

        XSDSchema[] schemas = new XSDSchema[containedSchemas.size()];

        for (int i = 0; i < schemas.length; i++) {
            schemas[i] = containedSchemas.get(i).getComponent();
        }

        return schemas;
    }

    public IType resolveType(XSDNamedComponent xsdComponent) {
        return ((Description) description).resolveType(xsdComponent);
    }

    public IType resolveType(String name, String namespace, IFile file) {
        return TypeResolverHelper.resolveExternalType(description.getModelRoot().getEnv(), name, namespace, file);
    }
}