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

import org.eclipse.wst.sse.sieditor.model.xsd.api.ISchema;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.dt.nodes.impl.NamespaceNode;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.labels.provider.ITreeNodeLabelsProvider;

public class ImportedSchemaNodeLabelsProvider implements ITreeNodeLabelsProvider {

    private final ISchema schema;

    public ImportedSchemaNodeLabelsProvider(final ISchema schema) {
        this.schema = schema;
    }

    @Override
    public String getDisplayName() {
        String importedSchemalocation = schema.getLocation();
        importedSchemalocation = importedSchemalocation == null ? UIConstants.EMPTY_STRING : importedSchemalocation; 

        final StringBuilder displayName = new StringBuilder(NamespaceNode.getNamespaceDisplayText(schema));
        if (displayName.length() > 0) {
            displayName.append(UIConstants.SPACE);
        }
        displayName.append(UIConstants.OPEN_BRACKET);
        displayName.append(importedSchemalocation);
        displayName.append(UIConstants.CLOSE_BRACKET);
        return displayName.toString();
    }

    @Override
    public String getTreeDisplayText() {
        return getDisplayName();
    }

    @Override
    public String getTypeDisplayText() {
        return UIConstants.EMPTY_STRING;
    }

}
