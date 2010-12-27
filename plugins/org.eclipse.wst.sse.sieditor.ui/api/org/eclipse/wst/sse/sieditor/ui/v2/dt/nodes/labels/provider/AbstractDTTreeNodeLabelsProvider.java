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

import org.eclipse.wst.sse.sieditor.ui.v2.nodes.labels.provider.ITreeNodeLabelsProvider;

public abstract class AbstractDTTreeNodeLabelsProvider implements ITreeNodeLabelsProvider {

    protected String getSimpleTypeDisplayText(final XSDSimpleTypeDefinition simpleType, final XSDSimpleTypeDefinition baseType) {
        final StringBuffer sb = new StringBuffer();

        sb.append(getTypeName(simpleType, baseType));
        // if (EmfXsdUtils.isRestriction(simpleType)) {
        // sb.append(UIConstants.SPACE).append(UIConstants.OPEN_BRACKET).append(AbstractEditorLabelProvider.ANONYMOUS_LABEL)
        // .append(UIConstants.CLOSE_BRACKET).toString();
        // }
        return sb.toString();
    }

    protected abstract String getTypeName(final XSDTypeDefinition type, final XSDTypeDefinition baseType);

}
