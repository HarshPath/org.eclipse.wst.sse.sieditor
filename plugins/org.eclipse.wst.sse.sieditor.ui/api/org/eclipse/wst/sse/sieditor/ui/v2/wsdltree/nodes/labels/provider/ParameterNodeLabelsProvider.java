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
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

public class ParameterNodeLabelsProvider extends AbstractSITreeNodeLabelsProvider {

    private final IParameter iParameter;

    public ParameterNodeLabelsProvider(final IParameter parameter) {
        this.iParameter = parameter;
    }

    @Override
    protected IModelObject getModelObject() {
        return iParameter;
    }

    @Override
    protected IType getType() {
        return iParameter.getType();
    }

    @Override
    protected String decorateText(final String text) {
        String ret = text;
        if (text.indexOf(".element") != -1) //$NON-NLS-1$
            ret = text.substring(0, text.lastIndexOf(".element")) + " [Element]"; //$NON-NLS-1$ //$NON-NLS-2$
        return ret;
    }
}
