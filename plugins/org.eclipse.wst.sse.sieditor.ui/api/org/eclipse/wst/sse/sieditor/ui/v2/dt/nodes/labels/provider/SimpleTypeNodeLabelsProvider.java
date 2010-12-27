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
package org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.labels.provider;

import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;

import org.eclipse.wst.sse.sieditor.model.xsd.api.ISimpleType;
import org.eclipse.wst.sse.sieditor.ui.v2.utils.UIUtils;

public class SimpleTypeNodeLabelsProvider extends AbstractDTTreeNodeLabelsProvider {

    private final ISimpleType iSimpleType;

    public SimpleTypeNodeLabelsProvider(final ISimpleType iSimpleType) {
        this.iSimpleType = iSimpleType;
    }

    @Override
    public String getDisplayName() {
        return UIUtils.instance().getDisplayName(iSimpleType);
    }

    @Override
    public String getTreeDisplayText() {
        return getDisplayName();
    }

    @Override
    public String getTypeDisplayText() {
        final XSDSimpleTypeDefinition xsdSimpleTypeDefinition = (XSDSimpleTypeDefinition) iSimpleType.getComponent();
        return getSimpleTypeDisplayText(xsdSimpleTypeDefinition, xsdSimpleTypeDefinition.getBaseTypeDefinition());
    }

    @Override
    protected String getTypeName(final XSDTypeDefinition typeDefinition, final XSDTypeDefinition baseType) {
        return baseType.getName();
    }

}