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
package org.eclipse.wst.sse.sieditor.model.reconcile;

import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTypeDefinition;

public interface IResolveUtils {

    public abstract XSDElementDeclaration resolveElementDeclaration(final XSDSchema schema, final String namespaceURI,
            final String elementName);

    public abstract XSDAttributeDeclaration resolveAttributeDeclaration(final XSDSchema schema, final String namespaceURI,
            final String attributeName);

    public abstract XSDTypeDefinition resolveTypeDefinition(XSDSchema changedSchema, String targetNamespace, String name);

}