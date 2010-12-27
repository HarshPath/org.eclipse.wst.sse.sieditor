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

import org.eclipse.xsd.util.XSDConstants;

public class XsdComplexTypeDefinitionRestrictionResolver extends XsdComplexTypeDefinitionExtensionResolver {

    private static final XsdComplexTypeDefinitionRestrictionResolver INSTANCE = new XsdComplexTypeDefinitionRestrictionResolver();

    private XsdComplexTypeDefinitionRestrictionResolver() {

    }

    public static XsdComplexTypeDefinitionRestrictionResolver instance() {
        return INSTANCE;
    }

    @Override
    protected String getDerivationMethod() {
        return XSDConstants.RESTRICTION_ELEMENT_TAG;
    }
}
