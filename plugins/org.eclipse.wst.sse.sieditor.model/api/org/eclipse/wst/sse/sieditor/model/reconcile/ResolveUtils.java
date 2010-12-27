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
package org.eclipse.wst.sse.sieditor.model.reconcile;

import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;

public class ResolveUtils implements IResolveUtils {

    private static final IResolveUtils INSTANCE = new ResolveUtils();

    private ResolveUtils() {

    }

    public static IResolveUtils instance() {
        return INSTANCE;
    }

    @Override
    public XSDComplexTypeDefinition resolveComplexTypeDefinition(final XSDSchema schema, final String namespaceURI,
            final String name) {
        return resolveDeclaration(schema, namespaceURI, name, XSDComplexTypeDefinition.class);
    }

    @Override
    public XSDSimpleTypeDefinition resolveSimpleTypeDefinition(final XSDSchema schema, final String namespaceURI,
            final String name) {
        return resolveDeclaration(schema, namespaceURI, name, XSDSimpleTypeDefinition.class);
    }

    @Override
    public XSDTypeDefinition resolveTypeDefinition(final XSDSchema schema, final String namespaceURI, final String name) {
        return resolveDeclaration(schema, namespaceURI, name, XSDTypeDefinition.class);
    }

    @Override
    public XSDElementDeclaration resolveElementDeclaration(final XSDSchema schema, final String namespaceURI,
            final String elementName) {
        return resolveDeclaration(schema, namespaceURI, elementName, XSDElementDeclaration.class);
    }

    @Override
    public XSDAttributeDeclaration resolveAttributeDeclaration(final XSDSchema schema, final String namespaceURI,
            final String attributeName) {
        return resolveDeclaration(schema, namespaceURI, attributeName, XSDAttributeDeclaration.class);
    }

    @SuppressWarnings("unchecked")
    private <T extends XSDNamedComponent> T resolveDeclaration(final XSDSchema schema, final String namespaceURI,
            final String resolveName, final Class<T> requiredInstanceClass) {
        for (final XSDConcreteComponent concreteComponent : schema.getContents()) {
            if (requiredInstanceClass.isInstance(concreteComponent)) {
                final T namedComponent = (T) concreteComponent;

                final boolean nameIsResolved = (resolveName == null && namedComponent.getName() == null)
                        || (resolveName != null && resolveName.equals(namedComponent.getName()));

                if (nameIsResolved && namespaceURI == null) {
                    return namedComponent;
                }
                if (nameIsResolved && namespaceURI != null && namespaceURI.equals(namedComponent.getTargetNamespace())) {
                    return namedComponent;
                }
            }
        }
        return null;
    }

}
