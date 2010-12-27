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

import java.util.Iterator;

import org.eclipse.wst.sse.sieditor.model.api.IModelObject;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IFault;
import org.eclipse.wst.sse.sieditor.model.wsdl.api.IParameter;
import org.eclipse.wst.sse.sieditor.model.xsd.api.IType;

public class FaultNodeLabelsProvider extends AbstractSITreeNodeLabelsProvider {

    private final IFault iFault;

    public FaultNodeLabelsProvider(final IFault fault) {
        this.iFault = fault;
    }

    @Override
    protected IModelObject getModelObject() {
        return iFault;
    }

    @Override
    protected IType getType() {
        final Iterator<IParameter> iterator = iFault.getParameters().iterator();
        final IParameter parameter = iterator.hasNext() ? iterator.next() : null;
        if (parameter != null) {
            return parameter.getType();
        }
        return null;
    }

    @Override
    protected String decorateText(final String text) {
        return text;
    }
}
