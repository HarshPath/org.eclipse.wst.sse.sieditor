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

import org.eclipse.wst.sse.sieditor.model.xsd.impl.UnresolvedType;

public class NullNodeLabelsProvider implements ITreeNodeLabelsProvider {

    @Override
    public String getDisplayName() {
        return UnresolvedType.instance().getName();
    }

    @Override
    public String getTreeDisplayText() {
        return getDisplayName();
    }

    @Override
    public String getTypeDisplayText() {
        return getDisplayName();
    }

}
