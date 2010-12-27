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
package org.eclipse.wst.sse.sieditor.model.reconcile.utils.componentresolver;

import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.util.XSDConstants;
import org.w3c.dom.Element;

import org.eclipse.wst.sse.sieditor.command.emf.common.utils.RemapReferencesUtils;
import org.eclipse.wst.sse.sieditor.model.reconcile.IResolveUtils;
import org.eclipse.wst.sse.sieditor.model.reconcile.ResolveUtils;
import org.eclipse.wst.sse.sieditor.model.utils.ElementAttributeUtils;

public abstract class BaseComponentResolver<T extends XSDNamedComponent, Y extends XSDNamedComponent> implements
        IConcreteComponentResolver<T, Y> {

    public static String getResolveComponentLocalName(final Element typedElement, final XSDTypeDefinition typeDefinition) {
        String localName = typeDefinition.getName();
        if (localName == null && ElementAttributeUtils.hasAttributeValue(typedElement, XSDConstants.TYPE_ATTRIBUTE)) {
            final String typeQName = typedElement.getAttribute(XSDConstants.TYPE_ATTRIBUTE);
            localName = typeQName.substring(typeQName.indexOf(':') + 1);
        }
        return localName;
    }

    // =========================================================
    // resolve helpers
    // =========================================================

    protected IResolveUtils resolveUtils() {
        return ResolveUtils.instance();
    }

    protected RemapReferencesUtils remapUtils() {
        return RemapReferencesUtils.instance();
    }

}
