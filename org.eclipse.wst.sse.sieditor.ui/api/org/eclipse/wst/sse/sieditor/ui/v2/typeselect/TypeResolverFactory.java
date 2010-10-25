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



import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;

/**
 * Singleton factory for obtaining {@link ITypeResolver} instances.
 * 
 * 
 * 
 */
public class TypeResolverFactory {

    private static final TypeResolverFactory instance = new TypeResolverFactory();

    private TypeResolverFactory() {
    }

    /**
     * Returns the {@link TypeResolverFactory} singleton instance
     * 
     * @return the {@link TypeResolverFactory} singleton instance
     */
    public static TypeResolverFactory getInstance() {
        return instance;
    }

    /**
     * Creates proper type resolver depending on the model object.
     * 
     * @param modelObject
     *            either {@link ISchema} or {@link IDescription}.
     * @return the newly created {@link ITypeResolver}
     */
    public ITypeResolver createTypeResolver(IModelObject modelObject) {
        if (modelObject instanceof IDescription) {
            return new DescriptionTypeResolver((IDescription) modelObject);
        } else if (modelObject instanceof ISchema) {
            return new SchemaTypeResolver((ISchema) modelObject);
        }
        return null;
    }
}
