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
package org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes.labels.provider;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.INamedObject;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;
import org.eclipse.wst.sse.sieditor.model.xsd.impl.UnresolvedType;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.labels.provider.ITreeNodeLabelsProvider;

public abstract class AbstractSITreeNodeLabelsProvider implements ITreeNodeLabelsProvider {

    @Override
    public String getDisplayName() {
        String name = null;
        if (getModelObject() instanceof INamedObject) {
            name = ((INamedObject) getModelObject()).getName();
        }
        return name == null ? UIConstants.EMPTY_STRING : name;
    }

    @Override
    public String getTreeDisplayText() {
        return decorateText(getDisplayName() + UIConstants.SPACE + UIConstants.COLON + UIConstants.SPACE + getTypeDisplayText());
    }

    @Override
    public String getTypeDisplayText() {
        final IType type = getType();
        return type == null ? UnresolvedType.instance().getName() : type.getName();
    }

    protected abstract IType getType();

    protected abstract IModelObject getModelObject();

    protected abstract String decorateText(String treeDislayText);

}
