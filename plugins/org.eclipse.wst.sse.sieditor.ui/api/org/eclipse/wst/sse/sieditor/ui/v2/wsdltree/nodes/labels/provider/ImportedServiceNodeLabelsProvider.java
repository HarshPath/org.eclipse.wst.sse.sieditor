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

import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.labels.provider.ITreeNodeLabelsProvider;

public class ImportedServiceNodeLabelsProvider implements ITreeNodeLabelsProvider {

    private final IDescription description;

    public ImportedServiceNodeLabelsProvider(final IDescription description) {
        this.description = description;
    }

    @Override
    public String getDisplayName() {
        String importedWSDLlocation = description.getLocation();
        importedWSDLlocation = importedWSDLlocation == null ? UIConstants.EMPTY_STRING : importedWSDLlocation;

        final StringBuilder displayName = new StringBuilder(description.getNamespace());
        if (displayName.length() > 0) {
            displayName.append(UIConstants.SPACE);
        }
        displayName.append(UIConstants.OPEN_BRACKET);
        displayName.append(importedWSDLlocation);
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
