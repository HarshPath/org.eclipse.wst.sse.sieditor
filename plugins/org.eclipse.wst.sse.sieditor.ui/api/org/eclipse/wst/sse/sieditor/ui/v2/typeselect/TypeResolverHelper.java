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

import org.eclipse.wst.sse.sieditor.core.common.IEnvironment;
import org.eclipse.wst.sse.sieditor.core.common.Logger;
import org.eclipse.wst.sse.sieditor.core.editorfwk.ModelHandler;
import org.eclipse.wst.sse.sieditor.model.api.IModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IWsdlModelRoot;
import org.eclipse.wst.sse.sieditor.model.api.IXSDModelRoot;
import org.eclipse.wst.sse.sieditor.model.utils.URIHelper;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.UnresolvedType;

public final class TypeResolverHelper {
    public static IType resolveExternalType(final IEnvironment env, final String name, final String namespace, final IFile file) {
        final IModelRoot modelRoot;

        modelRoot = (IModelRoot) ModelHandler.retrieveModelObject(env, URIHelper.convert(file.getLocationURI()), true);
        if (modelRoot instanceof IWsdlModelRoot) {
            final ISchema[] schemas = ((IWsdlModelRoot) modelRoot).getDescription().getSchema(namespace);
            if (schemas == null || schemas.length == 0) {
                return getUnresolvedType(namespace, name, file);
            }

            return getResolvedTypeFromSchema(schemas[0], namespace, name, file);
        } else if (modelRoot instanceof IXSDModelRoot) {
            final ISchema extSchema = ((IXSDModelRoot) modelRoot).getSchema();
			if (extSchema != null && extSchema.getNamespace() == null ? namespace == null
					: namespace.equals(extSchema.getNamespace())) {
                return getResolvedTypeFromSchema(extSchema, namespace, name, file);
            }

            return getUnresolvedType(namespace, name, file);
        } else {
            return getUnresolvedType(namespace, name, file);
        }
    }

    private static IType getResolvedTypeFromSchema(ISchema schema, String namespace, String name, IFile f) {
        IType type = null;

        IType[] types = schema.getAllTypes(name);
        if (types == null) {
            type = UnresolvedType.instance();
        } else {
            type = types[0];
        }

        if (type instanceof UnresolvedType) {
            Logger.logWarning(String.format("Cannot resolve type [%s, %s] from file [%s]", namespace, name, f.getLocation() //$NON-NLS-1$
                    .toOSString()));
        }

        return type;
    }

    private static IType getUnresolvedType(String namespace, String name, IFile f) {
        Logger.logWarning(String.format("Cannot resolve type [%s, %s] from file [%s]", namespace, name, f.getLocation() //$NON-NLS-1$
                .toOSString()));

        return UnresolvedType.instance();
    }

    private TypeResolverHelper() {
    }
}
