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
package org.eclipse.wst.sse.sieditor.ui.v2.wsdltree.nodes;

import java.util.List;

import org.eclipse.swt.graphics.Image;

import org.eclipse.wst.sse.sieditor.model.wsdl.api.IDescription;
import org.eclipse.wst.sse.sieditor.ui.Activator;
import org.eclipse.wst.sse.sieditor.ui.v2.UIConstants;
import org.eclipse.wst.sse.sieditor.ui.v2.nodes.ITreeNode;
import org.eclipse.wst.sse.sieditor.ui.v2.wsdl.controller.SIFormPageController;

public class ImportedServiceNode extends AbstractWsdlTreeNode {

    private SIFormPageController siFormPageController = null;
    
    public ImportedServiceNode(final IDescription description, final ITreeNode parent, final SIFormPageController siFormPageController) {
        super(description, parent, siFormPageController.getTreeNodeMapper());
        this.siFormPageController = siFormPageController;
    }

    @Override
    public String getDisplayName() {

        final IDescription description = (IDescription) getModelObject();
        String importedWSDLlocation = description.getLocation();
        importedWSDLlocation = importedWSDLlocation == null ? "" : importedWSDLlocation; //$NON-NLS-1$

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
    public Image getImage() {
        return getImageRegistry().get(Activator.NODE_NAMESPACE_GRAY);
    }

    @Override
    public Object[] getChildren() {
        final IDescription description = (IDescription) getModelObject();
        final List<ServiceInterfaceNode> serviceInterfaceNodes = siFormPageController.getServiceInterfaceNodes(this, description);
        return serviceInterfaceNodes.toArray();
    }
}
