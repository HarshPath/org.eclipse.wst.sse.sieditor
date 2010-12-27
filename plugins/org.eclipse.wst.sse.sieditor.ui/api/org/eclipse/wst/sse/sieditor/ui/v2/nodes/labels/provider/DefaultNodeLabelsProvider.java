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
package org.eclipse.wst.sse.sieditor.ui.v2.nodes.labels.provider;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.api.INamedObject;
import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.NamespaceNode;

public class DefaultNodeLabelsProvider implements ITreeNodeLabelsProvider {

    private final IModelObject modelObject;

    public DefaultNodeLabelsProvider(final IModelObject modelObject) {
        this.modelObject = modelObject;
    }

    @Override
    public String getDisplayName() {
        String name = null;
        if (modelObject instanceof INamedObject) {
            name = ((INamedObject) modelObject).getName();
        } else if (modelObject instanceof ISchema) {
            return NamespaceNode.getNamespaceDisplayText((ISchema) modelObject);
        }
        return name == null ? UIConstants.EMPTY_STRING : name;
    }

    @Override
    public String getTypeDisplayText() {
        return UIConstants.EMPTY_STRING;
    }

    @Override
    public String getTreeDisplayText() {
        return getDisplayName();
    }

}
