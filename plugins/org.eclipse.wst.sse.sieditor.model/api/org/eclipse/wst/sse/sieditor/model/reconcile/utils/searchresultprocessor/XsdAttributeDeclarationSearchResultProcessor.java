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

import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDTypeDefinition;

import org.eclipse.wst.sse.sieditor.model.reconcile.utils.ObjectsForResolveContainer;

public class XsdAttributeDeclarationSearchResultProcessor implements IXsdSearchResultProcessor<XSDAttributeDeclaration> {

    private static final XsdAttributeDeclarationSearchResultProcessor INSTANCE = new XsdAttributeDeclarationSearchResultProcessor();

    private XsdAttributeDeclarationSearchResultProcessor() {

    }

    public static XsdAttributeDeclarationSearchResultProcessor instance() {
        return INSTANCE;
    }
    
    @Override
    public XSDAttributeDeclaration getReference(final XSDAttributeDeclaration component) {
        return component.getResolvedAttributeDeclaration();
    }

    @Override
    public List<XSDNamedComponent> getResolveCollection(final XSDAttributeDeclaration component,
            final ObjectsForResolveContainer container) {
        return isReference(component) ? container.getAttributesForReferenceResolve() : container.getAttributesForTypeResolve();
    }

    @Override
    public XSDTypeDefinition getTypeDefinition(final XSDAttributeDeclaration component) {
        return component.getTypeDefinition();
    }

    @Override
    public boolean isReference(final XSDAttributeDeclaration component) {
        return component.isAttributeDeclarationReference();
    }

}
