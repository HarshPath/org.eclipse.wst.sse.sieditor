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
package org.eclipse.wst.sse.sieditor.model.reconcile.utils.searchresultprocessor;

import java.util.List;

import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDTypeDefinition;

import org.eclipse.wst.sse.sieditor.model.reconcile.utils.ObjectsForResolveContainer;

public class XsdElementDeclarationSearchResultProcessor implements IXsdSearchResultProcessor<XSDElementDeclaration> {

    private static final XsdElementDeclarationSearchResultProcessor INSTANCE = new XsdElementDeclarationSearchResultProcessor();

    private XsdElementDeclarationSearchResultProcessor() {

    }

    public static XsdElementDeclarationSearchResultProcessor instance() {
        return INSTANCE;
    }

    @Override
    public XSDElementDeclaration getReference(final XSDElementDeclaration component) {
        return component.getResolvedElementDeclaration();
    }

    @Override
    public List<XSDNamedComponent> getResolveCollection(final XSDElementDeclaration component,
            final ObjectsForResolveContainer container) {
        return isReference(component) ? container.getElementsForReferenceResolve() : container.getElementsForTypeResolve();
    }

    @Override
    public XSDTypeDefinition getTypeDefinition(final XSDElementDeclaration component) {
        return component.getTypeDefinition();
    }

    @Override
    public boolean isReference(final XSDElementDeclaration component) {
        return component.isElementDeclarationReference();
    }

}
